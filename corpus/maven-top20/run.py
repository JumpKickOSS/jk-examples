#!/usr/bin/env python3
"""Maven-top-20 corpus runner: Maven (via `jk mvn`) vs jk, one repo at a time.

Reads repos.toml, clones each pinned SHA under $CORPUS_SCRATCH (default
/home/bsant/src/scratch/maven-corpus/<name>), runs the protocol described in
README.md, appends one JSONL row per repo to results/<date>.jsonl and rewrites
RESULTS.md + results/tier3-reasons.md from the latest row of every repo.

Idempotent: existing clones are reused (reset to the pinned SHA and cleaned of
build output), measurements are redone.  `--only <name>` re-runs one repo,
`--render` only rewrites RESULTS.md from the rows on disk.
"""
from __future__ import annotations

import argparse
import datetime as dt
import json
import os
import platform
import re
import shutil
import signal
import subprocess
import sys
import time
import tomllib
import xml.etree.ElementTree as ET
from pathlib import Path

HERE = Path(__file__).resolve().parent
SCRATCH = Path(os.environ.get("CORPUS_SCRATCH", "/home/bsant/src/scratch/maven-corpus"))
M2 = SCRATCH / ".m2"                 # dedicated Maven local repo so the first run is really cold
RESULTS = HERE / "results"
REPO_CAP = int(os.environ.get("CORPUS_REPO_CAP", 45 * 60))   # hard cap per repo, seconds
TEST_CAP = int(os.environ.get("CORPUS_TEST_CAP", 1200))      # per test run, seconds
TOUCH_LINE = "\n// jk-corpus touch: one-line comment appended by run.py, reverted after the measurement\n"

JK = ["jk", "--no-progress", "--no-ansi", "--no-notify"]
JK_MAVEN = Path.home() / ".jk" / "store" / "tools" / "maven" / "3.9.9" / "bin" / "mvn"   # what `jk mvn` provisions
MVN_ARGS = ["-B", "-q", f"-Dmaven.repo.local={M2}"]
JDKS = Path.home() / ".jdks"


def maven_launcher(root: Path) -> list[str]:
    """The repo's own wrapper when it ships one (like `jk mvn`), else the Maven jk provisioned.

    We do not go through `jk mvn` itself: its PassthroughEnv deliberately strips MAVEN_OPTS and
    JAVA_TOOL_OPTIONS, and there is no other heap knob short of writing .mvn/jvm.config into the clone.
    """
    if (root / "mvnw").is_file():
        return ["sh", "./mvnw"]
    if not JK_MAVEN.is_file():
        subprocess.run(["jk", "mvn", "-version"], cwd=root, capture_output=True)
    return [str(JK_MAVEN)]


def maven_env(level: str) -> dict:
    """MAVEN_OPTS heap cap plus JAVA_HOME = the jk-installed Temurin matching the declared level."""
    env = {"MAVEN_OPTS": "-Xmx3g"}
    home = JDKS / f"temurin-{level.split('.')[0]}"
    if home.is_dir():
        env["JAVA_HOME"] = str(home)
        env["PATH"] = f"{home}/bin:" + os.environ.get("PATH", "")
    return env

MVN_BOILERPLATE = re.compile(
    r"^\[ERROR\]\s*$|-> \[Help \d\]|To see the full stack trace|Re-run Maven using|"
    r"For more information about the errors|Please read the following articles|"
    r"After correcting the problems|^\[ERROR\]\s+mvn <args>|^\[ERROR\]\s+\[Help \d\]"
)


# --------------------------------------------------------------------------- process plumbing

def run(cmd: list[str], cwd: Path, log: Path, timeout: float | None, env: dict | None = None) -> dict:
    """Run cmd in cwd, append output to log, return {status, exit, wall}.

    status: ok | fail | timeout | capped (no time left in the repo budget).
    Kills the whole process group on timeout so forked JVMs die too.
    """
    if timeout is not None and timeout <= 0:
        return {"status": "capped", "exit": None, "wall": 0.0}
    log.parent.mkdir(parents=True, exist_ok=True)
    full_env = dict(os.environ)
    if env:
        full_env.update(env)
    t0 = time.time()
    with open(log, "ab") as f:
        f.write(f"\n$ (cd {cwd} && {' '.join(cmd)})   # {dt.datetime.now():%H:%M:%S}\n".encode())
        f.flush()
        p = subprocess.Popen(cmd, cwd=cwd, stdout=f, stderr=subprocess.STDOUT, env=full_env,
                             start_new_session=True)
        try:
            rc = p.wait(timeout=timeout)
            status = "ok" if rc == 0 else "fail"
        except subprocess.TimeoutExpired:
            try:
                os.killpg(p.pid, signal.SIGTERM)
                p.wait(timeout=20)
            except Exception:
                os.killpg(p.pid, signal.SIGKILL)
                p.wait()
            rc, status = None, "timeout"
        f.write(f"# -> {status} exit={rc} wall={time.time() - t0:.1f}s\n".encode())
    return {"status": status, "exit": rc, "wall": round(time.time() - t0, 1)}


def git(root: Path, *args: str) -> subprocess.CompletedProcess:
    return subprocess.run(["git", "-C", str(root), *args], capture_output=True, text=True)


# --------------------------------------------------------------------------- repo helpers

def ensure_clone(repo: dict, root: Path, log: Path) -> dict:
    """Pinned shallow clone; reuse if present.  Always ends at the pinned SHA with a clean tree."""
    if not (root / ".git").is_dir():
        if root.exists():
            shutil.rmtree(root)
        root.mkdir(parents=True)
        git(root, "init", "-q")
        git(root, "remote", "add", "origin", repo["url"])
        r = run(["git", "fetch", "-q", "--depth", "1", "origin", repo["sha"]], root, log, 20 * 60)
        if r["status"] != "ok":
            return r
        git(root, "checkout", "-q", "FETCH_HEAD")
        r["status"] = "ok"
    else:
        r = {"status": "ok", "exit": 0, "wall": 0.0}
        head = git(root, "rev-parse", "HEAD").stdout.strip()
        if head != repo["sha"]:
            git(root, "fetch", "-q", "--depth", "1", "origin", repo["sha"])
            git(root, "checkout", "-q", repo["sha"])
    reset_tree(root)
    return r


def reset_tree(root: Path) -> None:
    """Drop everything the previous measurement left behind (jk.toml, target/, .m2 is elsewhere)."""
    git(root, "checkout", "-q", "--", ".")
    git(root, "clean", "-fdxq")


def poms(root: Path) -> list[Path]:
    return sorted(p for p in root.rglob("pom.xml")
                  if "target" not in p.parts and "node_modules" not in p.parts and ".git" not in p.parts)


def pick_touch_file(root: Path) -> Path | None:
    """One main .java file in the leaf module that has the most main sources (deterministic)."""
    dirs = [p.parent for p in poms(root)]
    leaves = [d for d in dirs if not any(o != d and o.is_relative_to(d) for o in dirs)]
    best: tuple[int, Path] | None = None
    for d in leaves:
        src = d / "src" / "main" / "java"
        if not src.is_dir():
            continue
        files = sorted(f for f in src.rglob("*.java") if f.name != "package-info.java" and f.name != "module-info.java")
        if files and (best is None or len(files) > best[0]):
            best = (len(files), files[0])
    return best[1] if best else None


def touch(root: Path, f: Path) -> None:
    with open(f, "a", encoding="utf-8") as fh:
        fh.write(TOUCH_LINE)


def untouch(root: Path, f: Path) -> None:
    git(root, "checkout", "-q", "--", str(f.relative_to(root)))


# --------------------------------------------------------------------------- result parsing

def sum_junit_xml(files: list[Path]) -> dict:
    tot = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0, "files": 0}
    for f in files:
        try:
            for ev, el in ET.iterparse(f):
                if el.tag == "testsuite":
                    for k in ("tests", "failures", "errors", "skipped"):
                        tot[k] += int(float(el.get(k, "0") or 0))
                    tot["files"] += 1
                    el.clear()
        except ET.ParseError:
            pass
    tot["passed"] = tot["tests"] - tot["failures"] - tot["errors"] - tot["skipped"]
    return tot


def surefire_totals(root: Path) -> dict:
    return sum_junit_xml([p for p in root.rglob("target/surefire-reports/TEST-*.xml")])


def jk_junit_totals(root: Path) -> dict:
    return sum_junit_xml([p for p in root.rglob("target/reports/test-results/**/*.xml")
                          if p.is_file() and "surefire-reports" not in p.parts])


def jk_results_tests_line(root: Path) -> dict | None:
    """Parse the `Tests:` headline of target/jk-results.md."""
    p = root / "target" / "jk-results.md"
    if not p.is_file():
        return None
    for line in p.read_text(encoding="utf-8", errors="replace").splitlines():
        if line.startswith("Tests:"):
            def num(pat: str) -> int:
                m = re.search(pat, line)
                return int(m.group(1)) if m else 0
            return {"failed": num(r"(\d+) failed"), "passed": num(r"(\d+) passed"),
                    "skipped": num(r"(\d+) skipped"), "total": num(r"\((\d+) total\)"), "line": line.strip()}
    return None


def jk_results_headline(root: Path) -> str:
    """The first cause in target/jk-results.md: the `## Failures` block's headline plus its first detail line."""
    p = root / "target" / "jk-results.md"
    if not p.is_file():
        return ""
    lines = p.read_text(encoding="utf-8", errors="replace").splitlines()
    if "## Failures" in lines:
        i = lines.index("## Failures")
        step = ""
        picked: list[str] = []
        in_block = False
        for line in lines[i + 1:]:
            if line.startswith("### ") and not step:
                step = line[4:].strip()
            elif line.startswith("```"):
                if in_block:
                    break
                in_block = True
            elif in_block:
                s = line.strip().lstrip("‼│|").strip()
                if s and not s.startswith("available:") and not s.startswith("Suggestions") and not s.startswith("•"):
                    picked.append(s)
                if len(picked) >= 2:
                    break
        if picked:
            return (f"[{step}] " if step else "") + " ".join(picked)[:220]
    for line in lines[1:]:
        s = line.strip().lstrip("|").strip()
        if s.startswith("- ") and "`" in s:
            return s[2:][:200]
    return ""


def pom_skips_tests(root: Path) -> bool:
    """True when the project's own poms turn tests off (`skipTests` / `maven.test.skip` = true)."""
    pat = re.compile(r"<(skipTests|maven\.test\.skip)>\s*true\s*</")
    return any(pat.search(p.read_text(encoding="utf-8", errors="replace")) for p in poms(root))


def first_mvn_error(log: Path) -> str:
    try:
        for line in log.read_text(encoding="utf-8", errors="replace").splitlines():
            if line.startswith("[ERROR]") and not MVN_BOILERPLATE.search(line):
                return line[len("[ERROR]"):].strip()[:200]
    except FileNotFoundError:
        pass
    return ""


def first_jk_error(log: Path, root: Path) -> str:
    reason = jk_results_headline(root)
    if reason:
        return reason
    try:
        for line in log.read_text(encoding="utf-8", errors="replace").splitlines():
            if "‼" in line or "Failure" in line or " ! " in line or "error:" in line.lower():
                return line.strip().lstrip("|").strip()[:200]
    except FileNotFoundError:
        pass
    return ""


def parse_import_report(path: Path) -> dict:
    """Count Tier-3 (ERROR) and Tier-2 (WARNING) bullets and keep the Tier-3 texts."""
    out = {"errors": 0, "warnings": 0, "tier3": [], "tier2": []}
    if not path.is_file():
        return out
    section = None
    for line in path.read_text(encoding="utf-8", errors="replace").splitlines():
        if line.startswith("## Tier 3"):
            section = "tier3"
        elif line.startswith("## Tier 2"):
            section = "tier2"
        elif line.startswith("## "):
            section = None
        elif section and line.startswith("- "):
            out[section].append(line[2:].strip())
    out["errors"] = len(out["tier3"])
    out["warnings"] = len(out["tier2"])
    return out


# --------------------------------------------------------------------------- the protocol

def measure(repo: dict, args) -> dict:
    name = repo["name"]
    root = SCRATCH / name
    rdir = RESULTS / name
    rdir.mkdir(parents=True, exist_ok=True)
    for old in rdir.glob("*.log"):
        old.unlink()
    started = time.time()
    deadline = started + REPO_CAP
    row: dict = {"repo": name, "full": repo["full"], "stars": repo["stars"], "sha": repo["sha"],
                 "java": repo["java"], "date": dt.datetime.now().isoformat(timespec="seconds"),
                 "steps": {}, "capped_at": None, "notes": []}

    def left() -> float:
        return deadline - time.time()

    def step(key: str, cmd: list[str], log: str, cap: float | None = None, cwd: Path = root, env: dict | None = None) -> dict:
        t = left() if cap is None else min(cap, left())
        r = run(cmd, cwd, rdir / log, t, env)
        row["steps"][key] = r
        if r["status"] == "capped" and row["capped_at"] is None:
            row["capped_at"] = key
        print(f"  {name}: {key:16s} {r['status']:8s} {r['wall']:8.1f}s", flush=True)
        return r

    print(f"== {name} ({repo['full']}) @ {repo['sha'][:10]}", flush=True)
    c = ensure_clone(repo, root, rdir / "clone.log")
    row["steps"]["clone"] = c
    if c["status"] != "ok":
        row["first_failure"] = "clone failed"
        return row
    row["modules"] = len(poms(root))
    tf = pick_touch_file(root)
    row["touch_file"] = str(tf.relative_to(root)) if tf else None

    # ---- Maven side --------------------------------------------------------
    M2.mkdir(exist_ok=True)
    mvn_first_error = ""
    MVN = maven_launcher(root) + MVN_ARGS
    menv = maven_env(repo["java"])
    row["maven_launcher"] = MVN[0] if MVN[0] != "sh" else "./mvnw"
    row["maven_java_home"] = menv.get("JAVA_HOME", "host")
    if not args.skip_mvn:
        r = step("mvn_cold", MVN + ["-DskipTests", "package"], "mvn-cold.log", env=menv)
        if r["status"] == "ok":
            step("mvn_warm_clean", MVN + ["-DskipTests", "clean", "package"], "mvn-warm-clean.log", env=menv)
            step("mvn_noop", MVN + ["-DskipTests", "package"], "mvn-noop.log", env=menv)
            if tf:
                touch(root, tf)
                step("mvn_touch", MVN + ["-DskipTests", "package"], "mvn-touch.log", env=menv)
                untouch(root, tf)
            step("mvn_test", MVN + ["test"], "mvn-test.log", cap=TEST_CAP, env=menv)
            row["mvn_tests"] = surefire_totals(root)
            if pom_skips_tests(root):
                row["notes"].append("the project's own pom sets skipTests/maven.test.skip=true, so `mvn test` runs nothing")
            if row["steps"]["mvn_test"]["status"] != "ok":
                mvn_first_error = first_mvn_error(rdir / "mvn-test.log")
        else:
            mvn_first_error = first_mvn_error(rdir / "mvn-cold.log") or f"mvn package {r['status']}"
            for k in ("mvn_warm_clean", "mvn_noop", "mvn_touch", "mvn_test"):
                row["steps"][k] = {"status": "skipped", "exit": None, "wall": 0.0}
        row["mvn_first_error"] = mvn_first_error
        reset_tree(root)   # jk starts from the same pristine checkout Maven did

    # ---- jk side -----------------------------------------------------------
    jk_first_error = ""
    report = rdir / "import-report.md"
    r = step("jk_import", JK + ["import", "pom.xml", "--report", str(report)], "jk-import.log", cap=600)
    row["import"] = parse_import_report(report)
    if r["status"] != "ok":
        jk_first_error = first_jk_error(rdir / "jk-import.log", root) or "jk import failed"
    lock_ok = False
    if (root / "jk.toml").is_file():
        r = step("jk_lock", JK + ["lock"], "jk-lock.log", cap=900)
        lock_ok = r["status"] == "ok"
        if not lock_ok and not jk_first_error:
            jk_first_error = first_jk_error(rdir / "jk-lock.log", root) or f"jk lock {r['status']}"
    else:
        row["steps"]["jk_lock"] = {"status": "skipped", "exit": None, "wall": 0.0}
        jk_first_error = jk_first_error or "jk import wrote no jk.toml"
    if lock_ok:
        r = step("jk_build_cold", JK + ["build", "--skip-tests"], "jk-build-cold.log")
        if r["status"] == "ok":
            step("jk_build_noop", JK + ["build", "--skip-tests"], "jk-build-noop.log")
            if tf:
                touch(root, tf)
                step("jk_build_touch", JK + ["build", "--skip-tests"], "jk-build-touch.log")
                untouch(root, tf)
            step("jk_test", JK + ["test"], "jk-test.log", cap=TEST_CAP)
            row["jk_tests_line"] = jk_results_tests_line(root)
            row["jk_tests"] = jk_junit_totals(root)
            if row["steps"]["jk_test"]["status"] != "ok" and not jk_first_error:
                jk_first_error = first_jk_error(rdir / "jk-test.log", root)
        else:
            jk_first_error = jk_first_error or first_jk_error(rdir / "jk-build-cold.log", root) or f"jk build {r['status']}"
            for k in ("jk_build_noop", "jk_build_touch", "jk_test"):
                row["steps"][k] = {"status": "skipped", "exit": None, "wall": 0.0}
    else:
        for k in ("jk_build_cold", "jk_build_noop", "jk_build_touch", "jk_test"):
            row["steps"][k] = {"status": "skipped", "exit": None, "wall": 0.0}
    if (root / "target" / "jk-results.md").is_file():
        shutil.copy(root / "target" / "jk-results.md", rdir / "jk-results.md")
    row["jk_first_error"] = jk_first_error
    row["first_failure"] = jk_first_error or mvn_first_error
    row["total_wall"] = round(time.time() - started, 1)
    if row["capped_at"]:
        row["notes"].append(f"timed out at step {row['capped_at']} (45-minute repo cap)")
    return row


# --------------------------------------------------------------------------- rendering

def host_info() -> dict:
    cpu = ""
    try:
        for line in Path("/proc/cpuinfo").read_text().splitlines():
            if line.startswith("model name"):
                cpu = line.split(":", 1)[1].strip()
                break
    except OSError:
        pass
    ram_gb = 0
    try:
        for line in Path("/proc/meminfo").read_text().splitlines():
            if line.startswith("MemTotal"):
                ram_gb = round(int(line.split()[1]) / 1024 / 1024)
    except OSError:
        pass
    jkv = subprocess.run(["jk", "--version"], capture_output=True, text=True).stdout.strip()
    return {"cpu": cpu, "cores": os.cpu_count(), "ram_gb": ram_gb, "os": platform.platform(), "jk": jkv}


def load_rows() -> dict[str, dict]:
    latest: dict[str, dict] = {}
    for f in sorted(RESULTS.glob("*.jsonl")):
        for line in f.read_text().splitlines():
            if not line.strip():
                continue
            row = json.loads(line)
            if row["repo"] not in latest or row["date"] >= latest[row["repo"]]["date"]:
                latest[row["repo"]] = row
    return latest


def fmt_step(s: dict | None) -> str:
    if not s:
        return "—"
    st = s["status"]
    if st == "ok":
        return f"{s['wall']:.0f}s"
    if st in ("skipped", "capped"):
        return st
    return f"{st} ({s['wall']:.0f}s)"


def fmt_status(s: dict | None) -> str:
    if not s:
        return "—"
    return {"ok": "ok", "fail": "FAIL", "timeout": "timeout", "skipped": "skipped", "capped": "capped"}.get(s["status"], s["status"])


def tests_cell(t: dict | None, line: dict | None = None) -> str:
    if line and line.get("total"):
        return f"{line['passed']}/{line['total']}"
    if t and t.get("tests"):
        return f"{t['passed']}/{t['tests']}"
    return "—"


def render(repos: list[dict], rows: dict[str, dict]) -> None:
    h = host_info()
    cfg = tomllib.load(open(HERE / "repos.toml", "rb"))
    lines = ["# Maven top-20 corpus — results", "",
             f"Generated {dt.datetime.now():%Y-%m-%d %H:%M} by `run.py`. Tool: `{h['jk']}`. "
             f"Host: {h['cpu']} ({h['cores']} threads, {h['ram_gb']} GB RAM), {h['os']}.", "",
             "Maven ran through the launcher `jk mvn` provisions (or the repo's `mvnw`) with `MAVEN_OPTS=-Xmx3g`, "
             f"`JAVA_HOME` = the Temurin matching the declared level, and a corpus-private local repo (`{M2}`); jk ran with defaults. Wall = seconds. `pass/total` from surefire XML (Maven) and "
             "the `Tests:` line of `target/jk-results.md` (jk). Per-step logs and each import report live "
             "under `results/<repo>/`.", "",
             "| # | repo | stars | modules | java | import E/W | lock | build | tests jk | tests mvn | mvn cold | mvn warm-clean | mvn no-op | mvn touch | mvn test | jk cold | jk no-op | jk touch | jk test | first failure |",
             "|--:|------|------:|--------:|:----:|:---------:|:----:|:-----:|:--------:|:---------:|--------:|---------------:|----------:|----------:|---------:|--------:|---------:|---------:|--------:|---------------|"]
    n_import_clean = n_build = n_tests_equal = n_lock = n_measured = 0
    for i, repo in enumerate(repos, 1):
        row = rows.get(repo["name"])
        if not row:
            lines.append(f"| {i} | [{repo['full']}](https://github.com/{repo['full']}) | {repo['stars']} | | {repo['java']} | not run | | | | | | | | | | | | | | |")
            continue
        n_measured += 1
        s = row["steps"]
        imp = row.get("import", {})
        if imp.get("errors", 0) == 0 and s.get("jk_import", {}).get("status") == "ok":
            n_import_clean += 1
        if s.get("jk_lock", {}).get("status") == "ok":
            n_lock += 1
        if s.get("jk_build_cold", {}).get("status") == "ok":
            n_build += 1
        jk_t = row.get("jk_tests_line") or {}
        mv_t = row.get("mvn_tests") or {}
        if jk_t.get("total") and mv_t.get("tests") and jk_t["total"] == mv_t["tests"]:
            n_tests_equal += 1
        first = (row.get("first_failure") or "").replace("|", "\\|")
        if row.get("capped_at"):
            first = f"timed out at {row['capped_at']}; " + first
        for n in row.get("notes", []):
            if "skipTests" in n:
                first = (first + "; " if first else "") + "pom skips tests under Maven"
        lines.append(
            f"| {i} | [{repo['full']}](https://github.com/{repo['full']}) | {row['stars']} | {row.get('modules', '')} | {row['java']} "
            f"| {imp.get('errors', '—')}/{imp.get('warnings', '—')} | {fmt_status(s.get('jk_lock'))} | {fmt_status(s.get('jk_build_cold'))} "
            f"| {tests_cell(row.get('jk_tests'), jk_t)} | {tests_cell(mv_t)} "
            f"| {fmt_step(s.get('mvn_cold'))} | {fmt_step(s.get('mvn_warm_clean'))} | {fmt_step(s.get('mvn_noop'))} | {fmt_step(s.get('mvn_touch'))} | {fmt_step(s.get('mvn_test'))} "
            f"| {fmt_step(s.get('jk_build_cold'))} | {fmt_step(s.get('jk_build_noop'))} | {fmt_step(s.get('jk_build_touch'))} | {fmt_step(s.get('jk_test'))} "
            f"| {first} |")
    lines += ["", f"Measured {n_measured} of {len(repos)} selected repos" + (" (**partial run**)." if n_measured < len(repos) else "."), ""]
    sk = cfg.get("skipped", [])
    lines += ["## Skipped (in the same star range, root pom.xml present)", "",
              "| repo | stars | reason | detail |", "|------|------:|--------|--------|"]
    for r in sk:
        lines.append(f"| {r['full']} | {r['stars']} | {r['reason']} | {r['detail']} |")
    lines += ["", "## Ratchet", "",
              f"- repos importing with zero Tier-3 errors: **{n_import_clean}** / {n_measured}",
              f"- repos whose `jk lock` succeeds: **{n_lock}** / {n_measured}",
              f"- repos whose `jk build --skip-tests` succeeds: **{n_build}** / {n_measured}",
              f"- repos whose jk test total equals Maven's: **{n_tests_equal}** / {n_measured}", "",
              "Rule: a run that lowers any of these counts is a regression; a run that raises one moves the bar.", ""]
    (HERE / "RESULTS.md").write_text("\n".join(lines))
    render_tier3(repos, rows)


def normalize_reason(text: str) -> str:
    t = re.sub(r"^-?\s*(`[^`]*`\s*)+:?\s*", "", text)             # drop "- `module` `step`: " prefix
    t = re.sub(r"^\[[^\]]+\]\s*", "", t)                         # drop [module] / [step — module] prefix
    t = re.sub(r"`?[\w.-]+:[\w.-]+(:[\w.-]+)?`?", "G:A", t)        # coordinates
    t = re.sub(r"/home/\S+", "<path>", t)
    t = re.sub(r"\d+(\.\d+)+", "N", t)                            # versions / numbers
    return t.strip()


def render_tier3(repos: list[dict], rows: dict[str, dict]) -> None:
    groups: dict[str, dict[str, set]] = {"import Tier 3 (not imported)": {}, "jk lock failure": {}, "jk build failure": {}, "jk test failure": {}, "Maven-side failure (for context)": {}}
    for repo in repos:
        row = rows.get(repo["name"])
        if not row:
            continue
        for t in row.get("import", {}).get("tier3", []):
            groups["import Tier 3 (not imported)"].setdefault(normalize_reason(t), set()).add(repo["name"])
        s = row["steps"]
        if s.get("jk_lock", {}).get("status") not in (None, "ok", "skipped"):
            groups["jk lock failure"].setdefault(normalize_reason(row.get("jk_first_error") or "lock failed"), set()).add(repo["name"])
        if s.get("jk_build_cold", {}).get("status") not in (None, "ok", "skipped"):
            groups["jk build failure"].setdefault(normalize_reason(row.get("jk_first_error") or "build failed"), set()).add(repo["name"])
        if s.get("jk_test", {}).get("status") not in (None, "ok", "skipped"):
            groups["jk test failure"].setdefault(normalize_reason(row.get("jk_first_error") or "test failed"), set()).add(repo["name"])
        if row.get("mvn_first_error"):
            groups["Maven-side failure (for context)"].setdefault(normalize_reason(row["mvn_first_error"]), set()).add(repo["name"])
    out = ["# Tier-3 reasons (import ERRORs and jk failures), grouped", "",
           f"Generated {dt.datetime.now():%Y-%m-%d %H:%M}. Module prefixes, coordinates, versions and paths are normalized "
           "so one line = one distinct cause = one ticket candidate.", ""]
    for g, reasons in groups.items():
        out.append(f"## {g}")
        out.append("")
        if not reasons:
            out.append("_none_")
        for reason, names in sorted(reasons.items(), key=lambda kv: (-len(kv[1]), kv[0])):
            out.append(f"- ({len(names)}) {reason}  \n  repos: {', '.join(sorted(names))}")
        out.append("")
    (RESULTS / "tier3-reasons.md").write_text("\n".join(out))


# --------------------------------------------------------------------------- main

def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--only", action="append", help="run only this repo name (repeatable)")
    ap.add_argument("--order", choices=["stars", "small-first"], default="stars",
                    help="run order; the table is always in star order")
    ap.add_argument("--skip-mvn", action="store_true", help="jk side only")
    ap.add_argument("--render", action="store_true", help="only rewrite RESULTS.md from rows on disk")
    ap.add_argument("--fresh-m2", action="store_true", help="wipe the corpus-private Maven local repo first")
    args = ap.parse_args()

    cfg = tomllib.load(open(HERE / "repos.toml", "rb"))
    repos = cfg["repo"]
    RESULTS.mkdir(exist_ok=True)
    if args.render:
        render(repos, load_rows())
        return 0
    if args.fresh_m2 and M2.exists():
        shutil.rmtree(M2)
    todo = [r for r in repos if not args.only or r["name"] in args.only]
    if args.order == "small-first":
        todo.sort(key=lambda r: (len(poms(SCRATCH / r["name"])) if (SCRATCH / r["name"]).is_dir() else 10 ** 6, -r["stars"]))
    out = RESULTS / f"{dt.date.today():%Y-%m-%d}.jsonl"
    for repo in todo:
        row = measure(repo, args)
        with open(out, "a") as f:
            f.write(json.dumps(row, sort_keys=True) + "\n")
        render(repos, load_rows())
        print(f"  {repo['name']}: total {row['total_wall'] if 'total_wall' in row else '?'}s -> {out}", flush=True)
    return 0


if __name__ == "__main__":
    sys.exit(main())

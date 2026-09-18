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


def without_delta_section(lines: list[str]) -> list[str]:
    """Drop the `## Since the previous run` section: its bullets describe the diff, not a failure."""
    out: list[str] = []
    skipping = False
    for line in lines:
        if line.startswith("## "):
            skipping = line.strip() == "## Since the previous run"
        if not skipping:
            out.append(line)
    return out


def jk_results_headline(root: Path) -> str:
    """The first cause in target/jk-results.md: the `## Failures` block's headline plus its first detail line."""
    p = root / "target" / "jk-results.md"
    if not p.is_file():
        return ""
    lines = without_delta_section(p.read_text(encoding="utf-8", errors="replace").splitlines())
    if "### Failed tests" in lines:
        i = lines.index("### Failed tests")
        cls = meth = exc = ""
        for line in lines[i + 1:]:
            if line.startswith("#### ") and not cls:
                cls = line[5:].strip()
            elif line.startswith("##### ") and not meth:
                meth = line[6:].strip().split("`")[1] if "`" in line else line[6:].strip()
            elif cls and meth and line.strip() and not line.startswith("```") and not exc:
                exc = line.strip()
                break
        if cls:
            return f"test failure: {cls}#{meth} — {exc}"[:260]
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
                if len(picked) >= 3:
                    break
        if picked:
            return (f"[{step}] " if step else "") + " · ".join(picked)[:260]
    for line in lines[1:]:
        s = line.strip().lstrip("|").strip()
        if s.startswith("- ") and "`" in s and "report (this file)" not in s and "transcript" not in s:
            return s[2:][:200]
    return ""


def first_log_line(log: Path) -> str:
    """The first line of a step log that is neither our banner nor a jk status footer."""
    try:
        for line in log.read_text(encoding="utf-8", errors="replace").splitlines():
            s = line.strip()
            if s and not s.startswith("$ ") and not s.startswith("# ->") and not s.startswith("jk: !") \
                    and not s.startswith("WARNING:"):
                return s[:220]
    except FileNotFoundError:
        pass
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
    reason = first_log_line(log)
    if reason:
        return reason
    try:
        for line in log.read_text(encoding="utf-8", errors="replace").splitlines():
            if "‼" in line or "Failure" in line or " ! " in line or "error:" in line.lower():
                return line.strip().lstrip("|").strip()[:200]
    except FileNotFoundError:
        pass
    return ""


def jk_workspace_modules(root: Path) -> int | None:
    """How many modules the imported jk.toml actually declares (None when there is no jk.toml)."""
    p = root / "jk.toml"
    if not p.is_file():
        return None
    try:
        d = tomllib.load(open(p, "rb"))
    except Exception:
        return None
    mods = d.get("workspace", {}).get("modules")
    if mods is None:
        return 1 if (root / "src").is_dir() else 0
    return len(mods)


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
    row.update(jk_identity())

    def left() -> float:
        return deadline - time.time()

    def step(key: str, cmd: list[str], log: str, cap: float | None = None, cwd: Path = root, env: dict | None = None) -> dict:
        t = left() if cap is None else min(cap, left())
        r = run(cmd, cwd, rdir / log, t, env)
        if r["status"] == "timeout" and (cap is None or t < cap):
            r["status"] = "capped"          # killed by the 45-minute repo cap, not by its own timeout
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
    mvn_args = list(repo.get("mvn_args", []))         # e.g. ["-P", "default,default-heavy"]; see repos.toml
    MVN = maven_launcher(root) + MVN_ARGS + mvn_args
    menv = maven_env(repo.get("maven_jdk", repo["java"]))
    row["maven_launcher"] = MVN[0] if MVN[0] != "sh" else "./mvnw"
    row["maven_java_home"] = menv.get("JAVA_HOME", "host")
    row["mvn_args"] = mvn_args
    prev = None if args.both else load_rows().get(name)
    if prev and prev.get("steps", {}).get("mvn_cold"):
        # --jk-only (the default): carry the last measured Maven side forward untouched
        for k in ("mvn_cold", "mvn_warm_clean", "mvn_noop", "mvn_touch", "mvn_test"):
            if k in prev["steps"]:
                row["steps"][k] = prev["steps"][k]
        for k in ("mvn_tests", "mvn_first_error", "maven_launcher", "maven_java_home", "mvn_args"):
            if k in prev:
                row[k] = prev[k]
        row["notes"] += [n for n in prev.get("notes", []) if "Maven" in n or "mvn" in n]
        row["mvn_reused_from"] = prev["date"]
        mvn_first_error = prev.get("mvn_first_error", "")
        print(f"  {name}: maven side reused from {prev['date']}", flush=True)
    elif not args.skip_mvn:
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
    import_args = list(repo.get("import_args", []))   # e.g. ["-P", "default,default-heavy"]; see repos.toml
    row["import_args"] = import_args
    r = step("jk_import", JK + ["import", "pom.xml", *import_args, "--report", str(report)], "jk-import.log", cap=600)
    row["import"] = parse_import_report(report)
    row["jk_modules"] = jk_workspace_modules(root)
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


# --------------------------------------------------------------------------- jk identity

def sha256_of(path: Path) -> str:
    import hashlib
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()[:16]


def jk_identity() -> dict:
    """Version + the commit that built it (when knowable) + content hashes of binary and engine jar.

    The binary embeds no commit. `JK_COMMIT` (set by whoever installed a main-built jk) wins;
    otherwise the `v<version>` tag is resolved in the jk checkout at `JK_SRC` (default ~/src/oss/jk).
    """
    version = subprocess.run(["jk", "--version"], capture_output=True, text=True).stdout.strip()
    commit = os.environ.get("JK_COMMIT", "")
    if not commit:
        src = Path(os.environ.get("JK_SRC", Path.home() / "src" / "oss" / "jk"))
        ver = version.split()[-1] if version else ""
        if (src / ".git").exists() and ver:
            r = subprocess.run(["git", "-C", str(src), "rev-parse", "--verify", "-q", f"v{ver}^{{commit}}"],
                               capture_output=True, text=True)
            if r.returncode == 0:
                commit = r.stdout.strip() + f" (tag v{ver} in {src})"
    binary = shutil.which("jk")
    engine = sorted((Path.home() / ".jk" / "lib" / "jk-engine").glob("jk-engine-*.jar"))
    return {"jk_version": version, "jk_commit": commit or "unknown (binary embeds none; set JK_COMMIT)",
            "jk_binary": f"{binary} sha256:{sha256_of(Path(binary))}" if binary else "",
            "jk_engine_jar": f"{engine[-1].name} sha256:{sha256_of(engine[-1])}" if engine else ""}


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


def all_rows() -> list[dict]:
    rows = []
    for f in sorted(RESULTS.glob("*.jsonl")):
        for line in f.read_text().splitlines():
            if line.strip():
                row = json.loads(line)
                row.setdefault("run", "run1")
                rows.append(row)
    return rows


def load_rows(run: str | None = None) -> dict[str, dict]:
    """Latest row per repo; restricted to one run label when given (Maven reuse wants any run)."""
    latest: dict[str, dict] = {}
    for row in all_rows():
        if run and row["run"] != run:
            continue
        if row["repo"] not in latest or row["date"] >= latest[row["repo"]]["date"]:
            latest[row["repo"]] = row
    return latest


def run_labels() -> list[str]:
    seen: dict[str, str] = {}
    for row in all_rows():
        seen[row["run"]] = min(row["date"], seen.get(row["run"], row["date"]))
    return sorted(seen, key=lambda k: seen[k])


TEST_STEPS = {"mvn_test", "jk_test", "jk_import", "jk_lock"}   # the only steps with their own timeout


def fmt_step(s: dict | None, key: str = "") -> str:
    if not s:
        return "—"
    st = s["status"]
    if st == "timeout" and key and key not in TEST_STEPS:
        st = "capped"                      # rows from before the capped/timeout split
    if st == "ok":
        return f"{s['wall']:.0f}s"
    if st in ("skipped", "capped"):
        return st
    return f"{st} ({s['wall']:.0f}s)"


def fmt_status(s: dict | None) -> str:
    if not s:
        return "—"
    return {"ok": "ok", "fail": "FAIL", "timeout": "timeout", "skipped": "skipped", "capped": "capped"}.get(s["status"], s["status"])


def build_cell(row: dict) -> str:
    """`jk build` status qualified by how much of the project the import actually handed it."""
    s = row["steps"].get("jk_build_cold")
    st = fmt_status(s)
    if st != "ok":
        return st
    jm, pm = row.get("jk_modules"), row.get("modules")
    if jm is None:
        return "ok"
    if jm == 0:
        return "built nothing (0 modules)"
    return f"ok ({jm}/{pm} modules)" if pm and jm < pm else "ok"


def tests_cell(step: dict | None, t: dict | None, line: dict | None = None) -> str:
    """pass/total; an `ok` step with zero parsed tests is "no tests ran", never a pass."""
    total = (line or {}).get("total") or (t or {}).get("tests") or 0
    passed = (line or {}).get("passed") if (line or {}).get("total") else (t or {}).get("passed", 0)
    st = (step or {}).get("status")
    if st in (None, "skipped", "capped"):
        return "—" if st != "capped" else "capped"
    if total == 0:
        # jk answers a run that found no test with exit 2 and `no tests ran`; Maven answers exit 0.
        if st == "ok" or (step or {}).get("exit") == 2:
            return "no tests ran"
        return f"{st}, no results"
    cell = f"{passed}/{total}"
    return cell if st == "ok" else f"{cell} ({st})"


def counts(repos: list[dict], rows: dict[str, dict]) -> dict:
    c = {"measured": 0, "import_clean": 0, "lock": 0, "build": 0, "tests_equal": 0, "tests_ran": 0}
    for repo in repos:
        row = rows.get(repo["name"])
        if not row:
            continue
        s = row["steps"]
        c["measured"] += 1
        if row.get("import", {}).get("errors", 0) == 0 and s.get("jk_import", {}).get("status") == "ok":
            c["import_clean"] += 1
        if s.get("jk_lock", {}).get("status") == "ok":
            c["lock"] += 1
        if s.get("jk_build_cold", {}).get("status") == "ok" and (row.get("jk_modules") or 0) > 0:
            c["build"] += 1   # a build that compiled nothing does not count
        jk_t = row.get("jk_tests_line") or {}
        mv_t = row.get("mvn_tests") or {}
        if jk_t.get("total") and s.get("jk_test", {}).get("status") == "ok":
            c["tests_ran"] += 1
        if jk_t.get("total") and mv_t.get("tests") and jk_t["total"] == mv_t["tests"] \
                and s.get("jk_test", {}).get("status") == "ok" and s.get("mvn_test", {}).get("status") == "ok":
            c["tests_equal"] += 1   # zero-test runs never count: "ran nothing" is not "equal"
    return c


def run_table(repos: list[dict], rows: dict[str, dict]) -> list[str]:
    lines = ["| # | repo | stars | modules | java | import E/W | lock | build | tests jk | tests mvn | mvn cold | mvn warm-clean | mvn no-op | mvn touch | mvn test | jk cold | jk no-op | jk touch | jk test | first failure |",
             "|--:|------|------:|--------:|:----:|:---------:|:----:|:-----:|:--------:|:---------:|--------:|---------------:|----------:|----------:|---------:|--------:|---------:|---------:|--------:|---------------|"]
    for i, repo in enumerate(repos, 1):
        row = rows.get(repo["name"])
        if not row:
            lines.append(f"| {i} | [{repo['full']}](https://github.com/{repo['full']}) | {repo['stars']} | | {repo['java']} | not run | | | | | | | | | | | | | | |")
            continue
        s = row["steps"]
        imp = row.get("import", {})
        jk_t = row.get("jk_tests_line") or {}
        mv_t = row.get("mvn_tests") or {}
        first = (row.get("first_failure") or "").replace("|", "\\|")
        if row.get("capped_at"):
            first = f"timed out at {row['capped_at']}; " + first
        for n in row.get("notes", []):
            if "skipTests" in n:
                first = (first + "; " if first else "") + "pom skips tests under Maven"
        lines.append(
            f"| {i} | [{repo['full']}](https://github.com/{repo['full']}) | {row['stars']} | {row.get('modules', '')} | {row['java']} "
            f"| {imp.get('errors', '—')}/{imp.get('warnings', '—')} | {fmt_status(s.get('jk_lock'))} | {build_cell(row)} "
            f"| {tests_cell(s.get('jk_test'), row.get('jk_tests'), jk_t)} | {tests_cell(s.get('mvn_test'), mv_t)}{'*' if row.get('mvn_reused_from') else ''} "
            f"| {fmt_step(s.get('mvn_cold'), 'mvn_cold')} | {fmt_step(s.get('mvn_warm_clean'), 'mvn_warm_clean')} | {fmt_step(s.get('mvn_noop'), 'mvn_noop')} | {fmt_step(s.get('mvn_touch'), 'mvn_touch')} | {fmt_step(s.get('mvn_test'), 'mvn_test')} "
            f"| {fmt_step(s.get('jk_build_cold'), 'jk_build_cold')} | {fmt_step(s.get('jk_build_noop'), 'jk_build_noop')} | {fmt_step(s.get('jk_build_touch'), 'jk_build_touch')} | {fmt_step(s.get('jk_test'), 'jk_test')} "
            f"| {first} |")
    return lines


def side_by_side(repos: list[dict], runs: dict[str, dict[str, dict]]) -> list[str]:
    """One row per repo, the jk-side outcome of every run next to each other; Maven as the reference."""
    labels = list(runs)
    head = "| # | repo | mvn build / tests | " + " | ".join(f"{l}: import E/W · lock · build · tests jk · jk cold/no-op/touch/test" for l in labels) + " |"
    sep = "|--:|------|-------------------|" + "|".join("----" for _ in labels) + "|"
    lines = [head, sep]
    for i, repo in enumerate(repos, 1):
        cells = []
        ref = None
        for l in labels:
            row = runs[l].get(repo["name"])
            if row and row.get("steps", {}).get("mvn_cold"):
                ref = row
            if not row:
                cells.append("not run")
                continue
            s = row["steps"]
            imp = row.get("import", {})
            walls = "/".join(fmt_step(s.get(k), k) for k in ("jk_build_cold", "jk_build_noop", "jk_build_touch", "jk_test"))
            cells.append(f"{imp.get('errors', '—')}/{imp.get('warnings', '—')} · {fmt_status(s.get('jk_lock'))} · {build_cell(row)} · "
                         f"{tests_cell(s.get('jk_test'), row.get('jk_tests'), row.get('jk_tests_line'))} · {walls}")
        if ref:
            rs = ref["steps"]
            mvn = f"{fmt_step(rs.get('mvn_warm_clean'), 'mvn_warm_clean')} / {tests_cell(rs.get('mvn_test'), ref.get('mvn_tests'))}"
        else:
            mvn = "—"
        lines.append(f"| {i} | {repo['full']} | {mvn} | " + " | ".join(cells) + " |")
    return lines


def render(repos: list[dict], rows: dict[str, dict] | None = None) -> None:
    h = host_info()
    cfg = tomllib.load(open(HERE / "repos.toml", "rb"))
    labels = run_labels()
    runs = {l: load_rows(l) for l in labels}
    latest_label = labels[-1] if labels else "run1"
    lines = ["# Maven top-20 corpus — results", "",
             f"Generated {dt.datetime.now():%Y-%m-%d %H:%M} by `run.py`. Host: {h['cpu']} ({h['cores']} threads, {h['ram_gb']} GB RAM), {h['os']}.", "",
             "Maven ran through the launcher `jk mvn` provisions (or the repo's `mvnw`) with `MAVEN_OPTS=-Xmx3g`, "
             f"`JAVA_HOME` = the Temurin matching the declared level (or `maven_jdk`), and a corpus-private local repo (`{M2}`); jk ran with defaults. "
             "Wall = seconds. `pass/total` from surefire XML (Maven) and the `Tests:` line of `target/jk-results.md` (jk). "
             "Per-step logs and each import report live under `results/<repo>/` (latest run).", ""]
    per_repo = [f"{r['name']}: `mvn {' '.join(r['mvn_args'])}` / `jk import {' '.join(r.get('import_args', []))}`"
                for r in cfg["repo"] if r.get("mvn_args") or r.get("import_args")]
    if per_repo:
        lines += ["Per-repo argument lists from `repos.toml`, so both sides measure the same reactor: " + "; ".join(per_repo) + ".", ""]
    for l in labels:
        ids = sorted({(r.get("jk_version", ""), r.get("jk_commit", ""), r.get("jk_binary", ""), r.get("jk_engine_jar", "")) for r in runs[l].values() if r.get("jk_version")})
        lines.append(f"- **{l}**: " + ("; ".join(f"`{v}` commit `{c}` — {b}; {e}" for v, c, b, e in ids) or "jk identity not recorded (rows predate the identity fields)"))
    lines.append("")
    if len(labels) > 1:
        lines += ["## Side by side (the ratchet delta)", "",
                  "Maven column = warm-clean wall / tests pass·total as the reference; each run column = jk import E/W · lock · build · tests jk · jk cold/no-op/touch/test walls.", ""]
        lines += side_by_side(repos, runs)
        lines += ["", "| count | " + " | ".join(labels) + " |", "|-------|" + "|".join("--:" for _ in labels) + "|"]
        cs = {l: counts(repos, runs[l]) for l in labels}
        for key, label in [("measured", "repos measured"), ("import_clean", "import with zero Tier-3 errors"), ("lock", "`jk lock` ok"),
                           ("build", "`jk build --skip-tests` ok (compiled something)"), ("tests_ran", "`jk test` ran and passed"), ("tests_equal", "jk test total == Maven total")]:
            lines.append(f"| {label} | " + " | ".join(str(cs[l][key]) for l in labels) + " |")
        lines.append("")
    for l in labels:
        c = counts(repos, runs[l])
        lines += [f"## {l}", ""] + run_table(repos, runs[l]) + [""]
        lines += [f"Measured {c['measured']} of {len(repos)} selected repos" + (" (**partial run**)." if c["measured"] < len(repos) else "."), ""]
        reused = sorted(n for n, r in runs[l].items() if r.get("mvn_reused_from"))
        if reused:
            lines += ["\\* Maven numbers reused from an earlier row (jk-only re-measurement): " + ", ".join(reused), ""]
    lines += ["`built nothing` / `ok (n/m modules)` = `jk build` exited 0 but the imported workspace covers none / only n of the m poms; a build of nothing does not count in the ratchet.", "",
              "`no tests ran` = the step exited 0 but no test result was produced (e.g. an aggregator root imported with no sources, or a pom that sets `maven.test.skip`); it never counts as a pass.", "",
              "`capped` = killed by the 45-minute repo cap; `timeout` = the step's own 20-minute test timeout.", ""]
    lines += lock_diff_section(repos)
    sk = cfg.get("skipped", [])
    lines += ["## Skipped (in the same star range, root pom.xml present)", "",
              "| repo | stars | reason | detail |", "|------|------:|--------|--------|"]
    for r in sk:
        lines.append(f"| {r['full']} | {r['stars']} | {r['reason']} | {r['detail']} |")
    c = counts(repos, runs.get(latest_label, {}))
    lines += ["", "## Ratchet", "", f"Current bar ({latest_label}):", "",
              f"- repos importing with zero Tier-3 errors: **{c['import_clean']}** / {c['measured']}",
              f"- repos whose `jk lock` succeeds: **{c['lock']}** / {c['measured']}",
              f"- repos whose `jk build --skip-tests` compiles something: **{c['build']}** / {c['measured']}",
              f"- repos whose `jk test` runs and passes: **{c['tests_ran']}** / {c['measured']}",
              f"- repos whose jk test total equals Maven's: **{c['tests_equal']}** / {c['measured']}", ""]
    if len(labels) > 1:
        prev = counts(repos, runs[labels[-2]])
        lines += [f"Delta vs {labels[-2]}: " + ", ".join(f"{k} {prev[k]}→{c[k]}" for k in ("import_clean", "lock", "build", "tests_ran", "tests_equal")), ""]
    lines += ["Rule: a run that lowers any of these counts is a regression; a run that raises one moves the bar.", ""]
    (HERE / "RESULTS.md").write_text("\n".join(lines))
    render_tier3(repos, runs.get(latest_label, {}), latest_label)


def lock_diff_section(repos: list[dict]) -> list[str]:
    """The latest lockdiff.py report's summary table, when one exists (results/lock-diff/<date>.jsonl)."""
    files = sorted((RESULTS / "lock-diff").glob("*.jsonl"))
    if not files:
        return []
    rows: dict[str, dict] = {}
    for line in files[-1].read_text().splitlines():
        if line.strip():
            r = json.loads(line)
            rows[r["repo"]] = r
    date = files[-1].stem
    lines = ["## Lock vs Maven resolution", "",
             f"From `lockdiff.py` on {date} ([lock-diff/{date}.md](lock-diff/{date}.md) has the per-repo examples): for every repo whose "
             "`jk lock` is green, each module Maven and jk both build, coordinate by coordinate. `pairs` = (module, coordinate) pairs whose "
             "version differs; rules: bom = a `[platform-dependencies]` BOM's version where Maven's differs, managed = an inline "
             "`<dependencyManagement>` version Maven applied to a transitive and jk did not, pin = another member's direct pin, depth = "
             "nearest-by-depth (Maven) against highest-declared (jk), unknown = unexplained. `relocked` = the lock was rewritten by the jk under test "
             "before the diff, `reimported` = its manifests were imported from the POMs by that jk as well; `partial` = a reactor module failed Maven's tree goal and the other modules' trees stand.", "",
             "| repo | maven | modules compared | modules that differ | pairs / coords | by rule (pairs) |",
             "|------|-------|-----------------:|--------------------:|---------------:|-----------------|"]
    tot = {"repos": 0, "compared": 0, "differ": 0, "pairs": 0}
    for repo in repos:
        r = rows.get(repo["name"])
        if not r:
            continue
        mv = r.get("maven", {})
        relocked = (", reimported + relocked" if r.get("reimport", {}).get("status") == "ok"
                    else ", relocked" if r.get("relock", {}).get("status") == "ok" else "")
        if mv.get("status") == "ok":
            mcell = f"ok ({mv.get('mode')}{relocked})"
        elif r.get("modules_maven"):
            mcell = f"partial ({mv.get('mode')}{relocked}; {r['modules_maven']} module trees)"
        else:
            mcell = f"maven unavailable: {mv.get('reason', mv.get('status', ''))}".replace("|", "\\|")[:120]
        rules = ", ".join(f"{k} {v}" for k, v in r.get("by_rule", {}).items() if v) or "—"
        lines.append(f"| {r['full']} | {mcell} | {r['modules_compared']} | {r['modules_differ']} | {r['pairs_differ']} / {r['coords_differ']} | {rules} |")
        if r["modules_compared"]:
            tot["repos"] += 1
            tot["compared"] += r["modules_compared"]
            tot["differ"] += r["modules_differ"]
            tot["pairs"] += r["pairs_differ"]
    lines += ["", f"{tot['repos']} repos answered by Maven: {tot['differ']} of {tot['compared']} modules differ on at least one version, {tot['pairs']} pairs.", ""]
    return lines


def normalize_reason(text: str) -> str:
    if text.startswith("environment references are not allowed here"):
        return "environment references are not allowed here: version (${revision}) … — CI-friendly ${revision}/${changelist} versions copied verbatim from the pom"
    t = re.sub(r"^-?\s*(`[^`]*`\s*)+:\s*", "", text)              # drop "- `module` `step`: " prefix
    t = re.sub(r"^\[[^\]]+\]\s*", "", t)                         # drop [module] / [step — module] prefix
    t = re.sub(r"jdk = \d+ is not supported", "jdk = <8|11> is not supported", t)
    t = re.sub(r"could not be resolved \(.*?\); nothing was inherited", "could not be resolved (…); nothing was inherited", t)
    t = re.sub(r"could not be built \(.*?\); nothing was inherited", "could not be built (…); nothing was inherited", t)
    t = re.sub(r"`<parent>` G:A(:\S+)? could not", "`<parent>` G:A could not", t)
    t = re.sub(r"test failure: (\S+)#(\S+) — (\S+).*", r"test failure: \1#\2 — \3", t) if t.startswith("test failure:") and False else t
    t = re.sub(r"\[[^\]]*,\s*\+∞\)", "[N,+∞)", t)                  # open version ranges
    t = re.sub(r"`?[\w.-]+:[\w.-]+(:[\w.-]+)?`?", "G:A", t)        # coordinates
    t = re.sub(r"/home/\S+", "<path>", t)
    t = re.sub(r"\d+(\.\d+)+", "N", t)                            # versions / numbers
    return t.strip()


def render_tier3(repos: list[dict], rows: dict[str, dict], label: str = "run1") -> None:
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
    out = [f"# Tier-3 reasons (import ERRORs and jk failures), grouped — {label}", "",
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
    ap.add_argument("--both", action="store_true",
                    help="re-measure the Maven side too (default: --jk-only, reuse the last Maven row when one exists)")
    ap.add_argument("--jk-only", action="store_true", help="(default) re-measure jk only; Maven numbers come from the last row")
    ap.add_argument("--skip-mvn", action="store_true", help="never run Maven, even when no earlier row exists")
    ap.add_argument("--render", action="store_true", help="only rewrite RESULTS.md from rows on disk")
    ap.add_argument("--fresh-m2", action="store_true", help="wipe the corpus-private Maven local repo first")
    ap.add_argument("--run", default="run1", help="run label stored in every row (default run1); rows of one label form one column set")
    args = ap.parse_args()

    cfg = tomllib.load(open(HERE / "repos.toml", "rb"))
    repos = cfg["repo"]
    RESULTS.mkdir(exist_ok=True)
    if args.render:
        render(repos)
        return 0
    if args.fresh_m2 and M2.exists():
        shutil.rmtree(M2)
    todo = [r for r in repos if not args.only or r["name"] in args.only]
    if args.order == "small-first":
        todo.sort(key=lambda r: (len(poms(SCRATCH / r["name"])) if (SCRATCH / r["name"]).is_dir() else 10 ** 6, -r["stars"]))
    out = RESULTS / (f"{dt.date.today():%Y-%m-%d}.jsonl" if args.run == "run1" else f"{dt.date.today():%Y-%m-%d}-{args.run}.jsonl")
    for repo in todo:
        row = measure(repo, args)
        row["run"] = args.run
        with open(out, "a") as f:
            f.write(json.dumps(row, sort_keys=True) + "\n")
        render(repos)
        print(f"  {repo['name']}: total {row['total_wall'] if 'total_wall' in row else '?'}s -> {out}", flush=True)
    return 0


if __name__ == "__main__":
    sys.exit(main())

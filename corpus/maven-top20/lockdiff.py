#!/usr/bin/env python3
"""Diff each corpus repo's jk lock against Maven's resolved versions, module by module.

For every repo whose latest `jk lock` is green, the clone (with the jk.toml / jk-lock.toml the
harness wrote) is copied to $LOCKDIFF_SCRATCH/<name> (default /home/bsant/src/scratch/lock-diff),
`dependency:go-offline` fills the harness's own Maven repo ($LOCKDIFF_SCRATCH/.m2, a hardlink copy
of the corpus .m2 made on first use, so the corpus repo stays as cold as the corpus runner left it)
with every POM the reactor's graphs name, Maven's verbose dependency tree is written per module
(`dependency:tree -Dverbose`, offline against that repo, 15 minutes per repo), and jk's per-module
closure is read with `jk tree <module> -t -f -s all` (its `[platform]` and `[managed]` sections are
pins and BOMs, not closure members, and are left out).  A POM the local repo lacks makes Maven render
the artifact as a leaf (`The POM for X is missing`), and its whole subtree then counts as "only jk":
every POM a tree run reports missing is fetched from Central or a repository the POMs declare
(with its parents and imports), and the run is repeated until none is missing; an online tree run is
the last resort when offline still fails.  The report carries the POMs filled and the ones still
missing so an "only jk" column is read against them.  With `--relock` the scratch copy's lock is
rewritten by the jk under test before the diff, and with `--reimport` its manifests are imported
from the POMs by that jk first (`--jk-home <dir>` names a private install; the default is the `jk`
on PATH), so the diff measures the importer and resolver as they are rather than what the corpus
run left.  For each module both builds know, every coordinate whose resolved version differs
is classified by the rule that produced jk's answer:

  managed    Maven's version is one an inline <dependencyManagement> entry in the repo's own POMs
             sets (`version managed from X` in the tree) and jk's row is not it: the entry did not
             reach the module's [managed-dependencies] (a property the import could not read, a
             profile, a parent outside the repo) or a BOM or pin of jk's own outranked it
  bom        Maven managed the coordinate too, from a BOM; jk's answer is another BOM's (`pinned-by`)
             or unmanaged, so the two BOM sets or their order differ
  bom-reach  jk's version is a [platform-dependencies] BOM's (`pinned-by`) where Maven's module
             manages nothing: the BOM reaches the module in jk (workspace table, plugin BOM) but
             Maven's module never imports it
  pin        jk's version is a version some *other* workspace member declares directly; under
             `pins = "nearest"` a member's pin is the version for the whole lock
  depth      neither side managed it; Maven's verbose tree omitted jk's version "for conflict" with a
             nearer declaration (nearest-by-depth), jk kept the highest declared version
  cascade    the coordinate's parent in Maven's tree is itself a differing coordinate, so the two
             builds read different POMs for it
  unknown    a version difference none of the above explains

jk's version is the lock row the module reads (member row, else the plain row of the matching scope);
a `jk tree` line that disagrees with that row is counted apart as `tree_vs_lock`.  Same-version scope
disagreements and coordinates only one side resolves are counted separately.  A coordinate jk reaches
only through a module's inactive-feature rows — `optional = true` rows a `[features.<name>]` table names
and no default feature activates, which `jk tree` renders like any declared root while the build and
Maven's profile-off tree never see them — is counted as `only jk via inactive features`, apart from
`only jk`; it is found by reading `jk tree` a second time over a copy of the module's manifest with those
rows and the `[features]` tables removed.
Writes lock-diff/<date>.md (one section per repo, a summary table) and one JSON line per repo to
results/lock-diff/<date>.jsonl.  Never touches the corpus clones themselves.
"""
from __future__ import annotations

import argparse
import datetime as dt
import json
import os
import re
import shutil
import subprocess
import sys
import time
import tomllib
from collections import Counter, defaultdict
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import run as harness  # noqa: E402  (the corpus harness: scratch paths, launchers, rows)

HERE = harness.HERE
SCRATCH = Path(os.environ.get("LOCKDIFF_SCRATCH", "/home/bsant/src/scratch/lock-diff"))
M2 = Path(os.environ.get("LOCKDIFF_M2", str(SCRATCH / ".m2")))
OUT_MD = HERE / "lock-diff"
OUT_JSONL = harness.RESULTS / "lock-diff"
REPO_CAP = int(os.environ.get("LOCKDIFF_REPO_CAP", 15 * 60))
TREE_FILE = "target/jk-lockdiff-tree.txt"
DEP_PLUGIN = "org.apache.maven.plugins:maven-dependency-plugin:3.8.1:tree"
GO_OFFLINE = "org.apache.maven.plugins:maven-dependency-plugin:3.8.1:go-offline"
MISSING_POM = re.compile(r"The POM for (\S+) is (?:missing, no dependency information available|invalid, transitive dependencies)")
ABSENT_POM = re.compile(r"(?:Could not find artifact|could not be resolved: |artifact ) ?(\S+?):(\S+?):pom:(\S+?)(?: \(absent\)|[ ,)\]]|$)")
UNAVAILABLE_POM = re.compile(r"(\S+?):(\S+?):pom:(\S+?) \(present, but unavailable\)")
CENTRAL = "https://repo.maven.apache.org/maven2"
FILL_ROUNDS = 8
GO_OFFLINE_CAP = int(os.environ.get("LOCKDIFF_GO_OFFLINE_CAP", 180))   # seconds; a throttled Central stalls rather than refuses
NET_TIMEOUTS = ["-Daether.connector.connectTimeout=10000", "-Daether.connector.requestTimeout=60000"]
JK_ENV: dict[str, str] = {}      # JK_HOME for a private install, empty for the jk on PATH
PACKAGINGS = {"jar", "pom", "war", "ear", "aar", "test-jar", "maven-plugin", "bundle", "ejb", "zip", "so", "dll", "dylib"}
MAVEN_MAIN_SCOPES = {"compile", "runtime", "provided", "system"}
JK_MAIN_SCOPES = {"main", "runtime", "provided", "processor"}
RULES = ("managed", "bom", "bom-reach", "pin", "depth", "cascade", "unknown")
DEP_TABLES = {"dependencies", "test-dependencies", "runtime-dependencies", "provided-dependencies",
              "processor-dependencies", "export-dependencies"}   # closure roots; managed/platform are pins


# --------------------------------------------------------------------------- Maven version order

_QUALIFIERS = ["alpha", "beta", "milestone", "rc", "snapshot", "", "sp"]
_ALIASES = {"ga": "", "final": "", "release": "", "cr": "rc", "a": "alpha", "b": "beta", "m": "milestone"}


def _items(v: str) -> list:
    out: list = []
    for tok in re.findall(r"\d+|[A-Za-z]+", v.lower()):
        if tok.isdigit():
            out.append(int(tok))
        else:
            out.append(_ALIASES.get(tok, tok))
    while out and out[-1] in (0, ""):
        out.pop()
    return out


def _cmp_item(a, b) -> int:
    if isinstance(a, int) and isinstance(b, int):
        return (a > b) - (a < b)
    if isinstance(a, int):
        return 1            # a number is newer than any qualifier
    if isinstance(b, int):
        return -1
    ka = (str(_QUALIFIERS.index(a)) if a in _QUALIFIERS else f"{len(_QUALIFIERS)}-{a}")
    kb = (str(_QUALIFIERS.index(b)) if b in _QUALIFIERS else f"{len(_QUALIFIERS)}-{b}")
    return (ka > kb) - (ka < kb)


def compare_versions(a: str, b: str) -> int:
    """Maven ComparableVersion, near enough for a direction: >0 when a is newer than b."""
    ia, ib = _items(a), _items(b)
    for x, y in zip(ia, ib):
        c = _cmp_item(x, y)
        if c:
            return c
    if len(ia) == len(ib):
        return 0
    rest = ia[len(ib):] if len(ia) > len(ib) else ib[len(ia):]
    sign = 1 if len(ia) > len(ib) else -1
    nxt = rest[0]
    if isinstance(nxt, int):
        return sign
    return sign * _cmp_item(nxt, "")


# --------------------------------------------------------------------------- Maven side

def coord_key(group: str, artifact: str, classifier: str = "") -> str:
    """group:artifact — classified variants (netty's native jars) share the main artifact's version."""
    return f"{group}:{artifact}"


def parse_maven_tree(text: str) -> dict:
    """One module's verbose tree → {root, resolved: {key: entry}, omitted: {key: [entries]}}."""
    lines = [l.rstrip() for l in text.splitlines() if l.strip()]
    if not lines:
        return {}
    root_parts = lines[0].split(":")
    module = {"root": lines[0], "root_ga": f"{root_parts[0]}:{root_parts[1]}", "resolved": {}, "omitted": defaultdict(list)}
    stack: list[str] = []          # resolved keys by depth, for each entry's parent chain
    for line in lines[1:]:
        m = re.match(r"^((?:[|+\\ -]{3})*)(.*)$", line)
        if not m:
            continue
        depth = len(m.group(1)) // 3
        content = m.group(2).strip()
        omitted = content.startswith("(")
        if omitted:
            content = content[1:]
            if content.endswith(")"):
                content = content[:-1]
        gav, _, note = content.partition(" ")
        note = note.strip()
        if note.startswith("- "):
            note = note[2:]
        note = note.strip("() ")
        parts = gav.split(":")
        if len(parts) < 4:
            continue
        group, artifact, typ = parts[0], parts[1], parts[2]
        if len(parts) == 6:
            classifier, version, scope = parts[3], parts[4], parts[5]
        elif len(parts) == 5:
            classifier, version, scope = "", parts[3], parts[4]
        else:
            classifier, version, scope = "", parts[3], ""
        managed_from = None
        mm = re.search(r"version managed from (\S+?)(?:;|$)", note)
        if mm:
            managed_from = mm.group(1)
        conflict_with = None
        mc = re.search(r"omitted for conflict with (\S+)", note)
        if mc:
            conflict_with = mc.group(1)
        del stack[depth - 1:]
        entry = {"group": group, "artifact": artifact, "type": typ, "classifier": classifier, "version": version,
                 "scope": scope, "depth": depth, "managed_from": managed_from, "optional": "optional" in note,
                 "conflict_with": conflict_with, "duplicate": "omitted for duplicate" in note,
                 "cycle": "omitted for cycle" in note, "parents": list(stack)}
        key = coord_key(group, artifact, classifier)
        if omitted:
            module["omitted"][key].append(entry)
        else:
            if key not in module["resolved"] or depth < module["resolved"][key]["depth"]:
                module["resolved"][key] = entry
            stack.append(key)
    return module


def local_repo() -> Path:
    """The harness's Maven repo: a hardlink copy of the corpus .m2, made once, that go-offline may fill."""
    if not M2.exists():
        M2.parent.mkdir(parents=True, exist_ok=True)
        subprocess.run(["cp", "-al", str(harness.M2), str(M2)], check=True)
    return M2


def missing_poms(log: Path, start: int) -> set[tuple[str, str, str]]:
    """The (group, artifact, version) of every POM Maven could not read, from byte offset `start` of the log.

    `The POM for G:A:ext[:classifier]:V is missing` names an artifact Maven then rendered as a leaf;
    `... is invalid` one whose parent or import it could not read; `Could not find artifact G:A:pom:V`,
    `G:A:pom:V (absent)` and `G:A:pom:V (present, but unavailable)` name the POM itself (a parent,
    or a BOM a dependency's POM imports; the last one is on disk under another repository's id).
    """
    with open(log, "rb") as f:
        f.seek(start)
        text = f.read().decode("utf-8", errors="replace")
    out: set[tuple[str, str, str]] = set()
    for gav in MISSING_POM.findall(text):
        parts = gav.split(":")
        if len(parts) >= 4:
            out.add((parts[0], parts[1], parts[-1]))
    for g, a, v in ABSENT_POM.findall(text):
        out.add((g, a, v))
    for g, a, v in UNAVAILABLE_POM.findall(text):
        out.add((g, a, v))
    return out


def declared_repositories(root: Path) -> list[str]:
    """Central, then every <repository><url> the repo's own POMs declare (http(s) only, in file order)."""
    urls = [CENTRAL]
    for pom in harness.poms(root):
        el = _pom(pom)
        if el is None:
            continue
        for rep in el.findall("repositories/repository/url") + el.findall("profiles/profile/repositories/repository/url"):
            u = (rep.text or "").strip().rstrip("/")
            if u.startswith("http") and "${" not in u and u not in urls:
                urls.append(u)
    return urls


def pom_path(repo: Path, g: str, a: str, v: str) -> Path:
    return repo / g.replace(".", "/") / a / v / f"{a}-{v}.pom"


def fetch_pom(repo: Path, g: str, a: str, v: str, urls: list[str], log: Path) -> bool:
    """Download one POM into the local repo from the first repository that serves it (curl; the JVM's
    TLS stack is what Central refuses when it throttles a host, so Maven itself cannot fill these)."""
    dest = pom_path(repo, g, a, v)
    dest.parent.mkdir(parents=True, exist_ok=True)
    # A POM another repository id fetched is "present, but unavailable" to a build that only knows
    # Central, and a cached transfer failure is not retried: dropping the resolver's bookkeeping
    # beside the file makes it a locally installed POM, which every build reads.
    stale = [p for p in dest.parent.iterdir() if p.name == "_remote.repositories" or p.name.endswith(".lastUpdated")]
    for p in stale:
        p.unlink()
    if dest.is_file():
        if stale:
            with open(log, "a", encoding="utf-8") as f:
                f.write(f"# made {g}:{a}:{v} available (dropped {', '.join(p.name for p in stale)})\n")
        return bool(stale)
    rel = f"{g.replace('.', '/')}/{a}/{v}/{a}-{v}.pom"
    for base in urls:
        r = subprocess.run(["curl", "-fsSL", "--max-time", "60", "-o", str(dest), f"{base}/{rel}"], capture_output=True)
        if r.returncode == 0 and dest.is_file() and dest.stat().st_size > 0:
            with open(log, "a", encoding="utf-8") as f:
                f.write(f"# filled {g}:{a}:{v} from {base}\n")
            return True
        dest.unlink(missing_ok=True)
    with open(log, "a", encoding="utf-8") as f:
        f.write(f"# no repository serves {g}:{a}:{v}\n")
    return False


def ensure_pom(repo: Path, g: str, a: str, v: str, urls: list[str], log: Path, seen: set) -> int:
    """Fetch a POM when the local repo lacks it, then its parent and its imported BOMs the same way.

    `${...}` in a parent or import coordinate is read from the POM's own properties and project.version;
    what stays unresolved is skipped.  Returns how many POMs were fetched.
    """
    key = (g, a, v)
    if key in seen or "${" in g + a + v:
        return 0
    seen.add(key)
    fetched = 1 if fetch_pom(repo, g, a, v, urls, log) else 0
    path = pom_path(repo, g, a, v)
    el = _pom(path) if path.is_file() else None
    if el is None:
        return fetched
    props = {"project.version": v, "project.groupId": g, "version": v, "groupId": g}
    for prop in el.findall("properties/*"):
        if prop.text:
            props[prop.tag] = prop.text.strip()

    def interp(x: str) -> str:
        return re.sub(r"\$\{([^}]+)\}", lambda m: props.get(m.group(1), m.group(0)), x)

    par = el.find("parent")
    if par is not None:
        pg, pa, pv = (interp((par.findtext(t) or "").strip()) for t in ("groupId", "artifactId", "version"))
        if pg and pa and pv:
            fetched += ensure_pom(repo, pg, pa, pv, urls, log, seen)
    for dep in el.findall("dependencyManagement/dependencies/dependency"):
        if (dep.findtext("scope") or "").strip() == "import":
            dg, da, dv = (interp((dep.findtext(t) or "").strip()) for t in ("groupId", "artifactId", "version"))
            if dg and da and dv:
                fetched += ensure_pom(repo, dg, da, dv, urls, log, seen)
    return fetched


def maven_trees(root: Path, repo: dict, log: Path, deadline: float) -> dict:
    """go-offline, then the verbose tree goal across the reactor, offline against the harness's repo;
    every POM the run reports missing is fetched into that repo and the run repeated until none is,
    then online as the last resort for a POM no repository served.

    Returns {status, mode, wall, reason, go_offline, missing_poms, poms_filled, rounds}: `go_offline`
    is that goal's status (`-U` so a cached "absent" marker is re-asked; a module failing there does
    not stop the tree; capped at $LOCKDIFF_GO_OFFLINE_CAP seconds, since a Central that throttles the
    host stalls the connection rather than refusing it), `poms_filled` how many POMs the fill fetched, `missing_poms` how many POMs the
    run that produced the trees still could not read.
    """
    launcher = harness.maven_launcher(root)
    env = harness.maven_env(repo.get("maven_jdk", repo["java"]))
    local = local_repo()
    common = launcher + ["-B", "-fae", f"-Dmaven.repo.local={local}"] + NET_TIMEOUTS
    base = common + [DEP_PLUGIN, "-Dverbose=true", "-DoutputType=text", f"-DoutputFile={TREE_FILE}"]
    t0 = time.time()
    warm = harness.run(common + ["-U", GO_OFFLINE], root, log, max(min(deadline - time.time(), GO_OFFLINE_CAP), 1), env)
    urls = declared_repositories(root)
    filled, rounds, seen = 0, 0, set()
    while True:
        rounds += 1
        tree_start = log.stat().st_size if log.exists() else 0
        r = harness.run(base + ["-o"], root, log, max(deadline - time.time(), 1), env)
        mode = "offline"
        missing = missing_poms(log, tree_start)
        if not missing or rounds >= FILL_ROUNDS or deadline - time.time() < 60:
            break
        got = sum(ensure_pom(local, g, a, v, urls, log, seen) for g, a, v in sorted(missing))
        filled += got
        print(f"  fill round {rounds}: {len(missing)} POMs missing, {got} fetched", flush=True)
        if not got:
            break
    if missing and deadline - time.time() > 60:          # POMs no repository served: let Maven ask itself
        tree_start = log.stat().st_size
        r = harness.run(base, root, log, deadline - time.time(), env)
        mode = "online"
        missing = missing_poms(log, tree_start)
    out = {"status": r["status"], "mode": mode, "wall": round(time.time() - t0, 1), "reason": "",
           "go_offline": {"status": warm["status"], "wall": round(warm["wall"], 1)},
           "missing_poms": len(missing), "poms_filled": filled, "rounds": rounds}
    if r["status"] != "ok":
        out["reason"] = harness.first_mvn_error(log) or f"mvn dependency:tree {r['status']}"
    return out


def maven_modules(root: Path) -> dict[str, dict]:
    """Every module whose tree was written, keyed by its path relative to the repo root ('.' = root)."""
    out = {}
    for f in sorted(root.rglob(TREE_FILE.split("/")[-1])):
        if f.parent.name != "target":
            continue
        rel = f.parent.parent.relative_to(root).as_posix()
        parsed = parse_maven_tree(f.read_text(encoding="utf-8", errors="replace"))
        if parsed:
            out[rel or "."] = parsed
    return out


# --------------------------------------------------------------------------- jk side

def jk_modules(root: Path) -> list[str]:
    d = tomllib.load(open(root / "jk.toml", "rb"))
    mods = d.get("workspace", {}).get("modules")
    if mods is None:
        return ["."]
    return list(mods)


def jk_tree(root: Path, module: str, log: Path) -> dict | None:
    """`jk tree <module> -t -f -s all` → {scope: {key: version}} plus the platform BOMs it lists."""
    cmd = harness.JK + ["--no-timeline", "tree"] + ([] if module == "." else [module]) + ["-t", "-f", "-s", "all"]
    p = subprocess.run(cmd, cwd=root, capture_output=True, text=True, timeout=600, env={**os.environ, **JK_ENV})
    with open(log, "a", encoding="utf-8") as f:
        f.write(f"\n$ (cd {root} && {' '.join(cmd)})   # exit {p.returncode}\n{p.stdout}{p.stderr}")
    if p.returncode != 0:
        return None
    scopes: dict[str, dict[str, str]] = defaultdict(dict)
    platforms: list[str] = []
    scope = None
    for line in p.stdout.splitlines():
        s = line.rstrip()
        mh = re.match(r"^\s*[|` ]*[+`]-\[(\w[\w-]*)\]\s*$", s)
        if mh:
            scope = mh.group(1)
            continue
        me = re.match(r"^\s*[|` ]*[+`]- (\S+)(\s+\(platform\))?\s*$", s)
        if not me or scope is None:
            continue
        gav = me.group(1)
        parts = gav.split(":")
        if len(parts) < 3:
            continue
        group, artifact, version = parts[0], parts[1], parts[-1]
        middle = parts[2:-1]
        if middle and middle[0] in PACKAGINGS:
            middle = middle[1:]
        classifier = ":".join(middle)
        if scope == "platform" or me.group(2):
            platforms.append(f"{group}:{artifact}:{version}")
            continue
        if scope == "managed":
            continue                # a [managed-dependencies] pin governs versions; it is not a closure member
        key = coord_key(group, artifact, classifier)
        prev = scopes[scope].get(key)
        if prev is None or compare_versions(version, prev) > 0:
            scopes[scope][key] = version
    return {"scopes": dict(scopes), "platforms": platforms}


def inactive_feature_rows(manifest: Path) -> dict[str, list[str]]:
    """{handle: [feature names]} for the module's optional rows that only inactive features name.

    A row is inactive when some `[features.<name>]` lists its handle under `deps` and no feature the
    `default` list activates (transitively, through `features`) does.  These rows are off in the build
    and in Maven's profile-off tree, yet `jk tree` renders them as roots.  The import names a test-jar
    dependency `<artifact>-tests` in the feature table while a workspace sibling's test-jar row is keyed by
    the module name with `kind = "tests"`, so `<key>-tests` names a `kind = "tests"` row `<key>` too.
    """
    try:
        d = tomllib.load(open(manifest, "rb"))
    except Exception:
        return {}
    feats = d.get("features", {})
    if not isinstance(feats, dict):
        return {}
    defs = {k: v for k, v in feats.items() if isinstance(v, dict)}
    active: set[str] = set()
    queue = [n for n in feats.get("default", []) if isinstance(n, str)]
    while queue:
        n = queue.pop()
        if n in active or n not in defs:
            continue
        active.add(n)
        queue += [x for x in defs[n].get("features", []) if isinstance(x, str)]
    active_deps = {h for n in active for h in defs[n].get("deps", [])}
    named: dict[str, list[str]] = {}
    for n, f in defs.items():
        for h in f.get("deps", []):
            named.setdefault(h, []).append(n)
    out: dict[str, list[str]] = {}
    for table, rows in d.items():
        if table not in DEP_TABLES or not isinstance(rows, dict):
            continue
        for h, v in rows.items():
            if not isinstance(v, dict) or v.get("optional") is not True:
                continue
            handles = [h] + ([h + "-tests"] if v.get("kind") == "tests" else [])
            naming = sorted({f for x in handles if x in named for f in named[x]})
            if naming and not any(x in active_deps for x in handles):
                out[h] = naming
    return out


def without_inactive_features(text: str, handles: set[str]) -> str:
    """The manifest text minus the rows of `handles` in the dependency tables and every `[features…]` table."""
    out: list[str] = []
    table = None
    skip_table = False
    for line in text.splitlines(keepends=True):
        m = re.match(r'^\s*\[\[?([^\]]+)\]\]?\s*(#.*)?$', line)
        if m:
            table = m.group(1).strip()
            head, _, rest = table.partition(".")
            skip_table = (table == "features" or table.startswith("features.")
                          or (head in DEP_TABLES and rest.strip('"') in handles))
            if not skip_table:
                out.append(line)
            continue
        if skip_table:
            continue
        if table in DEP_TABLES:
            km = re.match(r'^\s*(?:"([^"]+)"|([A-Za-z0-9_.-]+))\s*=', line)
            if km:
                key = km.group(1) if km.group(1) is not None else km.group(2)
                if key in handles or (km.group(1) is None and key.split(".")[0] in handles):
                    continue
        out.append(line)
    return "".join(out)


def jk_tree_without(root: Path, module: str, log: Path, handles: set[str]) -> dict | None:
    """`jk_tree` over the module with its inactive-feature rows removed; the manifest is restored afterwards.

    None when the trimmed manifest does not parse (the split is then skipped for the module) or the tree fails.
    """
    manifest = (root if module == "." else root / module) / "jk.toml"
    original = manifest.read_text(encoding="utf-8")
    trimmed = without_inactive_features(original, handles)
    try:
        tomllib.loads(trimmed)
    except Exception as e:
        with open(log, "a", encoding="utf-8") as f:
            f.write(f"\n# {module}: trimmed manifest does not parse ({e}); inactive-feature split skipped\n")
        return None
    try:
        manifest.write_text(trimmed, encoding="utf-8")
        return jk_tree(root, module, log)
    finally:
        manifest.write_text(original, encoding="utf-8")


def jk_version_for(tree: dict, key: str, maven_scope: str) -> tuple[str | None, set[str]]:
    """The version jk resolved for key on the side Maven's scope maps to, and every jk scope naming it."""
    scopes_with = {s for s, m in tree["scopes"].items() if key in m}
    if not scopes_with:
        return None, set()
    if maven_scope == "test":
        order = ["test", "main", "runtime", "provided", "processor"]
    else:
        order = ["main", "runtime", "provided", "processor", "test"]
    for s in order:
        if s in scopes_with:
            return tree["scopes"][s][key], scopes_with
    s = sorted(scopes_with)[0]
    return tree["scopes"][s][key], scopes_with


class Lock:
    """jk-lock.toml rows keyed by coordinate, member-partition aware."""

    def __init__(self, path: Path):
        d = tomllib.load(open(path, "rb"))
        self.generated_by = d.get("generated-by", "")
        self.rows: dict[str, list[dict]] = defaultdict(list)
        for row in d.get("artifact", []):
            g, a, _typ, cls = (row["name"].split(":") + ["", "", "", ""])[:4]
            self.rows[coord_key(g, a, cls)].append(row)
        self.modules = {m["path"]: f"{m['group']}:{m['name']}" for m in d.get("module", [])}

    def row(self, key: str, module: str, maven_scope: str = "compile") -> dict | None:
        """The row a module reads for key: its member row first, else the plain row whose scopes match the side."""
        rows = self.rows.get(key, [])
        for r in rows:
            if module in r.get("members", []):
                return r
        plain = [r for r in rows if not r.get("members")]
        want = {"test"} if maven_scope == "test" else JK_MAIN_SCOPES
        for r in plain:
            if set(r.get("scopes", [])) & want:
                return r
        return plain[0] if plain else (rows[0] if rows else None)


def declared_pins(root: Path, modules: list[str]) -> dict[str, dict[str, list[str]]]:
    """group:artifact → {version → [module paths that declare it directly]} across the workspace."""
    pins: dict[str, dict[str, list[str]]] = defaultdict(lambda: defaultdict(list))
    tables = ("dependencies", "test-dependencies", "runtime-dependencies", "provided-dependencies",
              "processor-dependencies", "compile-only-dependencies")
    for mod in ["."] + [m for m in modules if m != "."]:
        p = root / mod / "jk.toml"
        if not p.is_file():
            continue
        try:
            d = tomllib.load(open(p, "rb"))
        except Exception:
            continue
        for table in tables:
            for alias, spec in (d.get(table) or {}).items():
                if isinstance(spec, str):
                    parts = spec.split(":")
                    if len(parts) >= 3 and parts[2]:
                        pins[f"{parts[0]}:{parts[1]}"][parts[2]].append(mod)
                elif isinstance(spec, dict):
                    if spec.get("workspace"):
                        continue
                    g, v = spec.get("group"), spec.get("version")
                    a = spec.get("artifact") or spec.get("name") or alias
                    if g and v and not str(v).startswith(("^", "~", "[", "(", "latest")):
                        pins[f"{g}:{a}"][str(v)].append(mod)
    return pins


# --------------------------------------------------------------------------- the repo's own POMs

def _strip(tag: str) -> str:
    return tag.split("}", 1)[1] if "}" in tag else tag


def _pom(path: Path):
    import xml.etree.ElementTree as ET
    try:
        root = ET.parse(path).getroot()
    except ET.ParseError:
        return None
    for el in root.iter():
        el.tag = _strip(el.tag)
    return root


def pom_facts(root: Path) -> dict:
    """What the repo's own POMs say: inline-managed versions per group:artifact and optional edges.

    `${property}` is interpolated from the POM's own <properties> and its in-repo parent chain
    (`<relativePath>`, default ../pom.xml), plus project.version / revision; anything still
    unresolved is dropped.  Returns {"managed": {ga: {version: [pom]}}, "optional": {ga}}.
    """
    poms = {p: _pom(p) for p in harness.poms(root)}
    poms = {p: x for p, x in poms.items() if x is not None}

    def parent_of(path: Path, el) -> Path | None:
        par = el.find("parent")
        if par is None:
            return None
        rel = (par.findtext("relativePath") or "../pom.xml").strip()
        cand = (path.parent / rel).resolve()
        if cand.is_dir():
            cand = cand / "pom.xml"
        return cand if cand in poms else None

    def props(path: Path) -> dict[str, str]:
        chain, seen, cur = [], set(), path
        while cur is not None and cur not in seen:
            seen.add(cur)
            chain.append(cur)
            cur = parent_of(cur, poms[cur])
        out: dict[str, str] = {}
        for p in reversed(chain):                      # child properties win
            el = poms[p]
            for prop in el.findall("properties/*"):
                if prop.text:
                    out[prop.tag] = prop.text.strip()
            ver = el.findtext("version") or el.findtext("parent/version")
            if ver:
                out["project.version"] = ver.strip()
            grp = el.findtext("groupId") or el.findtext("parent/groupId")
            if grp:
                out["project.groupId"] = grp.strip()
        return out

    def interp(v: str, pr: dict[str, str], depth: int = 0) -> str:
        if depth > 5 or "${" not in v:
            return v
        out = re.sub(r"\$\{([^}]+)\}", lambda m: pr.get(m.group(1), m.group(0)), v)
        return interp(out, pr, depth + 1) if out != v else out

    managed: dict[str, dict[str, list[str]]] = defaultdict(lambda: defaultdict(list))
    optional: set[str] = set()
    for path, el in poms.items():
        pr = props(path)
        rel = path.parent.relative_to(root).as_posix() or "."
        for dep in el.findall("dependencyManagement/dependencies/dependency"):
            if (dep.findtext("scope") or "").strip() == "import":
                continue
            g, a, v = (interp((dep.findtext(t) or "").strip(), pr) for t in ("groupId", "artifactId", "version"))
            if g and a and v and "${" not in g + a + v:
                managed[f"{g}:{a}"][v].append(rel)
        for dep in el.findall("dependencies/dependency"):
            if (dep.findtext("optional") or "").strip() == "true":
                g, a = (interp((dep.findtext(t) or "").strip(), pr) for t in ("groupId", "artifactId"))
                if g and a and "${" not in g + a:
                    optional.add(f"{g}:{a}")
    return {"managed": {k: dict(v) for k, v in managed.items()}, "optional": optional}


# --------------------------------------------------------------------------- the diff

def classify(key: str, module: str, jk_v: str, mv: dict, omitted: list[dict], lock_row: dict | None,
             pins: dict, facts: dict) -> tuple[str, str]:
    """(rule, evidence) for one module's version difference; `cascade` is applied afterwards."""
    ga = key
    pinned_by = (lock_row or {}).get("pinned-by")
    jk_side = f"jk: BOM {pinned_by}" if pinned_by else "jk: no BOM manages it"
    if mv["managed_from"]:
        inline = facts["managed"].get(ga, {})
        if mv["version"] in inline:
            kept = " (jk kept the declared version)" if mv["managed_from"] == jk_v else ""
            return "managed", f"Maven: inline <dependencyManagement> in {inline[mv['version']][0]}/pom.xml sets {mv['version']} (depth {mv['depth']}); {jk_side}{kept}"
        return "bom", f"Maven: managed from {mv['managed_from']} by an imported BOM, no inline entry says {mv['version']}; {jk_side}"
    if pinned_by:
        return "bom-reach", f"{jk_side}; Maven: {module} manages nothing for it and took the declared version at depth {mv['depth']}"
    my_pins = pins.get(ga, {})
    if jk_v in my_pins and not (module in my_pins[jk_v] or "." in my_pins[jk_v]):
        return "pin", f"jk: pinned by member {', '.join(sorted(my_pins[jk_v])[:3])}; Maven: {module} resolved on its own (depth {mv['depth']})"
    omitted_versions = {o["version"] for o in omitted if o["conflict_with"]}
    if jk_v in omitted_versions:
        return "depth", f"Maven: omitted {jk_v} for conflict with the nearer {mv['version']} (depth {mv['depth']}); jk: highest declared"
    if compare_versions(jk_v, mv["version"]) > 0:
        return "depth", f"jk {jk_v} above Maven's {mv['version']} (depth {mv['depth']}); versions Maven omitted for conflict: " + (", ".join(sorted(omitted_versions)) or "none")
    return "unknown", f"jk {jk_v} below Maven's {mv['version']} (depth {mv['depth']}) with no pin, BOM or management on either side"


def diff_module(module: str, mtree: dict, jtree: dict, lock: Lock, pins: dict, facts: dict,
                jtree_active: dict | None = None, inactive_rows: dict[str, list[str]] | None = None) -> dict:
    """`jtree_active` is jk's closure with the module's inactive-feature rows removed (None = no such rows or
    unreadable): a coordinate in `jtree` Maven lacks and `jtree_active` lacks too is only jk's through those rows."""
    out = {"module": module, "root": mtree["root_ga"], "maven_coords": 0, "compared": 0, "differ": [], "scope_only": 0,
           "only_maven": Counter(), "only_jk": 0, "only_jk_optional": 0, "only_jk_inactive": 0, "tree_vs_lock": 0,
           "only_maven_names": [], "only_jk_names": [], "only_jk_inactive_names": [], "scope_only_names": [],
           "tree_vs_lock_names": [], "inactive_rows": inactive_rows or {},
           "test_side": 0, "test_side_names": []}
    active_keys = None
    if jtree_active is not None:
        active_keys = {k for m in jtree_active["scopes"].values() for k in m}
    seen = set()
    for key, mv in mtree["resolved"].items():
        if mv["scope"] not in MAVEN_MAIN_SCOPES | {"test"}:
            continue
        if mv["group"] + ":" + mv["artifact"] in lock.modules.values():
            continue                                    # a workspace sibling, not a locked artifact
        out["maven_coords"] += 1
        seen.add(key)
        jk_v, jk_scopes = jk_version_for(jtree, key, mv["scope"])
        if jk_v is None:
            out["only_maven"][mv["scope"] + (" optional" if mv["optional"] else "")] += 1
            out["only_maven_names"].append(f"{key}:{mv['version']} ({mv['scope']}, depth {mv['depth']})")
            continue
        out["compared"] += 1
        row = lock.row(key, module, mv["scope"])
        if row and row.get("version") != jk_v:
            out["tree_vs_lock"] += 1
            out["tree_vs_lock_names"].append(f"{key}: jk tree {jk_v}, lock row {row['version']} (maven {mv['version']})")
            jk_v = row["version"]                      # the lock is what the build reads; the tree's rendering is a separate finding
        if mv["scope"] in MAVEN_MAIN_SCOPES and row is not None:
            trow = lock.row(key, module, "test")        # Maven's test classpath carries the compile version; jk's test row may not
            if trow is not None and trow is not row and trow.get("version") != mv["version"]:
                out["test_side"] += 1
                out["test_side_names"].append(f"{key}: jk test row {trow['version']}, main row {row['version']}, maven {mv['version']} ({mv['scope']})")
        if jk_v == mv["version"]:
            maven_main = mv["scope"] in MAVEN_MAIN_SCOPES
            if maven_main != bool(jk_scopes & JK_MAIN_SCOPES):
                out["scope_only"] += 1
                out["scope_only_names"].append(f"{key}:{jk_v} maven {mv['scope']} / jk {','.join(sorted(jk_scopes))}")
            continue
        rule, evidence = classify(key, module, jk_v, mv, mtree["omitted"].get(key, []), row, pins, facts)
        out["differ"].append({"coord": key, "jk": jk_v, "maven": mv["version"], "maven_scope": mv["scope"],
                              "rule": rule, "evidence": evidence, "parents": mv["parents"],
                              "direction": "jk higher" if compare_versions(jk_v, mv["version"]) > 0 else "jk lower"})
    differing = {x["coord"]: x for x in out["differ"]}
    for x in out["differ"]:
        if x["rule"] in ("unknown", "depth") and "omitted" not in x["evidence"].split(";")[0]:
            up = [p for p in x["parents"] if p in differing]
            if up:
                p = differing[up[-1]]
                x["rule"] = "cascade"
                x["evidence"] = f"under {p['coord']} (jk {p['jk']}, Maven {p['maven']}, {p['rule']}); " + x["evidence"]
    for x in out["differ"]:
        del x["parents"]
    jk_keys: dict[str, tuple[str, str]] = {}
    for scope, m in jtree["scopes"].items():
        for key, v in m.items():
            jk_keys.setdefault(key, (v, scope))
    for key, (v, scope) in jk_keys.items():
        if key not in seen and key not in lock.modules.values():
            if active_keys is not None and key not in active_keys:
                out["only_jk_inactive"] += 1
                out["only_jk_inactive_names"].append(f"{key}:{v} ({scope})")
                continue
            out["only_jk"] += 1
            if key in facts["optional"]:
                out["only_jk_optional"] += 1
            out["only_jk_names"].append(f"{key}:{v} ({scope}{', optional in a POM' if key in facts['optional'] else ''})")
    out["only_maven"] = dict(out["only_maven"])
    return out


def failing_test_modules(repo: str, root: Path, lock: Lock) -> list[str]:
    """Modules whose jk test step reported failures, from the jk-results.md copied with the clone."""
    for p in (root / "jk-results.corpus.md", harness.RESULTS / repo / "jk-results.md"):
        if p.is_file():
            gas = set(re.findall(r"^#### \S+ — (\S+:\S+)$", p.read_text(encoding="utf-8", errors="replace"), re.M))
            by_ga = {v: k for k, v in lock.modules.items()}
            return sorted({by_ga[g] for g in gas if g in by_ga})
    return []


def jk_step(root: Path, log: Path, deadline: float, args: list[str]) -> dict:
    """One jk command in the scratch copy with the jk under test; {status, wall}."""
    cmd = harness.JK + ["--no-timeline"] + args
    t0 = time.time()
    with open(log, "a", encoding="utf-8") as f:
        f.write(f"\n$ (cd {root} && {' '.join(cmd)})\n")
    try:
        p = subprocess.run(cmd, cwd=root, capture_output=True, text=True, timeout=max(deadline - time.time(), 60),
                           env={**os.environ, **JK_ENV})
        status = "ok" if p.returncode == 0 else "fail"
        tail = p.stdout + p.stderr
    except subprocess.TimeoutExpired as e:
        status, tail = "timeout", str(e.stdout or "") + str(e.stderr or "")
    with open(log, "a", encoding="utf-8") as f:
        f.write(f"{tail}\n# jk {args[0]} {status}\n")
    return {"status": status, "wall": round(time.time() - t0, 1)}


def reimport(root: Path, log: Path, deadline: float) -> dict:
    """Drop every manifest and lock the corpus run wrote and import the POMs again with the jk under test."""
    for f in list(root.rglob("jk.toml")) + list(root.rglob("jk-lock.toml")):
        if "target" not in f.parts:
            f.unlink()
    return jk_step(root, log, deadline, ["import", "pom.xml", "--report", "target/jk-lockdiff-import.md"])


def relock(root: Path, log: Path, deadline: float) -> dict:
    """`jk lock -r` in the scratch copy with the jk under test; {status, wall}."""
    return jk_step(root, log, deadline, ["lock", "-r"])


def measure(repo: dict, args, date: str) -> dict:
    name = repo["name"]
    src = harness.SCRATCH / name
    root = SCRATCH / name
    corpus_row = harness.load_rows().get(name, {})
    row = {"repo": name, "full": repo["full"], "sha": repo["sha"], "date": dt.datetime.now().isoformat(timespec="seconds"),
           "corpus_run": f"{corpus_row.get('run', '')} {corpus_row.get('date', '')}".strip(),
           "corpus_jk": corpus_row.get("jk_commit", ""),
           "lock_generated_by": "", "reimport": {}, "relock": {}, "maven": {}, "modules_maven": 0, "modules_jk": 0, "modules_compared": 0,
           "modules_differ": 0, "pairs_differ": 0, "coords_differ": 0, "by_rule": {r: 0 for r in RULES},
           "by_rule_coords": {r: 0 for r in RULES}, "direction": {"jk higher": 0, "jk lower": 0}, "scope_only": 0,
           "only_maven": {}, "only_jk": 0, "only_jk_optional": 0, "only_jk_inactive": 0, "modules_with_inactive_rows": 0,
           "tree_vs_lock": 0, "maven_coords": 0, "compared": 0,
           "failing_test_modules": [], "failing_modules_with_diff": [], "examples": [], "modules": []}
    print(f"== {name}", flush=True)
    deadline = time.time() + REPO_CAP
    SCRATCH.mkdir(parents=True, exist_ok=True)
    log = SCRATCH / f"{name}.log"
    if not args.reuse:
        if log.exists():
            log.unlink()
        if not (src / "pom.xml").is_file():
            row["maven"] = {"status": "skipped", "reason": f"no corpus clone at {src}"}
            return row
        subprocess.run(["rsync", "-a", "--delete", "--exclude", ".git", "--exclude", "target", "--exclude", "node_modules",
                        f"{src}/", f"{root}/"], check=True)
        if (src / "target" / "jk-results.md").is_file():
            shutil.copy(src / "target" / "jk-results.md", root / "jk-results.corpus.md")   # the clone's latest jk test results
    if args.reimport and not args.reuse:
        row["reimport"] = reimport(root, log, deadline)
        print(f"  {name}: jk import {row['reimport']['status']} ({row['reimport']['wall']}s)", flush=True)
        if row["reimport"]["status"] != "ok":
            row["maven"] = {"status": "skipped", "reason": f"jk import {row['reimport']['status']} with the jk under test"}
            return row
    if not (root / "jk-lock.toml").is_file() and not args.relock:
        row["maven"] = {"status": "skipped", "reason": "no jk-lock.toml in the corpus clone"}
        return row
    if args.relock and not args.reuse:
        row["relock"] = relock(root, log, deadline)
        print(f"  {name}: jk lock {row['relock']['status']} ({row['relock']['wall']}s)", flush=True)
        if row["relock"]["status"] != "ok":
            row["maven"] = {"status": "skipped", "reason": f"jk lock {row['relock']['status']} with the jk under test"}
            return row
    lock = Lock(root / "jk-lock.toml")
    row["lock_generated_by"] = lock.generated_by
    if args.reuse and any(root.rglob(TREE_FILE.split("/")[-1])):
        row["maven"] = {"status": "ok", "mode": "reused", "wall": 0.0, "reason": ""}
    else:
        row["maven"] = maven_trees(root, repo, log, deadline)
    mtrees = maven_modules(root)
    row["modules_maven"] = len(mtrees)
    print(f"  {name}: maven {row['maven']['status']} ({row['maven'].get('mode')}, {row['maven'].get('wall')}s, "
          f"go-offline {row['maven'].get('go_offline', {}).get('status', '-')}, {row['maven'].get('poms_filled', 0)} POMs filled, "
          f"{row['maven'].get('missing_poms', '?')} missing), "
          f"{len(mtrees)} module trees", flush=True)
    jmods = jk_modules(root)
    row["modules_jk"] = len(jmods)
    if not mtrees:
        row["maven"]["reason"] = row["maven"].get("reason") or "no module tree written"
        return row
    pins = declared_pins(root, jmods)
    facts = pom_facts(root)
    row["inline_managed_gas"] = len(facts["managed"])
    failing = failing_test_modules(name, root, lock)
    row["failing_test_modules"] = failing
    pair_rule = Counter()
    coord_rule: dict[str, set] = defaultdict(set)
    examples: dict[str, dict] = {}
    for mod in jmods:
        if mod not in mtrees:
            continue
        if time.time() > deadline + 300:
            row["modules"].append({"module": mod, "status": "capped"})
            continue
        jt = jk_tree(root, mod, log)
        if jt is None:
            row["modules"].append({"module": mod, "status": "jk tree failed"})
            continue
        inactive = inactive_feature_rows((root if mod == "." else root / mod) / "jk.toml")
        jt_active = jk_tree_without(root, mod, log, set(inactive)) if inactive else None
        if inactive:
            row["modules_with_inactive_rows"] += 1
        d = diff_module(mod, mtrees[mod], jt, lock, pins, facts, jt_active, inactive)
        row["modules_compared"] += 1
        row["maven_coords"] += d["maven_coords"]
        row["compared"] += d["compared"]
        row["scope_only"] += d["scope_only"]
        row["only_jk"] += d["only_jk"]
        row["only_jk_optional"] = row.get("only_jk_optional", 0) + d["only_jk_optional"]
        row["only_jk_inactive"] += d["only_jk_inactive"]
        row["tree_vs_lock"] += d["tree_vs_lock"]
        row["test_side"] = row.get("test_side", 0) + d["test_side"]
        for k, v in d["only_maven"].items():
            row["only_maven"][k] = row["only_maven"].get(k, 0) + v
        if d["differ"]:
            row["modules_differ"] += 1
            if mod in failing:
                row["failing_modules_with_diff"].append(mod)
        for x in d["differ"]:
            pair_rule[x["rule"]] += 1
            coord_rule[x["rule"]].add(x["coord"])
            row["direction"][x["direction"]] += 1
            ex = examples.setdefault(x["coord"], {**x, "modules": []})
            ex["modules"].append(mod)
        row["modules"].append({"module": mod, "status": "ok", "maven_coords": d["maven_coords"], "compared": d["compared"],
                               "differ": len(d["differ"]), "scope_only": d["scope_only"],
                               "only_maven": sum(d["only_maven"].values()), "only_jk": d["only_jk"],
                               "rules": dict(Counter(x["rule"] for x in d["differ"])),
                               "only_maven_names": d["only_maven_names"][:12], "only_jk_names": d["only_jk_names"][:12],
                               "only_jk_optional": d["only_jk_optional"], "scope_only_names": d["scope_only_names"][:8],
                               "only_jk_inactive": d["only_jk_inactive"], "only_jk_inactive_names": d["only_jk_inactive_names"][:12],
                               "inactive_rows": d["inactive_rows"],
                               "tree_vs_lock_names": d["tree_vs_lock_names"][:8],
                               "test_side": d["test_side"], "test_side_names": d["test_side_names"][:8]})
        print(f"  {name}: {mod:50s} maven={d['maven_coords']:4d} compared={d['compared']:4d} differ={len(d['differ']):3d} "
              f"only-maven={sum(d['only_maven'].values()):3d} only-jk={d['only_jk']:3d} via-inactive={d['only_jk_inactive']:3d}", flush=True)
    row["pairs_differ"] = sum(pair_rule.values())
    row["by_rule"] = {r: pair_rule.get(r, 0) for r in RULES}
    row["by_rule_coords"] = {r: len(coord_rule.get(r, ())) for r in RULES}
    row["coords_differ"] = len(examples)
    row["examples"] = sorted(examples.values(), key=lambda e: (-len(e["modules"]), e["coord"]))[:40]
    for e in row["examples"]:
        e["module_count"] = len(e["modules"])
        e["modules"] = e["modules"][:4]
    return row


# --------------------------------------------------------------------------- rendering

def load_latest(date: str | None = None) -> tuple[str, dict[str, dict]]:
    files = sorted(OUT_JSONL.glob("*.jsonl"))
    if date:
        files = [f for f in files if f.stem == date]
    if not files:
        return "", {}
    rows: dict[str, dict] = {}
    for line in files[-1].read_text().splitlines():
        if line.strip():
            r = json.loads(line)
            rows[r["repo"]] = r
    return files[-1].stem, rows


def fmt_rules(r: dict, key: str = "by_rule") -> str:
    return ", ".join(f"{k} {v}" for k, v in r.get(key, {}).items() if v) or "—"


def render(date: str, rows: dict[str, dict], repos: list[dict]) -> None:
    OUT_MD.mkdir(exist_ok=True)
    total = Counter()
    lines = [f"# jk lock vs Maven resolution — {date}", "",
             "Per module, every coordinate Maven's verbose `dependency:tree` resolves is looked up in jk's per-module closure "
             "(`jk tree <module> -t -f -s all`, reading the harness's `jk-lock.toml`, member rows first). A row of the table is one repo; "
             "`pairs` = (module, coordinate) pairs whose version differs, `coords` = distinct coordinates behind them. Rules: "
             "**bom** = jk's version is a `[platform-dependencies]` BOM's (`pinned-by`) and Maven's differs; "
             "**managed** = Maven applied an inline `<dependencyManagement>` version to a transitive that no BOM manages in jk; "
             "**pin** = jk's version is another workspace member's direct pin (a pin any member declares is the whole lock's version under `pins = \"nearest\"`); "
             "**depth** = neither side managed it, Maven took the nearest declaration and jk the highest; **cascade** = the parent POM already differs; **unknown** = none of those. "
             "`test row differs` = compile-scope coordinates whose jk test-scope lock row is not the version Maven's test classpath carries (a pin or managed version that governs jk's main solve but not its test solve). "
             "`only jk` = coordinates on jk's closure Maven's tree lacks (a sibling's `<optional>` edge, or a `<dependencyManagement>` exclusion import did not carry); "
             "`via inactive features` = coordinates jk reaches only through the module's inactive-feature rows (`optional = true` rows a `[features.<name>]` table names and no default feature activates — a profile's dependencies as the import writes them), read from a second `jk tree` over the manifest with those rows removed and counted apart from `only jk`; `tree vs lock` = `jk tree` lines whose version is not the lock row the module reads. "
             "The `maven` cell names the tree run's status (`partial` = a reactor module failed under `-fae` and the others' trees stand), its mode, its wall, `dependency:go-offline`'s status, the POMs the harness fetched into Maven's local repo because a tree run reported them missing, and the POMs the final run still could not read (each one is an artifact Maven rendered as a leaf, so its transitives can only be `only jk`); the lock is the corpus clone's unless the cell says `relocked` (rewritten by the jk under test) or `reimported + relocked` (the manifests imported from the POMs by that jk as well).", "",
             "| repo | maven | modules mvn / jk / compared | modules that differ | pairs / coords differ | by rule (pairs) | jk higher / lower | test row differs | same version, scope differs | only Maven | only jk (optional in a POM) | via inactive features (modules) | tree vs lock | failing-test modules with a diff |",
             "|------|-------|----------------------------:|--------------------:|----------------------:|-----------------|------------------:|-----------------:|----------------------------:|-----------:|----------------------------:|--------------------------------:|-------------:|----------------------------------|"]
    for repo in repos:
        r = rows.get(repo["name"])
        if not r:
            continue
        mv = r["maven"]
        mstat = mv.get("status", "")
        relocked = (", reimported + relocked" if r.get("reimport", {}).get("status") == "ok"
                    else ", relocked" if r.get("relock", {}).get("status") == "ok" else "")
        detail = (f", go-offline {mv['go_offline']['status']}, {mv.get('poms_filled', 0)} POMs filled, {mv.get('missing_poms', 0)} missing"
                  if mv.get("go_offline") else "")
        if mstat == "ok":
            mcell = f"ok ({mv.get('mode')}, {mv.get('wall', 0):.0f}s{detail}{relocked})"
        elif r["modules_maven"]:
            mcell = (f"partial ({mv.get('mode')}, {mv.get('wall', 0):.0f}s{detail}{relocked}; {r['modules_maven']} module trees, "
                     f"{mv.get('reason', mstat)})").replace("|", "\\|")[:220]
        else:
            mcell = f"maven unavailable: {mv.get('reason', mstat)}".replace("|", "\\|")[:160]
        only_m = sum(r.get("only_maven", {}).values())
        fail = r.get("failing_test_modules", [])
        fcell = (f"{len(r.get('failing_modules_with_diff', []))} of {len(fail)}" if fail else "no failing tests")
        lines.append(f"| {r['full']} | {mcell} | {r['modules_maven']} / {r['modules_jk']} / {r['modules_compared']} | {r['modules_differ']} "
                     f"| {r['pairs_differ']} / {r['coords_differ']} | {fmt_rules(r)} | {r['direction'].get('jk higher', 0)} / {r['direction'].get('jk lower', 0)} "
                     f"| {r.get('test_side', 0)} | {r['scope_only']} | {only_m} | {r['only_jk']} ({r.get('only_jk_optional', 0)}) "
                     f"| {r.get('only_jk_inactive', 0)} ({r.get('modules_with_inactive_rows', 0)}) | {r['tree_vs_lock']} | {fcell} |")
        if r["modules_compared"]:
            total["repos"] += 1
            total["modules_compared"] += r["modules_compared"]
            total["modules_differ"] += r["modules_differ"]
            total["pairs"] += r["pairs_differ"]
            total["coords"] += r["coords_differ"]
            for k, v in r["by_rule"].items():
                total["rule " + k] += v
            for k, v in r["by_rule_coords"].items():
                total["rulec " + k] += v
    lines += ["",
              f"**Totals over the {total['repos']} repos Maven could answer for:** {total['modules_compared']} modules compared, "
              f"{total['modules_differ']} differ on at least one version; {total['pairs']} (module, coordinate) pairs and {total['coords']} distinct coordinates differ. "
              "By rule (pairs / coords): " + "; ".join(f"{r} {total['rule ' + r]} / {total['rulec ' + r]}" for r in RULES) + ".", ""]
    for repo in repos:
        r = rows.get(repo["name"])
        if not r or not r.get("examples"):
            continue
        lines += [f"## {r['full']}", "",
                  (f"Manifests imported from the POMs and the lock written by `{r['lock_generated_by']}` over the corpus clone from row {r.get('corpus_run', '?')}; "
                   if r.get("reimport", {}).get("status") == "ok" else
                   f"Lock rewritten by `{r['lock_generated_by']}` over the corpus clone from row {r.get('corpus_run', '?')}; "
                   if r.get("relock", {}).get("status") == "ok" else
                   f"Lock from corpus row {r.get('corpus_run', '?')} (`{r['lock_generated_by']}`, {r.get('corpus_jk', '')}); ")
                  + f"{r['compared']} of {r['maven_coords']} Maven coordinates found in jk's closure; "
                  f"jk tree vs lock row disagreements: {r['tree_vs_lock']}. Failing-test modules: {', '.join(r['failing_test_modules']) or 'none'}"
                  + (f"; with a version diff: {', '.join(r['failing_modules_with_diff'])}" if r["failing_modules_with_diff"] else "") + ".", "",
                  "| coordinate | jk | Maven | modules | rule | evidence |", "|------------|----|-------|--------:|------|----------|"]
        for e in r["examples"][:25]:
            ev = e["evidence"].replace("|", "\\|")
            lines.append(f"| {e['coord']} | {e['jk']} | {e['maven']} ({e['maven_scope']}) | {e['module_count']} | {e['rule']} | {ev} |")
        lines.append("")
    (OUT_MD / f"{date}.md").write_text("\n".join(lines))


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--only", action="append", help="one repo name (repeatable)")
    ap.add_argument("--reuse", action="store_true", help="keep the scratch copy and its Maven trees; redo the jk side and the diff")
    ap.add_argument("--render", action="store_true", help="only rewrite lock-diff/<date>.md from the latest rows")
    ap.add_argument("--relock", action="store_true", help="rewrite the scratch copy's jk-lock.toml with the jk under test before the diff")
    ap.add_argument("--reimport", action="store_true", help="drop the corpus run's manifests and `jk import pom.xml` again with the jk under test (implies --relock)")
    ap.add_argument("--jk-home", help="a private jk install: <dir>/bin/jk runs with JK_HOME=<dir> (default: the jk on PATH)")
    ap.add_argument("--date", default=f"{dt.date.today():%Y-%m-%d}", help="row file / report date (default today)")
    args = ap.parse_args()
    if args.reimport:
        args.relock = True
    if args.jk_home:
        home = Path(args.jk_home).expanduser().resolve()
        harness.JK = [str(home / "bin" / "jk")] + harness.JK[1:]
        JK_ENV["JK_HOME"] = str(home)
    cfg = tomllib.load(open(HERE / "repos.toml", "rb"))
    repos = cfg["repo"]
    if args.render:
        date, rows = load_latest(args.date)
        render(date, rows, repos)
        return 0
    latest = harness.load_rows()
    todo = []
    for repo in repos:
        if args.only and repo["name"] not in args.only:
            continue
        r = latest.get(repo["name"])
        if not r or r["steps"].get("jk_lock", {}).get("status") != "ok":
            continue
        todo.append(repo)
    OUT_JSONL.mkdir(parents=True, exist_ok=True)
    out = OUT_JSONL / f"{args.date}.jsonl"
    for repo in todo:
        row = measure(repo, args, args.date)
        with open(out, "a") as f:
            f.write(json.dumps(row, sort_keys=True) + "\n")
        _, rows = load_latest(args.date)
        render(args.date, rows, repos)
        print(f"  {repo['name']}: modules differ {row['modules_differ']}/{row['modules_compared']}, pairs {row['pairs_differ']}, {fmt_rules(row)}", flush=True)
    return 0


if __name__ == "__main__":
    sys.exit(main())

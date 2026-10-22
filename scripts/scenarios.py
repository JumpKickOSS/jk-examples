#!/usr/bin/env python3
"""The scenario manifests (`scenario.toml`) of this repository, for CI and for people.

  scripts/scenarios.py list [--cadence C] [--have NEEDS] [--dev]   scenario dirs, one per line
  scripts/scenarios.py check DIR                                   run DIR's check commands
  scripts/scenarios.py dev-checks DIR                              DIR's `jk dev` API checks
  scripts/scenarios.py lint                                        manifests and README agree

A manifest:

  cadence    = "nightly"            # pr | nightly | weekly | manual — the slowest CI that runs it
  needs      = ["docker"]           # docker, android-sdk, graalvm, idle-host (network is assumed)
  check      = ["jk build", "jk guard"]
  dev        = true                 # also run under `jk dev` by jk's example-dev-smoke.sh
  dev-checks = ["/api/hello=hello"] # paths fetched through the first front door, with expected text

`list --cadence nightly` selects every scenario a nightly runs: pr and nightly. `--have docker`
admits scenarios whose needs are all available. Python 3.11+, standard library only.
"""
import argparse
import pathlib
import re
import subprocess
import sys
import tomllib

ROOT = pathlib.Path(__file__).resolve().parent.parent
CADENCES = ["pr", "nightly", "weekly", "manual"]
NEEDS = {"docker", "android-sdk", "graalvm", "idle-host"}
KEYS = {"cadence", "needs", "check", "dev", "dev-checks"}


def manifests():
    for path in sorted(ROOT.glob("**/scenario.toml")):
        if any(part in {"node_modules", "target", "checkout"} for part in path.parts):
            continue
        yield path.parent, load(path)


def load(path):
    with path.open("rb") as f:
        data = tomllib.load(f)
    unknown = set(data) - KEYS
    if unknown:
        raise SystemExit(f"{path}: unknown keys {sorted(unknown)}")
    if data.get("cadence") not in CADENCES:
        raise SystemExit(f"{path}: cadence must be one of {CADENCES}")
    bad = set(data.get("needs", [])) - NEEDS
    if bad:
        raise SystemExit(f"{path}: unknown needs {sorted(bad)}; known: {sorted(NEEDS)}")
    if not data.get("check"):
        raise SystemExit(f"{path}: check names no command")
    if data.get("dev-checks") and not data.get("dev"):
        raise SystemExit(f"{path}: dev-checks without dev = true")
    return data


def rel(d):
    return d.relative_to(ROOT).as_posix()


def cmd_list(args):
    ceiling = CADENCES.index(args.cadence)
    have = set(filter(None, (args.have or "").split(",")))
    for d, m in manifests():
        if CADENCES.index(m["cadence"]) > ceiling:
            continue
        if not set(m.get("needs", [])) <= have:
            continue
        if args.dev and not m.get("dev"):
            continue
        print(rel(d))


def cmd_check(args):
    d = (ROOT / args.dir).resolve()
    m = load(d / "scenario.toml")
    for command in m["check"]:
        print(f"== {rel(d)}: {command}", flush=True)
        if subprocess.run(["bash", "-c", command], cwd=d).returncode != 0:
            print(f"!! {rel(d)}: `{command}` failed", file=sys.stderr)
            return 1
    return 0


def cmd_dev_checks(args):
    d = (ROOT / args.dir).resolve()
    for check in load(d / "scenario.toml").get("dev-checks", []):
        print(check)


def cmd_lint(_args):
    readme = (ROOT / "README.md").read_text(encoding="utf-8")
    listed = set(re.findall(r"^\| `([0-9a-z][^`]*/[^`]*)/` \|", readme, re.M))
    found = {rel(d) for d, _ in manifests()}
    problems = []
    for d in sorted(found - listed):
        problems.append(f"{d}: has a scenario.toml but no row in README.md")
    for d in sorted(listed - found):
        problems.append(f"{d}: listed in README.md but has no scenario.toml")
    for project in sorted(ROOT.glob("*/*/jk.toml")) + sorted(ROOT.glob("05-recipes/*/*/jk.toml")):
        d = project.parent
        if not (d / "scenario.toml").exists() and rel(d).count("/") in (1, 2):
            problems.append(f"{rel(d)}: a project with no scenario.toml")
    for p in problems:
        print(p, file=sys.stderr)
    return 1 if problems else 0


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)
    p = sub.add_parser("list")
    p.add_argument("--cadence", choices=CADENCES, default="manual")
    p.add_argument("--have", help="comma-separated needs this host meets")
    p.add_argument("--dev", action="store_true", help="only scenarios run under jk dev")
    p.set_defaults(func=cmd_list)
    p = sub.add_parser("check")
    p.add_argument("dir")
    p.set_defaults(func=cmd_check)
    p = sub.add_parser("dev-checks")
    p.add_argument("dir")
    p.set_defaults(func=cmd_dev_checks)
    p = sub.add_parser("lint")
    p.set_defaults(func=cmd_lint)
    args = parser.parse_args()
    sys.exit(args.func(args) or 0)


if __name__ == "__main__":
    main()

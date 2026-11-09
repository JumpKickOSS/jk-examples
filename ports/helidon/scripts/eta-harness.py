#!/usr/bin/env python3
# SPDX-License-Identifier: Apache-2.0
"""ETA accuracy harness: how close jk's build estimate and progress bar are to the real wall.

For each build shape, runs `jk build -O jsonl` (or `jk test`) N times on the imported Helidon checkout
and reads, from jk's own event stream:

- R0: the first remaining-time estimate the engine publishes, and when it arrives;
- the actual wall of the job (first event to last);
- the bar (`workspace-progress.progress`) at 25/50/75/90 % of that wall.

Accuracy is R0 / actual (1.00 is perfect) and the bar's distance from the wall fraction in points.
Results print as Markdown. Usage:

    ./scripts/eta-harness.py                 # every shape, 5 runs each
    RUNS=3 ./scripts/eta-harness.py mid-body leaf

A dirty shape appends a unique comment (body-only) or a new public method (API change) to one source
and restores it afterwards. `cold-cold-worker` stops the resident compiler worker first, as the
10-minute linger would; everything else runs on a warm engine and worker.
"""

import json
import os
import statistics
import subprocess
import sys
import time
from pathlib import Path

HERE = Path(__file__).resolve().parent.parent
CHECKOUT = HERE / "checkout"
RUNS = int(os.environ.get("RUNS", "5"))
FRACTIONS = (0.25, 0.5, 0.75, 0.9)

BODY_FILE = CHECKOUT / "common/common/src/main/java/io/helidon/common/LazyValue.java"
API_FILE = CHECKOUT / "common/common/src/main/java/io/helidon/common/Base64Value.java"
LEAF_FILE = next((CHECKOUT / "webserver/static-content/src/main/java").rglob("*.java"), None)


def jk(*args, capture=False):
    return subprocess.run(["jk", *args], cwd=CHECKOUT, text=True,
                          stdout=subprocess.PIPE if capture else subprocess.DEVNULL,
                          stderr=subprocess.STDOUT if capture else subprocess.DEVNULL)


def remove_targets():
    for t in CHECKOUT.rglob("target"):
        if t.is_dir() and ".git" not in t.parts:
            subprocess.run(["rm", "-rf", str(t)])


def stop_compiler_worker():
    """Stop the resident java-compiler worker (PluginMain --pull), matched on /proc, never by pattern kill."""
    me = os.getpid()
    for p in Path("/proc").iterdir():
        if not p.name.isdigit() or int(p.name) == me:
            continue
        try:
            exe = os.readlink(p / "exe")
            argv = (p / "cmdline").read_bytes().split(b"\0")
        except OSError:
            continue
        if exe.endswith("/bin/java") and b"cc.jumpkick.plugin.process.PluginMain" in argv and b"--pull" in argv:
            os.kill(int(p.name), 15)
    time.sleep(3)


def append(path, text):
    with open(path, "a") as f:
        f.write(text)


def restore(*paths):
    subprocess.run(["git", "checkout", "-q", "--", *[str(p) for p in paths]], cwd=CHECKOUT)


def measure(args):
    out = jk(*args, "-O", "jsonl", capture=True)
    events = [json.loads(l) for l in out.stdout.splitlines() if l.startswith("{")]
    if out.returncode != 0 or not events:
        return {"ok": False}
    t0 = events[0]["ts"]
    wall = (events[-1]["ts"] - t0) / 1000
    r0 = r0_at = None
    bar = []
    for e in events:
        if e.get("type") != "workspace-progress":
            continue
        t = (e["ts"] - t0) / 1000
        if r0 is None and (e.get("R0") or 0) > 0:
            r0, r0_at = e["R0"] / 1000, t
        if e.get("progress") is not None:
            bar.append((t, e["progress"]))
    points = []
    for f in FRACTIONS:
        if not bar:
            points.append(None)
            continue
        t = wall * f
        prior = [p for (ts, p) in bar if ts <= t]
        points.append(prior[-1] if prior else 0.0)
    starts = [(e["ts"] - t0) / 1000 for e in events if e.get("type") == "module-start"]
    # R0 predicts the execute phase (the engine's own quality line compares it to the execute wall,
    # and the countdown anchors at execute start), so that is what it is judged against.
    execute = wall - min(starts) if starts else wall
    return {"ok": True, "wall": wall, "execute": execute, "r0": r0, "r0_at": r0_at, "bar": points}


def shape_cold_warm():
    remove_targets()
    return measure(["build", "--skip-tests", "--redo"])


def shape_cold_cold():
    stop_compiler_worker()
    remove_targets()
    return measure(["build", "--skip-tests", "--redo"])


def shape_noop():
    return measure(["build", "--skip-tests"])


def shape_leaf():
    append(LEAF_FILE, f"\n// eta-harness {time.time_ns()}\n")
    try:
        return measure(["build", "--skip-tests"])
    finally:
        restore(LEAF_FILE)
        jk("build", "--skip-tests")


def shape_mid_body():
    append(BODY_FILE, f"\n// eta-harness {time.time_ns()}\n")
    try:
        return measure(["build", "--skip-tests"])
    finally:
        restore(BODY_FILE)
        jk("build", "--skip-tests")


def shape_mid_api():
    text = API_FILE.read_text()
    idx = text.rindex("}")
    API_FILE.write_text(text[:idx] + f"\n    /** eta-harness */\n    static int etaHarness{time.time_ns()}() {{ return 1; }}\n" + text[idx:])
    try:
        return measure(["build", "--skip-tests"])
    finally:
        restore(API_FILE)
        jk("build", "--skip-tests")


def shape_tests():
    return measure(["test", "-m", "webserver/webserver"])


SHAPES = {
    "cold-warm-worker": shape_cold_warm,
    "cold-cold-worker": shape_cold_cold,
    "noop": shape_noop,
    "leaf": shape_leaf,
    "mid-body": shape_mid_body,
    "mid-api": shape_mid_api,
    "tests": shape_tests,
}


def main():
    wanted = sys.argv[1:] or list(SHAPES)
    jk("build", "--skip-tests")  # settle the tree and warm the engine and worker
    print(f"# jk ETA accuracy — Helidon ({time.strftime('%Y-%m-%dT%H:%MZ', time.gmtime())}), {RUNS} runs per shape\n")
    print(f"- {jk('--version', capture=True).stdout.strip()}\n")
    print("| shape | wall s (median) | R0 s (median) | R0/execute (median [each run, in order]) | R0 arrives s | bar − wall fraction at 25/50/75/90 % (median, points) |")
    print("|---|---:|---:|---|---:|---|")
    for name in wanted:
        runs = []
        for _ in range(RUNS):
            r = SHAPES[name]()
            if r.get("ok"):
                runs.append(r)
        if not runs:
            print(f"| {name} | failed | | | | |")
            continue
        walls = [r["wall"] for r in runs]
        with_r0 = [r for r in runs if r["r0"]]
        ratios = [r["r0"] / r["execute"] for r in with_r0 if r["execute"] > 0]
        in_order = " ".join(f"{x:.2f}" for x in ratios)
        r0s = [r["r0"] for r in with_r0]
        arrivals = [r["r0_at"] for r in with_r0]
        devs = []
        for i, f in enumerate(FRACTIONS):
            pts = [r["bar"][i] - 100 * f for r in runs if r["bar"][i] is not None]
            devs.append(f"{statistics.median(pts):+.0f}" if pts else "–")
        ratio = (f"{statistics.median(ratios):.2f} [{in_order}]" if ratios else "no estimate")
        print(f"| {name} | {statistics.median(walls):.2f} | "
              f"{statistics.median(r0s):.2f} | {ratio} | "
              f"{statistics.median(arrivals):.2f} | {' / '.join(devs)} |" if r0s else
              f"| {name} | {statistics.median(walls):.2f} | – | no estimate | – | {' / '.join(devs)} |")
        sys.stdout.flush()


if __name__ == "__main__":
    main()

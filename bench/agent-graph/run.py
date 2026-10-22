#!/usr/bin/env python3
"""How many tokens does an agent read before it holds the answer? Three ways to find code, one tree.

  bench/agent-graph/run.py [--project DIR] [--graphify DIR] [--label NAME]

For each task in tasks.toml, over the project (default 06-production/hotel-reservations), two
counts per method: **find**, the tokens read until an answer file is named, and **hold**, until the
answer's code has been read.

  grep     `grep -rniE` for the task's words (its output is read), then whole files in order of
           hit count until an answer file has been read — the grep-and-open habit. It holds the
           answer as it finds it.
  graphify `graphify query "<question>"` over graphify's graph of the tree. Holding the answer
           means opening the answer file whole: graphify names a file and a line, with no reader.
  jk       `jk graph query "<question>"`; holding the answer means `jk graph read` of the answer
           file's type, which is what the guide tells an agent to do instead of opening it.

A graph method whose output names no answer file opens it whole, in both counts. Tokens are
characters / 4 for every method alike. Rows go to results/<date>.jsonl; RESULTS.md is
regenerated with the totals and the ratchet the next run must not lose.

graphify runs from a checkout (`--graphify ~/src/oss/graphify`, through `uv run`); without one
its column is skipped. Python 3.11+, standard library only.
"""
import argparse
import datetime
import json
import os
import pathlib
import re
import subprocess
import sys
import tomllib

HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parent.parent
SKIP = {"target", "node_modules", ".angular", "dist", "graphify-out", ".jk"}


def tokens(text):
    return (len(text) + 3) // 4


def run(cmd, cwd):
    p = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True)
    return p.stdout + (p.stderr if p.returncode not in (0, 3, 4) else "")


def source_files(project):
    for path in project.rglob("*"):
        if path.is_file() and not (set(path.relative_to(project).parts) & SKIP) and path.suffix in {
            ".java", ".ts", ".sql", ".yaml", ".html", ".scss", ".toml", ".md"}:
            yield path


def named(output, answer):
    return any(pathlib.Path(a).name in output for a in answer)


def grep_method(task, project):
    pattern = "|".join(re.escape(w) for w in task["grep"])
    excludes = [f"--exclude-dir={d}" for d in sorted(SKIP)]
    out = run(["grep", "-rniIE", *excludes, pattern, "."], project)
    spent = tokens(out)
    hits = {}
    for line in out.splitlines():
        f = line.split(":", 1)[0].removeprefix("./")
        hits[f] = hits.get(f, 0) + 1
    found = False
    for f, _ in sorted(hits.items(), key=lambda kv: -kv[1]):
        spent += tokens((project / f).read_text(errors="replace"))
        if f in task["answer"]:
            found = True
            break
    if not found:
        spent += penalty(task, project)
    return {"find": spent, "hold": spent, "found": found}


def penalty(task, project):
    return sum(tokens((project / a).read_text()) for a in task["answer"])


def graphify_method(task, project, checkout):
    out = run(["uv", "run", "--project", str(checkout), "graphify", "query", task["question"],
               "--graph", str(project / "graphify-out" / "graph.json")], project)
    found = named(out, task["answer"])
    find = tokens(out) + (0 if found else penalty(task, project))
    hold = tokens(out) + penalty(task, project)
    return {"find": find, "hold": hold, "found": found}


def jk_method(task, project):
    out = run(["jk", "graph", "query", task["question"], "--no-ansi"], project)
    found = named(out, task["answer"])
    if not found:
        spent = tokens(out) + penalty(task, project)
        return {"find": spent, "hold": spent, "found": False}
    answer = pathlib.Path(task["answer"][0]).stem
    read = tokens(run(["jk", "graph", "read", answer, "-w", "structure", "--no-ansi"], project))
    return {"find": tokens(out), "hold": tokens(out) + read, "found": True}


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--project", default=str(ROOT / "06-production" / "hotel-reservations"))
    ap.add_argument("--graphify", default=os.environ.get("GRAPHIFY_CHECKOUT"))
    ap.add_argument("--label", default="run")
    args = ap.parse_args()
    project = pathlib.Path(args.project).resolve()
    tasks = tomllib.loads((HERE / "tasks.toml").read_text())["task"]
    corpus = sum(tokens(p.read_text(errors="replace")) for p in source_files(project))

    print("building jk's graph …", file=sys.stderr)
    run(["jk", "graph", "--no-ansi"], project)
    gfy = pathlib.Path(args.graphify).expanduser() if args.graphify else None
    if gfy:
        print("building graphify's graph …", file=sys.stderr)
        run(["uv", "run", "--project", str(gfy), "graphify", "update", str(project), "--force"], project)

    rows = []
    for task in tasks:
        row = {"task": task["id"], "grep": grep_method(task, project), "jk": jk_method(task, project)}
        if gfy:
            row["graphify"] = graphify_method(task, project, gfy)
        rows.append(row)
        print(task["id"], {k: (v["find"], v["hold"]) for k, v in row.items() if k != "task"}, file=sys.stderr)

    date = datetime.date.today().isoformat()
    meta = {"date": date, "label": args.label, "corpus_tokens": corpus,
            "jk": run(["jk", "--version"], project).strip().splitlines()[0] if True else ""}
    (HERE / "results").mkdir(exist_ok=True)
    with (HERE / "results" / f"{date}.jsonl").open("a") as f:
        f.write(json.dumps({"meta": meta, "rows": rows}) + "\n")
    write_results(meta, rows, gfy is not None)


def write_results(meta, rows, with_graphify):
    methods = ["grep"] + (["graphify"] if with_graphify else []) + ["jk"]
    lines = [
        "# agent-graph — tokens to the answer",
        "",
        f"Run {meta['date']} ({meta['label']}), {meta['jk']}. The project's searchable text is "
        f"~{meta['corpus_tokens']:,} tokens. Each cell is **find / hold**: tokens read until an answer "
        "file is named, and until its code has been read. Lower is better; ✓ means the method's own "
        "output named the answer file, ✗ that the agent had to open it anyway (counted).",
        "",
        "| Task | " + " | ".join(methods) + " |",
        "|---|" + "---|" * len(methods),
    ]
    find = {m: 0 for m in methods}
    hold = {m: 0 for m in methods}
    found = {m: 0 for m in methods}
    for r in rows:
        cells = []
        for m in methods:
            v = r[m]
            find[m] += v["find"]
            hold[m] += v["hold"]
            found[m] += v["found"]
            cells.append(f"{v['find']:,} / {v['hold']:,} {'✓' if v['found'] else '✗'}")
        lines.append(f"| {r['task']} | " + " | ".join(cells) + " |")
    lines.append("| **total** | " + " | ".join(
        f"**{find[m]:,} / {hold[m]:,}** ({found[m]}/{len(rows)})" for m in methods) + " |")
    lines += [
        "",
        "## Ratchet",
        "",
        f"A later run must not raise jk's totals above **{find['jk']:,}** (find) or **{hold['jk']:,}** "
        f"(hold) tokens, or find fewer than **{found['jk']}** of {len(rows)} answers in jk's own output.",
        "",
    ]
    (HERE / "RESULTS.md").write_text("\n".join(lines))
    print("\n".join(lines))


if __name__ == "__main__":
    main()

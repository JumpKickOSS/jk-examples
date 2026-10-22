# agent-graph — what an agent reads before it finds the code

A coding agent pays for every token it reads. This benchmark asks ten navigation questions about
`06-production/hotel-reservations` ("where is the weekend uplift applied", "how is a double booking
prevented", …) and counts, for each way of finding code, two things: **find**, the tokens read
until an answer file is named, and **hold**, until the answer's code has been read.

| Method | Find | Hold |
|---|---|---|
| **grep** | `grep -rni` for the task's words, then whole files by hit count until one is an answer file | the same: it read the file to find it |
| **graphify** | `graphify query "<question>"` over graphify's graph of the tree | plus the answer file opened whole (graphify names a file and line; it has no reader) |
| **jk** | `jk graph query "<question>"` | plus `jk graph read <Type> -w structure` (declarations, no bodies), the step jk's agent guide prescribes instead of opening the file |

A graph method that does not name an answer file opens it whole, in both counts. Tokens are
characters ÷ 4 for every method. `tasks.toml` holds the questions, grep words and answer files.

```sh
bench/agent-graph/run.py                                   # grep and jk
bench/agent-graph/run.py --graphify ~/src/oss/graphify     # and graphify, from its checkout (uv)
```

Each run appends a row to `results/<date>.jsonl` and regenerates [RESULTS.md](RESULTS.md), whose
last section is the ratchet a later run must hold: neither of jk's totals may rise, and it may not
find fewer answers in its own output.

## What this does and does not measure

It measures retrieval: tokens to the right file, with each tool's default budget. It does not run
an agent end to end; an agent's reasoning and edits cost the same whichever tool found the file.
The grep column is the habit the guide replaces, not a strawman: it opens files the way an agent
without a graph does. Questions are phrased the way a person asks, not with the code's names —
which is what `[graph.aliases]` in the project's `jk.toml` is for.

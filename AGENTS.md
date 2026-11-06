# AGENTS.md

<!-- BEGIN:jumpkick -->
## JumpKick

This project builds with **JumpKick** (`jk`), not Maven or Gradle; `jk skill <topic>` has
details. jk rewrites this section; put project rules outside the markers.

- Loop: `jk -a test` (MCP `run(kind=test)`) → edit what the verdict names → rerun. Ignore the
  terminal and `target/jk-results.md`.
- Before handing work back: `jk guard` (MCP `run(kind=guard)`).
- Who calls `T.m`, or rename it: MCP `callers` or `rename` (`jk graph callers|rename`), first,
  with `T.m` as the task names it, before opening or searching a file. The answers are compiled
  facts and final: do not grep or re-read to check them.
- `jk add g:a` / `jk remove g:a` (MCP `deps`) edit `jk.toml` and relock `jk-lock.toml`.
- Never add `pom.xml` or Gradle files, run `mvn` or `./gradlew`, or edit
  `jk-guards-baseline.toml` (`jk guard explain <id>` says what to do).
- `node = N` in `jk.toml` pins Node.js: `jk node exec -- <cmd>`, never a host `node` or `npm`.
<!-- END:jumpkick -->

## This repository

jk-examples is about 50 self-contained JumpKick projects ("scenarios") that teach idiomatic jk
and serve as jk's out-of-tree acceptance suite. `README.md` lists every scenario and the
conventions they all follow; read it before adding or reshaping one.

- The root is not a jk project. Run jk inside one scenario, the directory holding
  `scenario.toml`. Each has its own `AGENTS.md` (read it), `jk.toml`, committed `jk-lock.toml` and
  `jk-guards.toml`. Scenarios never depend on one another.
- A scenario's check is the `check` list in its `scenario.toml`: `scripts/scenarios.py check <dir>`
  runs it. After adding, moving or renaming a scenario, `scripts/scenarios.py lint` must pass: it
  wants a manifest, a README row, and the agent files below.
- Conventions: bare identity keys at the top of `jk.toml`, `java = 25` (Android: 21), exact pins
  that `jk update` moves, a lock that is never hand-edited, a clean `jk guard`, `[image]` on every
  runnable service. A README names no version `jk.toml` or `jk-lock.toml` holds (a TOML snippet
  elides it as `"…"`), so `jk update` never leaves one behind; change the README with the
  behaviour it describes.
- Build scripts live in a scenario's `.jk/scripts/`. jk's code graph is a build artifact
  (`target/graph/` and jk's action cache), never in the tree.
- `ports/netty` and `04-clients/nowinandroid` are pinned upstream checkouts: edit `overlay/`, run
  `./setup.sh`, build in `checkout/` (gitignored). `bench/` holds harnesses and results, not
  scenarios.
- A new scenario starts from `jk new -t …` when a template fits. Then `jk agents <dir>` and commit
  `AGENTS.md`, `CLAUDE.md`, `.mcp.json` and `.claude/settings.json`; delete anything else it writes
  (`.gemini/`, `.agents/`, `.cursor/`, `.vscode/mcp.json`). Scenario-specific agent rules go below
  jk's section, outside the markers.

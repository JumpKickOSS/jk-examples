# AGENTS.md

<!-- BEGIN:jumpkick -->
## JumpKick

This repository groups self-contained **JumpKick** (`jk`) workspaces: `[workspaces] members` in
`jk.toml` lists them. jk rewrites this section; put repository rules outside the markers.

- Work inside one member (`cd <member>`): its own `AGENTS.md` and `jk.toml` apply, and it builds
  the same with or without this root.
- From here `jk build`, `jk test`, `jk install`, `jk guard`, `jk format`, `jk explain` and
  `jk clean` run over every member in dependency order; `-m <member>` runs one and what it
  depends on. A member left out by default prints why; `-p <profile>` adds it back.
- `jk release` and `jk publish` run in the member being released; its tag is
  `<artifact>-v<version>`.
- Never add `pom.xml` or Gradle files, or run `mvn` or `./gradlew`.
<!-- END:jumpkick -->

## This repository

jk-examples is about 50 self-contained JumpKick projects ("scenarios") that teach idiomatic jk
and serve as jk's out-of-tree acceptance suite. `README.md` lists every scenario and the
conventions they all follow; read it before adding or reshaping one.

- The root `jk.toml` only groups the scenarios (`[workspaces] members`): `jk build` there builds
  each in turn, as inside it. Work inside one scenario, the directory holding `scenario.toml`.
  Each has its own `AGENTS.md` (read it), `jk.toml`, committed `jk-lock.toml` and
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

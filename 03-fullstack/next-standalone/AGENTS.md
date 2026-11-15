# AGENTS.md

<!-- BEGIN:jumpkick -->
## JumpKick

This project builds with **JumpKick** (`jk`), not Maven or Gradle: `jk` MCP tools first, the CLI as
fallback. jk rewrites this section; put project rules outside the markers.

| To | MCP | CLI |
|---|---|---|
| test, fix what the verdict names, rerun | `run()` | `jk -a test` |
| one class / one suite | `run(classes=[C])` / `run(suites=[s])` | `-S C` / `-s s` |
| build | `run(kind=build)` | `jk -a build` |
| every error of the last run | `diagnostics()` | `jk results -A` |
| add / remove / pin a dependency | `deps` | `jk add` / `jk remove` |
| who calls `T.m` / rename it | `callers` / `rename` | `jk graph callers\|rename` |
| what a build would run | | `jk explain` |
| finish: tests + guards | `run(kind=gate)` | `jk -a test -g` |
| anything else | `skill(topic=…)` | `jk skill <topic>` |

- Done when the gate is OK; `· up to date` means nothing was left to run. A `W` saying an input
  moved: rerun. A guard rule id: `jk guard explain <id>`.
- `callers` and `rename` answers are compiled facts and final: do not grep or re-read to check.
- Never add `pom.xml` or Gradle files, run `mvn` or `./gradlew`, or edit `jk-guards-baseline.toml`.
- `node = N` in `jk.toml` pins Node.js: `jk node exec -- <cmd>`, never a host `node` or `npm`.
<!-- END:jumpkick -->

<!-- BEGIN:nextjs-agent-rules -->

## This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may all differ from your training data. Read the relevant guide in `node_modules/next/dist/docs/` (resolved from this file's directory; in monorepos the `next` package may not be visible from the repo root) before writing any code. Heed deprecation notices.

This block is written and re-added by `next dev` — verify at `node_modules/next/dist/server/lib/generate-agent-files.js`. Removing it from a diff only re-creates the uncommitted change; committing it with your work keeps the tree clean.

<!-- END:nextjs-agent-rules -->

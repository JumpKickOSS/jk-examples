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

<!-- BEGIN:nextjs-agent-rules -->

## This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may all differ from your training data. Read the relevant guide in `node_modules/next/dist/docs/` (resolved from this file's directory; in monorepos the `next` package may not be visible from the repo root) before writing any code. Heed deprecation notices.

This block is written and re-added by `next dev` — verify at `node_modules/next/dist/server/lib/generate-agent-files.js`. Removing it from a diff only re-creates the uncommitted change; committing it with your work keeps the tree clean.

<!-- END:nextjs-agent-rules -->

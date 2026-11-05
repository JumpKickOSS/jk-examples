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

## This scenario

Google's Now in Android at a pinned SHA, built by jk with no Gradle and no AGP. The jk project is
`checkout/` (gitignored), made by `./setup.sh`: it clones upstream and copies `overlay/` over it.
Run jk from `checkout/`, where `setup.sh` has also wired the agent files.

- Change the build in `overlay/` (the root `jk.toml`, the 27 module `jk.toml`s, `jk-lock.toml`, the
  guard files), never only in `checkout/`: the upstream sources stay unmodified.
- `./run.sh` is the scenario's check.

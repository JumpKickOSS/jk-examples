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

Netty at a pinned tag, ported to jk. The jk project is `checkout/` (gitignored), made by
`./setup.sh`: it clones upstream and copies `overlay/` over it. Run jk from `checkout/`, where
`setup.sh` has also wired the agent files.

- Change the build in `overlay/` (the root `jk.toml`, each module's `jk.toml`, `jk-lock.toml`, the
  guard files), never only in `checkout/`: the next `./setup.sh` resets the checkout to the tag.
- `MODULE_MAP.md` maps Maven modules to jk modules; `PARITY.md` records where the port deviates.
- `./run.sh` is the scenario's check.

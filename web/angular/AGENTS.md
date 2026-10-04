# AGENTS.md

This project builds with **JumpKick** (`jk`), not Maven or Gradle. Run `jk skill` once per
session — or call the MCP tool `skill` / read `jk://skill` — before the first build.

## Never

- Do not add `pom.xml`, `build.gradle`, or `build.gradle.kts`. Do not run `mvn` or `./gradlew`.
- Do not scrape the human terminal. With `--agent` or `JK_AGENT=1`, stdout is the verdict.
  Otherwise it is drawn for people. MCP `run` returns that same verdict.
- Do not edit `jk-guards-baseline.toml`. A guard exemption is an `allow` entry with a reason,
  and that is the user's call. `jk guard explain <id>` says what to do instead.

## Files

- `jk.toml` — the manifest.
- `jk-lock.toml` — the lockfile, committed. `jk build` does not re-resolve while it is valid.
- `target/jk-results.md` — the human report. Agents read the verdict: MCP `run`, or `jk --agent`.
- `node = 24` in a module's `jk.toml` pins its Node.js; jk provisions it and locks the exact
  release. Use `jk node exec -- <cmd>` or `jk node run <script>`, never a host `node` or `npm`.

## Loop

`jk test` (or `run(kind=test)`) → read the verdict → edit → `jk format` → `jk test`. Default
`jk test` is the unit suite. Do not pass `--all` as a habit.

## MCP

The engine serves MCP on loopback. `jk engine status --output json` includes `mcpUrl`. Pass
`dir` on the first call; that binds the connection. Tools: `run`, `diagnostics`, `deps`,
`why`, `skill`. `deps` edits `jk.toml` and relocks. Other tools: `tools/list` with
`extended` true.

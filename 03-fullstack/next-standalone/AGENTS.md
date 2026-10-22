# AGENTS.md

<!-- BEGIN:jumpkick -->
## JumpKick

This project builds with **JumpKick** (`jk`), not Maven or Gradle. Run `jk skill` once per
session — or call the MCP tool `skill` / read `jk://skill` — before the first build. jk
keeps this section current (`jk skill agents`); put project rules outside the markers.

### Never

- Do not add `pom.xml`, `build.gradle`, or `build.gradle.kts`. Do not run `mvn` or `./gradlew`.
- Do not scrape the human terminal. With `-a` (`--agent`) or `JK_AGENT=1`, stdout is the verdict.
  Otherwise it is drawn for people. MCP `run` returns that same verdict.
- Do not edit `jk-guards-baseline.toml`. A guard exemption is an `allow` entry with a reason,
  and that is the user's call. `jk guard explain <id>` says what to do instead.

### Files

- `jk.toml` — the manifest.
- `jk-lock.toml` — the lockfile, committed. `jk build` does not re-resolve while it is valid.
- `target/jk-results.md` — the human report. Agents read the verdict: MCP `run`, or `jk -a`.
- `node = 24` in a module's `jk.toml` pins its Node.js; jk provisions it and locks the exact
  release. Use `jk node exec -- <cmd>` or `jk node run <script>`, never a host `node` or `npm`.

### Finding code

`jk graph query "<question>"` answers where something is and how it connects, with `file:line`
for each hit, within a token budget. `jk graph read <Type.method>` prints one declaration, not
the whole file; `jk graph rules <file>` lists the guard rules that apply before you edit it.
Prefer these to grep and whole-file reads.

### Loop

`jk test` (or `run(kind=test)`) → read the verdict → edit → `jk format` → `jk test`. Default
`jk test` is the unit suite. `jk test -f` names the tests an edit touched. Do not pass `--all`
as a habit. `jk guard` before handing work back.

### MCP

The engine serves MCP on loopback. `jk engine status -O jsonl` includes `mcpUrl`. Pass
`dir` on the first call; that binds the connection. Tools: `run`, `diagnostics`, `deps`,
`why`, `skill`. `deps` edits `jk.toml` and relocks. Other tools: `tools/list` with
`extended` true.
<!-- END:jumpkick -->

<!-- BEGIN:nextjs-agent-rules -->

# This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may all differ from your training data. Read the relevant guide in `node_modules/next/dist/docs/` (resolved from this file's directory; in monorepos the `next` package may not be visible from the repo root) before writing any code. Heed deprecation notices.

This block is written and re-added by `next dev` — verify at `node_modules/next/dist/server/lib/generate-agent-files.js`. Removing it from a diff only re-creates the uncommitted change; committing it with your work keeps the tree clean.

<!-- END:nextjs-agent-rules -->

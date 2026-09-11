# AGENTS.md

This project builds with **JumpKick** (`jk`), not Maven or Gradle. Run `jk manual` once per
session — or call the MCP tool `jk_manual` / read `jk://manual` — before your first build.
It is short and it is the playbook.

## Never

- Do not add `pom.xml`, `build.gradle` or `build.gradle.kts`; do not run `mvn` or `./gradlew`.
- Do not parse the terminal output. It is drawn for people; the channels below are for you.
- Do not edit `jk-guards-baseline.toml` and do not add suppression comments (there is no such
  syntax). A guard exemption is an `allow` entry with a reason, and that is the user's call.

## Files

- `jk.toml` — the manifest: one at the workspace root, one per module.
- `jk-lock.toml` — the lockfile, committed. `jk build` never re-resolves while it is valid;
  `jk lock` / `jk update` move it.
- `target/jk-results.md` — the last run's report: status, failures with stack tails, tests,
  guards, deliverables. Read or grep it after every build or test; it is the same markdown
  `jk results` prints, without a process.
- `target/jk-tests-affected.md` — the ranked selection `jk test --affected` ran.

## Exit codes

`0` success · `1` the build ran and failed (read the report) · `2` bad project or argument ·
`64` wrong command line · `70` internal error · `130` interrupted.

## Loop

`jk test` → read `target/jk-results.md` → edit → `jk format` → `jk test`. Default `jk test` is
the unit tier; climb with `--suite integration` (or a profile) when the change needs that rung,
never `--all` by habit. `jk build` packages, `jk dev` runs the app with reload, `jk explain`
says what a build would redo and why, `jk guard` runs every house-rule lane.

## MCP (prefer it when the engine is up)

The resident engine serves MCP over loopback HTTP with a bearer token. `jk engine status
--output json` gives `mcpUrl`; the token file is `~/.jk/state/engine/<key>.http-token`.
Register it once (Claude Code: `claude mcp add --transport http jk <mcpUrl> --header
"Authorization: Bearer <token>"`), then `jk_bind` the project, `jk_run kind=test|build|guard`,
and read `jk_results` / `jk_diagnostics` — structured, no terminal to scrape.

## Machine output and CI

`--output json` (or `JK_OUTPUT=json`) streams one event per line and ends with
`workspace-finish` on every outcome. Headless runs: `JK_AOT_TRAIN=off JK_NO_ANSI=1`.

## Guards

`jk-guards.toml` holds the house rules. A failure whose code is a rule id is a guard:
`jk guard explain <id>` says what to do instead. `jk test --guard` is the pre-commit bar.

## For the user

`jk web` opens the dashboard — builds, phases, the module and dependency graph, live events.
`jk web --no-open` prints its URL when you want to hand it over rather than open it.

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

## This project

Kestrel & Vine Hotels: a Spring Boot 4 + Angular 22 workspace. Read `README.md` once for the shape.

### Where things go

- `backend/contract` — HTTP DTOs (records + Bean Validation). Nothing else.
- `backend/core` — domain rules in plain Java. No Spring, jOOQ or Jakarta (`core-is-plain-java`).
- `backend/service` — services behind interfaces in `dev.jkexamples.hotel.service`, implementations
  and jOOQ queries in `…service.internal`, migrations in `src/main/resources/db/migration`, seed in
  `db/seed`. MapStruct mappers map domain → DTO here.
- `backend/rest` — controllers over the service interfaces, security, `ApiErrors`. A controller
  returns contract types only (`dto-only-over-http`).
- `frontend` — Angular. API calls in `src/app/api/hotel-api.ts`; types in `models.ts` mirror
  `backend/contract` one for one — change both together.

### Rules

- Every Java package has a `package-info.java` with `@NullMarked`. NullAway failures are errors: fix
  the nullness, do not suppress.
- Schema changes are a new `V<n>__*.sql` in `db/migration`, never an edit to an applied one. jOOQ
  regenerates on the next build. PostgreSQL-only DDL goes between `/* [jooq ignore start] */` and
  `/* [jooq ignore stop] */`; indexed strings are `varchar(n)`.
- Money is minor units (`Money`, `MoneyView`); dates are ISO `LocalDate`s; never floats.
- Put a test in the cheapest tier that can fail for your reason: `src/test` (no Docker),
  `src/integration` (Testcontainers), `src/e2e` (Playwright). Run that tier, then `jk guard`.
- Do not add Lombok, a second logging facade, or a floating version.

### Fast answers

- `jk graph query "where is the weekend uplift applied"` before grep; `jk graph read Type.method`
  before opening a file; `jk graph rules <file>` before editing one.
- `jk test --class <fqcn>` for one class; `jk test -f` for the tests an edit touches.
- Demo staff: `desk` / `kestrel-desk`.

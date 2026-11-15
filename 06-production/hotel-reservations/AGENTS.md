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
| finish: format, tests + guards | `run(kind=gate)` | `jk -a test -g` |
| anything else | `skill(topic=…)` | `jk skill <topic>` |

- Done when the gate is OK; `· up to date` means nothing was left to run. A `W` saying an input
  moved: rerun. A guard rule id: `jk guard explain <id>`.
- `callers` and `rename` answers are compiled facts and final: do not grep or re-read to check.
- Never add `pom.xml` or Gradle files, run `mvn` or `./gradlew`, or edit `jk-guards-baseline.toml`.
- `node = N` in `jk.toml` pins Node.js: `jk node exec -- <cmd>`, never a host `node` or `npm`.
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

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

# Kestrel & Vine Hotels — a production-shaped JumpKick application

A hotel reservation system for a fictional chain of five hotels, built the way a real service is
built and held to the same standards. Guests search, book, find and cancel stays; front-desk staff
see the day's arrivals, departures and occupancy, and check guests in and out.

It is the last stop on jk-examples' learning path: every JumpKick feature a production team leans
on, in one application small enough to read in an afternoon.

| | |
|---|---|
| **Backend** | Spring Boot 4.1 on Java 25, virtual threads, Spring AOT, four modules under `backend/` |
| **Database** | PostgreSQL 18 (`uuidv7()` keys, a no-double-booking exclusion constraint), Flyway migrations and seed |
| **Data access** | jOOQ, generated from the migration scripts at build time — no database needed to compile |
| **Mapping** | MapStruct, generated at compile time; only DTOs cross HTTP |
| **Nullness** | JSpecify `@NullMarked` everywhere, NullAway as a compile error |
| **Architecture** | jk guards in the build (module layers, DTO-only controllers, jOOQ confined) + ArchUnit tests |
| **Frontend** | Angular 22 (signals, zoneless, standalone) with a customised Material 3 theme, under `frontend/` |
| **Tests** | unit (seconds) · integration (Testcontainers PostgreSQL) · end-to-end (Playwright for Java) |
| **Shipping** | an OCI image with a trained JEP 514 AOT cache, non-root, read-only filesystem ready |

## Setup

You need JumpKick and, for the database, Docker or Podman. jk installs the JDK and Node.js the
lockfile pins; nothing else.

```sh
# macOS / Linux
curl -fsSL https://jumpkick.build/install.sh | bash
```

```powershell
# Windows (PowerShell)
irm https://jumpkick.build/install.ps1 | iex
```

Podman users: Testcontainers reaches Podman through its socket. jk hands `DOCKER_HOST` and
Testcontainers' variables from your shell to the test JVMs, so this is enough:

```sh
export DOCKER_HOST="unix://$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}')"
export TESTCONTAINERS_RYUK_DISABLED=true
```

## Run it

```sh
jk dev            # PostgreSQL (dev/db.sh), the app on :8080, Angular on :4200 with /api proxied
```

Open <http://localhost:4200>. Edit a Java file and jk restarts the app; edit a component and the
page reloads. The first start migrates and seeds the database: five hotels, about 150 rooms, a few
hundred guests and six months of reservations around today. The front desk is at
<http://localhost:4200/desk> — sign in as `desk` (all hotels) or `harbor` (one hotel), password
`kestrel-desk`.

## Build, test, ship

```sh
jk build                 # compile, generate (jOOQ, MapStruct, Spring AOT), unit tests, guards, Angular build + Vitest
jk test                  # the unit tier: domain rules, mappers, web slices, ArchUnit — seconds, no Docker
jk test -g               # + the integration tier: the real schema on PostgreSQL 18 via Testcontainers
jk test -s e2e           # the end-to-end tier: Playwright drives Chromium through booking and the front desk
jk guard                 # the architecture rules alone
jk image                 # the OCI image, AOT cache trained, loaded into docker or podman
docker compose --profile app up   # the image against PostgreSQL 18 (podman compose works the same)
```

## How it is put together

```text
hotel-reservations/
  jk.toml                   # workspace: modules, Boot's BOM, shared versions, dev database, graph aliases
  jk-guards.toml            # the architecture, as rules the build enforces
  AGENTS.md                 # how a coding agent works here (jk's section + this project's rules)
  compose.yaml  dev/db.sh   # PostgreSQL 18 for dev; the image + database for "production"
  backend/
    contract/   # DTO records + Bean Validation — the HTTP contract, nothing else
    core/       # the domain: Money, StayDates, PricingPolicy, ReservationStatus — plain Java
    service/    # Spring services, jOOQ queries, Flyway migrations + seed, MapStruct mappers
    rest/       # controllers, security, error handling, the Boot application and its image
  frontend/     # Angular 22 + Material 3: search, hotel pages, booking, reservations, front desk
```

```text
rest ──▶ contract ◀── service ──▶ core
  └────▶ service                      rest ──▶ frontend (the built SPA, served from static/)
```

`rest` depends on `contract` and `service` only, so a controller *cannot* compile against a domain
type or a jOOQ record: "only DTOs over HTTP" holds at the module graph, and the `dto-only-over-http`
guard and an ArchUnit rule say it again at class level. The service API (`HotelCatalog`,
`Bookings`, `FrontDesk`) takes and returns contract types; MapStruct maps inside `service`.

### The database decides double bookings

```sql
ALTER TABLE reservation ADD CONSTRAINT reservation_no_double_booking
    EXCLUDE USING gist (room_id WITH =, daterange(check_in, check_out, '[)') WITH &&)
    WHERE (status <> 'CANCELLED');
```

The booking service claims a free room with `FOR UPDATE SKIP LOCKED`, so concurrent bookings take
different rooms; the constraint is the backstop. `HotelApiIntegrationTest` races more guests than
there are rooms and checks that exactly the right number win.

### jOOQ without a database

`[jooq]` in `backend/service/jk.toml` generates the table classes from `db/migration/*.sql` through
jOOQ's DDL interpreter: a fresh clone compiles with no PostgreSQL running, and a migration edit
regenerates. The interpreter runs on H2, so PostgreSQL-only DDL (the extension, `uuidv7()`
defaults, the exclusion constraint) sits between `[jooq ignore]` markers, and indexed strings are
`varchar(n)` rather than `text`. The seed lives apart in `db/seed`, which Flyway also applies.

### Tests in three tiers

| Tier | Command | Where | Runs |
|---|---|---|---|
| unit | `jk test` | `src/test` | pricing and stay rules, the MapStruct mapper, `@WebMvcTest` slices, ArchUnit, Vitest |
| integration | `jk test -g` | `src/integration` | migrations and seed on PostgreSQL 18, the exclusion constraint, the whole API over HTTP, the booking race, staff sign-in |
| e2e | `jk test -s e2e` | `src/e2e` | Playwright for Java: a guest books, finds and cancels; the desk signs in and checks a guest in |

The tier is the directory. Guards keep them honest: a test that touches Testcontainers must live in
`src/integration` (or the e2e tier), and Playwright only in `src/e2e`.

### Guards

| Rule | Protects |
|---|---|
| `module-layers` | rest → {contract, service, frontend}; service → {contract, core}; nothing else |
| `dto-only-over-http` | a `@RestController` depends on no domain, jOOQ or service-internal type |
| `core-is-plain-java` | no Spring, jOOQ or Jakarta in the domain |
| `jooq-stays-in-service` | SQL lives in the service's repositories |
| `null-marked` | every backend package is `@NullMarked` (jOOQ's generated package excepted) |
| `pinned-and-plain` | no Lombok, no floating versions |
| `containers-are-integration`, `browsers-are-e2e` | expensive tests stay in their tiers |
| packs `spring`, `monorepo` | Jakarta not javax, no `System.out`, one version per artifact, … |

`jk guard explain <id>` prints any rule with its reason and what to do instead.

A rule that never fires on this codebase can't show it would. `core-is-plain-java` has nothing to
catch in a clean `backend/core`, so `guard-fixtures/core-is-plain-java/` proves it bites: `Bad.java`
uses a Spring annotation (a stub `Component` stands in for Spring) and must be flagged, and
`Ok.java` must not be. `jk guard` runs the proof; `jk guard test` runs only the proofs.

### Production notes

- **Startup.** Spring AOT (`[spring-boot] aot = true`) generates bean definitions at build time,
  and the image carries a JEP 514 AOT cache trained by starting the app inside the image's JVM.
  The training run has no database, so `[image.training-env]` turns migrations off and exits once
  the context has refreshed; the image itself never sees those settings. Migrations are switched by
  `hotel.migrate-on-start` (read at run time) rather than `spring.flyway.enabled`, because Spring AOT
  fixes property conditions when it generates the context.
- **Threads.** `spring.threads.virtual.enabled`: a virtual thread per request; Hikari's pool is the
  only limit on concurrent queries.
- **Security.** Session sign-in for staff, CSRF via `csrf.spa()` (Angular sends the token on its
  own), a strict content security policy (fonts and icons are self-hosted), rate-limited sign-in,
  and lookups by POST so a last name never lands in a URL or an access log.
- **Errors** are RFC 9457 problem details with stable `type` URIs the frontend switches on.
- **Operations.** Actuator liveness and readiness probes, Prometheus metrics, `/actuator/info` with
  the git commit (`[build-info]`), the SBOM endpoint, ECS JSON logs, graceful shutdown.
- **What a real deployment adds:** TLS at the edge, secrets from a manager instead of environment
  defaults, database backups, and a shared rate limiter in front of several instances.

## Working with a coding agent

`AGENTS.md` starts with JumpKick's own section (jk keeps it current) and then this project's rules.
The workspace has a code graph: `jk graph query "how is a stay priced"` answers with `file:line`
references within a token budget, `jk graph read PricingPolicy.quote` prints one method, and
`[graph.aliases]` in `jk.toml` teaches it the hotel's vocabulary ("booking" is a `Reservation`).

## Credits

Every photograph is generated for this example (Grok Imagine) and depicts no real place. The logo is
drawn for it. Kestrel & Vine is fictional; any resemblance to a real hotel is coincidental.

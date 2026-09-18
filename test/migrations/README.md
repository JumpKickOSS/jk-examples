# test/migrations — Flyway and Liquibase against a PostgreSQL Testcontainer

Flyway and Liquibase migrate a schema; the build has nothing to do with it. The migrations are
resources of the module — the Flyway scripts under `src/main/resources/db/migration`, the Liquibase
change log under `src/main/resources/db/changelog` — the application applies both at startup
(`Migrations.apply`: `flyway migrate`, then `liquibase update` through Liquibase's `CommandScope`),
and the tests prove them on two databases, one per tier:

| Tier | Test | Database |
|---|---|---|
| `jk test` | `MigrationsH2Test` | an in-memory H2, no Docker |
| `jk test --profile integration` | `MigrationsPostgresTest` | a `postgres:18-alpine` Testcontainer |

```toml
[dependencies]
flyway-core       = "13.7.0"
flyway-postgresql = "13.7.0"     # Flyway's PostgreSQL support is its own module
liquibase-core    = "5.0.4"
postgresql        = "42.7.13"
h2                = "2.5.250"

[test-dependencies]
junit-jupiter             = "6.1.3"
testcontainers-postgresql = "1.21.4"
slf4j-simple              = "2.0.17"
```

Nothing about migrations is in `jk.toml` beyond those libraries. The same scripts feed
[`codegen/jooq-shop`](../../codegen/jooq-shop/), where the `[jooq]` preset generates from them
without any database at all.

```sh
jk build                        # compiles, runs the unit tier on H2, packages target/migrations-1.0.0.jar
jk test                         # Passed 2 tests
jk test --profile integration   # Passed 1 test — starts the Postgres container, migrates it twice
jk run                          # flyway: 2 applied [1, 2] · liquibase: 1 change set(s) · tables: audit_log, customers, databasechangelog, databasechangeloglock, flyway_schema_history, orders
jk run                          # flyway: 0 applied [] · liquibase: 1 change set(s) · tables: …   (the H2 file under target/ is already migrated)
jk guard
```

`jk run` migrates the database named by `JDBC_URL` (`DB_USER`, `DB_PASSWORD`), an H2 file under
`target/` when nothing is set; put a Postgres URL in the project's `.env` and every process jk
starts in the project inherits it.

## The integration tier needs a container runtime

Testcontainers talks to Docker's API. Docker Desktop and Docker Engine need no configuration;
rootless podman works through its socket with Ryuk off, since that helper wants a privileged
container:

```properties
# ~/.testcontainers.properties
tc.host=unix:///run/user/1000/podman/podman.sock
testcontainers.docker.socket.override=unix:///run/user/1000/podman/podman.sock
ryuk.disabled=true
```

Without a runtime `jk test --profile integration` fails on the container start; the unit tier is
unaffected.

## The tools as commands

Outside the application — a shared development database, an `info`, a `repair`, a `validate` in
CI — each tool is a command against a database, run as a pinned [tool](https://github.com/JumpKickOSS/jk/blob/main/docs/user/tools.md)
with the JDBC driver beside it and the URL through the tool's own variables (`FLYWAY_URL`,
`LIQUIBASE_COMMAND_URL`) or on the command line. Against a Postgres on `localhost:5432` with
`app`/`app`:

```sh
jk tool run org.flywaydb:flyway-commandline:12.9.0 --main org.flywaydb.commandline.Main \
  --with org.postgresql:postgresql:42.7.13 -- \
  -url=jdbc:postgresql://localhost:5432/app -user=app -password=app \
  -locations=filesystem:src/main/resources/db/migration migrate        # then: info | validate | repair

jk tool run org.liquibase:liquibase-core:5.0.4 --main liquibase.integration.commandline.LiquibaseCommandLine \
  --with org.postgresql:postgresql:42.7.13 --with info.picocli:picocli:4.7.7 -- \
  --url=jdbc:postgresql://localhost:5432/app --username=app --password=app \
  --changelog-file=db/changelog/db.changelog-master.yaml --search-path=src/main/resources update
```

`liquibase-core`'s manifest names a launcher that wants a `LIQUIBASE_HOME`, so the command-line
class is named, and picocli, optional in its POM, rides along. The Flyway command line is pinned
one major behind the library: `flyway-commandline` 13.x declares a runtime dependency
(`flyway-database-ignite`) that Maven Central does not carry, so its closure does not resolve
from Central; 12.9.0's does, and it applies these scripts unchanged.

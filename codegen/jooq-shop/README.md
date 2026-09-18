# codegen/jooq-shop — jOOQ classes from the DDL scripts

The `[jooq]` preset reads the schema from the module's own migrations: the Flyway scripts under
`src/main/resources/db/migration` are applied to an in-memory database in version order
(`V1__customers.sql`, `V2__orders.sql`) through jOOQ's `DDLDatabase`, and the resulting tables,
keys and references generate `com.acme.jooq.Tables`, `Customers` and `Orders`, which join the
compile. No database runs for the build, and the scripts are the step's cache key. The generator
is not in `[dependencies]`: the preset fetches `org.jooq:jooq-codegen` at its pinned release into
the store; `jooq = "3.21.8"` is the runtime the generated classes read, on the generator's number.

```toml
[jooq]
excludes = "flyway_schema_history"    # Flyway's history table is a table too, to DDLDatabase

[dependencies]
jooq = "3.21.8"
```

The names are upper case because DDLDatabase applies the scripts to H2, which folds an unquoted
identifier that way; `name-case = "lower"` keeps them lower for a PostgreSQL target.

The step is `generate-jooq` (`jk explain` shows it). A migration edit re-runs it and the compile;
unchanged scripts are a cache hit, so a second `jk build` is up to date. A live database is
opt-in (`jdbc-url`, `driver`), and running one is the module's own affair — see
[`test/migrations`](../../test/migrations/) for the tools that apply these same scripts to a real
Postgres.

```sh
jk build     # applies V1 and V2 in memory, generates com.acme.jooq.*, compiles, tests, packages target/jooq-shop-1.0.0.jar
jk run       # select "CUSTOMERS"."EMAIL", "ORDERS"."TOTAL" from "ORDERS" join "CUSTOMERS" on "ORDERS"."CUSTOMER_ID" = "CUSTOMERS"."ID"
jk test      # the tables carry the scripts' names, column types and the orders → customers reference
jk guard
```

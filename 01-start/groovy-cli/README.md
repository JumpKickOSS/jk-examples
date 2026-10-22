# 01-start/groovy-cli — Groovy and Java in one module, tested with Spock

`tally` sums a CSV of expenses by category. It is a Groovy CLI with a Java half, the shape most
Groovy codebases have:

| File | Language | Role |
|------|----------|------|
| `Expense.java` | Java | A record: one ledger line |
| `Ledger.groovy` | Groovy (`@CompileStatic`) | Parses the CSV, filters by date, sums with the GDK |
| `Report.java` | Java | Renders a `Ledger`: **Java calling Groovy** |
| `Main.groovy` | Groovy | `CliBuilder` (picocli) options, table or `JsonOutput` |
| `LedgerSpec.groovy` | Spock | Specs with a `where:` data table |

`jk.toml` declares `groovy = "6.0.0"` and nothing else to make it a Groovy module: `.groovy` files
under `src/main/groovy` (or beside the Java files) are enough. Groovy and Java compile **jointly**,
so each side may reference the other. Spock is a test dependency like any other and runs on the
JUnit Platform under `jk test`.

```sh
jk build                                    # compile both languages, run the specs
jk run -- sample/expenses.csv               # table
jk run -- --json --from 2026-10-10 sample/expenses.csv
jk test                                     # the unit tier
jk guard
```

## Teaches

- A Groovy module is `groovy = "<version>"` plus sources; there is no Groovy plugin to apply.
- Joint compilation in both directions, with no source-set ceremony.
- Spock's `spock-core` variant matches the Groovy line (`2.5-groovy-6.0` for Groovy 6).

## Validates

- The `groovy-compiler` worker in joint mode: Java → Groovy and Groovy → Java references compile.
- Spock specs are discovered and reported under their spec class in `target/jk-results.md` and
  the JUnit XML.

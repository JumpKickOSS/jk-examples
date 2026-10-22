# 05-recipes/migrate/from-maven — a Maven project, imported

`ledger` sums a CSV ledger by account: commons-csv, SLF4J, JUnit 6 through its BOM, AssertJ. Its
`pom.xml` is an ordinary one. `jk.toml` is exactly what `jk import pom.xml` wrote from it, committed
unedited, and both build the same sources:

```sh
jk mvn verify                # real Maven, provisioned and checksum-verified by jk
jk build                     # jk, from jk.toml
jk run -- ledger.csv
../../../scripts/reimport-check.sh pom.xml   # import again from a clean copy; must match jk.toml
```

## What the import did

| In `pom.xml` | In `jk.toml` |
|---|---|
| `<groupId>`, `<artifactId>`, `<version>`, `<description>` | the same fields |
| `maven.compiler.release` = 25 | `java = 25` (a language level, not a JDK pin) |
| `${commons-csv.version}`, `${slf4j.version}` properties | resolved into exact pins |
| `junit-bom` imported in `<dependencyManagement>` | `[platform-dependencies]`; `junit-jupiter = "managed"` |
| compile / runtime / test scopes | `[dependencies]` / `[runtime-dependencies]` / `[test-dependencies]` |
| `maven-jar-plugin` manifest `mainClass` | `[application] main` |
| compiler, surefire and jar plugin versions | nothing: jk's own steps do that work |

`jk import` also writes a report (Tier 1 lossless, Tier 2 review, Tier 3 not imported) and jk's
section of `AGENTS.md`. Here it has one Tier 2 note: JUnit's version comes from the BOM.

## Moving over

Keep both builds while you compare them: `jk mvn` runs Maven with jk's run report, so CI can
run both. When jk is the build, delete `pom.xml`. `jk export maven` writes a POM again whenever you
need one (to publish with Maven tooling, or for an IDE that wants one).

## Teaches

- `jk import` on a typical single-module POM, and what each Maven construct becomes.
- Running Maven and jk side by side on one tree during a migration.

## Validates

- Importer fidelity: re-importing from a clean copy reproduces the committed `jk.toml`.
- `jk mvn` provisioning (checksum-verified) and its run report.

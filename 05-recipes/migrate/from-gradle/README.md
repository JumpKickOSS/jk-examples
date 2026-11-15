# 05-recipes/migrate/from-gradle — a Gradle build, imported

`wordfreq` prints a file's most frequent words: a `core` library on Guava and an `app` on
picocli, in the shape most Gradle builds have today: a Kotlin DSL, a version catalog
(`gradle/libs.versions.toml`), a `subprojects { }` block, the `java-library` and `application`
plugins, and a Java toolchain. Every `jk.toml` here is exactly what `jk import settings.gradle.kts`
wrote, committed unedited:

```sh
jk gradle build              # real Gradle, provisioned and checksum-verified by jk
jk build                     # jk, from the jk.toml files: a workspace of core and app
jk run -m app -- -n 5 README.md
../../../scripts/reimport-check.sh settings.gradle.kts   # import again from a clean copy; must match
```

## What the import did

Gradle evaluates the build in a fork, so what jk reads is the configured model, not the script
text: the `subprojects { }` block, catalog accessors and plugin defaults are already applied.

| In Gradle | In `jk.toml` |
|---|---|
| `include("core", "app")` | a root `[workspace]` with both modules |
| `libs.guava`, `libs.picocli`, … (version catalog) | exact pins |
| `platform(libs.junit.bom)` | `[platform-dependencies]`; JUnit artifacts `"managed"` |
| `api` / `implementation` / `test*` | `[dependencies]` / `[test-dependencies]` |
| `implementation(project(":core"))` | `core.workspace = true` |
| `application { mainClass }` | `[application] main` |
| `toolchain.languageVersion = 25` | `java = 25` at the root, which both modules inherit |

A toolchain with no vendor at the major the host JDK already compiles to needs no JDK pin, so the
import writes only `java = 25`, the language level, and no `jdk` line.

## Teaches

- `jk import` on a multi-project Gradle build with a version catalog.
- Running Gradle and jk side by side on one tree during a migration.

## Validates

- The Gradle import in a fork: the configured model, catalogs, platforms, project dependencies.
- Importer fidelity: re-importing from a clean copy reproduces every committed `jk.toml`.

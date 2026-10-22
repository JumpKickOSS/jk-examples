# 01-start/java-library — a library, packaged the way it is published

`money` is a small value type: an amount in one currency, arithmetic that refuses to mix
currencies, and `allocate(…)`, which splits an amount by ratios without creating or losing a cent.
It starts from `jk new -t library` and adds what a release needs:

| What | Where |
|------|-------|
| Jar, **sources jar** and **javadoc jar** — written by `jk build` because there is no `[application]` | `target/` |
| A **POM Maven Central would accept**: name, description, URL, license, developers, SCM | `[publish]` + `description` in `jk.toml` |
| `git.properties` in the jar: commit, branch, dirty flag | `[build-info]` |
| A **CycloneDX** and an **SPDX** SBOM of the runtime closure | `jk publish -S` → `target/sbom/` |
| **JSpecify** `@NullMarked` packages; the `library` guard pack checks every package is marked | `package-info.java`, `jk-guards.toml` |
| JUnit 6 with a parameterized property test (allocation sums to the original) | `src/test/java` |

```sh
jk build                 # jar, sources jar, javadoc jar, POM; runs the tests
jk publish -S -U         # dry run: assemble the bundle and write the SBOMs; uploads nothing
jk install               # into the local repository, for another project on this machine
jk guard
```

Another jk project on the same machine then depends on it like anything from Central:

```toml
[dependencies]
money = "dev.jkexamples:money:0.1.0"
```

A real release is `jk publish -c -I -f <key>` to the Central Portal, signed; see jk's
[publish docs](https://github.com/JumpKickOSS/jk/blob/main/docs/user/publish.md).

## Teaches

- A library is a project with no `[application]`; jk then writes the three jars a release carries.
- `[publish]` holds the POM metadata, so `jk.toml` stays the one place the coordinate is described.
- The SBOM comes from the lockfile's runtime rows: what you locked is what you declare you ship.

## Validates

- Sources and javadoc jars beside the main jar, `git.properties` inside it.
- The POM's metadata and dependencies; the dry-run publish bundle and both SBOM formats.
- The `library` guard pack over a `@NullMarked` API.

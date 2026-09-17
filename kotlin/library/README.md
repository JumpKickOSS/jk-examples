# kotlin/library — a published Kotlin library

The `library` template for Kotlin (`jk new -t library --lang kotlin`), as `jk new` writes it and
with the lock it produced:

| What | Where |
|------|-------|
| Sources and **Dokka** javadoc jars beside the jar — the library has no `[application]`, so `jk build` writes `kotlin-library-0.1.0-sources.jar` and `-javadoc.jar`; the javadoc jar is Dokka's javadoc-format output (`[dokka] version = "2.2.0"`, a pin `jk update` moves), the form a Maven Central release expects | `target/lib/` |
| `git.properties` in the jar — commit, branch, build time and version | `[build-info]` |
| The `library` guard pack (`no-unsafe-defaults`, `no-snapshot-versions`, `converge-versions`, `file-size`; `null-marked-packages` leaves a package of Kotlin classes alone out of scope) | `jk-guards.toml` |
| Exact pins: `kotlin = "2.4.20"`, `junit-jupiter = "6.1.3"` as a catalog short name | `jk.toml` |
| Test tiers: `[test] exclude-tags` and one `[profiles.<tag>]` per tag | `jk.toml` |

```sh
jk build         # jar + sources jar + Dokka javadoc jar under target/lib/
jk test          # the unit tier
jk guard
jk publish       # dry-run by default
```

# jk-examples

**Black-box fitness suite and adopter examples for [JumpKick](https://github.com/JumpKickOSS/jk) (`jk`).**

Each directory is a self-contained JumpKick project (or an orchestrated third-party checkout) that
exercises a real slice of the tool against real repositories and toolchains. These are not
monorepo unit tests — they exist to:

1. **Validate** ongoing product changes end-to-end (lock, resolve, compile, test, package, run)
2. **Benchmark** cold / warm / incremental builds as the engine evolves
3. **Teach** early adopters idiomatic `jk.toml`, workspaces, plugins, guards, and layout conventions

Sibling product repo: [JumpKickOSS/jk](https://github.com/JumpKickOSS/jk). Scenarios track that
repo's `main` closely; build them with a current `jk`.

## Scenarios

| Dir | Validates |
|---|---|
| `android/nowinandroid/` | **The Android north star**: Google's reference app, all 27 modules — Compose, Hilt (unmodified sources), Room auto-migrations, protobuf/datastore, kotlinx-serialization, navigation3, a contentType variant dimension, non-transitive R, demo-debug APK + signed R8 release AAB. No Gradle, no AGP. |
| `micronaut/hello-http/` | Micronaut HTTP service — the `[micronaut]` plugin at an exact platform pin, assembly fat jar, `@MicronautTest` client smoke, `[image]`. |
| `spring-boot/kitchen-sink/` | Spring Boot at an exact pin — web + data-jpa + validation + actuator, BOM auto-import, boot-jar layout, Spring AOT step, `[build-info]`, dev-scope devtools, `[image]`. |
| `spring-boot/petshop/` | Multi-module Spring Boot pet shop (domain / service / web) — workspace DI + MockMvc tests, the spring and monorepo guard packs. |
| `spring-boot/webapp/` | **A JVM backend serving a single-page front end**: a resource-only `web` module fed by Vite's `build.outDir`, the app serving `classpath:/static/` with an SPA fallback, and `[dev.sidecars]` running the Vite dev server beside `jk dev`. |
| `aot/vanilla-cli/` | Core JVM AOT (`jk build --aot-cache`) with no framework and no plugin — the everyone-gets-this path. |
| `kotlin/serialization-cli/` | Project-declared Kotlin compiler plugins (`[[kotlin-plugins]]`) via kotlinx-serialization. |
| `kotlin/ktor-petshop/` | Multi-module Ktor + **Koin** + **Exposed** + H2 pet shop — Kotlin workspace dogfood; the `domain` library's Dokka javadoc jar and `[build-info]`. |
| `kotlin/library/` | The `library` template for Kotlin as `jk new` writes it: sources and **Dokka** javadoc jars beside the jar, `git.properties` from `[build-info]`, the `library` guard pack, exact pins. |
| `protobuf/messages/` | The protobuf plugin: provisioned per-OS protoc, Java + Kotlin DSL (lite) codegen. |
| `graphql/dgs-codegen/` | GraphQL codegen as a `[generate]` recipe: DGS codegen's command line over the schema, typed types + client joining the compile, the tool closure fetched and cached. |
| `jvm/netty/` | **Full Netty Maven→jk port** (~40 modules, Mill graph parity) — black-box + benchmarks. |
| `jvm/netty-echo/` | Small Netty echo server on the published `netty-all` — lightweight harness seed. |
| `jvm/scala-mixed/` | **A mixed Java/Scala module through jk's BSP**: one Zinc session for both halves referencing each other, one `scala` build target Metals imports (compiler version and jars, `scalacOptions` / `javacOptions`), `buildTarget/compile` with a Scala diagnostic — the wire recorded by `bsp-session.py`. |
| `jvm/shrink-cli/` | The minified jar: R8 `--classfile` full mode collapsing a fat jar. |
| `jvm/variants-cli/` | Core `[variants]`: a custom dimension on a plain JVM app — per-value `extra-src` + deps, union lockfile, mandatory selection. |
| `jvm/profiles-vs-variants/` | The decision matrix as running code: a profile (how), a feature (what capability), and the build-type variant (which product) side by side. |
| `codegen/avro-events/` | The `[avro]` preset: Avro schemas compiled in the generate stage, the classes joining the compile, `string-type`, an exact runtime pin on the compiler's number. |
| `codegen/jaxb-orders/` | The `[jaxb]` preset: xjc over `src/main/xsd` into one package, the `jakarta.xml.bind` API and the JAXB runtime as the only dependencies. |
| `codegen/jooq-shop/` | The `[jooq]` preset over DDL scripts: the Flyway migrations applied in memory through `DDLDatabase`, typed tables and references generated, no database for the build. |
| `lint/checkstyle/` | `[lint] checkstyle` as a cached step after compile: findings as diagnostics with the rule id, `fail-on`, a rule set that repeats nothing `jk format` owns. |
| `lint/all-tools/` | The four-tool `[lint]` table — Checkstyle, a project PMD ruleset, SpotBugs over the classes, detekt over Kotlin — each its own cached step; runs the real tools so their report parsers see real output. |
| `test/testng/` | TestNG through the JUnit team's engine: `org.testng:testng` declared, the engine injected by `jk lock`, a `@DataProvider` fanned out, a TestNG group as a Platform tag in the tier table. |
| `test/migrations/` | Flyway and Liquibase over one module's scripts: the unit tier on H2, the integration tier on a PostgreSQL Testcontainer, `jk run` migrating an H2 file, and both tools as pinned `jk tool run` commands. |

One scenario per directory; keep each self-contained and its README honest about deviations.

## Conventions every scenario follows

- **Bare identity keys.** `name`, `group`, `version`, `java` sit at the top of `jk.toml`; there is
  no `[project]` table.
- **`java = 25`** (bytecode target; the host JDK is already 25+). Prefer `java =`, never
  `jdk = 17/21` — those force obsolete runtime downloads. Android keeps `java = 21` for that
  platform.
- **Versions are exact pins**, the current stable when the scenario was written
  (`h2 = "2.5.250"`, a catalog short name with its version, or a `group:artifact:version` string;
  a BOM-managed artifact is its catalog short name set to `"managed"`, or a versionless coordinate
  under a handle that is not the catalog name), and `jk update` moves them — the rule the
  template catalog follows. A README quotes the manifest's pin or the lock, never a third number.
  The Netty port and Now in Android pin at the upstream tag they mirror and say so.
- **`jk-lock.toml` is committed** — one per scenario, at the workspace root. A clean clone builds
  the graph the lock describes, and `jk build` never rewrites it. For the two overlay scenarios
  the lock lives under `overlay/` and `setup.sh` copies it into the checkout.
- **`jk-guards.toml` in every scenario**: the framework pack where one exists (`spring`,
  `android`; `monorepo` beside it for workspaces), otherwise a house baseline with one file-size
  ratchet. `jk guard` is clean everywhere; where a scenario has tests, `[test] exclude-tags`
  and one profile per tag give every tagged test exactly one tier.
- **`[image]`** with a JRE 25 base and `aot-cache = true` on every runnable service.
- **`AGENTS.md`** at every scenario root is the same file `jk new` writes into every new project
  (the text is owned by jk and copied here unchanged). It is regenerated, never edited.

## Running

Prerequisite: a **current** JumpKick — scenarios track `jk` `main` closely; stale installed workers
fail in confusing ways.

```sh
# release install
curl -fsSL https://jumpkick.build/install.sh | bash
# or from a jk checkout
jk build --skip-tests && jk install --skip-tests

# then, in this repo:
cd spring-boot/petshop && jk build && jk guard
```

Per-scenario detail lives in each directory's README. The two third-party checkouts have a
`setup.sh` that clones the pinned upstream and applies the `overlay/`:

```sh
cd android/nowinandroid && ./setup.sh && ./run.sh
cd jvm/netty && ./setup.sh && ./run.sh
```

Network required throughout: Maven Central, Google Maven, and (for Android) the SDK
`repository2` feed. First runs download toolchains and dependencies; JumpKick's CAS makes repeat
runs warm.

## Role in CI / black-box testing

Treat this repository as the **out-of-tree acceptance surface**:

| Layer | Where |
|-------|--------|
| Fast unit / module tests | [jk](https://github.com/JumpKickOSS/jk) gate: `jk format` → `jk guard` → `jk build` |
| Integration / e2e (tagged) | [jk](https://github.com/JumpKickOSS/jk) `jk test --profile integration` |
| **Product scenarios** | **This repo** — `jk build` / `test` / `guard` / `run` on real layouts |

When changing resolve, packaging, plugins, workspaces, guards, or the CLI↔engine wire, re-run the
scenarios that touch that surface (or the full set overnight). Failures here are product bugs
unless the scenario README documents a known gap.

## Contributing

- Prefer **small, focused** scenarios over kitchen-sink monorepos (except where the north star
  *is* large, e.g. Now in Android).
- Commit `jk-lock.toml`; run `jk guard` before you push and commit any baseline it writes.
- Keep READMEs short: what it demonstrates, the exact commands, known gaps.
- Do not invent a second build language — only declarative `jk.toml`.

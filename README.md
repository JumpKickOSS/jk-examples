# jk-examples

**Examples of how to use [JumpKick](https://github.com/JumpKickOSS/jk) (`jk`) well, and the black-box
suite that holds the tool to them.**

Each directory is a self-contained JumpKick project (or an orchestrated third-party checkout) that
exercises a real slice of the tool against real repositories and toolchains. They exist to:

1. **Teach** idiomatic `jk.toml`, workspaces, plugins, guards, test tiers and layout conventions
2. **Validate** ongoing product changes end-to-end (lock, resolve, compile, test, package, run)
3. **Benchmark** cold / warm / incremental builds as the engine evolves

Sibling product repo: [JumpKickOSS/jk](https://github.com/JumpKickOSS/jk). Scenarios track that
repo's `main` closely; build them with a current `jk`.

## Layout

The numbered directories are a learning path: start at `01-start/` and stop when you have what you
need. `ports/` and `bench/` are validation and measurement, not teaching material.

| Dir | What's there |
|---|---|
| `01-start/` | One small project per language: the first thing to read. |
| `02-services/` | Backend services on a framework (Spring Boot, Micronaut, Ktor, Netty). |
| `03-fullstack/` | A JVM backend beside a front end jk builds as a node module, or the front end alone. |
| `04-android/` | Android apps. |
| `05-recipes/` | One jk feature per project, framework-free: codegen, lint, test tiers, packaging. |
| `ports/` | Third-party builds ported to jk via a pinned checkout plus an `overlay/` (`setup.sh`). |
| `bench/` | Benchmark harnesses that run jk against real repositories outside this tree. |

## Scenarios

### 01-start — one per language

| Dir | Language | Shows |
|---|---|---|
| `01-start/java-cli/` | Java | A plain CLI with no framework or plugin, plus core JVM AOT (`jk build --aot-cache`). |
| `01-start/java-library/` | Java | A library packaged for release: sources and javadoc jars, a Central-ready POM from `[publish]`, `git.properties`, CycloneDX and SPDX SBOMs, `jk install`, JSpecify. |
| `01-start/java-workspace/` | Java | A framework-free three-module workspace: identity and versions declared once, `[export-dependencies]` vs `[dependencies]`, a contract test shared with `kind = "tests"`, `-m`, `--affected`. |
| `01-start/kotlin-cli/` | Kotlin | Project-declared Kotlin compiler plugins (`[[kotlin-plugins]]`) via kotlinx-serialization. |
| `01-start/kotlin-library/` | Kotlin | The `library` template as `jk new` writes it: sources and **Dokka** javadoc jars beside the jar, `git.properties` from `[build-info]`, the `library` guard pack, exact pins. |
| `01-start/groovy-cli/` | Groovy + Java | Groovy and Java compiled jointly in one module (each calls the other), `CliBuilder`, Spock specs with a data table. |
| `01-start/java-scala-mixed/` | Java + Scala | **A mixed Java/Scala module through jk's BSP**: one Zinc session for both halves referencing each other, one `scala` build target Metals imports (compiler version and jars, `scalacOptions` / `javacOptions`), `buildTarget/compile` with a Scala diagnostic — the wire recorded by `bsp-session.py`. |

### 02-services — backends on a framework

| Dir | Language | Shows |
|---|---|---|
| `02-services/spring-boot-kitchen-sink/` | Java | Spring Boot at an exact pin — web + data-jpa + validation + actuator, BOM auto-import, boot-jar layout, Spring AOT step, `[build-info]`, the actuator `sbom` endpoint over jk's SBOM, dev-scope devtools, `[image]`. |
| `02-services/spring-boot-petshop/` | Java | Multi-module Spring Boot pet shop (domain / service / web) — Boot's BOM as the workspace platform, workspace DI + MockMvc tests, the spring and monorepo guard packs. |
| `02-services/micronaut-http/` | Java | Micronaut HTTP service — the `[micronaut]` plugin at an exact platform pin, assembly fat jar, `@MicronautTest` client smoke, `[native]` through `jk native`, `[image]`. |
| `02-services/ktor-petshop/` | Kotlin | Multi-module Ktor + **Koin** + **Exposed** + H2 pet shop — Kotlin workspace dogfood; the `domain` library's Dokka javadoc jar and `[build-info]`. |
| `02-services/netty-echo/` | Java | Small Netty echo server on the published `netty-all` (4.2 `MultiThreadIoEventLoopGroup`), an `EmbeddedChannel` handler test and `[image]` — lightweight harness seed. |

### 03-fullstack — a JVM backend and a front end

| Dir | Shows |
|---|---|
| `03-fullstack/spring-boot-spa/` | **A JVM backend serving a single-page front end**: a `web` node module jk builds on the locked Node.js and packages as a resource jar, the app serving `classpath:/static/` with an SPA fallback, and root `jk dev` running the Vite dev server beside the app, inferred from the dependency. |
| `03-fullstack/vite-react/` | A Boot app serving a Vite + React SPA from its image: `jk new -t …/webapp --frontend vite-react`, the SPA a node module packaged as a resource jar. |
| `03-fullstack/two-frontends/` | One Boot API, a Vite admin SPA and a Next storefront that both call it (Vite's proxy, a Next `rewrites()`); root `jk dev` runs all three with no `[dev.sidecars]`. |
| `03-fullstack/angular/` | An Angular SPA as a node module: `ng build` and `ng test` (Vitest) as jk steps, inferred from `angular.json`; `[image] kind = "static"` ships it on nginx. |
| `03-fullstack/next-standalone/` | A Next.js standalone server as a node module: `jk build`, `jk run`, a distroless Node.js image. |
| `03-fullstack/sveltekit-node/` | SvelteKit on adapter-node: a Node.js server `jk run` starts and `jk image` ships. |

### 04-android

| Dir | Shows |
|---|---|
| `04-android/nowinandroid/` | **The Android north star**: Google's reference app, all 27 modules — Compose, Hilt (unmodified sources), Room auto-migrations, protobuf/datastore, kotlinx-serialization, navigation3, a contentType variant dimension, non-transitive R, demo-debug APK + signed R8 release AAB. No Gradle, no AGP. A pinned checkout plus `overlay/`. |

### 05-recipes — one feature each

| Dir | Shows |
|---|---|
| `05-recipes/codegen/avro/` | The `[avro]` preset: Avro schemas compiled in the generate stage, the classes joining the compile, `string-type`, `[avro] version` written beside the runtime pin so `jk update` moves both. |
| `05-recipes/codegen/jaxb/` | The `[jaxb]` preset: xjc over `src/main/xsd` into one package, the `jakarta.xml.bind` API and the JAXB runtime as the only dependencies, `[jaxb] version` on the runtime's line. |
| `05-recipes/codegen/jooq/` | The `[jooq]` preset over DDL scripts: the Flyway migrations applied in memory through `DDLDatabase`, typed tables and references generated, no database for the build. |
| `05-recipes/codegen/protobuf/` | The protobuf plugin: provisioned per-OS protoc, Java + Kotlin DSL (lite) codegen. |
| `05-recipes/codegen/graphql/` | GraphQL codegen as a `[generate]` recipe: DGS codegen's command line over the schema, typed types + client joining the compile, the tool closure fetched and cached. |
| `05-recipes/lint/checkstyle/` | `[lint] checkstyle` as a cached step after compile: findings as diagnostics with the rule id, `fail-on`, a rule set that repeats nothing `jk format` owns. |
| `05-recipes/lint/all-tools/` | The four-tool `[lint]` table — Checkstyle, a project PMD ruleset, SpotBugs over the classes, detekt over Kotlin — each its own cached step; runs the real tools so their report parsers see real output. |
| `05-recipes/test/testng/` | TestNG through the JUnit team's engine: `org.testng:testng` declared, the engine injected by `jk lock`, a `@DataProvider` fanned out, a TestNG group as a Platform tag in the tier table. |
| `05-recipes/test/migrations/` | Flyway and Liquibase over one module's scripts: the unit tier on H2, the integration tier on a PostgreSQL Testcontainer, `jk run` migrating an H2 file, and both tools as pinned `jk tool install` / `jk tool run` commands. |
| `05-recipes/packaging/shrink/` | The minified jar: R8 `--classfile` full mode collapsing a fat jar. |
| `05-recipes/packaging/variants/` | Core `[variants]`: a custom dimension on a plain JVM app — per-value `extra-src` + deps, union lockfile, mandatory selection. |
| `05-recipes/packaging/profiles-vs-variants/` | The decision matrix as running code: a profile (how), a feature (what capability), and the build-type variant (which product) side by side. |

### ports and bench

| Dir | Shows |
|---|---|
| `ports/netty/` | **Full Netty Maven→jk port** (~40 modules, Mill graph parity) — black-box + benchmarks. A pinned checkout plus `overlay/`. |
| `bench/maven-top20/` | The 20 most-starred Maven-built repositories, each pinned to a SHA and driven through its own Maven build and `jk import` → `lock` → `build` → `test`; `RESULTS.md` is a ratchet. |

One scenario per directory; keep each self-contained and its README honest about deviations. A
new scenario goes in the first directory whose description fits it.

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
- **`AGENTS.md`** at every scenario root carries jk's section between `<!-- BEGIN:jumpkick -->`
  and `<!-- END:jumpkick -->`. jk writes it and every build refreshes it, so it is never edited by
  hand. A scenario's own agent rules go outside the markers.

## Running

Prerequisite: a **current** JumpKick — scenarios track `jk` `main` closely; stale installed workers
fail in confusing ways.

```sh
# release install
curl -fsSL https://jumpkick.build/install.sh | bash
# or from a jk checkout
jk build --skip-tests && jk install --skip-tests

# then, in this repo:
cd 01-start/java-cli && jk build && jk run
cd ../../02-services/spring-boot-petshop && jk build && jk guard
```

Per-scenario detail lives in each directory's README. The two third-party checkouts have a
`setup.sh` that clones the pinned upstream and applies the `overlay/`:

```sh
cd 04-android/nowinandroid && ./setup.sh && ./run.sh
cd ports/netty && ./setup.sh && ./run.sh
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

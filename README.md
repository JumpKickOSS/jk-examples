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
| `micronaut/hello-http/` | Micronaut HTTP service — `[micronaut]` plugin at `latest`, assembly fat jar, `@MicronautTest` client smoke, `[image]`. |
| `spring-boot/kitchen-sink/` | Spring Boot at `latest` — web + data-jpa + validation + actuator, BOM auto-import, boot-jar layout, Spring AOT step, `[build-info]`, dev-scope devtools, `[image]`. |
| `spring-boot/petshop/` | Multi-module Spring Boot pet shop (domain / service / web) — workspace DI + MockMvc tests, the spring and monorepo guard packs. |
| `spring-boot/webapp/` | **A JVM backend serving a single-page front end**: a resource-only `web` module fed by Vite's `build.outDir`, the app serving `classpath:/static/` with an SPA fallback, and `[dev.sidecars]` running the Vite dev server beside `jk dev`. |
| `aot/vanilla-cli/` | Core JVM AOT (`jk build --aot-cache`) with no framework and no plugin — the everyone-gets-this path. |
| `kotlin/serialization-cli/` | Project-declared Kotlin compiler plugins (`[[kotlin-plugins]]`) via kotlinx-serialization. |
| `kotlin/ktor-petshop/` | Multi-module Ktor + **Koin** + **Exposed** + H2 pet shop — Kotlin workspace dogfood; the `domain` library's Dokka javadoc jar and `[build-info]`. |
| `protobuf/messages/` | The protobuf plugin: provisioned per-OS protoc, Java + Kotlin DSL (lite) codegen. |
| `graphql/dgs-codegen/` | GraphQL codegen as a `[generate]` recipe: DGS codegen's command line over the schema, typed types + client joining the compile, the tool closure fetched and cached. |
| `jvm/netty/` | **Full Netty Maven→jk port** (~40 modules, Mill graph parity) — black-box + benchmarks. |
| `jvm/netty-echo/` | Small Netty echo server on the published `netty-all` — lightweight harness seed. |
| `jvm/shrink-cli/` | The minified jar: R8 `--classfile` full mode collapsing a fat jar. |
| `jvm/variants-cli/` | Core `[variants]`: a custom dimension on a plain JVM app — per-value `extra-src` + deps, union lockfile, mandatory selection. |
| `jvm/profiles-vs-variants/` | The decision matrix as running code: a profile (how), a feature (what capability), and the build-type variant (which product) side by side. |

One scenario per directory; keep each self-contained and its README honest about deviations.

## Conventions every scenario follows

- **Bare identity keys.** `name`, `group`, `version`, `java` sit at the top of `jk.toml`; there is
  no `[project]` table.
- **`java = 25`** (bytecode target; the host JDK is already 25+). Prefer `java =`, never
  `jdk = 17/21` — those force obsolete runtime downloads. Android keeps `java = 21` for that
  platform.
- **Versions float to `"latest"`** and `jk-lock.toml` pins them. A README quotes either `latest`
  or the lock, never a third number. The exceptions pin on purpose and say why: the Netty port
  (Mill parity at one tag), Now in Android (its documented deviations), and the two scenarios
  whose README lists jar contents (`jvm/variants-cli`, `jvm/shrink-cli`).
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

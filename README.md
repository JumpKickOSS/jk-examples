# jk-examples

**Black-box fitness suite and adopter examples for [JumpKick](https://github.com/jkbuild/jk) (`jk`).**

Each directory is a self-contained JumpKick project (or an orchestrated third-party checkout) that exercises a real slice of the tool against real repositories and toolchains. These are not monorepo unit tests — they exist to:

1. **Validate** ongoing product changes end-to-end (lock, resolve, compile, test, package, run)
2. **Benchmark** cold / warm / incremental builds as the engine evolves
3. **Teach** early adopters idiomatic `jk.toml`, workspaces, plugins, and layout conventions

Sibling product repo: [jkbuild/jk](https://github.com/jkbuild/jk). Prefer a current `jk` built from that `main` (or a release that tracks it).

## Scenarios

| Dir | Validates |
|---|---|
| `android/nowinandroid/` | **The Android north star**: Google's reference app, all 27 modules — Compose, Hilt (unmodified sources), Room auto-migrations, protobuf/datastore, kotlinx-serialization, navigation3, a contentType variant dimension, non-transitive R, demo-debug APK + signed R8 release AAB. No Gradle, no AGP. |
| `spring-boot/kitchen-sink/` | Spring Boot **4.1** — web + data-jpa + validation + actuator, BOM auto-import, boot-jar layout, Spring AOT step, build-info, dev-scope devtools. |
| `spring-boot/petshop/` | Multi-module Spring Boot **4.1** pet shop (domain / service / web) — workspace DI + Boot 4 MockMvc tests. |
| `aot/vanilla-cli/` | Core JVM AOT (`jk build --aot-cache`) with no framework and no plugin — the everyone-gets-this path. |
| `kotlin/serialization-cli/` | Project-declared Kotlin compiler plugins (`[[kotlin-plugins]]`) via kotlinx-serialization. |
| `kotlin/ktor-petshop/` | Multi-module Ktor + **Koin** + **Exposed** + H2 pet shop — Kotlin workspace dogfood. |
| `protobuf/messages/` | The protobuf plugin: provisioned per-OS protoc, Java + Kotlin DSL (lite) codegen. |
| `jvm/netty-echo/` | Netty 4.1 echo server — adoption sample + cold/warm build harness seed. |
| `jvm/shrink-cli/` | The shrink plugin: R8 `--classfile` full mode collapsing a fat jar. |
| `jvm/variants-cli/` | Core `[variants]`: a custom dimension on a plain JVM app — per-value `extra-src` + deps, union lockfile, mandatory selection. |
| `jvm/profiles-vs-variants/` | The decision matrix as running code: a profile (how), a feature (what capability), and the build-type variant (which product) side by side. |

One scenario per directory; keep each self-contained and its README honest about deviations. New scenarios land here as JumpKick grows (native-image apps, more workspaces, git-source deps, publishing round-trips, …).

## Running

Prerequisite: a **current** JumpKick — scenarios track `jk` `main` closely; stale installed workers fail in confusing ways.

```sh
# from a jk checkout
./gradlew clean dist installLocal && ./install.sh build/dist/jk
# or your release install

# then, in this repo:
cd spring-boot/petshop && jk lock && jk build && jk test --modules web
```

Per-scenario detail lives in each directory’s README. For Now in Android:

```sh
cd android/nowinandroid && ./setup.sh && ./run.sh
```

Network required throughout: Maven Central, Google Maven, and (for Android) the SDK `repository2` feed. First runs download toolchains and dependencies; JumpKick’s CAS makes repeat runs warm.

## Role in CI / black-box testing

Treat this repository as the **out-of-tree acceptance surface**:

| Layer | Where |
|-------|--------|
| Fast unit / module tests | [jk](https://github.com/jkbuild/jk) `./gradlew test` |
| Integration / e2e (tagged) | [jk](https://github.com/jkbuild/jk) `./gradlew integrationTest` |
| **Product scenarios** | **This repo** — `jk lock` / `build` / `test` / `run` on real layouts |

When changing resolve, packaging, plugins, workspaces, or the CLI↔engine wire, re-run the scenarios that touch that surface (or the full set overnight). Failures here are product bugs unless the scenario README documents a known gap.

## Contributing

- Prefer **small, focused** scenarios over kitchen-sink monorepos (except where the north star *is* large, e.g. Now in Android).
- Commit `jk-lock.toml` when the scenario depends on a stable graph for CI.
- Keep READMEs short: purpose, how to run, known gaps.
- Do not invent a second build language — only declarative `jk.toml`.

# jk-examples — example projects & conformance scenarios for [jk](../jk)

Real projects that validate jk end to end: each directory is an independent jk project
(or an orchestrated third-party checkout) exercising a distinct slice of the tool.
These are fitness tests, not unit tests — they hit real repositories, real toolchains,
and real reference apps, and they exist to catch the gaps unit suites can't.

## Scenarios

| Dir | Validates |
|---|---|
| `android/nowinandroid/` | **The Android north star**: Google's reference app, all 27 modules — Compose, Hilt (unmodified sources), Room auto-migrations, protobuf/datastore, kotlinx-serialization, navigation3, flavors, non-transitive R, demo-debug APK + signed R8 release AAB. No Gradle, no AGP. |
| `spring-boot/kitchen-sink/` | Spring Boot **4.1** — web + data-jpa + validation + actuator, BOM auto-import, boot-jar layout, Spring AOT step, build-info, dev-scope devtools. |
| `aot/vanilla-cli/` | Core JVM AOT (`jk build --aot-cache`) with no framework and no plugin — the everyone-gets-this path. |
| `kotlin/serialization-cli/` | Project-declared Kotlin compiler plugins (`[[kotlin-plugins]]`) via kotlinx-serialization. |
| `protobuf/messages/` | The protobuf plugin: provisioned per-OS protoc, Java + Kotlin DSL (lite) codegen. |
| `jvm/shrink-cli/` | The shrink plugin: R8 `--classfile` full mode collapsing a fat jar. |

More scenarios land here as jk grows (native-image apps, workspaces, git-source deps,
publishing round-trips, …). One scenario per directory; keep each self-contained and
its README honest about deviations.

## Running

Prerequisite: a **current** jk built from source — the scenarios track jk `main`
closely and stale installed workers fail in confusing ways:

```sh
cd ../jk && ./install.sh    # or your local install flow
```

Then per scenario, `jk build` (see each README for variants). For Now in Android:

```sh
cd android/nowinandroid && ./setup.sh && ./run.sh
```

Network required throughout: Maven Central, Google Maven, and (for Android) the SDK
`repository2` feed. First runs download toolchains and dependencies; jk's CAS makes
repeat runs warm.

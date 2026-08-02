# jvm/netty — full Netty Maven → JumpKick port (JK-1174)

**Loyal multi-module conversion** of [netty/netty](https://github.com/netty/netty) so JumpKick can be compared
apples-to-apples with Maven and with [Mill’s Netty case study](https://mill-build.org/mill/1.0.x/comparisons/maven.html).

This is **not** the small `jvm/netty-echo` sample (published `netty-all` jar). Here we build **Netty’s own sources** as a JumpKick workspace: same module graph Mill models in [`example/thirdparty/netty/build.mill`](https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill).

| Pin | Value |
|-----|--------|
| **Tag** | `netty-4.1.115.Final` |
| **Scale** | ~2,900+ Java files / ~500k LOC / ~40 workspace modules (Java compile path) |
| **Layout** | NiA-style: `overlay/` + `setup.sh` → `checkout/` |

## Layout

```text
jvm/netty/
  README.md                 # this file
  setup.sh                  # clone tag, apply overlay, common Groovy codegen
  run.sh                    # jk lock && jk build --skip-tests
  scripts/generate-common.sh
  overlay/
    jk.toml                 # workspace root (group io.netty, java 8)
    common/jk.toml
    buffer/jk.toml
    …
  checkout/                 # gitignored working tree (after setup)
  MODULE_MAP.md             # Maven module ↔ jk module
  PARITY.md                 # what is ported vs deferred vs N/A
```

## Quick start

```bash
# current jk on PATH (from jk monorepo install)
./setup.sh
./run.sh
# or:
cd checkout && jk lock && jk build --skip-tests
```

Optional: compile a single module after a full lock:

```bash
cd checkout
jk build --skip-tests --modules common
jk build --skip-tests --modules codec-http
```

## Comparison methodology

Align with Mill’s published Netty table (clean compile focus, tests skipped for the primary table):

| Scenario | Maven | Mill | JumpKick |
|----------|-------|------|----------|
| Sequential clean compile all | `mvn -Pfast -DskipTests … install` + `-T 1` | `mill -j1 __.compile` | `jk build --skip-tests -j 1` |
| Parallel clean compile all | `mvn -T 10 …` | `mill __.compile` | `jk build --skip-tests` (default parallel) |
| Clean single module | `mvn -pl common …` | `mill common.compile` | `jk build --skip-tests --modules common` |
| No-op / warm | second install | second compile | second `jk build --skip-tests` |
| Dirty single file | touch + install | touch + compile | touch + `jk build --skip-tests --modules common` |

Harness seed: `jk` monorepo `scripts/netty-echo-bench.sh` pattern — extend with `scripts/bench-netty.sh` (see below) once `run.sh` is green on your machine. Record results in the monorepo `docs/perf/netty-benchmark.md`.

**Fairness:** wipe project `target/` for “cold”; leave `~/.jk` CAS / `~/.m2` warm unless documenting a fully cold machine. Use the same JDK major for all three tools.

## Completeness (honest)

Same intent as Mill: **not** 100% release-pipeline parity with every Maven plugin. Covered:

- Declarative multi-module workspace mirroring Mill’s moduleDeps graph  
- External deps (main / provided / test) at versions Mill pins  
- **common** Groovy collection codegen (same `codegen.groovy` as Maven/Mill)  
- Traditional Maven source layout (`src/main/java`, …)

Deferred / partial (see [PARITY.md](PARITY.md)):

- JNI compile/link for `transport-native-*` / `resolver-dns-native-macos` (C/`clang`/`make`)  
- OSGi / shading / Autobahn / full H2Spec docker flows  
- Full test suite wall-clock (hours on Maven; Mill runs a curated subset)

## Design notes

- **No `jk.toml` scripting language** — modules are data; codegen is a setup script (loyal to Maven/Mill’s external Groovy step, not a Gradle-style configuration graph).  
- **One lockfile** at the workspace root (`jk-lock.toml`).  
- **Workspace deps** use `{ workspace = true }` with `[project].name` = Maven module directory name (`common`, `codec-http`, …).

## Refs

- Mill vs Maven (Netty): https://mill-build.org/mill/1.0.x/comparisons/maven.html  
- Mill `build.mill`: https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill  
- Upstream: https://github.com/netty/netty  
- Tickets: JK-1174 (port), JK-1175 (benchmark numbers)

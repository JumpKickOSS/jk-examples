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
cd checkout && jk lock && jk build --skip-tests --redo
```

**Why `--skip-tests`?** Mill’s published Netty tables disable tests and focus on **compile**. A full Netty
`jk build --redo` (with tests) will compile test sources (now with Guava/JUnit 6 aligned to the
jk runner) but still needs a **curated** test set — same as Mill’s `codec-*.test` smoke, not a full
multi-hour suite. Optional smoke:

```bash
cd checkout
jk test --modules common    # unit tests for common after JUnit 6 align
```

Optional: compile a single module after a full lock:

```bash
cd checkout
jk build --skip-tests --modules common
jk build --skip-tests --modules codec-http
```

## Comparison methodology

Align with Mill’s published Netty table (**main compile** focus; unit tests are a separate tier):

| Scenario | Maven | Mill | JumpKick |
|----------|-------|------|----------|
| Sequential clean compile all | `mvn -Pfast -DskipTests … install` + `-T 1` | `./mill -j1 _.compile` | `jk -j1 build --skip-tests --redo` |
| Parallel clean compile all | `mvn -T 10 …` | `./mill _.compile` | `jk build --skip-tests --redo` |
| Clean single module | `mvn -pl common …` | `./mill common.compile` | `jk build --skip-tests -m common` |
| No-op / warm | second install | second `_.compile` | second `jk build --skip-tests` |
| Dirty single file | touch + install | touch + `common.compile` | touch + `jk build --skip-tests -m common` |

**Use `_.compile` (main only), not `__.compile`** — the latter also compiles every module’s tests.

### Automated Mill vs jk bench

```bash
# mill clone next to this repo (or set MILL_REPO)
#   git clone https://github.com/com-lihaoyi/mill.git ../../mill

./setup.sh
MILL_REPO=../../mill ./scripts/prepare-mill-netty.sh   # → mill-workspace/ (gitignored)
RUNS=3 PARALLEL=1 ./scripts/bench-netty.sh both        # serial cold/warm/dirty
```

- **Cold (fair recompile):** jk wipes `target/` + `--redo` (no CAS classfile restore); Mill wipes `out/`.
- **Same pin:** both trees use `netty-4.1.115.Final` @ the same SHA.
- Record results in the jk monorepo [`docs/perf/netty-benchmark.md`](https://github.com/jkbuild/jk/blob/main/docs/perf/netty-benchmark.md).

**Fairness:** leave dep caches warm (`~/.jk` / coursier) unless documenting a fully cold machine. Use the same JDK major when possible (Mill’s launcher may pick its own JDK).

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

- **No `jk.toml` scripting language** — modules are data; codegen is **`.jk-build` SPI** on
  `BEFORE_COMPILE` (stage `generate`), running the same upstream `codegen.groovy` as Maven gmaven
  / Mill. Optional `scripts/generate-common.sh` remains for setup-without-jk (`NETTY_SKIP_SHELL_CODEGEN=1`
  to force the build-logic path).  
- **One lockfile** at the workspace root (`jk-lock.toml`).  
- **Workspace deps** use `{ workspace = true }` with `[project].name` = Maven module directory name (`common`, `codec-http`, …).  
- **Test helpers across modules** use `{ workspace = true, kind = "tests" }` (Mill `testModuleDeps` / Maven `test-jar`) — e.g. `transport`’s `ChannelHandlerMetadataUtil` stays under `transport/src/test`; no synthetic `nativeimage-testutil` module.

## Ideal long-term flow (clone → import → build)

```bash
git clone --branch netty-4.1.115.Final https://github.com/netty/netty.git checkout
cd checkout
jk import pom.xml --overwrite   # multi-module → workspace + kind=tests for test-jars
# still needed today (not pure import):
#   - overlay common/.jk-build (BEFORE_COMPILE codegen) + a few compiler/env patches
#   - optional test curation (JNI/OpenSSL/long suites)
jk lock && jk build --skip-tests   # codegen runs inside the plan
```

`setup.sh` + `overlay/` remain the dogfood path until import coverage + compiler-args land fully.
The overlay models **kind=tests** edges and **BEFORE_COMPILE** collection codegen.

## Refs

- Mill vs Maven (Netty): https://mill-build.org/mill/1.0.x/comparisons/maven.html  
- Mill `build.mill`: https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill  
- Upstream: https://github.com/netty/netty  
- Tickets: JK-1174 (port), JK-1175 (benchmark numbers)

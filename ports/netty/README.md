# ports/netty — full Netty Maven → JumpKick port

**Loyal multi-module conversion** of [netty/netty](https://github.com/netty/netty) so JumpKick can
be compared apples-to-apples with Maven and with
[Mill's Netty case study](https://mill-build.org/mill/1.0.x/comparisons/maven.html).

This is **not** the small [`../netty-echo`](../netty-echo/) sample (published `netty-all` jar).
Here we build **Netty's own sources** as a JumpKick workspace: the module graph Mill models in
[`example/thirdparty/netty/build.mill`](https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill).

| Pin | Value |
|-----|--------|
| **Tag** | `netty-4.1.139.Final` (newest 4.1.x; 4.2 splits `codec` and leaves Mill's graph) |
| **Scale** | ~2,900+ Java files / ~500k LOC / ~40 workspace modules (Java compile path) |
| **Layout** | `overlay/` + `setup.sh` → `checkout/` (gitignored) |

**Why these pins.** Every dependency is an exact pin at the version Netty's own POMs use at the
tag — JUnit Jupiter `5.14.3`, Mockito `4.11.0`, Bouncy Castle `1.70`, log4j `2.25.3`, … — so do
not `jk update` them; move them with the tag. The module graph and each module's dependency set
follow Mill's `build.mill`. The workspace root's `[managed-dependencies]` pins `slf4j-api` to
`1.7.30` as Netty's parent POM does, and `java = 25` compiles the same sources Mill compiles at a
current release. jk's test launcher follows the declared Jupiter (Platform `1.14.3` here), so no
module pins the platform.

## Layout

```text
ports/netty/
  README.md                 # this file
  setup.sh                  # clone tag, apply overlay, common Groovy codegen, curate tests
  run.sh                    # jk build --skip-tests (REDO=1 adds --redo)
  scripts/generate-common.sh
  overlay/
    jk.toml                 # workspace root (group io.netty, java 25, 40 modules)
    jk-lock.toml            # the committed lock; setup.sh copies it into the checkout
    jk-guards.toml          # monorepo pack + file-size ratchet, baselined against upstream
    jk-guards-baseline.toml # engine-written: the upstream sites the rules tolerate
    common/.jk/before-compile.groovy   # collection codegen (BEFORE_COMPILE)
    handler/jk.toml         # [javac] args export sun.security.x509 for OpenJdkSelfSignedCertGenerator
    common/jk.toml, buffer/jk.toml, …
  checkout/                 # gitignored working tree (after setup)
  MODULE_MAP.md             # Maven module ↔ jk module
  PARITY.md                 # what is ported vs deferred vs N/A
```

## Quick start

```bash
./setup.sh
./run.sh                      # jk build --skip-tests against the committed lock
# or:
cd checkout && jk build --skip-tests
cd checkout && jk guard
```

**Why `--skip-tests`?** Mill's published Netty tables disable tests and focus on **compile**. A
full Netty `jk build` (with tests) runs the **curated** test set `scripts/curate-tests.sh` leaves
(no OpenSSL/JNI/OCSP/network suites) — the same idea as Mill's `codec-*.test` smoke, not the
multi-hour Maven suite:

```bash
cd checkout && jk build       # every module plus the curated unit tests
```

One module's tests:

```bash
cd checkout
jk test -m common
```

Compile a single module:

```bash
cd checkout
jk build --skip-tests -m common
jk build --skip-tests -m codec-http
```

## Guards on an upstream tree

`overlay/jk-guards.toml` extends the `monorepo` pack (`0.15.3`) and adds the file-size ratchet.
Upstream is what it is, so two rules are baselines: `one-module-per-package` (microbench and a few
split-out modules share packages with the module that owns them) and `file-size`. The sites are
in `overlay/jk-guards-baseline.toml`, written by `jk guard freeze <id> -W "…"`, never by hand;
the port refuses new ones. `converge-versions` holds without a baseline: one version per
artifact across the workspace.

## Comparison methodology

Align with Mill's published Netty table (**main compile** focus; unit tests are a separate tier):

| Scenario | Maven | Mill | JumpKick |
|----------|-------|------|----------|
| Sequential clean compile all | `mvn -Pfast -DskipTests … install` + `-T 1` | `./mill -j1 _.compile` | `jk -j1 build --skip-tests --redo` |
| Parallel clean compile all | `mvn -T 10 …` | `./mill _.compile` | `REDO=1 ./run.sh` |
| Clean single module | `mvn -pl common …` | `./mill common.compile` | `jk build --skip-tests -m common` |
| No-op / warm | second install | second `_.compile` | second `jk build --skip-tests` |
| Dirty single file | touch + install | touch + `common.compile` | touch + `jk build --skip-tests -m common` |

**Use `_.compile` (main only), not `__.compile`** — the latter also compiles every module's tests.

### Automated Mill vs jk bench

```bash
# mill clone next to this repo (or set MILL_REPO)
#   git clone https://github.com/com-lihaoyi/mill.git ../../mill

./setup.sh
MILL_REPO=../../mill ./scripts/prepare-mill-netty.sh   # → mill-workspace/ (gitignored)
RUNS=3 PARALLEL=1 ./scripts/bench-netty.sh both        # serial cold/warm/dirty
```

- **Cold (fair recompile):** jk wipes `target/` + `--redo` (no CAS classfile restore); Mill wipes `out/`.
- **Same sources:** `prepare-mill-netty.sh` checks out the same tag (`netty-4.1.139.Final`) and
  drops Mill's `build.mill` onto it. Mill's published `build.mill` was written against an older
  Netty commit (`20a790ed`, a 4.1.113 snapshot), so its dependency list can lag the tag's POMs
  (e.g. `lz4-java` moved to `at.yawk.lz4`); fix those in the Mill workspace before benchmarking.

**Fairness:** leave dep caches warm (`~/.jk` / coursier) unless documenting a fully cold machine.
Use the same JDK major when possible (Mill's launcher may pick its own JDK).

## Completeness (honest)

Same intent as Mill: **not** 100% release-pipeline parity with every Maven plugin. Covered:

- Declarative multi-module workspace mirroring Mill's moduleDeps graph
- External deps (main / provided / test) on Mill's per-module sets, at the tag's POM versions
- **common** Groovy collection codegen (same `codegen.groovy` as Maven/Mill)
- Traditional Maven source layout (`src/main/java`, …)

Deferred / partial (see [PARITY.md](PARITY.md)):

- JNI compile/link for `transport-native-*` / `resolver-dns-native-macos` (C/`clang`/`make`)
- OSGi / shading / Autobahn / full H2Spec docker flows
- Full test suite wall-clock (hours on Maven; Mill runs a curated subset)

## Design notes

- **No `jk.toml` scripting language** — modules are data; codegen is
  **`common/.jk/before-compile.groovy`** on `BEFORE_COMPILE`, running the same upstream
  `codegen.groovy` as Maven gmaven / Mill. Optional `scripts/generate-common.sh` remains for
  setup-without-jk (`NETTY_SKIP_SHELL_CODEGEN=1` to force the build-logic path).
- **One lockfile** at the workspace root (`jk-lock.toml`), committed under `overlay/`.
- **Workspace deps** use `{ workspace = true }` with the module's `name` = Maven module directory
  name (`common`, `codec-http`, …).
- **Test helpers across modules** use `{ workspace = true, kind = "tests" }` (Mill
  `testModuleDeps` / Maven `test-jar`) — e.g. `transport`'s `ChannelHandlerMetadataUtil` stays
  under `transport/src/test`; no synthetic `nativeimage-testutil` module.

## Ideal long-term flow (clone → import → build)

```bash
git clone --branch netty-4.1.139.Final https://github.com/netty/netty.git checkout
cd checkout
jk import pom.xml --overwrite   # multi-module → workspace + kind=tests for test-jars
# still needed today (not pure import):
#   - overlay common/.jk (BEFORE_COMPILE codegen) and the SCTP stub strip (a Maven compiler exclude)
#   - optional test curation (JNI/OpenSSL/long suites)
jk build --skip-tests           # codegen runs inside the plan
```

`setup.sh` + `overlay/` remain the dogfood path until import covers codegen and compiler excludes.

## Refs

- Mill vs Maven (Netty): https://mill-build.org/mill/1.0.x/comparisons/maven.html
- Mill `build.mill`: https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill
- Upstream: https://github.com/netty/netty

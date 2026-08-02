# Parity matrix — Maven / Mill / JumpKick (Netty)

Pinned: **netty-4.1.115.Final** (`04f9b4a827d992ad439823eeba85d65d3a89c265`).  
Graph: Mill [`example/thirdparty/netty/build.mill`](https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill).

## Build status (this port)

| Gate | Result |
|------|--------|
| `jk lock` (workspace, 40 members) | **green** (~73 external coords) |
| `jk build --skip-tests` | **green — 36 modules** (~15–20s warm wall on a typical box; cold first compile longer) |
| Full `jk test` suite | **not default** (hours on Maven; Mill runs curated subsets) |
| JNI native libs | **deferred** |

## Capability matrix

| Capability | Maven | Mill | JumpKick | Notes |
|------------|-------|------|----------|-------|
| Multi-module graph | yes | yes | **yes** | `overlay/` + workspace |
| Compile Java main sources | yes | yes | **yes** | 36 modules green |
| Inter-module deps without `mvn install` | reactor jars | direct | **workspace jars** | `target/{module}/` |
| common Groovy codegen | gmaven | GroovyShell | **`scripts/generate-common.sh`** | same `codegen.groovy` |
| Java 8 bytecode (`-source 1.8`) | yes | yes | **java = 17** | jk LTS floor is 17; sources are the same |
| `javac --add-exports` for `sun.security.x509` | yes | forkArgs | **workaround** | drop `OpenJdkSelfSignedCertGenerator` + BC-only patch (product gap: project compiler-args) |
| SCTP `com.sun.nio.sctp` stubs | compiler exclude | as Maven | **setup strip** | matches Maven exclude |
| JNI epoll/kqueue/macos DNS | yes | clang/make | **deferred** | Java class modules included |
| OSGi / shading / Autobahn / H2Spec | yes | partial | **deferred** | same honesty class as Mill |
| Checkstyle / enforcer | yes | skipped in benches | **skipped** | fair compile comparison |

## Product gaps surfaced

1. **No project-level `javac` `--add-exports` / compiler-args** in `jk.toml` (handler SSL util).  
2. **Exact version pins** required for ancient artifacts (`protobuf-java:2.6.1`) — caret default rejects.  
3. **Large workspace lock + parallel compile** works end-to-end (evidence of viability).  
4. **Codegen-before-compile** is a setup script, not a build-script plugin — intentional anti-Gradle design.

## Comparison methodology

See [README.md](README.md). Record times with `scripts/bench-netty.sh` into the monorepo `docs/perf/netty-benchmark.md`.

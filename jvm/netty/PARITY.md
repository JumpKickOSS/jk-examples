# Parity matrix — Maven / Mill / JumpKick (Netty)

Pinned: **netty-4.1.115.Final** (`04f9b4a827d992ad439823eeba85d65d3a89c265`).  
Graph: Mill [`example/thirdparty/netty/build.mill`](https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill).

## Build status (this port)

| Gate | Result |
|------|--------|
| `jk lock` (workspace, 40 members) | **green** (~73 external coords) |
| `jk build --skip-tests --redo` | **green — 41 modules** (compile path; Mill-fair) |
| Test **compile** with Guava + JUnit **6.1** | **green** (aligned to jk test-runner) |
| Full `jk build --redo` (all unit tests) | **not green by default** — curated excludes + remaining suite gaps (Mill also curates) |
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

5. **JUnit version skew**: jk’s test-runner is **JUnit 6.1**; project pins must match Platform **6.x**. Pinning Jupiter 5.9 + auto-injected Platform “latest” → empty discovery or `NoSuchMethodError`.
6. **No first-class workspace `test-jar` deps** — Mill’s `testModuleDeps` approximated via `nativeimage-testutil` promotion + main-jar suite modules.
7. **Broken old POMs** (`apacheds-protocol-dns` `${groupId}` path) — excluded those integration tests in setup.

## setup.sh curated excludes (honest Mill-class)

| Exclude | Why |
|---------|-----|
| `OpenJdkSelfSignedCertGenerator` + SSL patch | needs `javac --add-exports` |
| SCTP `com/**` stubs | Maven compiler exclude / `jdk.sctp` conflict |
| `AmazonCorrettoSslEngineTest` | classifier-native crypto provider |
| ApacheDS DNS tests (`TestDnsServer`, …) | broken Maven POM / not resolved |
| `NativeLibraryLoaderTest` | UnsatisfiedLinkError without special native fixtures |
| Adaptive* buffer tests | capacity-floor failures on modern JDK (investigate) |

# Parity matrix — Maven / Mill / JumpKick (Netty)

Pinned: **netty-4.1.115.Final** (`04f9b4a827d992ad439823eeba85d65d3a89c265`).  
Graph: Mill [`example/thirdparty/netty/build.mill`](https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill).

## Build status (this port)

| Gate | Result |
|------|--------|
| `jk lock` (workspace) | **green** (~98 external coords) |
| `jk build --skip-tests --redo` | **green — 41 modules** |
| Test **compile** with Guava + JUnit **6.1** | **green** (aligned to jk test-runner) |
| Full `jk build --redo` (curated unit tests) | **green — 41 modules (~3–4 min)** |
| **`kind = "tests"`** (transport → codec smoke) | **green** — `TransportTestsKindSmokeTest` (`@Tag("kind-smoke")`); fails compile without the edge |
| **BEFORE_COMPILE codegen** (common collections) | **green** — `.jk-build/before-compile.groovy` runs upstream `codegen.groovy`; no shell required when `NETTY_SKIP_SHELL_CODEGEN=1` |
| JNI native libs (epoll/kqueue/tcnative) | **deferred** |

## Capability matrix

| Capability | Maven | Mill | JumpKick | Notes |
|------------|-------|------|----------|-------|
| Multi-module graph | yes | yes | **yes** | `overlay/` + workspace |
| Compile Java main sources | yes | yes | **yes** | |
| Inter-module deps without `mvn install` | reactor jars | direct | **workspace jars** | `target/{module}/` |
| common Groovy codegen | gmaven | GroovyShell | **`.jk-build/before-compile.groovy`** (+ optional shell) | same `codegen.groovy` |
| Java 8 bytecode (`-source 1.8`) | yes | yes | **java = 17** | jk LTS floor is 17; sources are the same |
| `javac --add-exports` for `sun.security.x509` | yes | forkArgs | **workaround** | drop `OpenJdkSelfSignedCertGenerator` + BC-only patch |
| SCTP `com.sun.nio.sctp` stubs | compiler exclude | as Maven | **setup strip** | matches Maven exclude |
| JNI epoll/kqueue/macos DNS | yes | clang/make | **deferred** | Java class modules included |
| OSGi / shading / Autobahn / H2Spec | yes | partial | **deferred** | same honesty class as Mill |
| Checkstyle / enforcer | yes | skipped in benches | **skipped** | fair compile comparison |

## Product gaps surfaced

1. **No project-level `javac` `--add-exports` / compiler-args** in `jk.toml` (handler SSL util).  
2. **Exact version pins** required for ancient artifacts (`protobuf-java:2.6.1`) — caret default rejects.  
3. **Large workspace lock + parallel compile** works end-to-end.  
4. **Codegen-before-compile** uses `.jk-build/before-compile.groovy` (`BEFORE_COMPILE` / stage
   `generate`) plus optional `scripts/generate-common.sh` for setup-without-jk.  
5. **JUnit version skew**: jk’s test-runner is **JUnit 6.1**; project pins must match Platform **6.x**.  
6. **Workspace `kind = "tests"`** — Mill’s `testModuleDeps` / Maven `test-jar` (first-class in jk; used in overlay).  
7. **Test workers use `-XX:ActiveProcessorCount=1`** — Netty Adaptive allocator must floor central-queue capacity at 2 (JCTools).  
8. **`jk import pom.xml`** now rewrites sibling GAs → `workspace = true` and `test-jar` → `kind = "tests"`, but still does not: flatten parent dependencyManagement fully, map Maven compiler excludes / `--add-exports`, or run Groovy codegen — so Netty still needs a thin setup path after import.

## setup / curate (honest Mill-class)

| Action | Why |
|--------|-----|
| `OpenJdkSelfSignedCertGenerator` + SSL patch | needs `javac --add-exports` |
| SCTP `com/**` stubs | Maven compiler exclude / `jdk.sctp` conflict |
| Drop tests: `handler`, `handler-ssl-ocsp`, epoll/kqueue/blockhound/macos natives, testsuites | OpenSSL/JNI/OCSP network hang / multi-hour suites |
| Drop codec compression tests | optional natives without full classifiers |
| Drop bootstrap / `NativeLibraryLoaderTest` / ApacheDS DNS tests | flaky / UnsatisfiedLinkError / broken POM |
| Drop `NativeImage*` / `*IntegrationTest` | optional suite cost; re-enable with `kind = "tests"` |
| `transport = { workspace = true, kind = "tests" }` | Mill `testModuleDeps` / Maven test-jar (ChannelHandlerMetadataUtil) |
| **Keep Adaptive\* buffer tests** | patch `CENTRAL_QUEUE_CAPACITY = Math.max(2, …)` (Netty #14579 / 4.1.116) |
| `brotli4j` + `native-linux-x86_64` | platform native for codec-http brotli tests |
| `HttpContentDecoderTest.isNotSupported → !Brotli.isAvailable()` | safety net if native missing |
| `git checkout -f` + `clean -fdq` on setup | soft re-checkout left prior curate deletions |

## Adaptive capacity floor (root cause of prior buffer failures)

jk test workers set **`-XX:ActiveProcessorCount=1`**. Netty 4.1.115 used
`CENTRAL_QUEUE_CAPACITY = availableProcessors()` for the Adaptive central
queue; JCTools `MpmcArrayQueue` requires capacity ≥ 2 →
`IllegalArgumentException: capacity: 1 (expected: >= 2)`.

Fix (overlay patch from 4.1.116): `Math.max(2, …)` + static validation.
All Adaptive\* buffer tests green under the real test runner.

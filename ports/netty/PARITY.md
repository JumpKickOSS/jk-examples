# Parity matrix — Maven / Mill / JumpKick (Netty)

Pinned: **netty-4.1.139.Final** (`7bc0414acbb21f752dd615c386821f58e722c5f5`).  
Graph: Mill [`example/thirdparty/netty/build.mill`](https://github.com/com-lihaoyi/mill/blob/main/example/thirdparty/netty/build.mill)
(written against Netty commit `20a790ed`, a 4.1.113 snapshot; dependency versions here follow the
4.1.139 POMs).

## Build status (this port)

| Gate | Result |
|------|--------|
| `jk lock` (workspace) | **green** (~138 external coords) |
| `jk build --skip-tests` | **green — 41 modules** |
| Tests on JUnit Jupiter **5.14.3** (the tag's pin) | **green** — jk's launcher follows it onto Platform 1.14.3 |
| Full `jk build` (curated unit tests) | **green — 41 modules, 25,396 passed / 1,321 skipped** (macOS aarch64) |
| **`kind = "tests"`** (transport → codec smoke) | **green** — `TransportTestsKindSmokeTest` (`@Tag("kind-smoke")`); fails compile without the edge |
| **BEFORE_COMPILE codegen** (common collections) | **green** — `.jk/scripts/before-compile.groovy` runs upstream `codegen.groovy`; no shell required when `NETTY_SKIP_SHELL_CODEGEN=1` |
| JNI native libs (epoll/kqueue/tcnative) | **deferred** |

## Capability matrix

| Capability | Maven | Mill | JumpKick | Notes |
|------------|-------|------|----------|-------|
| Multi-module graph | yes | yes | **yes** | `overlay/` + workspace |
| Compile Java main sources | yes | yes | **yes** | |
| Inter-module deps without `mvn install` | reactor jars | direct | **workspace jars** | `target/{module}/` |
| common Groovy codegen | gmaven | GroovyShell | **`.jk/scripts/before-compile.groovy`** (+ optional shell) | same `codegen.groovy` |
| Java 8 bytecode (`-source 1.8`) | yes | yes | **java = 25** | same sources at a current release |
| `javac --add-exports` for `sun.security.x509` | yes | forkArgs | **yes** | `handler/jk.toml` `[javac] args`; `OpenJdkSelfSignedCertGenerator` compiles |
| SCTP `com.sun.nio.sctp` stubs | compiler exclude | as Maven | **setup strip** | matches Maven exclude |
| JNI epoll/kqueue/macos DNS | yes | clang/make | **deferred** | Java class modules included |
| OSGi / shading / Autobahn / H2Spec | yes | partial | **deferred** | same honesty class as Mill |
| Checkstyle / enforcer | yes | skipped in benches | **skipped** | fair compile comparison |

## Product gaps surfaced

1. **Large workspace lock + parallel compile** works end-to-end.  
2. **Codegen-before-compile** uses `.jk/scripts/before-compile.groovy` (`BEFORE_COMPILE` / stage
   `generate`) plus optional `scripts/generate-common.sh` for setup-without-jk.  
3. **Workspace `kind = "tests"`** — Mill’s `testModuleDeps` / Maven `test-jar` (first-class in jk; used in overlay).  
4. **`jk import pom.xml`** now rewrites sibling GAs → `workspace = true` and `test-jar` → `kind = "tests"`, but still does not: flatten parent dependencyManagement fully, map Maven compiler excludes / `--add-exports`, or run Groovy codegen — so Netty still needs a thin setup path after import.

## setup / curate (honest Mill-class)

| Action | Why |
|--------|-----|
| SCTP `com/**` stubs | Maven compiler exclude / `jdk.sctp` conflict |
| Drop tests: `handler`, `handler-ssl-ocsp`, epoll/kqueue/blockhound/macos natives, `testsuite-native` | OpenSSL/JNI/OCSP network hang / multi-hour suites |
| Drop codec compression tests | optional natives without full classifiers |
| Drop bootstrap / `NativeLibraryLoaderTest` / ApacheDS DNS tests (`TestDnsServer` and its users) | flaky / UnsatisfiedLinkError / broken POM |
| Drop `HttpConversionUtilFuzzTest` | Jazzer fuzz harness |
| `ChannelHandlerMetadataUtil` drops its `NativeImageHandlerMetadataTest` import | a javadoc-only import of a dropped test |
| Drop `NativeImage*` / `*IntegrationTest` | optional suite cost; re-enable with `kind = "tests"` |
| `transport = { workspace = true, kind = "tests" }` | Mill `testModuleDeps` / Maven test-jar (ChannelHandlerMetadataUtil) |
| `brotli4j` + every platform native the POMs list | codec-http / codec-http2 Brotli tests load one on any host |
| codec-http2 `[test] jvm-args = ["-Xmx2g"]` | HPACK tests allocate 4 GiB-capacity tables concurrently |
| root `[managed-dependencies]` `slf4j-api` 1.7.30 | Netty parent POM's dependencyManagement |
| `HttpContentDecoderTest.isNotSupported → !Brotli.isAvailable()` | safety net if native missing |
| `git checkout -f` + `clean -fdq` on setup | soft re-checkout left prior curate deletions |

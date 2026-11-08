# Parity — Maven / JumpKick (Helidon 27)

Pinned: **Helidon 27.0.1** (`8e87693`). The conversion is `jk import` on Helidon's root POM, its
whole default reactor; `setup.sh` runs it fresh on every setup, so this port tracks what the
current jk import produces rather than a frozen copy of it.

## Import quality: the hand edits

Every file in `overlay/` and every patch in `patches/` is something `jk import` did not produce.
The goal is an empty list. Gaps fixed in jk while porting are kept here with the release that
fixes them, so the history of the metric stays visible.

| # | Hand edit | Why | jk ticket | Status |
|---|-----------|-----|-----------|--------|
| 1 | — (was: `META-INF/services` files generated from `module-info.java`) | `helidon-services-plugin` (218 modules) writes them from `provides … with`; without them no Helidon annotation processor runs | JK-4691 `[build] module-services` | fixed in jk |
| 2 | `overlay/common/common/.jk/scripts/before-compile.groovy` | `maven-resources-plugin` filters `src/main/templates` (`Version.java`) into a generated-source root; import keeps the root, drops the copy | JK-4692 | open |
| 3 | `patches/ucp-ojdbc17-aggregator.patch` | `ojdbc17-production` is a `<type>pom</type>` aggregator from Central; import wrote it as a BOM, losing `ojdbc17` | JK-4694 | open |

## jk fixes this port found (not hand edits)

| Symptom | jk ticket |
|---------|-----------|
| Annotation processors that need javac's own environment (`Trees.instance`) failed under jk; Helidon's API-stability check never ran | JK-4672 |
| javadoc of a module with `module-info.java` had no `--module-path` ("module not found") | JK-4689 |
| An incremental compile deleted `module-info.class` and never wrote it back | JK-4690 |
| A modular compile put the whole classpath on `--module-path`; unrelated automatic modules split packages | JK-4693 |
| The engine (on its own JDK) could not read Java 27 `module-info.class` files | JK-4695 |
| Maven's main and test compiles run different processor paths (the API-stability enforcer only on main); jk ran main's on the tests | JK-4697 `[test-processor-dependencies] inherit = false` |
| A processor reading `module-info.java` through the Filer found nothing (no `-sourcepath`); Helidon's codegen filed its registry as "unnamed" | JK-4699 |
| Resources were copied after compile, so Helidon's codegen could not merge into the static `META-INF/helidon/manifest` and the service registry was never listed | JK-4701 (resources copied before compile, as Maven's process-resources) |
| A provided-scope JUnit Jupiter API got no engine on the test classpath ("Cannot create Launcher without at least one TestEngine") | JK-4702 |
| Surefire `<environmentVariables>` were not imported; an extra execution's own properties leaked into every test | JK-4710 |
| maven-compiler-plugin `<proc>` was not imported (Helidon's codegen modules set `none`) | JK-4711 |

## Build and test status

| Gate | Result |
|------|--------|
| `jk build --skip-tests`, whole reactor (223 modules) | **green** (1m06s with `--redo`) |
| `jk test -m webserver/webserver -m config/config` (both modules and every module they build on) | **green — 3,787 tests** |

## Maven plugins jk import does not map (and why that is fine here)

| Plugin | Modules | Port stance |
|--------|---------|-------------|
| maven-enforcer-plugin | 220 | Rules Helidon's build runs on itself; jk-guards covers the same ground |
| buildnumber-maven-plugin | 220 | Feeds only `Version.REVISION` (hand edit 2) |
| directory-maven-plugin | 220 | Resolves the reactor root for other plugins; jk knows its workspace root |
| maven-plugin-plugin | 219 | Only `service/maven-plugin` is a Maven plugin; its descriptor is not needed to compile |
| maven-dependency-plugin | ~100 | `analyze` checks; out of scope |
| maven-checkstyle-plugin | 220 | No execution bound; not run by default under Maven either |
| maven-invoker-plugin | 1 | `codegen/api-stability` integration projects; test-only |

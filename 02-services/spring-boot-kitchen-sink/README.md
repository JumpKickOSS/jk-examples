# 02-services/spring-boot-kitchen-sink — the spring-boot plugin's whole surface

One module, every knob the `[spring-boot]` plugin has, plus the core `[build-info]` table:

- `version = "4.1.1"` — the Boot release, an exact pin; the BOM auto-imports and every
  starter is a versionless coordinate it manages.
- `aot = true` — the Spring AOT step runs in the build.
- `aot-jvm-args`, `aot-args`, `include-tools`, `platform` — written out at their defaults so
  every key is in view: flags for the AOT processor's JVM, arguments the app sees under AOT
  processing, the nested `jarmode` tools (`java -Djarmode=tools -jar …`), and whether Boot's
  BOM is imported as the platform.
- `[build-info]` — `git.properties` and `META-INF/build-info.properties` in the jar, so the
  actuator's `/info` reports the commit and version the build came from.
- `[dev-dependencies] devtools` — on the classpath for `jk dev`, never in the jar.
- `[runtime-dependencies] h2` — runtime only.
- The CycloneDX SBOM jk writes into every jar (`META-INF/sbom/application.cdx.json`) is what the
  actuator's `sbom` endpoint serves: `curl -s localhost:8080/actuator/sbom/application`.
- `[image]` with a JRE 25 base and `aot-cache = true`; Boot jars are unpacked into the CDS/AOT
  layout before training.
- The `cc.jumpkick.guards:spring` pack in `jk-guards.toml`.

```sh
jk build           # layered boot jar at target/kitchen-sink-1.0.0.jar
jk run             # then: curl -s localhost:8080/actuator/info
jk dev             # reload loop with devtools
jk guard
jk image
```

There is no test suite here on purpose — the scenario is the packaging surface; see
[`../petshop`](../petshop/) for Boot tests in a workspace.

# spring-boot/kitchen-sink — the spring-boot plugin's whole surface

One module, every knob the `[spring-boot]` plugin has, plus the core `[build-info]` table:

- `version = "4.1.1"` — the Boot release, an exact pin; the BOM auto-imports and every
  starter is a versionless coordinate it manages.
- `aot = true` — the Spring AOT step runs in the build.
- `[build-info]` — `git.properties` and `META-INF/build-info.properties` in the jar, so the
  actuator's `/info` reports the commit and version the build came from.
- `[dev-dependencies] devtools` — on the classpath for `jk dev`, never in the jar.
- `[runtime-dependencies] h2` — runtime only.
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

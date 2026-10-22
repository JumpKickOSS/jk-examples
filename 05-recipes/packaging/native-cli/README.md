# 05-recipes/packaging/native-cli — a CLI shipped as a native executable

`sums` prints and checks file checksums, like `sha256sum`, with [picocli] for options, help and
exit codes. `jk build` is an ordinary JVM build; `jk native` turns the same code into one native
executable with GraalVM native-image. It starts in milliseconds and needs no JRE on the target
machine.

```sh
jk build                          # JVM: compile, tests, guards; no GraalVM needed
jk run -- jk.toml                 # JVM, until a native binary exists
jk native                         # target/sums, built by native-image
./target/sums -a SHA-512 jk.toml
./target/sums jk.toml jk-lock.toml > SUMS && ./target/sums -c SUMS
```

## What makes it native-ready

```toml
[native]
enabled = true          # `jk native` builds it; `jk build` stays JVM. "always" builds both.

[processor-dependencies]
picocli-codegen = "info.picocli:picocli-codegen:4.7.7"
```

- **No hand-written reflection config.** picocli binds options to fields by reflection, which
  native-image cannot see. `picocli-codegen` in `[processor-dependencies]` runs during compile and
  writes `META-INF/native-image/picocli-generated/` into the classes; native-image reads it from
  there. Remove it and `jk native` still succeeds, but the binary rejects every option.
- **No GraalVM install step.** jk uses `GRAALVM_HOME`, or a GraalVM JDK it already manages, or
  provisions one.
- **Same code.** There is no native-only source set; `[native] args` is the one place for extra
  native-image flags. Libraries that need config and don't generate it are usually covered by the
  GraalVM reachability-metadata repository, which jk pins in the lock (`metadata-repository`).

## Teaches

- `[native] enabled = true` vs `"always"`: opt-in native builds that don't slow down the JVM loop.
- A reflection-based library made native-ready by its own annotation processor.
- `jk run` prefers the most self-contained artifact: after `jk native` it runs the binary.

## Validates

- `jk native` on a plain application: toolchain selection, processor-generated native-image config
  picked up from the classes, the executable at `target/<name>`.
- `[native]` declared does not change what `jk build` produces.

[picocli]: https://picocli.info/

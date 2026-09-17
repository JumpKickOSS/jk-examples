# jvm/profiles-vs-variants — the decision matrix as running code

Three knobs that are easy to confuse, side by side in one small app:

| Knob | Question | Here | Command |
|------|----------|------|---------|
| **Profile** | *how* to build | `[profiles.strict]` turns javac lint into errors; same artifact either way | `jk build --profile strict` |
| **Feature** | *what* optional capability | `[features.json]` gates the `gson` dependency (`optional = true`) | `jk build` (default on) |
| **Variant** | *which* product | the built-in build-type dimension; `release` adds a `BUILD_STAMP` resource via `extra-src` | `jk build --release` |

Environments (dev / test / prod configuration) are **none** of these — that is runtime
configuration (env vars, config files, Spring profiles): build once, promote the same artifact.

```sh
jk build && java -jar target/profiles-vs-variants-1.0.0-all.jar
jk build --profile strict
jk build --release && java -jar target/profiles-vs-variants-1.0.0-all.jar
jk guard
```

`gson` is an exact pin (`2.14.0`) that `jk update` moves. The fat jar (`assembly = true`) is what
makes `java -jar` work with the feature dependency on the classpath.

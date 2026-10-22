# jvm/profiles-vs-variants — the decision matrix as running code

Three knobs that are easy to confuse, side by side in one small app:

| Knob | Question | Here | Command |
|------|----------|------|---------|
| **Profile** | *how* to build | `[profiles.strict]` turns javac lint into errors; same artifact either way | `jk build --profile strict` |
| **Feature** | *what* optional capability | `[features.json]` gates the `gson` dependency (`optional = true`) | `jk build` (default on); `jk tree --no-default-features` shows the graph without it |
| **Variant** | *which* product | the built-in build-type dimension; `release` compiles the extra `ReleaseStamp` class from `src-release/` via `extra-src` | `jk build --release` |

Environments (dev / test / prod configuration) are **none** of these — that is runtime
configuration (env vars, config files, Spring profiles): build once, promote the same artifact.

```sh
jk build && java -jar target/profiles-vs-variants-1.0.0-all.jar
jk build --profile strict
jk tree --no-default-features     # (no dependencies): gson leaves with the json feature
jk build --release && java -jar target/profiles-vs-variants-1.0.0-all.jar
jk guard
./run.sh                          # all of the above, then back to debug
```

The jar prints `debug build, json feature ON` by default and `release build, json feature ON`
after `--release`; a following plain `jk build` prints `debug build` again — the release-only
class does not survive a variant switch, so no `jk clean` is needed between selections.

`gson` is an exact pin (`2.14.0`) that `jk update` moves. The fat jar (`assembly = true`) is what
makes `java -jar` work with the feature dependency on the classpath.

# 05-recipes/plugins/jk-plugin — writing a jk plugin, and using it

Two projects. `hello-plugin/` is a jk build plugin written outside jk's own tree, compiled against
the published SDK (`cc.jumpkick:jk-plugin-sdk:0.20.0`). `consumer/` is an ordinary project that uses
it: it pins the plugin jar by SHA-256, configures it with a `[hello]` table, and its app prints what
the plugin's build step wrote.

```sh
(cd hello-plugin && jk build)                 # target/hello-plugin-0.1.0.jar is the plugin
cd consumer
jk trust plugin sha256:fab614f97477e86680f5d79a95b576abac06ab60bf6b8b6d07ce77e454bc2380
jk build && jk run                            # Hello from a jk plugin
```

## The plugin

| File | What |
|---|---|
| `jk.toml` | An ordinary library depending on `cc.jumpkick:jk-plugin-sdk`, which resolves from the JumpKick repository only. |
| `src/main/resources/jk-plugin.toml` | The declarative layer: the `[hello]` table it owns, its schema (`greeting`), a javac argument it contributes, and the SDK release it was built against. |
| `src/main/java/…/HelloPlugin.java` | The code layer: registers the `hello-greeting` step, which writes `greeting.txt` into a directory jk adds to the consumer's resources. |
| `META-INF/services/cc.jumpkick.plugin.Plugin` | How the worker finds the plugin class. |

jk reads the manifest in-process (schema, contributions) and forks the jar as a worker only for
its code. The step is cached like any of jk's own: same `[hello]` config, same output.

## The consumer

```toml
[plugins]
hello = { path = "../hello-plugin/target/hello-plugin-0.1.0.jar", sha256 = "fab614f9…" }

[hello]
greeting = "Hello from a jk plugin"
```

The pin is by content: a different jar at that path is refused. `jk lock` validates `[hello]`
against the plugin's schema and locks the plugin's SDK floor as `plugin`-scoped rows. jk will not
run third-party build code until you trust it; the refusal names the exact `jk trust plugin`
command. The jar builds reproducibly, so the committed hash matches a fresh build of the same
sources; change the plugin and the pin changes with it (the refusal names the new digest).

## Teaches

- The two layers of a jk plugin: a declarative manifest jk reads, and code it forks.
- Writing a step: inputs, an output directory, contributing it to the consumer's resources.
- Using a plugin you did not get from jk: a content pin, and trust before its code runs.

## Validates

- The published SDK from outside jk's tree, at the version jk ships.
- Plugin loading from a path pin, schema validation, trust, and a plugin step's contribution.

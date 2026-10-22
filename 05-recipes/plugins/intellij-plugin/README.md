# 05-recipes/plugins/intellij-plugin — an IntelliJ Platform plugin

**Sort Lines** adds one action to IntelliJ IDEA's Edit menu (⌥⇧S): it sorts the selected lines, or
the whole file when nothing is selected, as one undoable edit. The build compiles against the
IDE itself, runs the tests inside a headless IDE, and packages the zip an IDE installs.

```sh
jk build                     # compile against IntelliJ IDEA 2026.2, run the tests in a headless IDE
ls target/sort-lines-1.0.0.zip
```

Install it with **Settings → Plugins → ⚙ → Install Plugin from Disk…** and pick the zip.

## The `[intellij]` table

```toml
[intellij]
version = "262.10968.63"   # the platform build; the IDE's About dialog shows IC-262.10968.63

[repositories.jetbrains]
url    = "https://www.jetbrains.com/intellij-repository/releases"
groups = ["com.jetbrains.intellij.*"]

[test-dependencies]
junit = "junit:junit:4.13.2"
```

jk fetches the IDE distribution once and compiles against its jars, which stay out of the zip. The
tests run in a JVM started with the IDE's own launcher flags and a sandbox under
`target/intellij-sandbox/`. The platform's test bases are JUnit 3 `TestCase` classes: declaring
JUnit 4 makes jk add the JUnit Vintage engine that runs them. Leave it out and the build fails,
naming the missing engine. The packaged `plugin.xml` gets its `<version>` and `<idea-version>`
from `jk.toml`.

| File | What |
|---|---|
| `META-INF/plugin.xml` | The plugin: id, name, the action and its menu and shortcut. |
| `LineSorter.java` | The text rule, with no IDE types. |
| `SortLinesAction.java` | The action: whole lines of the selection (or the file), one write command. |
| `SortLinesActionTest.java` | `BasePlatformTestCase`: a real editor, the registered action, the result. |

## Teaches

- An IntelliJ plugin without Gradle's IntelliJ plugin: one table, a repository, a `plugin.xml`.
- Testing an action the way the platform does, in a headless IDE.

## Validates

- The intellij plugin: the distribution as a provided classpath, the headless test JVM, and the zip.
- JUnit 3 suites under `jk test`, through the Vintage engine.

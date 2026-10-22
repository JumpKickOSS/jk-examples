# 04-clients/javafx-desktop — a JavaFX desktop app

A loan calculator: type an amount and a rate, slide the term, read the monthly payment and the
interest. One FXML view, a controller, and the arithmetic in a plain record.

```sh
jk build                     # compile, unit tests (no display needed)
jk run                       # opens the window: JavaFX on the module path
```

## The `[javafx]` table

```toml
[javafx]
modules = ["javafx.controls", "javafx.fxml"]

[dependencies]
javafx-controls = { group = "org.openjfx", version = "27.0.1" }
javafx-fxml     = { group = "org.openjfx", version = "27.0.1" }
```

JavaFX runs as named modules. `[javafx]` puts every OpenJFX jar on the module path with
`--add-modules` for `jk run` and the test JVM, the job `javafx-maven-plugin` or the
`org.openjfx.javafxplugin` Gradle plugin does. OpenJFX ships a native jar per platform, so the
lock pins this machine's `javafx-graphics` classifier.

| File | What |
|---|---|
| `Amortization.java` | The payment formula and the total interest, with `BigDecimal` and range checks. Tested on its own. |
| `loan.fxml` | The view: two fields, a slider, two result labels. |
| `LoanController.java` | Recomputes on every edit; shows the reason when the input is out of range. |
| `LoanApp.java` | The `Application`: loads the FXML into a window. |
| `Launcher.java` | The main class, which does not extend `Application` (see below). |

## One jar to hand someone

Add `assembly = true` under `[application]` and `jk build` also writes `target/loans-0.1.0-all.jar`,
which runs with `java -jar`. JavaFX then loads from the classpath, so the main class has to be one
that does not extend `Application`; that's what `Launcher` is for. With a fat jar present,
`jk run` starts that instead.

## Teaches

- A JavaFX app in one `jk.toml`: the OpenJFX dependencies and the `[javafx]` module list.
- Keeping logic out of the UI so tests run without a display.

## Validates

- The javafx plugin: the module path and `--add-modules` for `jk run` and tests, the host classifier.

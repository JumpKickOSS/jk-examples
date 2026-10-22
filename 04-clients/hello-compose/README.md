# 04-clients/hello-compose — the small Android app path

One screen, built the way a new Android app starts: Jetpack Compose with Material 3, a
`ViewModel` holding the screen's state, tests on the JVM, and a debug APK. It starts from
`jk new hello-compose -t android/compose`. There is no Gradle, no AGP and no wrapper: one
`jk.toml`.

```sh
jk build                     # Kotlin + Compose compile, resources, dex → target/hello-compose-0.1.0.apk; tests
jk test                      # JUnit 5 on the logic and the ViewModel, Robolectric on the screen
jk run                       # install and launch on the connected device or running emulator
```

jk provisions the Android SDK pieces it needs (platform, build tools) on first build and pins
them in the lock.

## Layout

| File | What |
|---|---|
| `Greeter.kt` | The rules in plain Kotlin: the greeting for a name, the tally line. |
| `GreetingViewModel.kt` | A `StateFlow<GreetingState>`, updated by `onNameChange` and `greet`. |
| `MainActivity.kt` | `GreetingScreen`: a text field, a button, the greeting and the tally. |
| `GreeterTest.kt`, `GreetingViewModelTest.kt` | JUnit 5, plain JVM: a ViewModel is just a class. |
| `GreetingScreenTest.kt` | Robolectric + `createComposeRule`: type a name, tap, read the screen. |

## Tests without a device

`GreetingScreenTest` renders the real composable on the JVM. That takes Robolectric, JUnit 4
(Robolectric's runner is JUnit 4; declaring it makes jk add the Vintage engine that runs it beside
the JUnit 5 tests), and Compose's `ui-test-junit4` with `ui-test-manifest`, which declares the
activity the rule launches. The android plugin writes
Robolectric's `test_config.properties` and gives the test JVM the JDK flags Robolectric needs, so
there is no `[test]` table.

## Teaches

- An Android app in one `jk.toml`: `[android]` with `compose = true`, the Compose BOM as a platform.
- Where state lives (a `ViewModel`) and how each layer is tested without an emulator.

## Validates

- The android plugin's app path: Kotlin with the Compose compiler, aapt2 resources, d8, the APK.
- Robolectric under `jk test`: the unit-test manifest and resources, and the JDK flags.
- An Android app ships an APK only: no library sources or javadoc jars.

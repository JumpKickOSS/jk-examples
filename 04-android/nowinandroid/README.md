# Now in Android — the Android north-star conformance run

[`android/nowinandroid`](https://github.com/android/nowinandroid) is Google's reference app and
the de-facto conformance suite for the recommended Android stack: Compose, Hilt, Room (with
auto-migrations), protobuf/datastore, kotlinx-serialization, navigation3, a demo/prod
contentType variant dimension, ~25 workspace AAR modules with non-transitive R.

jk builds all 27 modules of it — every `:core:*`, every `feature/*/api+impl`, `sync/work`, and
`:app` — producing a demo-debug APK and a signed R8-full-mode release AAB, with no Gradle and
no AGP.

## Layout

- `overlay/` — the hand-written `jk.toml` per module, the root `[workspace]` manifest (with the
  one repository set every member resolves against), the committed `jk-lock.toml`,
  `jk-guards.toml` (the `android` and `monorepo` packs) with its engine-written
  `jk-guards-baseline.toml`, and the app's `jk-missing-rules.pro` (R8 `-dontwarn` for
  compile-only references, AGP's `missing_rules.txt` equivalent). This is the only jk-specific
  content; NiA's sources are untouched.
- `setup.sh` — clones NiA at the pinned SHA (`2982e63c`, upstream `main` with Navigation3 deep
  linking) into `checkout/` and applies the overlay.
- `run.sh` — accepts the SDK licenses, then builds the app as demo-debug (APK) and demo-release
  (signed AAB); the workspace scheduler builds the 26 upstream modules on the way.

## What it needs

- **Network**: Maven Central, Google Maven, and the Android SDK `repository2` feed. jk provisions
  the SDK components (platform 36, build-tools) into its store on first use; `jk android licenses
  --yes` accepts the licenses. The first run downloads a lot; later runs are warm.
- **Tests are skipped** (`--skip-tests`), and the overlay declares no test dependencies. jk hands
  Robolectric the binary resources (`android_resource_apk`) and the suites start, but the
  manifest Robolectric reads is the module's main merged manifest: a test-scope AAR's manifest
  (`ui-test-manifest`'s `ComponentActivity`) is not merged in, so every `createComposeRule()`
  test fails with "Unable to resolve activity for Intent … ComponentActivity" — a recorded jk
  gap. Most suites also need NiA's `core:testing` / `core:screenshot-testing` modules, which the
  port does not carry, and the instrumented suites need a device.
- **`min-sdk = 24`**, NiA's own floor (Navigation3 1.2's AARs require 24).
- **`java = 21`** on purpose: that is the Android platform's language level; the host JDK 25
  emits it (`java =`, not `jdk =`).

```sh
./setup.sh
./run.sh
# or, one step at a time:
cd checkout/app
jk android licenses --yes
jk build --skip-tests --variant contentType=demo
```

## Known deviations from NiA's Gradle build

Pins follow NiA's `gradle/libs.versions.toml` at the pinned SHA and move with it — do not
`jk update` them. Every version below is either what the lock pins or a deliberate call:

- **Kotlin `2.4.20`** (NiA pins 2.3.0): jk's Build-Tools-API worker floor is 2.4.
- **compile-sdk 36** (matches current NiA): platform is provisioned from the managed SDK.
- **`compose-bom-alpha = 2026.07.01`** (NiA uses `2025.09.01`): the alpha BOM aligns Material3
  adaptive 1.3 + navigation-suite + Compose runtime for Navigation3. A pure *stable* compose-bom
  cannot co-resolve suite 1.4 with adaptive-navigation3 1.3 today — Google's own catalog uses
  the alpha BOM for this stack.
- **Navigation3 `1.2.0`** — NiA's own pin (its deep-link API, `UriDeepLinkMatcher`, is new in
  1.2).
- **Lifecycle `2.11.0`**, **Hilt `2.60.1`**, **Activity Compose `1.13.0`**, **Room `2.8.4`**,
  **Work `2.11.2`** — newer than NiA's catalog, chosen when the port was written; they co-resolve
  with the alpha Compose BOM above.
- **No `runtime-tracing`**: AndroidX POM floors fight Compose runtime under PubGrub; optional
  tracing dep omitted (use `tracing-ktx` only).
- **kotlinx-datetime `0.6.1`** exact (source-level API).
- **`guava` on `sync/work`**: processor-scope guava vs `listenablefuture`.
- **One flavor selection for the workspace**: `--variant contentType=demo` on the app's build
  selects `demo` in every member that declares the dimension (`core/notifications` compiles
  `src/demo`). `jk guard` at the workspace root needs the same `--variant contentType=demo`.
- Skipped Gradle-side machinery: jacoco, roborazzi, baseline-profile, Firebase/oss-licenses
  Gradle plugins (runtime oss-licenses dep is fine).

## Guards on an upstream tree

`overlay/jk-guards.toml` extends `cc.jumpkick.guards:android` and `cc.jumpkick.guards:monorepo`
(both `0.15.3`). NiA logs with `android.util.Log` in six classes (`StubAnalyticsHelper`,
`NiaPreferencesDataSource`, `SyncUtilities`, `StubSyncSubscriber`, `JankStatsModule`,
`ProfileVerifierLogger`), which the android pack bans, so each is a
`[[guards.no-android-log.allow]]` entry with its reason. An entry that stops matching is red, so
the list tracks upstream; any new site fails `jk guard`. Two monorepo rules are baselines in
`overlay/jk-guards-baseline.toml`, written by `jk guard freeze <id> -W "…"`: `converge-versions`
(the catalog's pins and per-module classpaths diverge the way Gradle's do — Lifecycle 2.5 vs 2.11
in `core/analytics`, guava `-android` vs `-jre`, …) and `one-module-per-package` (upstream's
shared packages and Hilt's generated `hilt_aggregated_deps`). Sources stay unmodified.

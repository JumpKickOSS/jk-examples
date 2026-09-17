# Now in Android — the Android north-star conformance run

[`android/nowinandroid`](https://github.com/android/nowinandroid) is Google's reference app and
the de-facto conformance suite for the recommended Android stack: Compose, Hilt, Room (with
auto-migrations), protobuf/datastore, kotlinx-serialization, navigation3, a demo/prod
contentType variant dimension, ~25 workspace AAR modules with non-transitive R.

jk builds all 27 modules of it — every `:core:*`, every `feature/*/api+impl`, `sync/work`, and
`:app` — producing a demo-debug APK and a signed R8-full-mode release AAB, with no Gradle and
no AGP.

## Layout

- `overlay/` — the hand-written `jk.toml` per module, the root `[workspace]` manifest, the
  committed `jk-lock.toml`, `jk-guards.toml` (the `android` pack) with its engine-written
  `jk-guards-baseline.toml`, and the app's `jk-missing-rules.pro` (R8 `-dontwarn` for
  compile-only references, AGP's `missing_rules.txt` equivalent). This is the only jk-specific
  content; NiA's sources are untouched.
- `setup.sh` — clones NiA at the pinned SHA into `checkout/` and applies the overlay.
- `run.sh` — accepts the SDK licenses, then builds the app as demo-debug (APK) and demo-release
  (signed AAB); the workspace scheduler builds the 26 upstream modules on the way.

## What it needs

- **Network**: Maven Central, Google Maven, and the Android SDK `repository2` feed. jk provisions
  the SDK components (platform 36, build-tools) into its store on first use; `jk android licenses
  --yes` accepts the licenses. The first run downloads a lot; later runs are warm.
- **Tests are skipped** (`--skip-tests`): NiA's unit suites need Robolectric binary resources (a
  recorded jk gap) and its instrumented suites need a device.
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

Every version below is either what the lock pins or a deliberate call, each traced to a jk gap:

- **Kotlin `2.4.20`** (NiA pins 2.3.0): jk's Build-Tools-API worker floor is 2.4.
- **compile-sdk 36** (matches current NiA): platform is provisioned from the managed SDK.
- **`compose-bom-alpha = 2026.07.01`** (NiA uses `2025.09.01`): the alpha BOM aligns Material3
  adaptive 1.3 + navigation-suite + Compose runtime for Navigation3. A pure *stable* compose-bom
  cannot co-resolve suite 1.4 with adaptive-navigation3 1.3 today — Google's own catalog uses
  the alpha BOM for this stack.
- **Lifecycle `=2.11.0`**, **Navigation3 `=1.0.1`**, **Hilt `=2.60.1`**, **Activity Compose
  `=1.13.0`**, **Room `=2.8.4`**, **Work `=2.11.2`** — newest stables as of the lock date
  (NiA's catalog is older on several of these).
- **No `runtime-tracing`**: AndroidX POM floors fight Compose runtime under PubGrub; optional
  tracing dep omitted (use `tracing-ktx` only).
- **kotlinx-datetime `=0.6.1`** exact (source-level API).
- **`guava` on `sync/work`**: processor-scope guava vs `listenablefuture`.
- **Flavors select per-module** (`--variant contentType=demo`): workspace-wide variant
  propagation is a recorded follow-up.
- Skipped Gradle-side machinery: jacoco, roborazzi, baseline-profile, Firebase/oss-licenses
  Gradle plugins (runtime oss-licenses dep is fine).

## Guards on an upstream tree

`overlay/jk-guards.toml` extends `cc.jumpkick.guards:android`. NiA's own `StubAnalyticsHelper`
logs with `android.util.Log`, which the pack bans, so `no-android-log` is a baseline here: the
upstream site is recorded in `jk-guards-baseline.toml` (`jk guard freeze … --reason`) and the
port refuses new ones. Sources stay unmodified.

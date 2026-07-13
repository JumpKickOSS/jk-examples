# Now in Android — the Android north-star conformance run

[`android/nowinandroid`](https://github.com/android/nowinandroid) is Google's reference
app and the de-facto conformance suite for the recommended Android stack: Compose,
Hilt, Room (with auto-migrations), protobuf/datastore, kotlinx-serialization,
navigation3, a demo/prod contentType variant dimension, ~25 workspace AAR modules with non-transitive R.

jk builds all 27 modules of it — every `:core:*`, every `feature/*/api+impl`,
`sync/work`, and `:app` — producing a demo-debug APK and a signed R8-full-mode release
AAB, with no Gradle and no AGP. First validated 2026-07-13 (twenty product findings came
out of getting here; see jk's `docs/android-plan.md`).

## Layout

- `overlay/` — the hand-written `jk.toml` per module, the root `[workspace]` manifest,
  and the app's `jk-missing-rules.pro` (R8 `-dontwarn` for compile-only references,
  AGP's `missing_rules.txt` equivalent). This is the only jk-specific content; NiA's
  sources are untouched.
- `setup.sh` — clones NiA at the pinned SHA into `checkout/` and applies the overlay.
- `run.sh` — builds everything in workspace order via the `jk` CLI, then the app as
  demo-debug (APK) and demo-release (signed AAB).

## Known deviations from NiA's Gradle build

Recorded honestly; each traces to a jk gap or a deliberate call:

- **Kotlin `^2.4.0`** (NiA pins 2.3.0): jk's Build-Tools-API worker floor is 2.4.
- **compile-sdk 34** (NiA pins 36): keeps the provisioned platform small; bump per
  module if API-35+ symbols are needed.
- **No `[platform-dependencies]` compose BOM** — compose versions are pinned explicitly.
  jk's BOM pins are currently hard (Gradle's `platform()` is a recommendation);
  the soft-pin semantics is decided but parked on solver work (finding 13).
- **kotlinx-datetime 0.6.1 exact** everywhere (matches NiA's source-level API usage).
- **`guava` added to `sync/work`**: jk resolves all scopes in one graph, so
  processor-scope guava evicts main-scope `listenablefuture:1.0` with the
  `9999.0-empty` artifact (finding 15); guava on main supplies the class either way.
- **Flavors select per-module** (`--variant contentType=demo`): workspace variant
  propagation (the app's selection reaching sibling AAR builds) is a recorded follow-up.
- Skipped Gradle-side machinery with no jk equivalent yet: jacoco, roborazzi,
  baseline-profile generation, the Firebase/oss-licenses Gradle plugins (the
  oss-licenses *runtime* dependency builds fine).

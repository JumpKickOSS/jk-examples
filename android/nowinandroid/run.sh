#!/usr/bin/env bash
# The Now-in-Android conformance run. jk's workspace scheduler builds all 27 modules
# from one entry invocation, applying the entry's variant selection workspace-wide —
# so the whole run is: licenses, one demo-debug build (APK), one demo-release build
# (R8 full mode, signed AAB). Requires a current jk install (see the repo README) and
# network; the first run downloads the Android SDK components and every dependency.
#
# Tests are skipped: NiA's unit suites need Robolectric binary resources (a recorded
# jk gap) and its instrumented suites need a device.
set -euo pipefail
cd "$(dirname "$0")/checkout"

echo "=== jk android licenses"
(cd app && jk android licenses --yes)

echo "=== jk build: workspace (demo debug — APK)"
(cd app && jk build --skip-tests --flavor contentType=demo)

echo "=== jk build: workspace (demo release — R8 full mode, signed AAB)"
KS="$PWD/../release.jks"
if [ ! -f "$KS" ]; then
    keytool -genkeypair -keystore "$KS" -storepass harness-pass -alias upload \
        -keyalg RSA -keysize 2048 -validity 365 -dname CN=jk-examples
fi
# PKCS12: the key password IS the store password.
(cd app && RELEASE_KEYSTORE="$KS" RELEASE_STORE_PASSWORD=harness-pass \
    RELEASE_KEY_PASSWORD=harness-pass jk build --release --skip-tests --flavor contentType=demo)

echo
echo "artifacts:"
ls -la app/target/lib/*.apk app/target/lib/*.aab 2>/dev/null || true

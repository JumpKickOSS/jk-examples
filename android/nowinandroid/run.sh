#!/usr/bin/env bash
# The Now-in-Android conformance run: build all 27 modules in workspace order with the
# real jk CLI, then the app twice — demo-debug APK and a signed demo-release AAB
# (R8 full mode). Requires a current jk install (see the repo README) and network
# (Maven Central + Google Maven + the Android SDK feed; first run downloads a lot).
set -euo pipefail
cd "$(dirname "$0")/checkout"

# Modules in dependency order — the root jk.toml's [workspace] list is authoritative.
MODULES=$(python3 - <<'EOF'
import re
s = open("jk.toml").read()
print("\n".join(re.findall(r'"([^"]+)"', s.split("modules = [")[1].split("]")[0])))
EOF
)

# Android SDK licenses (hash-recorded, same on-disk format as sdkmanager).
jk android licenses --yes 2>/dev/null || true

for m in $MODULES; do
    echo "=== jk build: $m"
    case "$m" in
        core/network|core/analytics|core/notifications|sync/work)
            (cd "$m" && jk build --flavor contentType=demo) ;;
        *)
            (cd "$m" && jk build) ;;
    esac
done

echo "=== jk build: app (demo debug APK)"
(cd app && jk build --flavor contentType=demo)

echo "=== jk build: app (demo release AAB, R8 full mode)"
KS="$PWD/../release.jks"
if [ ! -f "$KS" ]; then
    keytool -genkeypair -keystore "$KS" -storepass harness-pass -alias upload \
        -keyalg RSA -keysize 2048 -validity 365 -dname CN=jk-harness
fi
# PKCS12: the key password IS the store password.
(cd app && RELEASE_KEYSTORE="$KS" RELEASE_STORE_PASSWORD=harness-pass \
    RELEASE_KEY_PASSWORD=harness-pass jk build --release --flavor contentType=demo)

echo
echo "artifacts:"
ls -la app/target/lib/*.apk app/target/lib/*.aab 2>/dev/null || true

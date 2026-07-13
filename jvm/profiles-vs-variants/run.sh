#!/usr/bin/env bash
# One project, three axes: profile (how), feature (what capability), variant (which product).
set -euo pipefail
cd "$(dirname "$0")"
JAR=target/profiles-vs-variants-1.0.0-all.jar

echo "=== debug (default variant), default features"
jk build --skip-tests
java -jar "$JAR"

echo "=== strict profile (same artifact, -Werror compile)"
jk build --skip-tests --profile strict
java -jar "$JAR"

echo "=== release variant (src-release joins the compile)"
# Variant switches share target/ — a class the previous selection added would survive
# until `jk clean` (recorded gap), so the additive release build runs last here.
jk build --skip-tests --release
java -jar "$JAR"

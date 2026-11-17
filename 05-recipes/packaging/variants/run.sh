#!/usr/bin/env bash
# Both variants from one lockfile; a build that selects none builds the default, demo.
set -euo pipefail
cd "$(dirname "$0")"

echo "=== demo variant"
jk build --skip-tests --variant contentType=demo
java -jar target/variants-cli-1.0.0-SNAPSHOT-all.jar

echo "=== prod variant"
jk build --skip-tests --variant contentType=prod
java -jar target/variants-cli-1.0.0-SNAPSHOT-all.jar

echo "=== no selection (the default, demo)"
jk build --skip-tests
out="$(java -jar target/variants-cli-1.0.0-SNAPSHOT-all.jar)"
echo "$out"
if [[ "$out" != *"demo"* ]]; then
    echo "ERROR: a build with no selection should be the demo variant" >&2
    exit 1
fi

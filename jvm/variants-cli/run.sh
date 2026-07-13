#!/usr/bin/env bash
# Both variants from one lockfile; the unselected build fails loudly (mandatory dimension).
set -euo pipefail
cd "$(dirname "$0")"

echo "=== demo variant"
jk build --skip-tests --variant contentType=demo
java -jar target/variants-cli-1.0.0-all.jar

echo "=== prod variant"
jk build --skip-tests --variant contentType=prod
java -jar target/variants-cli-1.0.0-all.jar

echo "=== no selection (must fail)"
if jk build --skip-tests 2>/dev/null; then
    echo "ERROR: build without a selection should have failed" >&2
    exit 1
fi
echo "failed as designed: [variants.contentType] declares no default"

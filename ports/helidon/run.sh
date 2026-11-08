#!/usr/bin/env bash
# Build the Helidon port. REDO=1 ./run.sh rebuilds every step instead of restoring from the action
# cache (the cold bench). Extra arguments go to jk build, e.g. ./run.sh -m webserver/webserver.
set -euo pipefail
cd "$(dirname "$0")"
[ -f checkout/jk.toml ] || ./setup.sh
export PATH="${HOME}/.jk/bin:${PATH}"
cd checkout
args=(build --skip-tests)
[ "${REDO:-}" = "1" ] && args+=(--redo)
echo "== jk ${args[*]} $* =="
jk "${args[@]}" "$@"

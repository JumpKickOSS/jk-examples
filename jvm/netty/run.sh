#!/usr/bin/env bash
# Build the Netty JumpKick port (compile-focused; Mill's comparison baseline).
set -euo pipefail
cd "$(dirname "$0")"

if [ ! -f checkout/jk.toml ]; then
  ./setup.sh
fi

export PATH="${HOME}/.jk/bin:${PATH}"
if ! command -v jk >/dev/null 2>&1; then
  echo "jk not on PATH — install from the jk monorepo first" >&2
  exit 1
fi

cd checkout
echo "== jk lock =="
jk lock
# Mill's published Netty numbers are compile-focused (-DskipTests / __.compile).
# Full Netty unit suites are multi-hour and need curated selection (see PARITY.md / README).
echo "== jk build --skip-tests --redo =="
jk build --skip-tests --redo "$@"
echo "OK: Netty workspace built under target/"
echo "Tip: smoke unit tests on one module: jk test --modules common"

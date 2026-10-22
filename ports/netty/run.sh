#!/usr/bin/env bash
# Build the Netty JumpKick port (compile-focused; Mill's comparison baseline) from the committed lock.
# REDO=1 ./run.sh rebuilds every step instead of restoring from the action cache (the cold bench).
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
# Mill's published Netty numbers are compile-focused (-DskipTests / _.compile).
# Full Netty unit suites are multi-hour and need curated selection (see PARITY.md / README).
args=(build --skip-tests)
if [ "${REDO:-}" = "1" ]; then
  args+=(--redo)
fi
echo "== jk ${args[*]} $* =="
jk "${args[@]}" "$@"
echo "OK: Netty workspace built under target/"
echo "Tip: smoke unit tests on one module: jk test -m common"

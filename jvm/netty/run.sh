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
echo "== jk build --skip-tests =="
jk build --skip-tests "$@"
echo "OK: Netty workspace built under target/"

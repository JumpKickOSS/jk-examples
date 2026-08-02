#!/usr/bin/env bash
# Prepare a Mill workspace for the same Netty pin as the JumpKick port.
# Layout: jvm/netty/mill-workspace/  (gitignored) = netty sources + Mill's build.mill
#
# Usage:
#   ./scripts/prepare-mill-netty.sh
#   MILL_REPO=../../mill NETTY_TAG=netty-4.1.115.Final ./scripts/prepare-mill-netty.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

NETTY_TAG="${NETTY_TAG:-netty-4.1.115.Final}"
NETTY_REPO="${NETTY_REPO:-https://github.com/netty/netty.git}"
# Default: sibling of jk-examples → ../../mill when this tree is oss/jk-examples
MILL_REPO="${MILL_REPO:-}"
if [ -z "$MILL_REPO" ]; then
  for candidate in \
    "$ROOT/../../../mill" \
    "$ROOT/../../mill" \
    "$HOME/src/oss/mill" \
    /home/bsant/src/oss/mill
  do
    if [ -f "$candidate/example/thirdparty/netty/build.mill" ]; then
      MILL_REPO="$candidate"
      break
    fi
  done
fi
if [ -z "${MILL_REPO:-}" ] || [ ! -f "$MILL_REPO/example/thirdparty/netty/build.mill" ]; then
  echo "error: mill repo with example/thirdparty/netty/build.mill not found" >&2
  echo "  set MILL_REPO=/path/to/mill (clone of com-lihaoyi/mill)" >&2
  exit 1
fi
MILL_REPO="$(cd "$MILL_REPO" && pwd)"
WS="$ROOT/mill-workspace"

echo "mill repo:  $MILL_REPO ($(git -C "$MILL_REPO" rev-parse --short HEAD) $(git -C "$MILL_REPO" describe --tags --always 2>/dev/null || true))"
echo "netty tag:  $NETTY_TAG"
echo "workspace:  $WS"

if [ ! -d "$WS/.git" ]; then
  git clone --filter=blob:none "$NETTY_REPO" "$WS"
fi
git -C "$WS" fetch -q --tags origin "refs/tags/${NETTY_TAG}:refs/tags/${NETTY_TAG}" 2>/dev/null \
  || git -C "$WS" fetch -q origin tag "$NETTY_TAG" --no-tags
git -C "$WS" checkout -q -f "$NETTY_TAG"
git -C "$WS" clean -fdq
SHA=$(git -C "$WS" rev-parse HEAD)

# Mill build definition (from latest mill tree)
cp "$MILL_REPO/example/thirdparty/netty/build.mill" "$WS/build.mill"

# Mill launcher from the mill monorepo (bootstraps published mill version)
cp "$MILL_REPO/mill" "$WS/mill"
chmod +x "$WS/mill"

# Prefer a stable published mill for the project (matches what ./mill monorepo just resolved)
# Override with MILL_VERSION=... for experiments.
if [ -n "${MILL_VERSION:-}" ]; then
  printf '%s\n' "$MILL_VERSION" > "$WS/.mill-version"
elif [ -f "$MILL_REPO/.mill-version" ]; then
  cp "$MILL_REPO/.mill-version" "$WS/.mill-version"
else
  # Fall back to latest RC the monorepo launcher reported
  printf '%s\n' "1.2.0-RC1" > "$WS/.mill-version"
fi

# Same JDK/main-source workarounds Mill/Maven need on modern JDKs (compile-only fairness).
# Mill's test forkArgs add --add-exports; javac for handler still needs the OpenJDK util gone
# or exports — match jk setup for a fair main-compile surface.
if [ -d "$WS/transport-sctp/src/main/java/com" ]; then
  rm -rf "$WS/transport-sctp/src/main/java/com"
  echo "stripped transport-sctp com.sun.nio.sctp stubs (JDK module conflict)"
fi
OJSSCG="$WS/handler/src/main/java/io/netty/handler/ssl/util/OpenJdkSelfSignedCertGenerator.java"
if [ -f "$OJSSCG" ]; then
  rm -f "$OJSSCG"
  echo "stripped OpenJdkSelfSignedCertGenerator (needs javac --add-exports)"
fi
# Companion BC-only SelfSignedCertificate (same as jk setup.sh) so handler compiles without OpenJDK util.
SSC_PATCH="$ROOT/overlay/patches/SelfSignedCertificate.java"
if [ -f "$SSC_PATCH" ]; then
  cp "$SSC_PATCH" \
    "$WS/handler/src/main/java/io/netty/handler/ssl/util/SelfSignedCertificate.java"
  echo "applied SelfSignedCertificate BC-only patch"
fi

printf '%s\n' "$NETTY_TAG" > "$WS/.jk-netty-tag"
printf '%s\n' "$SHA" > "$WS/.jk-netty-sha"
printf '%s\n' "$MILL_REPO" > "$WS/.mill-repo-path"

echo "mill-workspace ready (tag $NETTY_TAG, sha $SHA)"
echo "Next: cd mill-workspace && ./mill -j 1 _.compile   # main only (fair vs jk --skip-tests)"

#!/usr/bin/env bash
# Clone Helidon at the pinned tag, convert its Maven build with `jk import`, then apply the hand
# edits in overlay/. Every file in overlay/ is an import gap; PARITY.md lists each one.
# Checkout lands in ./checkout (gitignored). Re-running starts from a clean tree.
set -euo pipefail
cd "$(dirname "$0")"

HELIDON_TAG="${HELIDON_TAG:-27.0.1}"
HELIDON_REPO="${HELIDON_REPO:-https://github.com/helidon-io/helidon.git}"

export PATH="${HOME}/.jk/bin:${PATH}"
command -v jk >/dev/null 2>&1 || { echo "jk not on PATH" >&2; exit 1; }

if [ ! -d checkout/.git ]; then
  git clone -q --filter=blob:none --depth 1 --branch "$HELIDON_TAG" "$HELIDON_REPO" checkout
fi
git -C checkout fetch -q --depth 1 origin tag "$HELIDON_TAG" --no-tags
git -C checkout checkout -q -f "$HELIDON_TAG"
git -C checkout clean -fdxq
SHA=$(git -C checkout rev-parse HEAD)

# The conversion itself: Helidon's root POM, its whole default reactor.
(cd checkout && jk import -a --report ../import-report.md pom.xml)

# Hand edits: what jk import does not (yet) produce. overlay/ adds files; patches/ edit the
# manifests the import wrote. Each one is an import gap listed in PARITY.md.
cp -a overlay/. checkout/
for p in patches/*.patch; do
  [ -e "$p" ] && git -C checkout apply "../$p"
done
[ -f jk-lock.toml ] && cp jk-lock.toml checkout/jk-lock.toml

printf '%s\n' "$HELIDON_TAG" > checkout/.jk-helidon-tag
printf '%s\n' "$SHA" > checkout/.jk-helidon-sha
echo "helidon ready at checkout/ (tag $HELIDON_TAG, $SHA)"
echo "Next: ./run.sh   (or: cd checkout && jk build --skip-tests)"

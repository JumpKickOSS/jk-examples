#!/usr/bin/env bash
# Clone Now in Android at the validated SHA and overlay the jk build files.
# The clone lands in ./checkout (gitignored); re-running refreshes the overlay only.
set -euo pipefail
cd "$(dirname "$0")"

NIA_SHA=7d45eae4f8720a0c77f507712ba2437ff974b6ed
NIA_REPO=https://github.com/android/nowinandroid.git

if [ ! -d checkout/.git ]; then
    git clone --filter=blob:none "$NIA_REPO" checkout
fi
git -C checkout fetch -q origin "$NIA_SHA"
git -C checkout checkout -q "$NIA_SHA"

# Overlay: the hand-written jk.tomls (root workspace + 27 modules) and the app's
# R8 missing-rules file. Paths mirror the NiA tree.
cp -r overlay/. checkout/

echo "nowinandroid ready at checkout/ (SHA $NIA_SHA, overlay applied)"

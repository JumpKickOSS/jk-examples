#!/usr/bin/env bash
# reimport-check.sh BUILD_FILE: import the build again from a clean copy of this directory and
# require every committed jk.toml to come out the same. Run from the recipe directory.
set -euo pipefail
build_file="${1:?usage: reimport-check.sh <pom.xml | settings.gradle.kts>}"
scratch="$(mktemp -d)"
trap 'rm -rf "$scratch"' EXIT
rsync -a --exclude target --exclude build --exclude .gradle --exclude jk.toml --exclude jk-lock.toml \
  --exclude AGENTS.md ./ "$scratch/"
(cd "$scratch" && jk -q import "$build_file" >/dev/null)
status=0
while IFS= read -r manifest; do
  if ! diff -u "$manifest" "$scratch/$manifest"; then status=1; fi
done < <(find . -name jk.toml -not -path './target/*' | sort)
[ "$status" -eq 0 ] && echo "re-import matches every committed jk.toml"
exit "$status"

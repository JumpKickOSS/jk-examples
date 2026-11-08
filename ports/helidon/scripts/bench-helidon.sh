#!/usr/bin/env bash
# Maven vs jk walls on the same Helidon tag, one machine. Maven builds its own clean clone
# (maven-checkout/, gitignored) because both tools write target/ at the same paths; jk builds the
# imported checkout/. Results go to stdout as Markdown; RESULTS.md is a copy of one run.
#
# Usage:
#   ./scripts/bench-helidon.sh            # both tools
#   ./scripts/bench-helidon.sh jk         # jk only
#   ./scripts/bench-helidon.sh maven      # Maven only
#   RUNS=1 ./scripts/bench-helidon.sh     # one run per scenario (default 3)
#
# Fairness:
# - Dependencies are cached for both: ~/.m2 is primed by one untimed Maven build, jk's store by
#   setup.sh/run.sh. Cold means build outputs gone (mvn clean / target/ removed + jk --redo, so
#   jk's action cache cannot restore classes), not an empty dependency cache.
# - Each tool runs what its own build runs by default: Maven `package` with Helidon's bound plugins
#   (enforcer, dependency analysis, services, ...), jk `build` (which also writes a sources jar per
#   library module; javadoc jars are written by jk publish / jk javadoc, as Maven writes them only
#   in a release). The flags are printed with the results.
# - A dirty run appends a unique comment to one source, so the content changes on every run:
#   a touch alone is a no-op to jk's content hashing but a recompile to Maven's staleness check.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

MODE="${1:-both}"
RUNS="${RUNS:-3}"
export PATH="${HOME}/.jk/bin:${PATH}"
MVN="${MVN:-mvn}"
MVN_ARGS=(-B -q -T1C -DskipTests)
JAVA27="${JAVA27:-$HOME/.jdks/temurin-27}"
LEAF="webserver/static-content/src/main/java/io/helidon/webserver/staticcontent/StaticContentFeature.java"
MID="common/common/src/main/java/io/helidon/common/LazyValue.java"
LOG="$ROOT/bench-results/cmd.log"
mkdir -p "$ROOT/bench-results"

median() { sort -n | awk '{a[NR]=$1} END{if(NR==0)print 0; else if(NR%2)print a[(NR+1)/2]; else print (a[NR/2]+a[NR/2+1])/2}'; }

# time_cmd LABEL PREP -- CMD...   (PREP may be ":"; it runs untimed before every iteration)
time_cmd() {
  local label=$1 prep=$2; shift 3
  local times=() i t0 t1
  for ((i=1;i<=RUNS;i++)); do
    BENCH_RUN=$i bash -c "$prep" >>"$LOG" 2>&1 || true
    t0=$(date +%s%3N)
    if ! "$@" >>"$LOG" 2>&1; then
      echo "  ! failed (run $i): $*" >&2; tail -20 "$LOG" >&2; times+=(-1); continue
    fi
    t1=$(date +%s%3N); times+=($((t1 - t0)))
  done
  local ok med
  ok=$(printf '%s\n' "${times[@]}" | grep -v '^-' || true)
  if [ -z "$ok" ]; then
    printf '| %s | failed | %s |\n' "$label" "$(printf '%s ' "${times[@]}")"
    return 0
  fi
  med=$(printf '%s\n' "$ok" | median)
  printf '| %s | %.1f | %s |\n' "$label" "$(echo "$med/1000" | bc -l)" "$(printf '%s ' "${times[@]}")"
}

# dirty FILE: append a comment unique to this run, so the content differs every time
dirty() { echo "printf '\n// bench %s %s\n' \"\$BENCH_RUN\" \"\$(date +%s%N)\" >> '$1'"; }

header() {
  echo "# Helidon Maven vs jk ($(date -u +%Y-%m-%dT%H:%MZ))"
  echo
  echo "- host: $(hostname) | $(uname -srm) | $(nproc) threads | $(free -g | awk '/Mem:/{print $2}') GB"
  echo "- java: $("$JAVA27/bin/java" -version 2>&1 | head -1)"
  echo "- runs per scenario: $RUNS (median; all runs in ms)"
  echo
}

run_jk() {
  [ -f checkout/jk.toml ] || ./setup.sh
  ( cd checkout
    echo "## jk"; echo
    echo "- jk: $(jk --version | head -1); tag $(cat .jk-helidon-tag)"
    echo "- flags: \`jk build --skip-tests\`; tests \`jk test -t report -m webserver/webserver\`"; echo
    echo "| scenario | median s | runs ms |"; echo "|---|---:|---|"
    jk build --skip-tests >>"$LOG" 2>&1   # prime: lock, store, workers
    time_cmd "cold (targets removed, --redo)" \
      "find . -name target -type d -prune -not -path './.git/*' -exec rm -rf {} +" -- \
      jk build --skip-tests --redo
    time_cmd "warm no-op" ":" -- jk build --skip-tests
    time_cmd "dirty leaf (static-content, 1 file)" "$(dirty "$LEAF")" -- jk build --skip-tests
    time_cmd "dirty mid-graph (common/common, 1 file)" "$(dirty "$MID")" -- jk build --skip-tests
    time_cmd "tests, one module (webserver)" ":" -- jk test -t report -m webserver/webserver
    git checkout -q -- "$LEAF" "$MID"
    echo )
}

run_maven() {
  if [ ! -d maven-checkout/.git ]; then
    git clone -q --depth 1 --branch "$(cat checkout/.jk-helidon-tag 2>/dev/null || echo 27.0.1)" \
      "$(git -C checkout remote get-url origin)" maven-checkout
  fi
  ( cd maven-checkout
    export JAVA_HOME="$JAVA27" PATH="$JAVA27/bin:$PATH"
    echo "## Maven"; echo
    echo "- $("$MVN" -v 2>/dev/null | head -1)"
    echo "- flags: \`mvn ${MVN_ARGS[*]} package\`; tests \`mvn -B -q -pl webserver/webserver test\`"; echo
    echo "| scenario | median s | runs ms |"; echo "|---|---:|---|"
    "$MVN" "${MVN_ARGS[@]}" package >>"$LOG" 2>&1   # prime ~/.m2
    time_cmd "cold (mvn clean first)" "\"$MVN\" -B -q clean" -- "$MVN" "${MVN_ARGS[@]}" package
    time_cmd "warm no-op" ":" -- "$MVN" "${MVN_ARGS[@]}" package
    time_cmd "dirty leaf (static-content, 1 file)" "$(dirty "$LEAF")" -- "$MVN" "${MVN_ARGS[@]}" package
    time_cmd "dirty mid-graph (common/common, 1 file)" "$(dirty "$MID")" -- "$MVN" "${MVN_ARGS[@]}" package
    time_cmd "tests, one module (webserver)" ":" -- "$MVN" -B -q -pl webserver/webserver test
    git checkout -q -- "$LEAF" "$MID"
    echo )
}

header
case "$MODE" in
  jk) run_jk ;;
  maven) run_maven ;;
  *) run_jk; run_maven ;;
esac

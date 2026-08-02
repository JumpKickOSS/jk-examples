#!/usr/bin/env bash
# Head-to-head cold/warm/dirty **compile** times: JumpKick vs Mill (same Netty pin).
# Tier 1 fairness: main sources only — no unit tests (Mill tables use compile focus).
#
# Usage:
#   ./scripts/bench-netty.sh              # both tools if mill-workspace present
#   ./scripts/bench-netty.sh jk           # JumpKick only
#   ./scripts/bench-netty.sh mill         # Mill only
#   RUNS=3 PARALLEL=0 ./scripts/bench-netty.sh   # sequential workers
#
# Prep:
#   ./setup.sh                            # jk checkout/
#   ./scripts/prepare-mill-netty.sh       # mill-workspace/ from ../mill
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

MODE="${1:-both}"   # both | jk | mill
RUNS="${RUNS:-3}"
# PARALLEL=0 → serial (-j 1); empty → tool default parallelism
PARALLEL="${PARALLEL-}"
JK_BIN="${JK_BIN:-jk}"
export PATH="${HOME}/.jk/bin:${PATH}"

median() {
  sort -n | awk '{a[NR]=$1} END{if(NR==0)print 0; else if(NR%2)print a[(NR+1)/2]; else print (a[NR/2]+a[NR/2+1])/2}'
}

# time_cmd LABEL [PREP_CMD...] -- CMD...
# If PREP is given (before --), it runs before every timed iteration (e.g. rm -rf out).
time_cmd() {
  local label=$1; shift
  local prep=()
  if [[ "$*" == *' -- '* ]] || [[ "$1" == '--' ]]; then
    while [ $# -gt 0 ] && [ "$1" != '--' ]; do
      prep+=("$1"); shift
    done
    [ "${1:-}" = '--' ] && shift
  fi
  local times=() i t0 t1 rc=0
  for ((i=1;i<=RUNS;i++)); do
    if [ ${#prep[@]} -gt 0 ]; then
      "${prep[@]}" || true
    fi
    t0=$(date +%s%3N)
    if "$@" >/tmp/netty-bench-cmd.log 2>&1; then
      :
    else
      rc=$?
      echo "  ! command failed (run $i, exit $rc): $*" >&2
      tail -20 /tmp/netty-bench-cmd.log >&2 || true
      times+=(-1)
      continue
    fi
    t1=$(date +%s%3N)
    times+=($((t1 - t0)))
  done
  local med
  med=$(printf '%s\n' "${times[@]}" | grep -v '^-' | median)
  printf '| %s | %s | %s |\n' "$label" "$med" "$(printf '%s ' "${times[@]}")"
}

meta_header() {
  echo "# Netty compile bench ($(date -u +%Y-%m-%dT%H:%MZ))"
  echo
  echo "- host: $(hostname) | $(uname -srm)"
  echo "- cpu: $(nproc) threads"
  echo "- java: $(java -version 2>&1 | head -1)"
  echo "- runs per scenario: $RUNS"
  echo "- parallel: ${PARALLEL:-tool-default}"
  echo
}

jk_jobs=()
mill_jobs=()
if [ -n "$PARALLEL" ]; then
  if [ "$PARALLEL" = "0" ] || [ "$PARALLEL" = "1" ]; then
    jk_jobs=(-j 1)
    mill_jobs=(-j 1)
  else
    jk_jobs=(-j "$PARALLEL")
    mill_jobs=(-j "$PARALLEL")
  fi
fi

run_jk() {
  if [ ! -f checkout/jk.toml ]; then
    echo "jk checkout missing — running ./setup.sh" >&2
    ./setup.sh
  fi
  (
    cd checkout
    echo "## JumpKick"
    echo
    echo "- jk: $($JK_BIN --version 2>/dev/null | head -1)"
    echo "- tag: $(cat .jk-netty-tag 2>/dev/null || echo unknown)"
    echo "- sha: $(cat .jk-netty-sha 2>/dev/null || echo unknown)"
    echo
    echo "| scenario | median_ms | runs_ms |"
    echo "|----------|----------:|---------|"
    # Cold recompile: wipe target/ + --redo so CAS action cache cannot restore
    # classfiles (matches Mill wiping out/ and recompiling). Deps stay cached.
    time_cmd "jk cold  build --skip-tests --redo" \
      bash -c 'rm -rf target' -- \
      $JK_BIN "${jk_jobs[@]}" build --skip-tests --redo
    # Warm no-op: leave target/ intact, no --redo
    time_cmd "jk warm  build --skip-tests" \
      $JK_BIN "${jk_jobs[@]}" build --skip-tests
    local touch_file=common/src/main/java/io/netty/util/AbstractConstant.java
    [ -f "$touch_file" ] || touch_file=$(find common/src/main/java -name '*.java' | head -1)
    # Dirty: touch one source before every run (do not wipe target)
    time_cmd "jk dirty common (1 file)" \
      touch "$touch_file" -- \
      $JK_BIN "${jk_jobs[@]}" build --skip-tests -m common
    echo
  )
}

run_mill() {
  if [ ! -f mill-workspace/build.mill ]; then
    echo "mill-workspace missing — running ./scripts/prepare-mill-netty.sh" >&2
    ./scripts/prepare-mill-netty.sh
  fi
  (
    cd mill-workspace
    echo "## Mill"
    echo
    echo "- mill launcher: $(head -1 .mill-version 2>/dev/null || echo default)"
    echo "- mill --version:"
    ./mill --version 2>&1 | sed 's/^/  /' | head -6
    echo "- tag: $(cat .jk-netty-tag 2>/dev/null || echo unknown)"
    echo "- sha: $(cat .jk-netty-sha 2>/dev/null || echo unknown)"
    echo "- mill-repo: $(cat .mill-repo-path 2>/dev/null || echo unknown)"
    echo
    echo "| scenario | median_ms | runs_ms |"
    echo "|----------|----------:|---------|"
    # Cold: wipe mill out/
    # _.compile = top-level main sources only (excludes *.test.compile).
    # Matches jk build --skip-tests (no test compile). __.compile would also build tests.
    time_cmd "mill cold  _.compile" \
      bash -c 'rm -rf out' -- \
      ./mill "${mill_jobs[@]}" _.compile
    time_cmd "mill warm  _.compile" \
      ./mill "${mill_jobs[@]}" _.compile
    local touch_file=common/src/main/java/io/netty/util/AbstractConstant.java
    [ -f "$touch_file" ] || touch_file=$(find common/src/main/java -name '*.java' | head -1)
    time_cmd "mill dirty common.compile" \
      touch "$touch_file" -- \
      ./mill "${mill_jobs[@]}" common.compile
    echo
  )
}

meta_header
case "$MODE" in
  jk)   run_jk ;;
  mill) run_mill ;;
  both)
    run_jk
    run_mill
    ;;
  *)
    echo "usage: $0 [both|jk|mill]" >&2
    exit 2
    ;;
esac

echo "Record rows in the jk monorepo docs/perf/netty-benchmark.md with machine + tool versions."

#!/usr/bin/env bash
# Cold / warm / dirty wall times for the Netty JumpKick port (JK-1175).
# Usage: ./scripts/bench-netty.sh
# Optional: RUNS=3 JK_BIN=jk
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
[[ -f checkout/jk.toml ]] || ./setup.sh
cd checkout
JK_BIN="${JK_BIN:-jk}"
RUNS="${RUNS:-3}"

median() {
  sort -n | awk '{a[NR]=$1} END{if(NR==0)print 0; else if(NR%2)print a[(NR+1)/2]; else print (a[NR/2]+a[NR/2+1])/2}'
}

time_cmd() {
  local label=$1; shift
  local times=() i t0 t1
  for ((i=1;i<=RUNS;i++)); do
    t0=$(date +%s%3N)
    "$@" >/dev/null 2>&1 || true
    t1=$(date +%s%3N)
    times+=($((t1-t0)))
  done
  local med
  med=$(printf '%s\n' "${times[@]}" | median)
  printf '| %s | %s | %s |\n' "$label" "$med" "$(printf '%s ' "${times[@]}")"
}

echo "# netty jk bench ($(date -u +%Y-%m-%dT%H:%MZ))"
echo
echo "- tag: $(cat .jk-netty-tag 2>/dev/null || echo unknown)"
echo "- sha: $(cat .jk-netty-sha 2>/dev/null || echo unknown)"
echo "- jk: $($JK_BIN --version 2>/dev/null | head -1)"
echo "- java: $(java -version 2>&1 | head -1)"
echo
echo "| scenario | median_ms | runs_ms |"
echo "|----------|----------:|---------|"

# Cold: wipe targets
rm -rf target
time_cmd "cold full build --skip-tests" $JK_BIN build --skip-tests

# Warm no-op
time_cmd "warm no-op build --skip-tests" $JK_BIN build --skip-tests

# Dirty single file in common
touch common/src/main/java/io/netty/util/AbstractConstant.java 2>/dev/null || \
  find common/src/main/java -name '*.java' | head -1 | xargs touch
time_cmd "dirty common (touch one source)" $JK_BIN build --skip-tests --modules common

echo
echo "Record rows in jk monorepo docs/perf/netty-benchmark.md with machine + tool versions."

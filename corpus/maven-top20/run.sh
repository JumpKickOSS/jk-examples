#!/bin/bash
# Thin wrapper. Default is --jk-only (Maven numbers reused from the last row); pass --both to re-measure Maven.
# ./run.sh [--only <repo>]... [--both] [--order small-first] [--render] [--skip-mvn] [--fresh-m2] [--steps import,lock,build | --no-tests]
exec python3 "$(dirname "$(readlink -f "$0")")/run.py" "$@"

#!/bin/bash
# Thin wrapper: ./run.sh [--only <repo>]... [--order small-first] [--render] [--skip-mvn] [--fresh-m2]
exec python3 "$(dirname "$(readlink -f "$0")")/run.py" "$@"

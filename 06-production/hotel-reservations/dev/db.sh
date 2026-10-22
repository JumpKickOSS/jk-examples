#!/usr/bin/env bash
# PostgreSQL 18 for `jk dev`: the same image production runs, on localhost:5432, data kept in a
# named volume between sessions. Uses docker, or podman when docker is absent (CONTAINER_CLI to
# choose). Runs in the foreground, so jk dev's Ctrl-C stops it.
set -euo pipefail
cli="${CONTAINER_CLI:-$(command -v docker || command -v podman || true)}"
if [[ -z "$cli" ]]; then
  echo "db: neither docker nor podman is on PATH; install one, or point DATABASE_URL at a PostgreSQL 18" >&2
  exit 1
fi
"$cli" rm -f kestrel-vine-db >/dev/null 2>&1 || true
exec "$cli" run --rm --name kestrel-vine-db -p 5432:5432 \
  -e POSTGRES_DB=hotel -e POSTGRES_USER=hotel -e POSTGRES_PASSWORD=hotel \
  -v kestrel-vine-db:/var/lib/postgresql \
  docker.io/library/postgres:18-alpine

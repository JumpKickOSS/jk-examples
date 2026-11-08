# ports/helidon — Helidon 27 from Maven to jk, measured against Maven

**Helidon's own repository**, its whole default Maven reactor, converted with `jk import` and built
by jk: 223 modules, about 4,300 Java sources, ~200 modules running annotation processors that are
built in the same workspace. It shows three things at once:

1. **A new JDK by declaration.** Helidon 27 needs JDK 27. The imported root says `java = 27`; on a
   machine without JDK 27 the first build installs it, no manual step:

   ```text
   $ ./run.sh
   W installed JDK temurin-27.0.1.0.2 to ~/.jdks/temurin-27.0.1.0.2
   OK build helidon-project
   ```

2. **Importing a large Maven build.** `setup.sh` runs `jk import` on the checkout every time, so
   this port measures what jk import produces today. What it does not produce is kept as hand edits
   in `overlay/` and `patches/`, each one a filed jk import gap ([PARITY.md](PARITY.md)). The size
   of that list is the import's quality metric.
3. **A/B against Maven** on the same tree, in [RESULTS.md](RESULTS.md) (`scripts/bench-helidon.sh`):
   after the first build jk is 3–26× faster (no-op under a second against Maven's ~21 s, a one-file
   edit 5–7 s against ~21 s); the cold build is slower (35 s against 23 s, tracked in jk).

| Pin | Value |
|-----|-------|
| **Tag** | Helidon `27.0.1` |
| **JDK** | 27 (`java = 27`, installed by jk on first use) |
| **Layout** | `setup.sh` → `checkout/` (gitignored): clone, `jk import`, `overlay/`, `patches/`, `jk-lock.toml` |

## Run it

```bash
./setup.sh           # clone the tag, jk import, apply the hand edits
./run.sh             # jk build --skip-tests over the whole workspace
REDO=1 ./run.sh      # every step again (the cold bench)
cd checkout && jk test -m webserver/webserver -m config/config
```

## Layout

```text
ports/helidon/
  setup.sh, run.sh          # convert and build
  scripts/bench-helidon.sh  # Maven vs jk walls → RESULTS.md
  overlay/                  # files jk import does not write (hand edits)
  patches/                  # edits to manifests jk import wrote (hand edits)
  jk-lock.toml              # the committed lock
  import-report.md          # jk import's report for this tag
  MODULE_MAP.md, PARITY.md, RESULTS.md
```

See [MODULE_MAP.md](MODULE_MAP.md) for what the workspace holds and [PARITY.md](PARITY.md) for the
hand edits and the jk fixes this port drove.

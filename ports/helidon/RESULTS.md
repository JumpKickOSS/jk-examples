# Results — Helidon 27.0.1, Maven vs jk

One machine, one tag, three runs per scenario (median; every run's wall in the raw files under
`bench-results/` when you re-run). Produced by `scripts/bench-helidon.sh` on 2026-11-08.

- **Host:** 24 threads, 30 GB, Linux x86_64 (Fedora, kernel 7.2)
- **JDK:** Temurin 27.0.1 for both tools
- **Maven:** 3.9.15, `mvn -B -q -T1C -DskipTests package` in a separate clean clone of the tag;
  tests `mvn -B -q -pl webserver/webserver test`
- **jk:** 0.50.4-SNAPSHOT (main `7047b121b`), `jk build --skip-tests` on the imported checkout;
  tests `jk test -m webserver/webserver`
- **Caches:** dependencies cached for both (one untimed priming build each). *Cold* means build
  outputs gone: `mvn clean` for Maven; every `target/` removed plus `--redo` for jk, so its action
  cache cannot restore anything.
- **Dirty edits** append a unique comment to one source each run, so the content really changes
  (a `touch` would be a no-op to jk's content hashing and a recompile to Maven's staleness check).

## Walls (seconds, median of 3)

| Scenario | Maven | jk (javadoc jars off) | jk (default: javadoc jars on) |
|---|---:|---:|---:|
| Cold build, whole reactor (223 modules) | **23.1** | 35.2 | 66.4 |
| Warm no-op | 20.8 | **0.8** | **0.8** |
| Dirty leaf (`webserver/static-content`, 1 file) | 20.7 | **4.7** | 5.3 |
| Dirty mid-graph (`common/common`, 1 file) | 22.4 | **6.7** | 6.9 |
| Tests, one module (`webserver`) | 16.0 | **5.4** | 5.7 |

The "javadoc jars off" column is the like-for-like one: Maven's `package` writes main jars only,
while jk writes a sources jar and a javadoc jar for every library module by default
(`JAVADOC=0 ./scripts/bench-helidon.sh jk` sets `javadoc = false` in every module for the run).

## Reading it

- **Every build after the first is 3–26× faster under jk.** A no-op is under a second because jk
  hashes inputs and replays recorded steps; Maven re-walks 220 modules and re-runs its bound
  plugins (enforcer, dependency analysis, Helidon's services plugin) every time. A one-file edit
  recompiles that module and only the dependents whose compile inputs (the edited module's ABI)
  changed; Maven's reactor rebuilds every module downstream and repackages all of them.
- **The cold build is slower under jk** — 35 s against 23 s like-for-like. jk's compile step sums
  184 s of work across 223 modules at about 5.4× effective parallelism; the per-module compile
  carries jk's incremental bookkeeping (Zinc analysis, generated-file provenance, the reference
  index) that makes the warm and dirty columns possible. Tracked as JK-4712.
- **The one-module test row is not quite the same work.** `mvn -pl webserver/webserver test` runs
  against the *published* 27.0.1 jars of every upstream module from `~/.m2`; `jk test -m` builds
  (or restores) the upstream workspace modules and runs on them. jk's first test run, which
  compiles the test sources, took 26–30 s; the median is a warm re-run.
- **Dev loop** (`helidon dev` vs `jk dev`) is not measured here: the reactor is libraries, not an
  app. The `02-services/helidon-petshop` scenario covers `jk dev` on a Helidon app.

## Re-run

```bash
./setup.sh
RUNS=3 ./scripts/bench-helidon.sh                 # both tools, jk with javadoc jars (default)
JAVADOC=0 RUNS=3 ./scripts/bench-helidon.sh jk    # jk without javadoc jars
./setup.sh                                         # restore the imported manifests afterwards
```

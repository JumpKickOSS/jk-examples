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

Run 4, 2026-11-08, jk `origin/main` 7f6c7a645 (resident multi-threaded compiler worker, javadoc
opt-in, ETA priced beside the build). Raw: `bench-results/run4.md`.

| Scenario | Maven | jk |
|---|---:|---:|
| Cold build, whole reactor (223 modules) | 22.0 | **15.0** |
| Warm no-op | 20.7 | **0.8** |
| Dirty leaf (`webserver/static-content`, 1 file) | 20.6 | **4.5** |
| Dirty mid-graph (`common/common`, 1 file) | 22.2 | **7.4** |
| Tests, one module (`webserver`) | 16.0 | **2.3** |

The first cold run after the engine starts, with the compiler worker's JIT still cold, took 20.3 s;
the median is the next two.

### How the cold build got here

Run 2 (jk 7047b121b) took 66.4 s by default and 35.2 s with javadoc off, against Maven's 23.1 s.
What changed, each measured on this reactor:

| Change | Ticket | Effect |
|---|---|---|
| `jk build` writes no javadoc jar unless a module opts in (`jk publish` / `jk javadoc` still do) | JK-4714 | 66 s → 35 s |
| The ETA is priced beside the build, not before it | JK-4716 | first compile at 1.2 s instead of 4.9 s |
| One resident compiler JVM per JDK runs every compile on its own threads, instead of one cold JVM per concurrent compile (17 per build) | JK-4718 | 26–40 s → 15–21 s |
| The worker's heap follows its concurrency and admits compiles by estimated demand | JK-4720 | no memory-cap kills mid-build |

## Reading it

- **Every scenario is faster under jk.** A no-op is under a second because jk hashes inputs and
  replays recorded steps; Maven re-walks 220 modules and re-runs its bound plugins every time. A
  one-file edit recompiles that module and only the dependents whose compile inputs (the edited
  module's ABI) changed.
- **The cold build is critical-path bound** (a 20-module chain from `metadata` through `webserver`
  to `grpc-security`). jk's compiles on that chain now run on a warm, shared JIT, as Maven's do in
  its one JVM, and jk starts the first one about a second in.
- **The one-module test row is not quite the same work.** `mvn -pl webserver/webserver test` runs
  against the *published* 27.0.1 jars of every upstream module from `~/.m2`; `jk test -m` builds
  (or restores) the upstream workspace modules and runs on them. jk's first test run, which
  compiles the test sources, took 26.9 s; the median is a warm re-run.
- **Dev loop** (`helidon dev` vs `jk dev`) is not measured here: the reactor is libraries, not an
  app. The `02-services/helidon-petshop` scenario covers `jk dev` on a Helidon app.

## Re-run

```bash
./setup.sh
RUNS=3 ./scripts/bench-helidon.sh                 # both tools, jk with javadoc jars (default)
./setup.sh                                         # restore the imported manifests afterwards
```

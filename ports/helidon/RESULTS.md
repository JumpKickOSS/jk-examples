# Results — Helidon 27.0.1, Maven vs jk

One machine, one tag, three runs per scenario (median; every run's wall in the raw files under
`bench-results/` when you re-run). Produced by `scripts/bench-helidon.sh` on 2026-11-08 and 2026-11-09.

- **Host:** 24 threads, 30 GB, Linux x86_64 (Fedora, kernel 7.2)
- **JDK:** Temurin 27.0.1 for both tools
- **Maven:** 3.9.15, `mvn -B -q -T1C -DskipTests package` in a separate clean clone of the tag;
  tests `mvn -B -q -pl webserver/webserver test`
- **jk:** 0.50.4-SNAPSHOT (main `d96e985b0` for run 6), `jk build --skip-tests` on the imported checkout;
  tests `jk test -m webserver/webserver`
- **Caches:** dependencies cached for both (one untimed priming build each). *Cold* means build
  outputs gone: `mvn clean` for Maven; every `target/` removed plus `--redo` for jk, so its action
  cache cannot restore anything.
- **Dirty edits** append a unique comment to one source each run, so the content really changes
  (a `touch` would be a no-op to jk's content hashing and a recompile to Maven's staleness check).

## Walls (seconds, median of 3)

Run 6, 2026-11-09, jk `origin/main` d96e985b0 (the end of epic JK-4727). Raw: `bench-results/run6.md`.

| Scenario | Maven | jk |
|---|---:|---:|
| Cold build, whole reactor (223 modules) | 23.3 | **12.9** |
| Warm no-op | 21.6 | **0.8** |
| Dirty leaf (`webserver/static-content`, 1 file) | 21.8 | **1.5** |
| Dirty mid-graph (`common/common`, 1 file) | 23.3 | **2.2** |
| Tests, one module (`webserver`) | 16.2 | **1.1** |

The first cold run after the engine starts, with the compiler worker's JIT still cold, took 14.9 s;
the median is the next two. A project's compiler worker now stays warm while the project is worked
on (any build, test, MCP or web UI request about it, or an edit to its sources, restarts a
10-minute window, 30 minutes on CI), so the ~6 s cold-worker cost is paid after a pause, not between
builds (JK-4765).

Run 5 (jk 8a064b3f2): Maven 25.0 / 24.1 / 23.7 / 25.7 / 16.6, jk 14.1 / 0.8 / 1.8 / 2.3 / 1.2.
Run 4 (jk 7f6c7a645): Maven 22.0 / 20.7 / 20.6 / 22.2 / 16.0, jk 15.0 / 0.8 / 4.5 / 7.4 / 2.3.

### How it got here

Run 2 (jk 7047b121b) took 66.4 s by default and 35.2 s with javadoc off, against Maven's 23.1 s.
What changed, cold build first, each measured on this reactor:

| Change | Ticket | Effect |
|---|---|---|
| `jk build` writes no javadoc jar unless a module opts in (`jk publish` / `jk javadoc` still do) | JK-4714 | 66 s → 35 s |
| The ETA is priced beside the build, not before it | JK-4716 | first compile at 1.2 s instead of 4.9 s |
| One resident compiler JVM per JDK runs every compile on its own threads, instead of one cold JVM per concurrent compile (17 per build) | JK-4718 | 26–40 s → 15–21 s |
| The worker's heap follows its concurrency and admits compiles by estimated demand | JK-4720 | no memory-cap kills mid-build |
| The forecast walks modules by dependency, the residual schedule is linear, lock freshness and the member index are asked once per request | JK-4745 | mid-graph edit 7.4 s → 2.3 s |
| Groovy build scripts fork from a startup cache | JK-4743 | helidon-common's script 0.69 s → 0.41 s on the chain |
| Test-class history is scanned once per build, not once per module | JK-4744 | first compile at 0.4 s instead of 0.65 s |
| The worker's heap is held to the memory the host has free, and admission waits while it is short | JK-4762 | no swap storm on a busy desktop (one cold build had taken 125 s) |
| A fresh engine's first request parses manifests side by side, parses the library catalog once, and stops forking git per past session episode | JK-4760 | first event 1.6 s → 0.8 s on a fresh engine |
| A warm compiler worker stays while its project is worked on and leaves when it is not | JK-4765 | no ~6 s cold worker between builds |
| An incremental build forecasts only the modules its edit can reach | JK-4767 | leaf edit's check 0.36 s → 0.29 s, execute starts at 0.5 s instead of 0.88 s |
| Reverting an edit forecasts its dependents against the trees it restores | JK-4771 | toggling a mid-graph method back and forth: re-add 15.2 s (168 recompiles) → under 1 s, revert 2.8 s → under 1 s |

### How accurate the ETA is

`scripts/eta-harness.py` measures jk's up-front estimate (R0) against the execute wall, and the
progress bar as the terminal paints it against the wall fraction, over six runs per shape on a
clean history (`~/.jk/state/builds` wiped first). Final run (jk 1b7eb19a5, JK-4766/4769/4770):

| Shape | Wall s | R0 / execute (median) | Bar − wall at 25 / 50 / 75 / 90 % (points) |
|---|---:|---:|---|
| Cold, warm compiler worker | 12.8 | 0.96 | +2 / +7 / +4 / +3 |
| Cold, cold compiler worker | 17.2 | 1.01 | −2 / −3 / +3 / +2 |
| Warm no-op | 0.6 | (no work to estimate) | −22 / −46 / +25 / +10 |
| Dirty leaf | 1.1 | 1.03 | −22 / +0 / +0 / +9 |
| Mid-graph body-only edit | 2.0 | 0.92 | −19 / −5 / +12 / +9 |
| Mid-graph API change | 11.6 | 1.02 | +2 / +5 / +3 / +3 |

The estimate is within 10% of the execute wall for every shape, and the bar within 10 points once
it arrives (one +12). The negative 25 % column on short builds (and the no-op row) is the first
~0.5 s, before the estimate exists: until the cache check ends nothing says whether the build is a
no-op, a one-file edit or a cold build. Since this table was measured, the terminal draws no bar
there: the header shows the breathing orb beside the preflight stage and the clock, and the taskbar
gets the indeterminate state (OSC 9;4;3), until the estimate arrives or the build reaches execute
without one; `eta-harness.py` now reports those samples as "–". The estimate costs no wall time:
it is priced beside the build.

## Reading it

- **Every scenario is faster under jk.** A no-op is under a second because jk hashes inputs and
  replays recorded steps; Maven re-walks 220 modules and re-runs its bound plugins every time. A
  one-file edit recompiles that module and only the dependents whose compile inputs (the edited
  module's ABI) changed.
- **The cold build is critical-path bound** (a 20-module chain from `metadata` through `webserver`
  to `grpc-security`). jk's compiles on that chain run on a warm, shared JIT, as Maven's do in its
  one JVM; jk starts the first one 0.4 s in, and each dependent starts compiling 6–25 ms after its
  dependency finishes.
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

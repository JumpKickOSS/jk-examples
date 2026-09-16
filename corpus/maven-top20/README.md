# Maven top-20 corpus

The 20 most-starred Maven-built Java repositories on GitHub, pinned to a SHA, driven through both
their own Maven build and `jk import` → `jk lock` → `jk build` → `jk test`, one repo at a time.
The numbers are a ratchet: [RESULTS.md](RESULTS.md) is regenerated on every run and its last section
states the counts a later run must not lower.

## Layout

| path | what |
|------|------|
| `repos.toml` | the pinned list: `[[repo]]` (selected), `[[skipped]]` (root pom present but excluded, with reason), `[[alternate]]` (next in line) |
| `run.sh` / `run.py` | the harness (Python 3.11+, stdlib only) |
| `results/<date>.jsonl` | one row per repo per run, every number and status |
| `results/<repo>/` | per-step logs, `import-report.md` (the `jk import` fidelity report), `jk-results.md` |
| `results/tier3-reasons.md` | every distinct import ERROR / jk failure reason with the repos it affects — ticket fodder |
| `RESULTS.md` | the summary table + Ratchet section |

Clones live **outside** this repo under `/home/bsant/src/scratch/maven-corpus/<name>/`
(override with `CORPUS_SCRATCH`). The corpus-private Maven local repository is
`$CORPUS_SCRATCH/.m2`, so the first Maven run on a fresh machine really is cold.

## How the 20 were chosen

`gh search repos --language=java --sort=stars --limit 100` (then `--stars 9000..19040` for the next
page), keeping repos whose default-branch root has a `pom.xml`, and dropping:

- documentation / interview / tutorial collections with no real build (JavaGuide, hello-algo, CS-Notes,
  LeetCode lists, SpringBoot-Labs, springboot-learning-example, …);
- Android apps and anything without a root `pom.xml` (Gradle, Bazel, Make: spring-boot, elasticsearch,
  ghidra, kafka, selenium, RxJava, …);
- Tycho / Eclipse RCP p2 builds (dbeaver);
- repos whose root pom declares Java < 17 (`maven.compiler.source|release|target`, `java.version` and
  friends), marked `needs-jdk-below-17` in `repos.toml`. This is the rule that excludes guava, netty,
  dubbo, arthas, rocketmq, druid, hutool, canal, mybatis-3, gson, redisson, seata, skywalking,
  shardingsphere and Sentinel; jk supports project JDKs 17+ only.

`TheAlgorithms/Java` is an educational collection but has a real single-module Maven build with a large
JUnit suite, so it stays. Every declared level was read from the root pom (or the parent / property it
points at) and is recorded as `java` in `repos.toml`.

## Protocol (per repo, sequential)

Hard cap 45 minutes per repo (`CORPUS_REPO_CAP`); a step that would start after the cap is recorded as
`capped` and the row says `timed out at step X`. Every step's wall time and exit status is recorded.

1. **Clone** `--depth 1` at the pinned SHA (reused if present; the tree is reset to the SHA and
   `git clean -fdx`ed before each side).
2. **Maven**, `MAVEN_OPTS=-Xmx3g`, `JAVA_HOME` = the jk-installed Temurin matching the declared level:
   `package -DskipTests` cold → `clean package -DskipTests` (warm clean) → `package -DskipTests` again
   (no-op) → append one comment line to one main `.java` file in the leaf module with the most sources,
   `package -DskipTests` (touch), revert → `test` once with a 20-minute timeout
   (`CORPUS_TEST_CAP`); totals summed from `**/target/surefire-reports/TEST-*.xml`.
   The launcher is the repo's `mvnw` when it ships one, else the Maven distribution `jk mvn`
   provisions (`~/.jk/store/tools/maven/3.9.9/bin/mvn`). We call that launcher directly rather than
   through `jk mvn` because `jk mvn` strips `MAVEN_OPTS` / `JAVA_TOOL_OPTIONS` on purpose
   (`PassthroughEnv`), and there is no other way to cap the heap without writing into the clone.
3. **jk**: `jk import pom.xml --report results/<repo>/import-report.md` (Tier 3 = ERROR, Tier 2 =
   WARNING, counted from the report) → `jk lock` → `jk build --skip-tests` cold → again (no-op) → the
   same touch + `jk build --skip-tests` → revert → `jk test` once with the 20-minute timeout; totals
   from the `Tests:` line of `target/jk-results.md` (and the JUnit XML under
   `target/reports/test-results/` as a cross-check). When a jk step fails, the reason is taken from
   `target/jk-results.md`, not the TTY.
4. **Row** appended to `results/<date>.jsonl`; `RESULTS.md` and `results/tier3-reasons.md` re-rendered
   from the latest row of every repo.

Dependent steps are skipped, not faked: if the cold build fails, the no-op / touch / test steps of that
side are `skipped`; if `jk lock` fails, the jk build steps are `skipped`.

The clone is never modified except for the touch (reverted with `git checkout -- <file>`) and the files
jk itself writes (`jk.toml`, `jk-lock.toml`, `target/`), which the next run's reset removes.

## Running

```sh
./run.sh                       # all 20, star order, appends to results/<today>.jsonl
./run.sh --order small-first   # same, cheapest repos first (fills the table from the cheap end)
./run.sh --only nacos          # one repo
./run.sh --render              # only rewrite RESULTS.md / tier3-reasons.md from rows on disk
./run.sh --skip-mvn            # jk side only
./run.sh --fresh-m2            # wipe the corpus .m2 first so Maven cold is cold again
```

Never `jk update` mid-run; the tool version is recorded in the table header.

## Reading the ratchet

`RESULTS.md` ends with four counts: repos importing with zero Tier-3 errors, repos whose `jk lock`
succeeds, repos whose `jk build --skip-tests` succeeds, repos whose jk test total equals Maven's.
A run that lowers any of them is a regression. `results/tier3-reasons.md` groups every distinct cause
(module prefixes, coordinates, versions and paths normalized) with the repos it hits — each line is a
ticket candidate.

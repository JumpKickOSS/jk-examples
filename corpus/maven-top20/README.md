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
| `lockdiff.py` | the lock-vs-Maven diff: per module, the coordinates jk's lock and Maven's `dependency:tree` resolve to different versions, each named by the rule behind jk's answer |
| `lock-diff/<date>.md`, `results/lock-diff/<date>.jsonl` | its report (summary table + per-repo examples) and one JSON row per repo |
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
2. **Maven**, `MAVEN_OPTS=-Xmx3g`, `JAVA_HOME` = the jk-installed Temurin matching the declared level,
   plus the repo's `mvn_args` from `repos.toml` when it has any:
   `package -DskipTests` cold → `clean package -DskipTests` (warm clean) → `package -DskipTests` again
   (no-op) → append one comment line to one main `.java` file in the leaf module with the most sources,
   `package -DskipTests` (touch), revert → `test` once with a 20-minute timeout
   (`CORPUS_TEST_CAP`); totals summed from `**/target/surefire-reports/TEST-*.xml`.
   The launcher is the repo's `mvnw` when it ships one, else the Maven distribution `jk mvn`
   provisions (`~/.jk/store/tools/maven/3.9.9/bin/mvn`). We call that launcher directly rather than
   through `jk mvn` because `jk mvn` strips `MAVEN_OPTS` / `JAVA_TOOL_OPTIONS` on purpose
   (`PassthroughEnv`), and there is no other way to cap the heap without writing into the clone.
3. **jk**, with `JK_CACHE_DIR` = a per-repo action cache wiped first (see below): `jk import pom.xml
   --report results/<repo>/import-report.md` (Tier 3 = ERROR, Tier 2 =
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
./run.sh                       # all 20, star order: re-measure jk, reuse each repo's last Maven row (--jk-only is the default)
./run.sh --both                # re-measure the Maven side too (run #1 semantics; also used when no Maven row exists yet)
./run.sh --order small-first   # cheapest repos first (fills the table from the cheap end)
./run.sh --only nacos          # one repo (repeatable)
./run.sh --render              # only rewrite RESULTS.md / tier3-reasons.md from rows on disk
./run.sh --skip-mvn            # never run Maven, even when no earlier row exists
./run.sh --fresh-m2            # wipe the corpus .m2 first so Maven cold is cold again
JK_COMMIT=<sha> ./run.sh       # record the commit of a main-built jk in every row (the binary embeds none)
./run.sh --run run2              # label the rows; every label gets its own table and a side-by-side column
./run.sh --steps import,lock,build --only nacos   # a cold-wall probe: the jk stages named, in protocol order, and no jk test
./run.sh --no-tests            # the same prefix spelled short: import, lock and the three builds, no jk test
```

`--steps` names a prefix of the jk protocol — `import`, `import,lock`, `import,lock,build` or all four
stages — so a probe after the cold, no-op and touch walls pays no test run (about 40 s for a small
repo instead of 60 s). The stages left out are recorded as `not-run` and render as `—`, never as
`skipped`, which stays the word for a step whose prerequisite failed; the row carries the note
`jk steps filtered to …`. The Maven side is untouched by the filter: it is reused from the last row as
usual, or measured when none exists (`--skip-mvn` to never run it).

Run labels are how the ratchet compares two jk builds: rows carry `run` (default `run1`, file
`results/<date>.jsonl`; any other label goes to `results/<date>-<label>.jsonl`). `RESULTS.md` renders a
side-by-side table (one column per label, Maven as the fixed reference), a per-label table, and the
Ratchet counts of the latest label with the delta against the previous one. `results/tier3-reasons.md`
is always the latest label's.

Never `jk update` mid-run. Every row records `jk --version`, the jk commit (from `JK_COMMIT`, else the
`v<version>` tag resolved in the jk checkout at `JK_SRC`, default `~/src/oss/jk`), and the sha256 of the
`jk` binary and the engine jar (read under `JK_HOME` when a private install sets it), so two runs' rows are
attributable to the exact jk that produced them. A private install drives a run as
`JK_HOME=<home> PATH=<home>/bin:$PATH ./run.sh …`.
Rows whose Maven side was reused carry `mvn_reused_from = <date of the measured row>` and a `*` in the
table.

`repos.toml` may give a repo a `maven_jdk` when its Maven build demands more than the level its root pom
declares (an enforcer rule, or dependencies compiled for a newer class-file level); jk still imports the
declared level. Run #1 found three such repos (analysis-ik, jenkins, zipkin: all need 21).

`repos.toml` may also give a repo an `import_args` list, which `run.py` appends to `jk import pom.xml`,
and an `mvn_args` list, appended to every `mvn` step of that repo. tutorials declares its modules only
inside profiles that nothing activates on their own (its README says `mvn -Pdefault,default-heavy`), so
it is imported with `-P default,default-heavy` and its Maven side runs with the same `-P`, and the row
measures the reactor on both sides rather than an empty workspace against a build of zero modules. Both
lists are recorded in the row (`import_args`, `mvn_args`) and named above the tables.

## What counts, what does not

- A `jk test` or `mvn test` step that exits 0 with zero parsed tests is rendered `no tests ran` and never
  counts as a pass or as "equal totals" (tutorials and dataease root aggregators, poms that set
  `maven.test.skip`).
- A `jk build` that exits 0 but whose imported workspace covers none of the poms is rendered
  `built nothing`; one that covers only some is `ok (n/m modules)`. Only builds that compiled something
  count in the ratchet.
- A step killed by the 45-minute repo cap is `capped`, distinct from a step that hit its own 20-minute
  test timeout (`timeout`).
- `jk cold` is cold on every run: each repo's jk steps run with `JK_CACHE_DIR` pointing at
  `$CORPUS_SCRATCH/.jk-cache/<name>` (override the parent with `CORPUS_JK_CACHE`), wiped before the
  repo's first jk step, so the action cache holds nothing and the cold build compiles everything; the
  no-op and touch builds then read the cache that cold build filled. The artifact store stays the
  install's, so nothing re-downloads — the same warm-downloads, cold-compilation shape Maven gets from the
  corpus `.m2`. The row records the directory (`jk_cache_dir`).

## Diffing the lock against Maven's resolution

`./lockdiff.py` runs after a corpus run, on every repo whose latest `jk lock` is green. It copies the
clone (with the harness's `jk.toml` / `jk-lock.toml`) to `/home/bsant/src/scratch/lock-diff/<name>`
(`LOCKDIFF_SCRATCH`); with `--relock` it rewrites that copy's lock with the jk under test first, and
with `--reimport` it drops the corpus run's manifests and runs `jk import pom.xml` with that jk before
the lock (`--jk-home <dir>` names a private install, `<dir>/bin/jk` run with `JK_HOME=<dir>`; the
default is the `jk` on PATH), so the diff measures the importer and resolver as they are. Maven runs against the harness's own
local repo (`LOCKDIFF_SCRATCH/.m2`, a hardlink copy of the corpus `.m2` made on first use, so the
corpus repo stays as cold as the runner left it): `dependency:go-offline -U` first, then the verbose
tree per module (`maven-dependency-plugin:3.8.1:tree -Dverbose -DoutputFile=target/jk-lockdiff-tree.txt`,
offline, `-fae` so one module's failure keeps the others, 15 minutes per repo via
`LOCKDIFF_REPO_CAP`). A POM the local repo lacks makes Maven render that artifact as a leaf (`The POM
for X is missing`) and its whole subtree lands in `only jk`, so every POM a tree run reports missing,
absent or present-but-unavailable is fetched with curl from Central or a repository the POMs declare
(with its parents and imported BOMs), and the run repeats until none is missing; Central refuses the
JVM's TLS stack when it throttles a host, which is why Maven cannot fill them itself and why
`go-offline` may show `fail`. An online tree run is the last resort for a POM no repository served.
The report's `maven` cell carries the POMs filled and the ones still missing. jk's per-module closure
is read with `jk tree <module> -t -f -s all`. Modules both builds know are compared coordinate by coordinate
(`group:artifact[:classifier]`, Maven `compile|runtime|provided|system` against jk's main-side
scopes, Maven `test` against jk's `test`); workspace siblings are skipped. Every version difference
is classified by the rule that produced jk's answer:

| rule | meaning |
|------|---------|
| `bom` | jk's version is a `[platform-dependencies]` BOM's (the lock row carries `pinned-by`); Maven managed the coordinate from a different BOM set or order, or did not manage it at all |
| `managed` | Maven's tree says `version managed from X` and the version is an inline `<dependencyManagement>` entry of the repo's own POMs that jk's row does not carry: the entry reached no `[managed-dependencies]` table (a property the import could not read, a profile, a parent outside the repo) or a BOM or pin of jk's own outranked it |
| `pin` | jk's version is a version another workspace member declares directly; under `pins = "nearest"` a pin any member declares is the whole lock's version |
| `depth` | neither side managed it; Maven omitted jk's version "for conflict" with a nearer declaration (nearest-by-depth) where jk kept the highest declared version |
| `unknown` | none of the above explains it |

Same-version scope disagreements and coordinates only one side resolves are counted apart from
version differences. Among the coordinates only jk resolves, those it reaches only through a module's
**inactive-feature rows** — `optional = true` rows a `[features.<name>]` table names (a test-jar as
`<artifact>-tests`, the handle the import gives it) and no default feature activates, which the build
and Maven's profile-off tree never see but `jk tree` renders like any declared root — are counted in
their own column, `via inactive features`, read from a second `jk tree` over a copy of the module's
manifest with those rows and the `[features]` tables removed (the copy is restored afterwards; a copy
that does not parse skips the split for that module). hadoop's `hadoop-client-integration-tests` has
four such rows, and 13 of its 206 test coordinates come only through them: the rest arrive through
the shaded client siblings (`hadoop-client-api`, `hadoop-client-minicluster`, `hadoop-client-runtime`),
whose Maven trees are leaves because their dependency-reduced POMs declare nothing while jk exports
each sibling's declared graph. A Maven side that cannot answer is a `maven unavailable: <reason>` cell, not a
failed run. `--only <name>` limits the repos, `--reuse` keeps the scratch copy and its Maven trees and
redoes only the jk side and the diff, `--render` rewrites the report from the rows on disk. The
corpus clones are never touched. `RESULTS.md` carries the latest report's summary table.

## Reading the ratchet

`RESULTS.md` ends with four counts: repos importing with zero Tier-3 errors, repos whose `jk lock`
succeeds, repos whose `jk build --skip-tests` succeeds, repos whose jk test total equals Maven's.
A run that lowers the lock, build or test count is a regression. The import count is read with its
reasons: it falls legitimately when import starts naming a mismatch it had been silent about (a parent
it cannot fetch where it wrote `=unresolved`, a `war` module it would have built as a jar), and that
kind of fall must show up in `results/tier3-reasons.md` as a new reason, not as a repo that stopped
importing. `results/tier3-reasons.md` groups every distinct cause (module prefixes, coordinates,
versions and paths normalized) with the repos it hits — each line is a ticket candidate.

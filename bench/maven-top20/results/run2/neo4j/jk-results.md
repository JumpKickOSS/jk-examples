# jk results — FAIL

**FAIL** · test · `org.neo4j:parent` · #8 · 861ms · **exit 4** · jid 17
trigger: cli · commit: bbf3b91f · jk 0.13.7

- `annotations` `run-tests`: 1 test failure

Modules: 3 (**1 failed**)
Tests: **1 failed**, 0 passed (1 total)
Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/neo4j/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/0187dac78cc21adebb63aedb469fb744/runs/8/details.jsonl` — JSONL, same shape as `--output json`
- JUnit XML: `target/reports/test-results/`

## Failures

### run-tests — annotations
```
1 test failure
```

## Failed steps

| Module | Task | Status | Time |
|---|---|---|---|
| org.neo4j:annotations | `run-tests` | FAIL | 99ms |

_18 tasks skipped (cache)._

## Modules

| Module | Outcome | Time |
|---|---|---|
| org.neo4j:annotations | FAIL | 310ms |
| org.neo4j.build:packaging-build | OK | 4ms |
| org.neo4j.build:community-build | OK | 5ms |


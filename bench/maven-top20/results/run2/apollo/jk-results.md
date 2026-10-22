# jk results — FAIL

**FAIL** · lock · `com.ctrip.framework.apollo:apollo` · 970ms · **exit 6** · jid 310
trigger: cli · commit: 39b8d49 · jk 0.13.7

- `apollo` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/apollo/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/84d40cc1412a2514098dbf39273a130c/runs/j-20260916T061156890-310/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — apollo
```
‼ Cannot resolve dependencies:
  │ Package com.ctrip.framework.apollo:apollo-audit-api was not found in any repository
  │ The project depends on com.ctrip.framework.apollo:apollo-audit-api 3.0.0-SNAPSHOT
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Relax or remove the project constraint on com.ctrip.framework.apollo:apollo-audit-api
```


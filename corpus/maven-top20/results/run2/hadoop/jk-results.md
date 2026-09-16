# jk results — FAIL

**FAIL** · lock · `org.apache.hadoop:hadoop-main` · 655ms · **exit 6** · jid 5
trigger: cli · commit: 90b043bd · jk 0.13.7

- `hadoop` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/hadoop/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/795c5fcb4a4798a9bdb3622d2c4dcb0a/runs/j-20260916T061656321-5/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — hadoop
```
‼ Cannot resolve dependencies:
  │ No versions of commons-io:commons-io match unresolved
  │   available: 2.22.0, 2.21.0, 2.20.0, 2.19.0, 2.18.0, 2.17.0, 2.16.1, 2.16.0, 2.15.1, 2.15.0, 2.14.0, 2.13.0, …
  │ The project depends on commons-io:commons-io unresolved
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin commons-io:commons-io to 2.22.0 (e.g. `commons-io:commons-io:2.22.0`)
  • Or relax the version constraint on commons-io:commons-io so one of [2.22.0, 2.21.0, 2.20.0] is allowed
  • Relax or remove the project constraint on commons-io:commons-io
```


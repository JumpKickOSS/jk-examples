# jk results — FAIL

**FAIL** · lock · `com.xuxueli:xxl-job` · 630ms · **exit 6** · jid 133
trigger: cli · commit: e74c784 · jk 0.13.7

- `xxl-job` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/xxl-job/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/c25d62c6d24ff0d0a95cce6246238102/runs/j-20260916T055138298-133/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — xxl-job
```
‼ Cannot resolve dependencies:
  │ No versions of org.apache.groovy:groovy match unresolved
  │   available: 6.0.0-RC-2, 6.0.0-RC-1, 6.0.0-beta-3, 6.0.0-beta-2, 6.0.0-beta-1, 6.0.0-alpha-2, 6.0.0-alpha-1, 5.1.2, 5.1.1, 5.1.0, 5.0.8, 5.0.7, …
  │ The project depends on org.apache.groovy:groovy unresolved
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin org.apache.groovy:groovy to 6.0.0-RC-2 (e.g. `org.apache.groovy:groovy:6.0.0-RC-2`)
  • Or relax the version constraint on org.apache.groovy:groovy so one of [6.0.0-RC-2, 6.0.0-RC-1, 6.0.0-beta-3] is allowed
  • Relax or remove the project constraint on org.apache.groovy:groovy
```


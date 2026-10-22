# jk results — FAIL

**FAIL** · lock · `com.macro.mall:mall` · 642ms · **exit 6** · jid 135
trigger: cli · commit: dcaa93b · jk 0.13.7

- `mall` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/mall/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/107c2f234965496e7b71e63e83fa9073/runs/j-20260916T055139188-135/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — mall
```
‼ Cannot resolve dependencies:
  │ No versions of org.codehaus.janino:janino match unresolved
  │   available: 3.1.12, 3.1.11, 3.1.10, 3.1.9, 3.1.8, 3.1.7, 3.1.6, 3.1.4, 3.1.3, 3.1.2, 3.1.1, 3.1.0, …
  │ The project depends on org.codehaus.janino:janino unresolved
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin org.codehaus.janino:janino to 3.1.12 (e.g. `org.codehaus.janino:janino:3.1.12`)
  • Or relax the version constraint on org.codehaus.janino:janino so one of [3.1.12, 3.1.11, 3.1.10] is allowed
  • Relax or remove the project constraint on org.codehaus.janino:janino
```


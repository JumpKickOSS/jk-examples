# jk results — FAIL

**FAIL** · lock · `com.thealgorithms:Java` · 651ms · **exit 6** · jid 123
trigger: cli · commit: b6fb8ad · jk 0.13.7

- `TheAlgorithms-Java` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/TheAlgorithms-Java/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/0ef064476b666319127494665624a3c9/runs/j-20260916T055126737-123/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — TheAlgorithms-Java
```
‼ Cannot resolve dependencies:
  │ No versions of org.junit.jupiter:junit-jupiter match unresolved
  │   available: 6.1.3, 6.1.2, 6.1.1, 6.1.0, 6.1.0-RC1, 6.1.0-M1, 6.0.3, 6.0.2, 6.0.1, 6.0.0, 6.0.0-RC3, 6.0.0-RC2, …
  │ The project depends on org.junit.jupiter:junit-jupiter unresolved
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin org.junit.jupiter:junit-jupiter to 6.1.3 (e.g. `org.junit.jupiter:junit-jupiter:6.1.3`)
  • Or relax the version constraint on org.junit.jupiter:junit-jupiter so one of [6.1.3, 6.1.2, 6.1.1] is allowed
  • Relax or remove the project constraint on org.junit.jupiter:junit-jupiter
```


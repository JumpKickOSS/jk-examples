# jk results — FAIL

**FAIL** · lock · `org.thingsboard:thingsboard` · 640ms · **exit 6** · jid 149
trigger: cli · commit: 687d808 · jk 0.13.7

- `thingsboard` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/thingsboard/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/fdbbf3d546bbb9c652db2079977bbeb9/runs/j-20260916T055141839-149/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — thingsboard
```
‼ Cannot resolve dependencies:
  │ No versions of com.google.guava:guava match unresolved
  │   available: 33.7.1-jre, 33.7.1-android, 33.7.0-jre, 33.7.0-android, 33.6.0-jre, 33.6.0-android, 33.5.0-jre, 33.5.0-android, 33.4.8-jre, 33.4.8-android, 33.4.7-jre, 33.4.7-android, …
  │ The project depends on com.google.guava:guava unresolved
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin com.google.guava:guava to 33.7.1-jre (e.g. `com.google.guava:guava:33.7.1-jre`)
  • Or relax the version constraint on com.google.guava:guava so one of [33.7.1-jre, 33.7.1-android, 33.7.0-jre] is allowed
  • Relax or remove the project constraint on com.google.guava:guava
```


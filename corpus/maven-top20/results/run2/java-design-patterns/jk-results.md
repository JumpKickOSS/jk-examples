# jk results — FAIL

**FAIL** · lock · `com.iluwatar:java-design-patterns` · 4.1s · **exit 6** · jid 188
trigger: cli · commit: 4cabb20 · jk 0.13.7

- `java-design-patterns` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/java-design-patterns/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/084fe58b8a7659af79ee0d4587d6a3b6/runs/j-20260916T060723302-188/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — java-design-patterns
```
‼ Cannot resolve dependencies:
  │ Com.fasterxml.jackson.core:jackson-databind 2.22.1 depends on com.fasterxml.jackson.core:jackson-core [2.22.1,+∞)
  │ The project depends on com.fasterxml.jackson.core:jackson-databind 2.22.1
  │ Therefore, not com.fasterxml.jackson.core:jackson-core [2.22.1,+∞) and the project cannot be resolved
  │ The project depends on com.fasterxml.jackson.core:jackson-core 2.21.4
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Relax or remove the project constraint on com.fasterxml.jackson.core:jackson-databind
  • Relax or remove the project constraint on com.fasterxml.jackson.core:jackson-core
```


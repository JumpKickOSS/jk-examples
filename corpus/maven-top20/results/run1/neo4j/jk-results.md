# jk results — FAIL

**FAIL** · lock · `org.neo4j:parent` · 452ms · **exit 6** · jid 155
trigger: cli · commit: bbf3b91f · jk 0.13.7

- `neo4j` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/neo4j/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/c12e1ab46d2feca02ef227ce4b67ad06/runs/j-20260916T055144054-155/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — neo4j
```
‼ Cannot resolve dependencies:
  │ No versions of com.google.testing.compile:compile-testing match unresolved
  │   available: 0.23.0, 0.22.0, 0.21.0, 0.20, 0.19, 0.18, 0.17, 0.16, 0.15, 0.14, 0.13, 0.12, …
  │ The project depends on com.google.testing.compile:compile-testing unresolved
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin com.google.testing.compile:compile-testing to 0.23.0 (e.g. `com.google.testing.compile:compile-testing:0.23.0`)
  • Or relax the version constraint on com.google.testing.compile:compile-testing so one of [0.23.0, 0.22.0, 0.21.0] is allowed
  • Relax or remove the project constraint on com.google.testing.compile:compile-testing
```


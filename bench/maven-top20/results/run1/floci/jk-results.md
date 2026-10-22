# jk results — FAIL

**FAIL** · lock · `io.github.hectorvent:floci` · 4.4s · **exit 6** · jid 127
trigger: cli · commit: c30df83 · jk 0.13.7

- `floci` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/floci/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/37141f972b3e28b6e07dfbc28f7634ba/runs/j-20260916T055130666-127/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — floci
```
‼ Cannot resolve dependencies:
  │ No versions of org.apache.james:apache-mime4j-dom match unresolved
  │   available: 0.8.15, 0.8.14, 0.8.13, 0.8.12, 0.8.11, 0.8.10, 0.8.9, 0.8.8, 0.8.7, 0.8.6, 0.8.5, 0.8.4, …
  │ The project depends on org.apache.james:apache-mime4j-dom unresolved
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin org.apache.james:apache-mime4j-dom to 0.8.15 (e.g. `org.apache.james:apache-mime4j-dom:0.8.15`)
  • Or relax the version constraint on org.apache.james:apache-mime4j-dom so one of [0.8.15, 0.8.14, 0.8.13] is allowed
  • Relax or remove the project constraint on org.apache.james:apache-mime4j-dom
```


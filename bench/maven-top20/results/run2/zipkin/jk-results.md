# jk results — FAIL

**FAIL** · lock · `io.zipkin:zipkin-parent` · 8.8s · **exit 6** · jid 329
trigger: cli · commit: 878ce2a · jk 0.13.7

- `zipkin` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/zipkin/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/511d3b44ea4792653986a5a134587a5a/runs/j-20260916T061531938-329/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — zipkin
```
‼ Cannot resolve dependencies:
  │ No versions of io.zipkin.zipkin2:zipkin-collector match 3.6.2-SNAPSHOT
  │   available: 3.6.1, 3.6.0, 3.5.1, 3.5.0, 3.4.4, 3.4.3, 3.4.2, 3.4.1, 3.4.0, 3.3.1, 3.3.0, 3.2.1, …
  │ The project depends on io.zipkin.zipkin2:zipkin-collector 3.6.2-SNAPSHOT
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin io.zipkin.zipkin2:zipkin-collector to 3.6.1 (e.g. `io.zipkin.zipkin2:zipkin-collector:3.6.1`)
  • Or relax the version constraint on io.zipkin.zipkin2:zipkin-collector so one of [3.6.1, 3.6.0, 3.5.1] is allowed
  • Relax or remove the project constraint on io.zipkin.zipkin2:zipkin-collector
```


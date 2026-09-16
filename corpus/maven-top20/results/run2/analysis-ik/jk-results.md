# jk results — FAIL

**FAIL** · lock · `com.infinilabs:analysis-ik` · 231ms · **exit 6** · jid 327
trigger: cli · commit: 6d2d70f · jk 0.13.7

- `analysis-ik` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/analysis-ik/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/296143ad9e9a082f781d35d1cd88407a/runs/j-20260916T061531517-327/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — analysis-ik
```
‼ Cannot resolve dependencies:
  │ Org.elasticsearch:elasticsearch 9.4.0 depends on org.apache.lucene:lucene-core [10.4.0,+∞)
  │ The project depends on org.apache.lucene:lucene-core 10.2.2
  │ Therefore, org.elasticsearch:elasticsearch 9.4.0 and the project cannot be resolved
  │ The project depends on org.elasticsearch:elasticsearch 9.4.0
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Relax or remove the project constraint on org.apache.lucene:lucene-core
  • Relax or remove the project constraint on org.elasticsearch:elasticsearch
```


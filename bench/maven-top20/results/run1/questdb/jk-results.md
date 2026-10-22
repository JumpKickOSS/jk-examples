# jk results — FAIL

**FAIL** · lock · `org.questdb:questdb-parent` · 2.3s · **exit 6** · jid 131
trigger: cli · commit: f9d4f51 · jk 0.13.7

- `questdb` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/questdb/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/7de106d0460254ed4200162f75fcfdb8/runs/j-20260916T055135838-131/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — questdb
```
‼ Cannot resolve dependencies:
  │ No versions of org.questdb:questdb-client match 1.3.10-SNAPSHOT
  │   available: 1.3.9, 1.3.8, 1.3.7, 1.3.6, 1.3.5, 1.3.4, 1.3.3, 1.3.2, 1.3.1, 1.3.0, 1.2.2, 1.2.1, …
  │ The project depends on org.questdb:questdb-client 1.3.10-SNAPSHOT
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Pin org.questdb:questdb-client to 1.3.9 (e.g. `org.questdb:questdb-client:1.3.9`)
  • Or relax the version constraint on org.questdb:questdb-client so one of [1.3.9, 1.3.8, 1.3.7] is allowed
  • Relax or remove the project constraint on org.questdb:questdb-client
```


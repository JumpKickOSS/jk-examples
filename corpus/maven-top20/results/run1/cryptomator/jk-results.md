# jk results — FAIL

**FAIL** · lock · `org.cryptomator:cryptomator` · 2.9s · **exit 6** · jid 125
trigger: cli · commit: 1712d31 · jk 0.13.7

- `cryptomator` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/cryptomator/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/76dc2e5ed4b3eed8532b392d9e29c4c0/runs/j-20260916T055127492-125/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — cryptomator
```
‼ Cannot resolve dependencies:
  │ Org.cryptomator:cryptofs 2.10.0 depends on jakarta.inject:jakarta.inject-api [2.0.1.MR,+∞)
  │ The project depends on jakarta.inject:jakarta.inject-api 2.0.1
  │ Therefore, org.cryptomator:cryptofs 2.10.0 and the project cannot be resolved
  │ The project depends on org.cryptomator:cryptofs 2.10.0
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Relax or remove the project constraint on jakarta.inject:jakarta.inject-api
  • Relax or remove the project constraint on org.cryptomator:cryptofs
```


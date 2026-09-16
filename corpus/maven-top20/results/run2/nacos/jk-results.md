# jk results — FAIL

**FAIL** · lock · `com.alibaba.nacos:nacos-all` · 731ms · **exit 6** · jid 304
trigger: cli · commit: 946138f · jk 0.13.7

- `nacos` `resolve-deps`: ‼ Cannot resolve dependencies:

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/nacos/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/a2f74298183a64d1e00ed67fd9ae009a/runs/j-20260916T061106772-304/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — nacos
```
‼ Cannot resolve dependencies:
  │ Ch.qos.logback:logback-classic 1.5.32 depends on org.slf4j:slf4j-api [2.0.17,+∞)
  │ The project depends on org.slf4j:slf4j-api 2.0.13
  │ Therefore, ch.qos.logback:logback-classic 1.5.32 and the project cannot be resolved
  │ The project depends on ch.qos.logback:logback-classic 1.5.32
  │ Therefore, the project's requirements cannot be resolved

Suggestions:
  • Relax or remove the project constraint on org.slf4j:slf4j-api
  • Relax or remove the project constraint on ch.qos.logback:logback-classic
```


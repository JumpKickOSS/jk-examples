# jk results — FAIL

**FAIL** · lock · `org.keycloak:keycloak-parent` · 270ms · **exit 6** · jid 302
trigger: cli · commit: f9e4f758 · jk 0.13.7

- `keycloak` `resolve-deps`: platform BOM conflict on `com.github.ben-manes.caffeine:caffeine`: org.apache.directory.api:api-parent:2.1.8 constrains to 2.9.3, but io.quarkus.platform:quarkus-bom:3.39.2 constrains to 3.2.4. Pick o…

Diagnostics: **1 error**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/keycloak/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/9f49a41ecad45ced6baae6756d1b543c/runs/j-20260916T061057267-302/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### resolve-deps — keycloak
```
platform BOM conflict on `com.github.ben-manes.caffeine:caffeine`: org.apache.directory.api:api-parent:2.1.8 constrains to 2.9.3, but io.quarkus.platform:quarkus-bom:3.39.2 constrains to 3.2.4. Pick one BOM or pin the coord explicitly.
```


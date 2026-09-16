# jk results — FAIL

**FAIL** · build · `com.xuxueli:xxl-job` · #3 · 1.8s · **exit 1** · jid 23
trigger: cli · commit: e74c784 · jk 0.13.7

- `xxl-job-admin` `package-javadoc`: /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/trigger/JobTrigger.java:52: error: malformed HTML
- `xxl-job-admin` `package-javadoc`: /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/thread/JobTriggerPoolHelper.java:94: error: malformed HTML

Modules: 3 (**1 failed**)
Diagnostics: **2 errors**, 200 warnings

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/xxl-job/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/0187dac78cc21adebb63aedb469fb744/runs/3/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### package-javadoc — xxl-job-admin
`xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/trigger/JobTrigger.java:52`
```
/home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/trigger/JobTrigger.java:52: error: malformed HTML
```

`xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/thread/JobTriggerPoolHelper.java:94`
```
/home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/thread/JobTriggerPoolHelper.java:94: error: malformed HTML
```

## Deliverables

| Module | Task | Status | Time |
|---|---|---|---|
| com.xuxueli:xxl-job-executor-samples | `package-jar` | SUCCESS | 2ms |
| com.xuxueli:xxl-job-core | `package-jar` | SUCCESS | 5ms |
| com.xuxueli:xxl-job-admin | `package-jar` | SUCCESS | 177ms |

## Failed steps

| Module | Task | Status | Time |
|---|---|---|---|
| com.xuxueli:xxl-job-admin | `package-javadoc` | FAIL | 1.3s |

_17 tasks skipped (cache)._

## Warnings

- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/glue/impl/SpringGlueFactory.java:17` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/glue/impl/SpringGlueFactory.java:17: warning: no main description
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/handler/impl/MethodJobHandler.java:8` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/handler/impl/MethodJobHandler.java:8: warning: no main description
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminBiz.java:8` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminBiz.java:8: warning: no main description
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/executor/dto/IdleBeatRequest.java:6` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/executor/dto/IdleBeatRequest.java:6: warning: no main description
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/executor/dto/KillRequest.java:6` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/executor/dto/KillRequest.java:6: warning: no main description
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/executor/dto/LogRequest.java:6` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/executor/dto/LogRequest.java:6: warning: no main description
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:19` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:19: warning: no @param for request
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:19` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:19: warning: no @return
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:24` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:24: warning: no @param for request
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:24` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:24: warning: no @return
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:29` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:29: warning: no @param for request
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:29` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:29: warning: no @return
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:34` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:34: warning: no @param for request
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:34` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:34: warning: no @return
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:39` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:39: warning: no @param for request
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:39` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:39: warning: no @return
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:44` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:44: warning: no @param for request
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:44` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/AdminJobBiz.java:44: warning: no @return
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/dto/CallbackData.java:17` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/dto/CallbackData.java:17: warning: no comment
- `package-javadoc` `xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/dto/CallbackData.java:18` — /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/openapi/admin/dto/CallbackData.java:18: warning: no comment
- _+180 more — see `details.jsonl`._

## Modules

| Module | Outcome | Time |
|---|---|---|
| com.xuxueli:xxl-job-admin | FAIL | 1.7s |
| com.xuxueli:xxl-job-executor-samples | OK | 13ms |
| com.xuxueli:xxl-job-core | OK | 1.2s |


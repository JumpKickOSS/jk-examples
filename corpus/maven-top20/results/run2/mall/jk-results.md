# jk results — FAIL

**FAIL** · build · `com.macro.mall:mall` · #1 · 4.8s · **exit 1** · jid 281
trigger: cli · commit: dcaa93b · jk 0.13.7

- `mall-common` `compile-java`: /home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:68: error: cannot find symbol
- `mall-common` `compile-java`: /home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:72: error: cannot find symbol
- `mall-common` `compile-java`: /home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:73: error: cannot find symbol

Diagnostics: **16 errors**

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/mall/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/0187dac78cc21adebb63aedb469fb744/runs/1/details.jsonl` — JSONL, same shape as `--output json`

## Failures

### compile-java — mall-common
`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:68`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:68: error: cannot find symbol
  symbol:   method setDescription(java.lang.String)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:72`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:72: error: cannot find symbol
  symbol:   method setBasePath(java.lang.String)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:73`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:73: error: cannot find symbol
  symbol:   method setUsername(java.lang.String)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:74`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:74: error: cannot find symbol
  symbol:   method setIp(java.lang.String)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:75`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:75: error: cannot find symbol
  symbol:   method setMethod(java.lang.String)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:76`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:76: error: cannot find symbol
  symbol:   method setParameter(java.lang.Object)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:77`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:77: error: cannot find symbol
  symbol:   method setResult(java.lang.Object)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:78`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:78: error: cannot find symbol
  symbol:   method setSpendTime(int)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:79`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:79: error: cannot find symbol
  symbol:   method setStartTime(long)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:80`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:80: error: cannot find symbol
  symbol:   method setUri(java.lang.String)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:81`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:81: error: cannot find symbol
  symbol:   method setUrl(java.lang.String)
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:83`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:83: error: cannot find symbol
  symbol:   method getUrl()
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:84`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:84: error: cannot find symbol
  symbol:   method getMethod()
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:85`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:85: error: cannot find symbol
  symbol:   method getParameter()
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:86`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:86: error: cannot find symbol
  symbol:   method getSpendTime()
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

`mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:87`
```
/home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:87: error: cannot find symbol
  symbol:   method getDescription()
  location: variable webLog of type com.macro.mall.common.domain.WebLog
```

## Failed steps

| Module | Task | Status | Time |
|---|---|---|---|
| com.macro.mall:mall-common | `compile-java` | FAIL | 1.1s |

_2 tasks skipped (cache)._


# jvm/variants-cli — core `[variants]`

One custom dimension (`contentType`) on a plain JVM app: per-value sources via `extra-src` and
per-value dependencies. `jk lock` resolves the **union** of both values' dependencies — one
lockfile covers every variant — and a build folds in only the selected value.

```sh
jk build --variant contentType=demo     # stub backend, no external client dep
jk build --variant contentType=prod     # real Backend using commons-lang3
jk build --variant demo                 # bare value works: only one custom dimension
jk build                                # FAILS on purpose: the dimension declares no default

java -jar target/variants-cli-1.0.0-all.jar
unzip -l target/variants-cli-1.0.0-all.jar | grep -c commons   # prod: many; demo: none

jk guard --variant contentType=demo     # every verb that builds needs the selection
```

**Why `commons-lang3` is pinned exactly.** The jar listing above names one library build; the
union lock has to hold still for the comparison to mean anything. Everywhere else in this repo
versions float to `latest`.

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
unzip -l target/variants-cli-1.0.0-all.jar | grep -c commons   # prod: 458 entries; demo: 0

jk guard --variant contentType=demo     # every verb that builds needs the selection
```

The prod value pins `commons-lang3 = "3.21.0"` under `[variants.contentType.prod.dependencies]`;
the one lock holds it for both values, and the demo jar carries none of it.

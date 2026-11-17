# 05-recipes/packaging/variants — core `[variants]`

One custom dimension (`contentType`) on a plain JVM app: per-value sources via `extra-src` and
per-value dependencies. `jk lock` resolves the **union** of both values' dependencies — one
lockfile covers every variant — and a build folds in only the selected value.

```sh
jk build --variant contentType=demo     # stub backend, no external client dep
jk build --variant contentType=prod     # real Backend using commons-lang3
jk build --variant demo                 # bare value works: only one custom dimension
jk build                                # the dimension's default, demo

java -jar target/variants-cli-1.0.0-SNAPSHOT-all.jar
unzip -l target/variants-cli-1.0.0-SNAPSHOT-all.jar | grep -c commons   # prod: 458 entries; demo: 0

jk guard --variant contentType=demo     # every verb that builds takes the selection
```

`[variants.contentType] default = "demo"` is what a build that selects nothing builds, so the
example also builds from the repository root with the others. Without a default the dimension is
mandatory: `jk build` with no `--variant` fails and names the values.

The prod value pins `commons-lang3` under `[variants.contentType.prod.dependencies]`;
the one lock holds it for both values, and the demo jar carries none of it.

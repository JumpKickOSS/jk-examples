# jvm/shrink-cli — the minified jar

`[application] minified = true` runs R8 `--classfile` full mode over the app and its runtime
closure and writes one slim executable jar beside the fat jar. Validation is behavioral (the
`-min.jar` runs) and structural (unreferenced library code is gone).

```sh
jk build
java -jar target/shrink-cli-1.0.0-min.jar
ls -la target/shrink-cli-1.0.0-all.jar target/shrink-cli-1.0.0-min.jar
unzip -l target/shrink-cli-1.0.0-min.jar | grep -c 'org/apache/commons/lang3'   # a handful, not hundreds
jk guard
```

**Why `commons-lang3` is pinned exactly.** The point of the scenario is a before/after size
comparison, and that only means something against one specific library build. Everywhere else
in this repo versions float to `latest`; here `=3.18.0` is deliberate.

The Google Maven repository is declared because R8 is published there. The effective keep rules
jk derived are written next to the artifact as `target/shrink-cli-1.0.0-keep.pro`.

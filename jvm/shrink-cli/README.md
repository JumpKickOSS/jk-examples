# jvm/shrink-cli — the minified jar

`[application] minified = true` runs R8 `--classfile` full mode over the app and its runtime
closure and writes one slim executable jar beside the fat jar. Validation is behavioral (the
`-min.jar` runs) and structural (unreferenced library code is gone).

```sh
jk build
java -jar target/shrink-cli-1.0.0-min.jar
ls -la target/shrink-cli-1.0.0-all.jar target/shrink-cli-1.0.0-min.jar
unzip -l target/shrink-cli-1.0.0-min.jar | grep -c 'org/apache/commons/lang3'   # 2, against 457 in the -all.jar
jk guard
```

Against the pinned `commons-lang3` `3.21.0`, a build writes a 740,655-byte
`shrink-cli-1.0.0-all.jar` (457 `org/apache/commons/lang3` entries) and a 15,798-byte
`shrink-cli-1.0.0-min.jar` (2 of them); `java -jar` on the slim jar prints
`Hello from a shrunk jar`. The numbers move with the pin, which `jk update` moves.

R8 is a tool jk provisions itself, so the manifest declares no extra repository for it. The
effective keep rules jk derived are written next to the artifact as
`target/shrink-cli-1.0.0-min-keep.pro`.

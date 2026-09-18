# codegen/avro-events — Avro schemas as generated classes

The `[avro]` preset runs Avro's `SpecificCompiler` over `src/main/avro` in the generate stage —
one parser shared by every schema, so `UserCreated.avsc` may name the `Role` enum another file
defines — and the generated `com.acme.events` classes join the compile. No compiler jar is in
`[dependencies]`: the preset fetches `org.apache.avro:avro-compiler` at its pinned release into the
store and hashes it into the step's key; `avro = "1.12.2"` is the runtime the generated classes
read, kept on the compiler's number.

```toml
[avro]
string-type = "String"      # java.lang.String getters instead of CharSequence

[dependencies]
avro = "1.12.2"
```

The step is `generate-avro` (`jk explain` shows it). A schema edit re-runs it and the compile; an
unchanged schema is a cache hit, so a second `jk build` is up to date.

```sh
jk build     # generates com.acme.events.{Role,UserCreated}, compiles, tests, packages target/avro-events-1.0.0.jar
jk run       # ada@acme.com is ADMIN
jk test      # the record round-trips through Avro binary and its getters are java.lang.String
jk guard
```

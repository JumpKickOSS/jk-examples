# protobuf/messages — the protobuf plugin

`[protobuf]` provisions a per-OS `protoc`, generates Java and the Kotlin DSL (`lite = true`,
`kotlin = true`) from `src/main/proto`, and joins the generated sources to both compilers and
the packaged jar.

```toml
[protobuf]
version = "latest"
lite    = true
kotlin  = true
```

`protoc` and `protobuf-kotlin-lite` both float to `latest`; `jk-lock.toml` pins one protobuf
release for the two, so generated code and runtime always agree.

```sh
jk build
jk run
jk guard
```

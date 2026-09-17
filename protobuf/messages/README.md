# protobuf/messages — the protobuf plugin

`[protobuf]` provisions a per-OS `protoc`, generates Java and the Kotlin DSL (`lite = true`,
`kotlin = true`) from `src/main/proto`, and joins the generated sources to both compilers and
the packaged jar.

```toml
[protobuf]
version = "4.36.1"
lite    = true
kotlin  = true
```

`protoc` and `protobuf-kotlin-lite` are pinned to the same protobuf release, `4.36.1`, so
generated code and runtime agree; `jk update` moves the pair together.

```sh
jk build
jk run
jk guard
```

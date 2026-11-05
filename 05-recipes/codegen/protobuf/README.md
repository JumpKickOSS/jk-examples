# 05-recipes/codegen/protobuf — the protobuf plugin

`[protobuf]` provisions a per-OS `protoc`, generates Java and the Kotlin DSL (`lite = true`,
`kotlin = true`) from `proto/` (the table's default `src`), and joins the generated sources to both compilers and
the packaged jar.

```toml
[protobuf]
version = "…"
lite    = true
kotlin  = true
```

`protoc` and `protobuf-kotlin-lite` are pinned to the same protobuf release, so
generated code and runtime agree; `jk update` moves the pair together.

```sh
jk build
jk run
jk guard
```

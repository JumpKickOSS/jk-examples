# netty-echo (JK-1174 / JK-1175)

Idiomatic **JumpKick** sample using published **Netty 4.1** (`netty-all`).

This is **not** the full Mill-style monorepo port of `github.com/netty/netty` sources
(~50 modules). That remains a multi-week overlay project (see Mill’s
`example/thirdparty/netty`). This module is the dogfood unit for:

- declarative `jk.toml` + application main
- cold / warm / no-op benches against Maven/Mill-style messaging (JK-1175)

## Build

```bash
export PATH="$HOME/.jk/bin:$PATH"   # or your install
jk lock
jk build
jk test
# jk run   # listens on 8080
```

## Bench

From the `jk` repo:

```bash
./scripts/netty-echo-bench.sh /path/to/jk-examples/jvm/netty-echo
```

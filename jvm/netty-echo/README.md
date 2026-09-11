# jvm/netty-echo — Netty echo server on the published artifact

An idiomatic single-module JumpKick app on `io.netty:netty-all` (`latest`, pinned by
`jk-lock.toml`). This is the small dogfood unit for cold / warm / no-op benches; the full
multi-module port of Netty's own sources is [`../netty`](../netty/).

It demonstrates:

- a declarative `jk.toml` with `[application] main`
- the test-tier table (`[test] exclude-tags` plus one profile per tag) and the house guard baseline

```sh
jk build          # compiles, runs the unit tier, packages target/netty-echo-0.1.0.jar
jk test
jk guard
jk run            # listens on 8080
```

Bench, from the `jk` repo: `./scripts/netty-echo-bench.sh /path/to/jk-examples/jvm/netty-echo`.

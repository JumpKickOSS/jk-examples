# 02-services/netty-echo — Netty echo server on the published artifact

An idiomatic single-module JumpKick app on `io.netty:netty-all` (pinned to `4.2.19.Final`). This is the small dogfood unit for cold / warm / no-op benches; the full
multi-module port of Netty's own sources is [`../netty`](../netty/).

It demonstrates:

- a declarative `jk.toml` with `[application] main`
- Netty 4.2's event-loop idiom (`MultiThreadIoEventLoopGroup` over `NioIoHandler.newFactory()`)
- an `EmbeddedChannel` unit test of the echo handler, no socket needed
- `[image]` with a JRE 25 base and an AOT cache (`jk image`)
- the test-tier table (`[test] exclude-tags` plus one profile per tag) and the house guard baseline

```sh
jk build          # compiles, runs the unit tier, packages target/netty-echo-0.1.0.jar
jk test
jk guard
jk run            # listens on 8080; `nc localhost 8080` echoes what you type
jk image          # OCI image on the JRE 25 base
```

Bench, from the `jk` repo: `./scripts/netty-echo-bench.sh /path/to/jk-examples/02-services/netty-echo`.

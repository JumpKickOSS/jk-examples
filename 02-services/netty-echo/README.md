# 02-services/netty-echo — Netty echo server on the published artifact

An idiomatic single-module JumpKick app on `io.netty:netty-all` (an exact 4.2 pin). This is the small dogfood unit for cold / warm / no-op benches; the full
multi-module port of Netty's own sources is [`../netty`](../netty/).

It demonstrates:

- a declarative `jk.toml` with `[application] main`
- Netty 4.2's event-loop idiom (`MultiThreadIoEventLoopGroup` over `NioIoHandler.newFactory()`)
- an `EmbeddedChannel` unit test of the echo handler, no socket needed
- `[image]` on the default base (a JRE of the project's `java`) with an AOT cache (`jk image`)
- the test tiers (the built-in `integration` suite plus one `[test.suites.<tag>]` per other cost tag) and the house guard baseline

```sh
jk build          # compiles, runs the unit tier, packages target/netty-echo-0.1.0.jar
jk test
jk guard
jk run            # listens on 8080; `nc localhost 8080` echoes what you type
jk image          # OCI image on the default JRE base
```

Bench, from the `jk` repo: `./scripts/netty-echo-bench.sh /path/to/jk-examples/02-services/netty-echo`.

# 01-start/java-cli — core JVM AOT

A plain Java CLI with one dependency and no framework or plugin. It demonstrates the path every
JVM project gets for free:

- `jk build --aot-cache` (`-T`) — trains a JEP 514 AOT cache from one run of the application and
  writes it with a launcher under `target/aot-cache/`, entirely core jk. The build starts the app
  once with the cache and fails if the JVM refuses it.
- `./target/aot-cache/run.sh` — the payoff: the app starts from the mapped cache. The cache is
  keyed to the JVM that trained it and to that directory, and `run.sh` pins both.
- `jk run` — classpath execution from `target/classes/`; it does not use the cache (a cache dump
  rejects directory classpath entries).

`gson` is pinned to `2.14.0`; `jk update` moves it.

```sh
jk build --aot-cache
./target/aot-cache/run.sh
jk run
jk guard
```

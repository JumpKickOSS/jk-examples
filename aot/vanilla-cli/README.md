# aot/vanilla-cli — core JVM AOT

A plain Java CLI with one dependency and no framework or plugin. It demonstrates the path every
JVM project gets for free:

- `jk build --aot-cache` — the training run and the mapped-cache start (JDK 25 AOT, AppCDS
  fallback on older JDKs), entirely core jk.
- `jk run` — classpath execution of a plain application.

`gson` is pinned to `2.14.0`; `jk update` moves it.

```sh
jk build --aot-cache
jk run
jk guard
```

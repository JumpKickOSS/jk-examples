# vanilla-cli — core JVM AOT

Plain Java app, no plugins. Validates:

- `jk build --aot-cache` — the training run + mapped-cache start (JDK 25 AOT;
  AppCDS fallback on older JDKs), entirely core jk (no framework involved).
- `jk run` — classpath exec of a plain application.

Run: `jk build --aot-cache && jk run`

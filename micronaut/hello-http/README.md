# micronaut/hello-http — a Micronaut HTTP service

Minimal Micronaut service on the built-in `[micronaut]` plugin. It demonstrates:

- `[micronaut] version = "5.1.5"` — the platform BOM is an exact pin, starters are versionless,
  and `jk update` moves the pin.
- `assembly = true` — one runnable fat jar (Maven shade parity).
- `[image]` with a JRE 25 base and `aot-cache = true` — `jk image` writes a shippable container.
- `@MicronautTest` with `@Client("/")` as the smoke test, in the unit tier of the
  `[test] exclude-tags` table.
- The house guard baseline (there is no Micronaut guard pack yet).

```sh
jk build
jk test
jk run             # then: curl -s localhost:8080/hello
jk guard
jk image           # optional: an OCI image with a trained AOT cache
```

`src/test/resources/application-test.properties` binds the test server to `localhost`.
Without it Micronaut advertises the machine's hostname, and on a host that resolves only to
IPv6 link-local or VPN addresses the injected client waits on a socket nothing answers.

Optional deploy optimization, not for every edit cycle: `[micronaut] aot = true`.

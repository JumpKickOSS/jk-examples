# Micronaut hello-http

Minimal **Micronaut 5** HTTP service for JumpKick dogfood (JK-1538).

Uses the built-in `[micronaut]` plugin with a **5.x major-line floor** (caret). First
`jk lock` pins an exact platform version; `jk update` may lift within 5.x.

## Run

```sh
# from a current jk install (see repo root README)
cd micronaut/hello-http
jk lock
jk build
jk test
jk run                              # INFO (default)
MICRONAUT_ENVIRONMENTS=dev jk run   # DEBUG
# then: curl -s localhost:8080/hello
```

Optional AOT (deploy optimization, not every edit cycle):

```toml
[micronaut]
version = "5"
aot = true
```

## Layout

Traditional `src/main/java` + `src/test/java`. Fat assembly jar for `java -jar` / `jk run`.

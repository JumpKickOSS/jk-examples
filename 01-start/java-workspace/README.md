# 01-start/java-workspace — three modules, one lockfile

`links` is a tiny URL shortener split the way a real codebase splits: an API, its implementations,
and an application. No framework, so what you see is jk's workspace model and nothing else.

```text
java-workspace/
  jk.toml          # [workspace] modules, shared versions, identity, the integration tier
  jk-lock.toml     # the one lockfile for every module
  api/             # ShortLink, Codes, the LinkStore interface + its contract test
  store/           # MemoryLinkStore, FileLinkStore (tests reuse api's contract test)
  cli/             # `links add|open|list`, the [application]
```

| Idea | Where |
|------|-------|
| **Identity once**: `group`, `version`, `java` live in the root; a module's `jk.toml` needs only `name` | `*/jk.toml` |
| **One version per library**: `[workspace.dependencies]` pins JSpecify and JUnit; modules say `jspecify.workspace = true` | root `jk.toml` |
| **API vs implementation**: `api` *exports* JSpecify (`[export-dependencies]`), so every module that depends on `api` compiles against it; a plain `[dependencies]` entry would reach consumers only at runtime | `api/jk.toml` |
| **Sibling edges**: `api.workspace = true` — built in dependency order, from source, no install step | `store/`, `cli/` |
| **Shared test code**: `api`'s abstract `LinkStoreContract` runs against both stores through `api = { workspace = true, kind = "tests" }` | `store/jk.toml` |
| **Tiers once**: the root declares `[profiles.integration]`; every module gets it | root `jk.toml` |

```sh
jk build                     # every module, in order; unit tests
jk build -m store            # one module (and what it needs)
jk test -p integration       # the integration tier: FileLinkStoreConcurrencyTest
jk run -m cli -- add https://jumpkick.build
jk run -m cli -- list
jk tree store --transitive   # what store compiles against: api, and the JSpecify api exports
jk explain                   # the module graph and what would run
jk build --affected          # only the modules your working tree touched, and their dependents
jk guard
```

## Teaches

- A workspace is a root `jk.toml` with `[workspace] modules` and one lockfile; members are small.
- The difference between `[dependencies]` and `[export-dependencies]` for a module others build on.
- Sharing tests between modules with `kind = "tests"` instead of a separate test-utils module.

## Validates

- Sibling build order, inherited identity and profiles, one lock for every member.
- A sibling's exported shared (`x.workspace = true`) dependency on its consumers' compile classpath.
- Test classes of one module on another's test classpath.

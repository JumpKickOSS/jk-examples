# 05-recipes/supply-chain — what you ship, pinned and checked

`csvjson` turns a CSV file into JSON with Jackson 3. The program is small on purpose; the point is
the supply-chain controls around it, all declared in `jk.toml` and `jk-guards.toml` and all run by
jk itself, with no plugins and no extra CI tools:

| Control | Where | What it does |
|---|---|---|
| Lockfile | `jk-lock.toml` | Every artifact, its exact version and its SHA-256. `jk build` never re-resolves; a changed byte fails the fetch. |
| Exact pins | `[guards.no-floating-versions]` | No ranges, no `latest`, no snapshots in `jk.toml`. |
| Repository deny list | `[deny.sources]` | Resolution refuses the named hosts; `jk deny` re-checks the lock. |
| Advisory audit | `jk audit` | Every locked artifact checked against OSV; non-zero exit on a finding, so CI fails on it. |
| Reviewed exception | `[audit] ignore` | One advisory accepted with a reason and an expiry. On `until` it counts again. |
| Test-only quarantine | `[guards.test-libraries-stay-in-test]` | The excepted library may only ever be a test dependency. |
| SBOM | the jar, `jk publish -S` | CycloneDX in every application jar; CycloneDX and SPDX files on publish. |

```sh
jk build                     # lock-checked fetch, compile, tests, guards
jk run -- src/test/resources/guests.csv
jk audit                     # OSV advisories for the locked closure; reports the accepted one
jk deny                      # the deny policy against the lock
jk publish -S -U             # dry run: writes target/sbom/csvjson-0.1.0.{cdx,spdx}.json
unzip -p target/csvjson-0.1.0.jar META-INF/sbom/application.cdx.json
```

## The accepted advisory

`commons-io` 2.13.0 has `GHSA-78wr-2p64-hpwj`, a denial of service in `XmlStreamReader`. Here it is
a test dependency that reads a fixture file, so the advisory cannot reach production. The exception
records that judgement where the next reader will see it:

```toml
[audit]
ignore = [
  { id = "GHSA-78wr-2p64-hpwj", reason = "commons-io is test-only here; XmlStreamReader never sees untrusted input", until = "2027-03-31" },
]
```

The guard makes the reason enforceable: if someone moves `commons-io` to `[dependencies]`,
`jk build` fails with `instead = "java.nio.file.Files"`. Once `until` passes, `jk audit` counts
the advisory again and CI fails until someone looks at it. The real fix is `jk update`.

## Teaches

- Exact pins plus a lockfile with checksums are the baseline; `jk add` and `jk update` write both.
- An advisory exception has a reason and an expiry, and a guard that keeps its reason true.
- The SBOM comes from the same lock that built the jar, so it lists what actually shipped.

## Validates

- `jk audit` against OSV with an `[audit] ignore` entry that has an expiry, and its exit code.
- `[deny.sources]` at resolve time and through `jk deny`.
- `depend` guards: `no-dynamic`/`no-snapshot`, and `only-in` with `instead`.
- SBOM: embedded in the jar, and written by `jk publish -S -U`.

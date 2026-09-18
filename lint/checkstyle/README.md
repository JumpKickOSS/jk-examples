# lint/checkstyle — Checkstyle as a cached build step

`[lint] checkstyle` names the rule set; the `lint-checkstyle` step runs after compile over
`src/main/java`, and every finding is a diagnostic — file, line, message and the rule id — in
`target/jk-results.md`, on the terminal, and in the MCP `jk_diagnostics` an agent reads. A clean
module is a cache hit on the next build; a source or rule-set edit re-runs the step and nothing
else does. Checkstyle is not in `[dependencies]`: the step fetches `com.puppycrawl.tools:checkstyle`
at its pinned release (`checkstyle-version`, `14.1.0` unless you say otherwise) into the store and
hashes it into the step's key, with the rule set beside it.

```toml
[lint]
checkstyle = "checkstyle.xml"
```

`checkstyle.xml` turns on five rules a formatter cannot decide — `MagicNumber`,
`MissingJavadocMethod`, `NeedBraces`, `FinalClass`, `HideUtilityClassConstructor` — and
`Sample.java` visibly satisfies each. Nothing `jk format` owns (layout, import order, unused
imports) is repeated here.

```sh
jk build     # compiles, lints, tests, packages target/lint-checkstyle-0.0.1.jar; a second build is up to date
jk run       # 200 -> OK / 400 -> FAIL / 429 -> RETRY / 500 -> RETRY
jk test
jk guard
```

## A finding

Write `classify` the quick way — `if (status < 400) return Outcome.OK;` — and javac and the
tests still pass, but the step does not:

```text
✘ lint-checkstyle
  src/main/java/demo/Sample.java:41:9: 'if' construct must use '{}'s. [NeedBraces]
  src/main/java/demo/Sample.java:41:22: '400' is a magic number. [MagicNumber]
  …
```

The rule set says `severity = "error"`, so each finding is an error and the step fails the
build; `fail-on = "warning"` would fail on warnings too, `fail-on = "never"` reports and passes.
The four-tool variant is [`../all-tools`](../all-tools/).

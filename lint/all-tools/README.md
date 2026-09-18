# lint/all-tools — Checkstyle, PMD, SpotBugs and detekt in one module

The four-tool variant of [`../checkstyle`](../checkstyle/): one `[lint]` table enables each tool
with its configuration, and each is a cached step after compile — `lint-checkstyle` and `lint-pmd`
over `src/main/java`, `lint-spotbugs` over the compiled classes against the compile classpath,
`lint-detekt` over `src/main/kotlin`. Every tool's report is read back (Checkstyle's and detekt's
XML, PMD's XML, SpotBugs's `BugCollection`), so a finding is a diagnostic with its rule id in
`target/jk-results.md` and the MCP `jk_diagnostics`, and severity follows the tool: PMD
priorities 1–2 and SpotBugs priority 1 are errors, the rest and detekt's findings warnings.
`fail-on = "error"` (the default) fails the build on the errors and lets warnings through.

```toml
[lint]
checkstyle = "checkstyle.xml"
pmd        = ["pmd.xml"]
spotbugs   = true
detekt     = true
```

None of the tools is in `[dependencies]`: each is fetched at its pinned release
(`checkstyle-version` `14.1.0`, `pmd-version` `7.27.0`, `spotbugs-version` `4.10.4`,
`detekt-version` `1.23.8`) into the store and forked on the build JDK. `pmd.xml` is a project
ruleset over PMD's `category/java/bestpractices.xml` minus `SystemPrintln`, since a command-line
program's stdout is its output; detekt runs its default configuration, so `Band.kt` names its
constants (`MagicNumber`) and matches its file name (`MatchingDeclarationName`). The Java and
Kotlin sides are independent: `Sample` is the program, `Band` is what detekt audits.

```sh
jk build     # compiles Java and Kotlin, runs the four tools, tests, packages target/lint-all-tools-0.0.1.jar
jk build     # up to date: four cached lint steps
jk explain   # lists lint-checkstyle, lint-pmd, lint-spotbugs, lint-detekt
jk run       # 200 -> OK / 400 -> FAIL / 429 -> RETRY / 500 -> RETRY
jk test
jk guard
```

## A finding per tool

- Drop the braces off an `if` in `Sample.java`: Checkstyle's `NeedBraces` is an error and fails
  the build.
- Put a `System.out.println` back into `pmd.xml`'s scope by deleting the `<exclude>`: PMD's
  `SystemPrintln` (priority 2) fails the build.
- Compare two `Integer`s with `==` in `Sample.java`: SpotBugs's `RC_REF_COMPARISON` is a
  high-priority pattern and fails the build.
- Replace `SUCCESS_FLOOR` with `200` in `Band.kt`: detekt's `MagicNumber` is a warning — reported
  as a diagnostic, and the build passes under the default `fail-on`; `fail-on = "warning"` fails it.

# test/testng — TestNG through its JUnit Platform engine

`jk test` discovers and runs tests through the JUnit Platform launcher and nothing else. Declare
`org.testng:testng` and `jk lock` adds `org.junit.support:testng-engine` — the JUnit team's TestNG
engine, on a release line of its own — beside the launcher, so the lock is the only place the
engine appears:

```toml
[test-dependencies]
testng = "7.12.0"
```

Discovery is the engine's class scan over the test root, the way Jupiter's is: every class with a
TestNG `@Test` runs, a `@DataProvider` fans out into one result per row, and a `testng.xml` suite
file is not read. A TestNG `groups` value is a Platform tag, so the tier table applies unchanged:
`AdderIntegrationTest` is `@Test(groups = "integration")`, out of the unit tier and in
`--profile integration`. Results render per test as they do for Jupiter — the class and the
method from the engine's ids — and a failing method is named in `target/jk-results.md` with
TestNG's own assertion message (`expected [5] but found [4]`).

```sh
jk build                        # compiles, runs the unit tier (5 TestNG tests), packages target/testng-adder-1.0.0.jar
jk test                         # Passed 5 tests: AdderTest's two methods and the three data-provider rows
jk test --profile integration   # Passed 1 test: AdderIntegrationTest
jk run                          # 2 + 2 = 4
jk guard
```

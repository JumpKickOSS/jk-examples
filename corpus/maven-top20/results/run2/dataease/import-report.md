# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/dataease/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<resources>` with `<filtering>true</filtering>` on src/main/resources — jk has no resource filtering; `${...}` placeholders in those files are copied as written. Read the values at runtime or check the filled-in file in.
- `<plugin>flatten-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [sdk] `<resources>` with `<filtering>true</filtering>` on src/main/resources — jk has no resource filtering; `${...}` placeholders in those files are copied as written. Read the values at runtime or check the filled-in file in.
- [sdk] `<plugin>flatten-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [sdk] `<dependencyManagement>` inherited from parent io.dataease:dataease:2.10.26 is carried as `[platform]` io.dataease:dataease:2.10.26, so its managed versions govern transitive dependencies as well.
- [sdk] dependencies org.projectlombok:lombok, org.bouncycastle:bcprov-jdk15to18 inherited from parent io.dataease:dataease:2.10.26.
- [sdk] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.


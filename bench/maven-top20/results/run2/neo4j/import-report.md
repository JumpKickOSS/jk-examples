# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/neo4j/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `build-helper-maven-plugin` goal `regex-property` was not imported; only `add-source` and `add-test-source` map to source roots.
- `<resources>` directory src/main/resources/META-INF,  is outside `src/main/resources` — jk's layout reads `src/main/resources` only; move the files there.
- `<testResources>` directory  is outside `src/test/resources` — jk's layout reads `src/test/resources` only; move the files there.
- `<plugin>maven-failsafe-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-dependency-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>spotless-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>properties-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-enforcer-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `build-helper-maven-plugin` goal `regex-property` was not imported; only `add-source` and `add-test-source` map to source roots.
- [annotations] `<resources>` directory src/main/resources/META-INF,  is outside `src/main/resources` — jk's layout reads `src/main/resources` only; move the files there.
- [annotations] `<testResources>` directory  is outside `src/test/resources` — jk's layout reads `src/test/resources` only; move the files there.
- [annotations] `<plugin>maven-failsafe-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<plugin>maven-dependency-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<plugin>spotless-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<plugin>properties-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<plugin>maven-enforcer-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<plugin>licensing-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<plugin>license-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<plugin>license-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [annotations] `<exclusions>` on com.google.guava:guava — exclusion support lands in a later slice; exclusions were dropped.
- [annotations] `<exclusions>` on com.google.testing.compile:compile-testing — exclusion support lands in a later slice; exclusions were dropped.
- [annotations] `<exclusions>` on com.google.truth:truth — exclusion support lands in a later slice; exclusions were dropped.
- [annotations] `<dependencyManagement>` in workspace parent org.neo4j:parent:2026.08.0 pins 116 versions no declared dependency uses (at.yawk.lz4:lz4-java, blue.strategic.parquet:parquet-floor, co.helmethair:scalatest-junit-runner, com.fasterxml.jackson.core:jackson-annotations, com.fasterxml.jackson.core:jackson-core, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [annotations] `<dependencyManagement>` in a parent pins 18 versions no declared dependency uses (com.lihaoyi:fansi_3, com.lihaoyi:pprint_3, com.lihaoyi:sourcecode_3, io.circe:circe-core_3, io.circe:circe-generic_3, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [annotations] versions for com.google.guava:guava, com.google.testing.compile:compile-testing, com.google.truth:truth managed by workspace parent org.neo4j:parent:2026.08.0.
- [community] `build-helper-maven-plugin` goal `regex-property` was not imported; only `add-source` and `add-test-source` map to source roots.
- [community] `<resources>` directory src/main/resources/META-INF,  is outside `src/main/resources` — jk's layout reads `src/main/resources` only; move the files there.
- [community] `<testResources>` directory  is outside `src/test/resources` — jk's layout reads `src/test/resources` only; move the files there.
- [community] `<plugin>maven-failsafe-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [community] `<plugin>maven-dependency-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [community] `<plugin>spotless-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [community] `<plugin>properties-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [community] `<plugin>maven-enforcer-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [community] `<dependencyManagement>` in workspace parent org.neo4j:parent:2026.08.0 pins 119 versions no declared dependency uses (at.yawk.lz4:lz4-java, blue.strategic.parquet:parquet-floor, co.helmethair:scalatest-junit-runner, com.fasterxml.jackson.core:jackson-annotations, com.fasterxml.jackson.core:jackson-core, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [community] `<dependencyManagement>` in a parent pins 18 versions no declared dependency uses (com.lihaoyi:fansi_3, com.lihaoyi:pprint_3, com.lihaoyi:sourcecode_3, io.circe:circe-core_3, io.circe:circe-generic_3, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [community] Maven profile `jdk25-modules` (activation jdk=25): active on this machine and folded into the import: no payload.
- [community] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.
- [packaging] `build-helper-maven-plugin` goal `regex-property` was not imported; only `add-source` and `add-test-source` map to source roots.
- [packaging] `<resources>` directory src/main/resources/META-INF,  is outside `src/main/resources` — jk's layout reads `src/main/resources` only; move the files there.
- [packaging] `<testResources>` directory  is outside `src/test/resources` — jk's layout reads `src/test/resources` only; move the files there.
- [packaging] `<plugin>maven-failsafe-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [packaging] `<plugin>maven-dependency-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [packaging] `<plugin>spotless-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [packaging] `<plugin>properties-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [packaging] `<plugin>maven-enforcer-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [packaging] `<dependencyManagement>` in workspace parent org.neo4j:parent:2026.08.0 pins 119 versions no declared dependency uses (at.yawk.lz4:lz4-java, blue.strategic.parquet:parquet-floor, co.helmethair:scalatest-junit-runner, com.fasterxml.jackson.core:jackson-annotations, com.fasterxml.jackson.core:jackson-core, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [packaging] `<dependencyManagement>` in a parent pins 18 versions no declared dependency uses (com.lihaoyi:fansi_3, com.lihaoyi:pprint_3, com.lihaoyi:sourcecode_3, io.circe:circe-core_3, io.circe:circe-generic_3, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [packaging] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.


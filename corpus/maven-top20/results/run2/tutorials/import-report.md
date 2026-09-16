# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/tutorials/pom.xml`

## Tier 3 — not imported

These constructs have no jk equivalent and were skipped or stubbed.

- `<build><extensions>` is not supported. Move build extensions to a custom jk task once tasks land.

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<plugin>exec-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-surefire-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-pmd-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>directory-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-install-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-war-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<target>` declared 8; jk's floor is 17; bytecode level raised — written as `java = 17`.
- `<dependency><optional>true</optional></dependency>` on org.apache.maven.surefire:surefire-logger-api — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- Maven profile `default-jdk8`: compiler settings → `[profiles.default-jdk8]` `javac`; select with `--profile default-jdk8`.
- Maven profile `default-jdk8`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default-heavy`: compiler settings → `[profiles.default-heavy]` `javac`; select with `--profile default-heavy`.
- Maven profile `default-heavy`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-jdk8`: compiler settings → `[profiles.integration-jdk8]` `javac`; select with `--profile integration-jdk8`.
- Maven profile `integration-jdk8`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-heavy`: compiler settings → `[profiles.integration-heavy]` `javac`; select with `--profile integration-heavy`.
- Maven profile `integration-heavy`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default-jdk17`: compiler settings → `[profiles.default-jdk17]` `javac`; select with `--profile default-jdk17`.
- Maven profile `default-jdk17`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default`: compiler settings → `[profiles.default]` `javac`; select with `--profile default`.
- Maven profile `default`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default-jdk22`: compiler settings → `[profiles.default-jdk22]` `javac`; select with `--profile default-jdk22`.
- Maven profile `default-jdk22`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default-jdk23`: compiler settings → `[profiles.default-jdk23]` `javac`; select with `--profile default-jdk23`.
- Maven profile `default-jdk23`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default-jdk24`: compiler settings → `[profiles.default-jdk24]` `javac`; select with `--profile default-jdk24`.
- Maven profile `default-jdk24`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default-jdk25`: compiler settings → `[profiles.default-jdk25]` `javac`; select with `--profile default-jdk25`.
- Maven profile `default-jdk25`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `default-jdk26`: compiler settings → `[profiles.default-jdk26]` `javac`; select with `--profile default-jdk26`.
- Maven profile `default-jdk26`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-jdk17`: compiler settings → `[profiles.integration-jdk17]` `javac`; select with `--profile integration-jdk17`.
- Maven profile `integration-jdk17`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration`: compiler settings → `[profiles.integration]` `javac`; select with `--profile integration`.
- Maven profile `integration`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-jdk22`: compiler settings → `[profiles.integration-jdk22]` `javac`; select with `--profile integration-jdk22`.
- Maven profile `integration-jdk22`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-jdk23`: compiler settings → `[profiles.integration-jdk23]` `javac`; select with `--profile integration-jdk23`.
- Maven profile `integration-jdk23`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-jdk24`: compiler settings → `[profiles.integration-jdk24]` `javac`; select with `--profile integration-jdk24`.
- Maven profile `integration-jdk24`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-jdk25`: compiler settings → `[profiles.integration-jdk25]` `javac`; select with `--profile integration-jdk25`.
- Maven profile `integration-jdk25`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-jdk26`: compiler settings → `[profiles.integration-jdk26]` `javac`; select with `--profile integration-jdk26`.
- Maven profile `integration-jdk26`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `live-all`: compiler settings → `[profiles.live-all]` `javac`; select with `--profile live-all`.
- Maven profile `live-all`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `parents`: no convertible payload; dropped.
- Maven profile `default-disabled`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `integration-disabled`: plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.


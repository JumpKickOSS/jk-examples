# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/questdb/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<plugin>maven-deploy-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>jacoco-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-release-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [core] `<resources>` directory .. is outside `src/main/resources` — jk's layout reads `src/main/resources` only; move the files there.
- [core] `<plugin>maven-enforcer-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [core] `<plugin>maven-clean-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [core] `<plugin>maven-deploy-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [core] `<plugin>buildnumber-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [core] `<plugin>maven-surefire-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [core] dependencies org.jetbrains:annotations inherited from a parent.
- [core] Maven profile `per-fork-test-log` (activation property=env.QDB_LOG_W_FILE_LOCATION, a command-line switch jk has no equivalent for): plugins=[maven-surefire-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [core] Maven profile `local-client`: no convertible payload; dropped.
- [core] Maven profile `javadoc`: plugins=[maven-javadoc-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [core] Maven profile `maven-central-release`: plugins=[maven-javadoc-plugin,maven-source-plugin,maven-gpg-plugin,central-publishing-maven-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [core] Maven profile `build-web-console`: plugins=[download-maven-plugin,maven-assembly-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [core] Maven profile `build-binaries`: plugins=[exec-maven-plugin,maven-assembly-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [core] Maven profile `build-rust-library`: plugins=[rust-maven-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [core] Maven profile `platform-windows-x86-64` (activation os=windows): no convertible payload; dropped.
- [core] Maven profile `platform-linux-x86-64` (activation os=unix): active on this machine and folded into the import: properties=[jemalloc.so,runtime.assembly,runtime.name,archive.name,platform.dir.name].
- [core] Maven profile `platform-freebsd-x86-64` (activation os=FreeBSD): no convertible payload; dropped.
- [core] Maven profile `platform-linux-aarch64` (activation os=unix): no convertible payload; dropped.
- [core] Maven profile `platform-osx-aarch64` (activation os=Mac): no convertible payload; dropped.
- [core] Maven profile `jacoco`: plugins=[jacoco-maven-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [core] Maven profile `java25+` (activation jdk=(24,)): active on this machine and folded into the import: 1 dependency; properties=[excludePattern1,javac.compile.target,java.enforce.version,questdb.artifactid,excludeTestPattern1,jdk.version,javac.compile.source].
- [core] Maven profile `qdbr-release`: no convertible payload; dropped.
- [core] Maven profile `qdbr-coverage`: plugins=[rust-maven-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [benchmarks] `<plugin>maven-shade-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [benchmarks] Maven profile `local-client`: no convertible payload; dropped.
- [benchmarks] Maven profile `javadoc`: plugins=[maven-javadoc-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [utils] `<plugin>maven-shade-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [utils] Maven profile `local-client`: no convertible payload; dropped.
- [utils] Maven profile `java17+` (activation jdk=(17,)): active on this machine and folded into the import: properties=[questdb.artifactid].


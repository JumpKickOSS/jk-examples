# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/cryptomator/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<plugin>maven-surefire-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-dependency-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>license-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<exclusions>` on com.auth0:java-jwt — exclusion support lands in a later slice; exclusions were dropped.
- dependencies org.cryptomator:integrations-linux inherited from a parent.
- Maven profile `coverage`: plugins=[jacoco-maven-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `dependency-check`: plugins=[dependency-check-maven] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `mac` (activation os=mac): 1 dependency → `[features.mac]` (optional deps integrations-mac; not in `default`, activate with `--features mac`).
- Maven profile `linux-aarch64` (activation os=unix): per-platform dependencies org.cryptomator:integrations-linux, org.openjfx:javafx-base:linux-aarch64, org.openjfx:javafx-graphics:linux-aarch64, org.openjfx:javafx-controls:linux-aarch64, org.openjfx:javafx-fxml:linux-aarch64 → declare a `[variants]` dimension for the platform axis; nothing written.
- Maven profile `linux-x86_64` (activation os=unix): active on this machine and folded into the import: 1 dependency.
- Maven profile `win` (activation os=windows): 1 dependency → `[features.win]` (optional deps integrations-win; not in `default`, activate with `--features win`).
- Maven profile `run`: plugins=[maven-dependency-plugin,exec-maven-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- Maven profile `dev`: no convertible payload; dropped.
- Maven profile `run-win` (activation os=windows): no convertible payload; dropped.
- Maven profile `run-mac` (activation os=mac): no convertible payload; dropped.
- Maven profile `run-linux` (activation os=unix): active on this machine and folded into the import: properties=[run.jvmArgs].


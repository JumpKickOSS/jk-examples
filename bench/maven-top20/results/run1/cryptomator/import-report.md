# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/cryptomator/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<exclusions>` on com.auth0:java-jwt — exclusion support lands in a later slice; exclusions were dropped.
- Maven profile `coverage`: plugins=[jacoco-maven-plugin] (plugin mapping is not yet implemented).
- Maven profile `dependency-check`: plugins=[dependency-check-maven] (plugin mapping is not yet implemented).
- Maven profile `mac`: activation=os=mac (use jk target predicates per dep); 1 dependency (convert to a jk feature `mac` if opt-in, or move into the main deps list).
- Maven profile `linux-aarch64`: activation=os=unix (use jk target predicates per dep); 5 dependencies (convert to a jk feature `linux-aarch64` if opt-in, or move into the main deps list).
- Maven profile `linux-x86_64`: activation=os=unix (use jk target predicates per dep); 1 dependency (convert to a jk feature `linux-x86_64` if opt-in, or move into the main deps list).
- Maven profile `win`: activation=os=windows (use jk target predicates per dep); 1 dependency (convert to a jk feature `win` if opt-in, or move into the main deps list).
- Maven profile `run`: plugins=[maven-dependency-plugin,exec-maven-plugin] (plugin mapping is not yet implemented).
- Maven profile `dev`: properties=[run.appDir,run.linuxFuseArgs,run.macMountPointsDir,run.macUpdateArgs] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `run-win`: activation=os=windows (use jk target predicates per dep); properties=[run.jvmArgs] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `run-mac`: activation=os=mac (use jk target predicates per dep); properties=[run.jvmArgs] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `run-linux`: activation=os=unix (use jk target predicates per dep); properties=[run.jvmArgs] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- `<plugin>maven-jar-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-surefire-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-dependency-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>license-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/dataease/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<parent>` was referenced (org.springframework.boot:spring-boot-starter-parent:3.5.15) but jk-import did not flatten its dependencyManagement / properties / build config. Run `mvn help:effective-pom` and re-import if any dependency versions are unresolved.
- `<plugin>flatten-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [sdk] `<parent>` was referenced (io.dataease:dataease:${dataease.version}) but jk-import did not flatten its dependencyManagement / properties / build config. Run `mvn help:effective-pom` and re-import if any dependency versions are unresolved.
- [sdk] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.


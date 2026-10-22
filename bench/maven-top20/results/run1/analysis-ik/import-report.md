# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/analysis-ik/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<parent>` was referenced (org.sonatype.oss:oss-parent:9) but jk-import did not flatten its dependencyManagement / properties / build config. Run `mvn help:effective-pom` and re-import if any dependency versions are unresolved.
- Maven profile `disable-java8-doclint`: activation=jdk=[1.8,); properties=[additionalparam] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `release`: plugins=[nexus-staging-maven-plugin,maven-release-plugin,maven-compiler-plugin,maven-gpg-plugin,maven-source-plugin,maven-javadoc-plugin] (plugin mapping is not yet implemented).
- `<plugin>maven-surefire-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-source-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [elasticsearch] `<plugin>maven-assembly-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [opensearch] `<plugin>maven-assembly-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


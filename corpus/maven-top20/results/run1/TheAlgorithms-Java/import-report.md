# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/TheAlgorithms-Java/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<dependency>` org.junit.jupiter:junit-jupiter has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<plugin>maven-surefire-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>jacoco-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-checkstyle-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>spotbugs-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-pmd-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


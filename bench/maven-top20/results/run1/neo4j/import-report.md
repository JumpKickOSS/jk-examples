# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/neo4j/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- Maven profile `errorprone`: plugins=[maven-compiler-plugin] (plugin mapping is not yet implemented).
- Maven profile `has-sources`: activation=file-existence (jk has no equivalent — refactor to a jk profile); plugins=[licensing-maven-plugin,license-maven-plugin,license-maven-plugin] (plugin mapping is not yet implemented).
- Maven profile `custom-vm-target`: activation=property=env.VM_TARGET_VERSION (no jk equivalent — replace with an explicit jk profile or feature); properties=[vm.target.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `parallelTestExecution`: activation=property=!sequentialTests (no jk equivalent — replace with an explicit jk profile or feature); properties=[forkCounts,parallel.tests,reuseForks] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `multiVersionBlockFormat`: activation=property=NEO4J_OVERRIDE_STORE_FORMAT (no jk equivalent — replace with an explicit jk profile or feature); properties=[excludedTestGroups,test.runner.jvm.settings.db.format] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `test-block`: activation=property=NEO4J_OVERRIDE_STORE_FORMAT (no jk equivalent — replace with an explicit jk profile or feature); properties=[test.runner.jvm.settings.db.format] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `test-envelopes`: properties=[test.runner.jvm.settings.log.format] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `test-spd`: 1 dependency (convert to a jk feature `test-spd` if opt-in, or move into the main deps list); properties=[test.runner.jvm.settings.dbms.factory,test.vm.netty.leakDetection.level] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `zinc`: properties=[scala.plugin.recompileMode] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `build-service`: activation=property=build.service.target.dir (no jk equivalent — replace with an explicit jk profile or feature).
- Maven profile `default-query-lang-cypher-25`: activation=property=NEO4J_OVERRIDE_QUERY_LANGUAGE (no jk equivalent — replace with an explicit jk profile or feature); properties=[excludedTestGroups,test.runner.jvm.settings.query.language] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `scala-fatal-warnings`: activation=property=scalaFatalWarnings (no jk equivalent — replace with an explicit jk profile or feature); properties=[scala.wunused.conf] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- `<plugin>maven-failsafe-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-dependency-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>spotless-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-source-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>properties-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>build-helper-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-enforcer-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [annotations] `<dependency>` com.google.guava:guava has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [annotations] `<dependency>` com.google.testing.compile:compile-testing has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [annotations] `<dependency>` com.google.truth:truth has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [annotations] `<plugin>maven-dependency-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [community] Maven profile `jdk25-modules`: activation=jdk=25.
- [community] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.
- [packaging] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.


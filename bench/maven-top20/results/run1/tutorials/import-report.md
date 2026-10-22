# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/tutorials/pom.xml`

## Tier 3 — not imported

These constructs have no jk equivalent and were skipped or stubbed.

- `<build><extensions>` is not supported. Move build extensions to a custom jk task once tasks land.

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<dependency><optional>true</optional></dependency>` on org.apache.maven.surefire:surefire-logger-api — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- Maven profile `default-jdk8`: properties=[maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default-heavy`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-jdk8`: properties=[maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-heavy`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default-jdk17`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven.compiler.release] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default-jdk22`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default-jdk23`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default-jdk24`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default-jdk25`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `default-jdk26`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-jdk17`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven.compiler.release] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-jdk22`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-jdk23`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-jdk24`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-jdk25`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-jdk26`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target,maven-pmd-plugin.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `live-all`: properties=[project.build.sourceEncoding,java.version,maven.compiler.source,maven.compiler.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `parents`: contained no convertible payload; dropped.
- Maven profile `default-disabled`: plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- Maven profile `integration-disabled`: plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- `<plugin>exec-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-surefire-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-pmd-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>directory-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-install-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-war-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/zipkin/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- Maven profile `include-lens`: activation=property=!skipLens (no jk equivalent — replace with an explicit jk profile or feature).
- Maven profile `include-benchmarks`: activation=property=!skipTests (no jk equivalent — replace with an explicit jk profile or feature).
- Maven profile `release`: plugins=[flatten-maven-plugin,central-publishing-maven-plugin,maven-gpg-plugin,maven-source-plugin,maven-javadoc-plugin] (plugin mapping is not yet implemented).
- Maven profile `netbeans`: activation=activeByDefault; properties=[org-netbeans-modules-editor-indent.CodeStyle.usedProfile,org-netbeans-modules-editor-indent.CodeStyle.project.indent-shift-width,org-netbeans-modules-editor-indent.CodeStyle.project.spaces-per-tab,org-netbeans-modules-editor-indent.CodeStyle.project.tab-size,org-netbeans-modules-editor-indent.CodeStyle.project.text-limit-width,org-netbeans-modules-editor-indent.CodeStyle.project.expand-tabs] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- Maven profile `module-info`: activation=file-existence (jk has no equivalent — refactor to a jk profile); plugins=[maven-bundle-plugin,maven-jar-plugin] (plugin mapping is not yet implemented).
- `<plugin>maven-dependency-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-help-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-surefire-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-failsafe-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-enforcer-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>license-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [zipkin] `<dependency><optional>true</optional></dependency>` on com.google.code.gson:gson — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin] `<plugin>maven-shade-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [zipkin-junit5] `<exclusions>` on com.squareup.okhttp3:mockwebserver — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-storage] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.
- [zipkin-collector] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.
- [zipkin-server] `<exclusions>` on org.springframework.boot:spring-boot-starter — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-server] `<exclusions>` on org.springframework.boot:spring-boot-starter-actuator — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-server] `<exclusions>` on com.linecorp.armeria:armeria-spring-boot3-autoconfigure — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-server] `<dependency>` io.micrometer:micrometer-registry-prometheus has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] `<dependency>` io.micrometer:micrometer-registry-prometheus-simpleclient has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] `<dependency>` io.micrometer:micrometer-core has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] `<dependency>` org.apache.logging.log4j:log4j-core has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-storage-cassandra — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-storage-elasticsearch — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-storage-mysql-v1 — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on org.mariadb.jdbc:mariadb-java-client — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on com.zaxxer:HikariCP — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-collector-activemq — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-collector-kafka — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-collector-rabbitmq — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-collector-scribe — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.zipkin2:zipkin-collector-pulsar — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.brave:brave — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency>` io.zipkin.brave:brave has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.brave:brave-context-slf4j — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<dependency>` io.zipkin.brave:brave-context-slf4j has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] `<dependency><optional>true</optional></dependency>` on io.zipkin.reporter2:zipkin-reporter-brave — jk has no `<optional>`; emitted as a normal dep. Use a feature flag if it should be opt-in.
- [zipkin-server] `<exclusions>` on com.squareup.okhttp3:okhttp — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-server] `<exclusions>` on org.springframework.boot:spring-boot-test-autoconfigure — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-server] `<exclusions>` on org.springframework.boot:spring-boot-test — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-server] `<exclusions>` on org.springframework:spring-web — exclusion support lands in a later slice; exclusions were dropped.
- [zipkin-server] `<dependency>` org.apache.logging.log4j:log4j-jul has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] `<dependency>` org.apache.logging.log4j:log4j-1.2-api has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [zipkin-server] Maven profile `actuator`: activation=property=!skipActuator (no jk equivalent — replace with an explicit jk profile or feature); 1 dependency (convert to a jk feature `actuator` if opt-in, or move into the main deps list).
- [zipkin-server] Maven profile `include-lens`: activation=property=!skipLens (no jk equivalent — replace with an explicit jk profile or feature); 1 dependency (convert to a jk feature `include-lens` if opt-in, or move into the main deps list).
- [zipkin-server] `<plugin>maven-dependency-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [zipkin-server] `<plugin>maven-failsafe-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [zipkin-server] `<plugin>wire-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [zipkin-server] `<plugin>build-helper-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [zipkin-server] `<plugin>git-commit-id-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [zipkin-server] `<plugin>spring-boot-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


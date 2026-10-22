# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/floci/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<exclusions>` on io.quarkus:quarkus-rest-jackson — exclusion support lands in a later slice; exclusions were dropped.
- `<dependency>` io.quarkus:quarkus-rest-jackson has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.quarkus:quarkus-config-yaml has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.vertx:vertx-mail-client has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` org.apache.james:apache-mime4j-dom has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` com.fasterxml.jackson.core:jackson-databind has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` com.fasterxml.jackson.datatype:jackson-datatype-jsr310 has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` com.fasterxml.jackson.dataformat:jackson-dataformat-cbor has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` com.fasterxml.jackson.dataformat:jackson-dataformat-yaml has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.quarkus:quarkus-vertx has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.smallrye.reactive:smallrye-mutiny-vertx-web-client has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.vertx:vertx-mqtt has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` org.xerial.snappy:snappy-java has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.quarkus:quarkus-security has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<exclusions>` on io.apicurio:apicurio-registry-schema-util-json — exclusion support lands in a later slice; exclusions were dropped.
- `<exclusions>` on io.apicurio:apicurio-registry-schema-util-protobuf — exclusion support lands in a later slice; exclusions were dropped.
- `<classifier>uber</classifier>` on com.cedarpolicy:cedar-java — classifier support lands in a later slice; the coord was emitted without it.
- `<dependency>` io.quarkus:quarkus-junit has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.quarkus:quarkus-junit-mockito has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.rest-assured:rest-assured has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.fabric8:kubernetes-server-mock has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` io.fabric8:kubernetes-client has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` com.fasterxml.jackson.core:jackson-annotations has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` com.mysql:mysql-connector-j has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` org.postgresql:postgresql has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependency>` org.graalvm.sdk:nativeimage has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- `<dependencyManagement>` entry com.google.protobuf:protobuf-java is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- `<dependencyManagement>` entry com.google.protobuf:protobuf-java-util is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- `<dependencyManagement>` entry com.google.api.grpc:proto-google-common-protos is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- `<dependencyManagement>` entry org.apache.httpcomponents.core5:httpcore5 is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- `<dependencyManagement>` entry org.apache.httpcomponents.core5:httpcore5-h2 is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- `<dependencyManagement>` entry com.h2database:h2-mvstore is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- `<dependencyManagement>` entry com.squareup.wire:wire-runtime is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- `<dependencyManagement>` entry com.squareup.wire:wire-runtime-jvm is a version pin, not a BOM import. jk has no equivalent; the pin was dropped. Inline the version on the matching `<dependency>` instead.
- Maven profile `native`: activation=property=native (no jk equivalent — replace with an explicit jk profile or feature); properties=[skipITs,quarkus.native.enabled,maven.compiler.release] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- `<plugin>quarkus-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-surefire-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>build-helper-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-enforcer-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


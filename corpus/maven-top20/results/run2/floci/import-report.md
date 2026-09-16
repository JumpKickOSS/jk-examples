# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/floci/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<plugin>quarkus-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-surefire-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-enforcer-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<exclusions>` on io.quarkus:quarkus-rest-jackson — exclusion support lands in a later slice; exclusions were dropped.
- `<exclusions>` on io.apicurio:apicurio-registry-schema-util-json — exclusion support lands in a later slice; exclusions were dropped.
- `<exclusions>` on io.apicurio:apicurio-registry-schema-util-protobuf — exclusion support lands in a later slice; exclusions were dropped.
- `<classifier>uber</classifier>` on com.cedarpolicy:cedar-java — classifier support lands in a later slice; the coord was emitted without it.
- `<exclusions>` on io.rest-assured:rest-assured — exclusion support lands in a later slice; exclusions were dropped.
- `<exclusions>` on com.mysql:mysql-connector-j — exclusion support lands in a later slice; exclusions were dropped.
- `<exclusions>` on org.postgresql:postgresql — exclusion support lands in a later slice; exclusions were dropped.
- `<dependencyManagement>` in this POM pins 8 versions no declared dependency uses (com.google.protobuf:protobuf-java, com.google.protobuf:protobuf-java-util, com.google.api.grpc:proto-google-common-protos, org.apache.httpcomponents.core5:httpcore5, org.apache.httpcomponents.core5:httpcore5-h2, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- versions for io.quarkus:quarkus-rest-jackson, io.quarkus:quarkus-config-yaml, io.vertx:vertx-mail-client, org.apache.james:apache-mime4j-dom, com.fasterxml.jackson.core:jackson-databind, com.fasterxml.jackson.datatype:jackson-datatype-jsr310, com.fasterxml.jackson.dataformat:jackson-dataformat-cbor, com.fasterxml.jackson.dataformat:jackson-dataformat-yaml, io.quarkus:quarkus-vertx, io.smallrye.reactive:smallrye-mutiny-vertx-web-client, io.vertx:vertx-mqtt, org.xerial.snappy:snappy-java, io.quarkus:quarkus-security, io.quarkus:quarkus-junit, io.quarkus:quarkus-junit-mockito, io.rest-assured:rest-assured, io.fabric8:kubernetes-server-mock, io.fabric8:kubernetes-client, com.fasterxml.jackson.core:jackson-annotations, com.mysql:mysql-connector-j, org.postgresql:postgresql, org.graalvm.sdk:nativeimage managed by a BOM imported by this POM.
- Maven profile `native` (activation property=native, a command-line switch jk has no equivalent for): compiler settings → `[profiles.native]` `javac`; select with `--profile native`.


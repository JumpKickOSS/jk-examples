# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/spring-cloud-alibaba/pom.xml`

## Tier 3 — not imported

These constructs have no jk equivalent and were skipped or stubbed.

- `<parent>` org.springframework.cloud:spring-cloud-build:5.0.2 could not be resolved (Non-resolvable import POM: no repository has com.alibaba.cloud:spring-cloud-alibaba-dependencies:2025.1.0.1-SNAPSHOT (asked jumpkick, central, google)); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.
- [spring-cloud-alibaba-examples] `<parent>` com.alibaba.cloud:spring-cloud-alibaba:${revision} could not be resolved (Non-resolvable import POM: no repository has com.alibaba.cloud:spring-cloud-alibaba-dependencies:2025.1.0.1-SNAPSHOT (asked jumpkick, central, google)); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.
- [spring-cloud-alibaba-starters] `<parent>` com.alibaba.cloud:spring-cloud-alibaba:${revision} could not be resolved (Non-resolvable import POM: no repository has com.alibaba.cloud:spring-cloud-alibaba-dependencies:2025.1.0.1-SNAPSHOT (asked jumpkick, central, google)); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.
- [spring-cloud-alibaba-coverage] `<parent>` com.alibaba.cloud:spring-cloud-alibaba:${revision} could not be resolved (Non-resolvable import POM: no repository has com.alibaba.cloud:spring-cloud-alibaba-dependencies:2025.1.0.1-SNAPSHOT (asked jumpkick, central, google)); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.
- [spring-cloud-alibaba-tests] `<parent>` com.alibaba.cloud:spring-cloud-alibaba:${revision} could not be resolved (Non-resolvable import POM: no repository has com.alibaba.cloud:spring-cloud-alibaba-dependencies:2025.1.0.1-SNAPSHOT (asked jumpkick, central, google)); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<plugin>maven-enforcer-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-eclipse-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-checkstyle-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>maven-surefire-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>flatten-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [spring-cloud-alibaba-dependencies] `<dependencyManagement>` in this POM pins 48 versions no declared dependency uses (com.alibaba.nacos:nacos-client, com.alibaba.csp:sentinel-core, com.alibaba.csp:sentinel-parameter-flow-control, com.alibaba.csp:sentinel-datasource-extension, com.alibaba.csp:sentinel-datasource-apollo, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [spring-cloud-alibaba-dependencies] Maven profile `release`: plugins=[maven-source-plugin,maven-javadoc-plugin,maven-gpg-plugin,flatten-maven-plugin,central-publishing-maven-plugin] — port by hand; docs/user/migration.md lists where each plugin lands.
- [spring-cloud-alibaba-examples] `<plugin>maven-deploy-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [spring-cloud-alibaba-examples] `<plugin>native-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [spring-cloud-alibaba-examples] Maven profile `native`: no convertible payload; dropped.
- [spring-cloud-alibaba-examples] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.
- [spring-cloud-alibaba-starters] `<plugin>jacoco-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [spring-cloud-alibaba-starters] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.
- [spring-cloud-alibaba-coverage] `<plugin>jacoco-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [spring-cloud-alibaba-coverage] `<plugin>maven-deploy-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-alibaba-sentinel-datasource has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-starter-alibaba-sentinel has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-circuitbreaker-sentinel has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-starter-alibaba-seata has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-starter-stream-rocketmq has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-starter-bus-rocketmq has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-coverage] `<dependency>` com.alibaba.cloud:spring-cloud-starter-alibaba-sidecar has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-tests] `<plugin>maven-deploy-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [spring-cloud-alibaba-tests] `<dependency>` org.testcontainers:testcontainers-bom has no resolved `<version>` anywhere in its parent chain; jk wrote `=unresolved`. Pin it in the POM and re-import.
- [spring-cloud-alibaba-tests] `<dependencyManagement>` in this POM pins 1 version no declared dependency uses (com.alibaba.cloud:spring-cloud-alibaba-test-support); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [spring-cloud-alibaba-tests] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.


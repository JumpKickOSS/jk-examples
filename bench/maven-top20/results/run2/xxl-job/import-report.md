# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/xxl-job/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<plugin>maven-gpg-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- `<plugin>central-publishing-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-core] `<plugin>maven-gpg-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-core] `<plugin>central-publishing-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-core] `<dependencyManagement>` in workspace parent com.xuxueli:xxl-job:3.5.0-SNAPSHOT pins 8 versions no declared dependency uses (org.slf4j:slf4j-reload4j, org.mybatis.spring.boot:mybatis-spring-boot-starter, com.mysql:mysql-connector-j, com.xuxueli:xxl-job-core, com.xuxueli:xxl-sso-core, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [xxl-job-core] versions for org.slf4j:slf4j-api, jakarta.annotation:jakarta.annotation-api, io.netty:netty-codec-http, com.google.code.gson:gson, com.xuxueli:xxl-tool, org.apache.groovy:groovy, org.springframework:spring-context, org.junit.jupiter:junit-jupiter-engine managed by workspace parent com.xuxueli:xxl-job:3.5.0-SNAPSHOT.
- [xxl-job-admin] `<plugin>maven-gpg-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-admin] `<plugin>central-publishing-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-admin] `<plugin>spring-boot-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-admin] `<dependencyManagement>` in workspace parent com.xuxueli:xxl-job:3.5.0-SNAPSHOT pins 12 versions no declared dependency uses (org.slf4j:slf4j-api, org.slf4j:slf4j-reload4j, org.junit.jupiter:junit-jupiter-engine, jakarta.annotation:jakarta.annotation-api, io.netty:netty-codec-http, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [xxl-job-admin] versions for org.springframework.boot:spring-boot-starter-web, org.springframework.boot:spring-boot-starter-test, org.springframework.boot:spring-boot-starter-freemarker, org.springframework.boot:spring-boot-starter-mail, org.springframework.boot:spring-boot-starter-actuator managed by a BOM imported by workspace parent com.xuxueli:xxl-job:3.5.0-SNAPSHOT.
- [xxl-job-admin] versions for org.mybatis.spring.boot:mybatis-spring-boot-starter, com.mysql:mysql-connector-j, com.xuxueli:xxl-job-core, com.xuxueli:xxl-sso-core managed by workspace parent com.xuxueli:xxl-job:3.5.0-SNAPSHOT.
- [xxl-job-executor-samples] `<plugin>maven-gpg-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-executor-samples] `<plugin>central-publishing-maven-plugin</plugin>` was not imported; docs/user/migration.md lists where it lands in jk.
- [xxl-job-executor-samples] `<dependencyManagement>` in workspace parent com.xuxueli:xxl-job:3.5.0-SNAPSHOT pins 16 versions no declared dependency uses (org.slf4j:slf4j-api, org.slf4j:slf4j-reload4j, org.junit.jupiter:junit-jupiter-engine, jakarta.annotation:jakarta.annotation-api, io.netty:netty-codec-http, …); jk applies managed versions to declared dependencies only, so transitive versions follow the resolver.
- [xxl-job-executor-samples] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.


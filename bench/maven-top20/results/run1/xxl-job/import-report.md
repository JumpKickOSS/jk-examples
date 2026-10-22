# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/xxl-job/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- Maven profile `release`: activation=activeByDefault; plugins=[maven-compiler-plugin,maven-source-plugin,maven-javadoc-plugin,maven-gpg-plugin,central-publishing-maven-plugin] (plugin mapping is not yet implemented).
- [xxl-job-core] `<dependency>` org.slf4j:slf4j-api has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-core] `<dependency>` jakarta.annotation:jakarta.annotation-api has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-core] `<dependency>` io.netty:netty-codec-http has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-core] `<dependency>` com.google.code.gson:gson has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-core] `<dependency>` com.xuxueli:xxl-tool has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-core] `<dependency>` org.apache.groovy:groovy has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-core] `<dependency>` org.springframework:spring-context has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-core] `<dependency>` org.junit.jupiter:junit-jupiter-engine has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` org.springframework.boot:spring-boot-starter-web has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` org.springframework.boot:spring-boot-starter-test has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` org.springframework.boot:spring-boot-starter-freemarker has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` org.springframework.boot:spring-boot-starter-mail has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` org.springframework.boot:spring-boot-starter-actuator has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` org.mybatis.spring.boot:mybatis-spring-boot-starter has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` com.mysql:mysql-connector-j has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` com.xuxueli:xxl-job-core has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<dependency>` com.xuxueli:xxl-sso-core has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [xxl-job-admin] `<plugin>spring-boot-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [xxl-job-executor-samples] `<modules>` block present but this import was run in single-POM mode. Re-run as `jk import pom.xml` from the project root to materialise a workspace.


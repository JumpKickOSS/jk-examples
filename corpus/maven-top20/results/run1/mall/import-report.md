# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/mall/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- `<parent>` was referenced (org.springframework.boot:spring-boot-starter-parent:3.5.14) but jk-import did not flatten its dependencyManagement / properties / build config. Run `mvn help:effective-pom` and re-import if any dependency versions are unresolved.
- [mall-common] `<dependency>` com.github.pagehelper:pagehelper has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-common] `<dependency>` org.springframework.boot:spring-boot-starter-web has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-common] `<dependency>` org.springframework.boot:spring-boot-starter-data-redis has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-common] `<dependency>` org.springframework.data:spring-data-commons has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-common] `<dependency>` net.logstash.logback:logstash-logback-encoder has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-common] `<dependency>` org.codehaus.janino:janino has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-common] `<dependency>` org.springdoc:springdoc-openapi-starter-webmvc-ui has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-common] `<dependency>` org.springframework.boot:spring-boot-starter-validation has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-mbg] `<dependency>` com.macro.mall:mall-common has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-mbg] `<dependency>` com.github.pagehelper:pagehelper-spring-boot-starter has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-mbg] `<dependency>` org.mybatis.spring.boot:mybatis-spring-boot-starter has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-mbg] `<dependency>` com.alibaba:druid-spring-boot-3-starter has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-mbg] `<dependency>` org.mybatis.generator:mybatis-generator-core has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-mbg] `<dependency>` com.mysql:mysql-connector-j has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-security] `<dependency>` com.macro.mall:mall-common has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-security] `<dependency>` org.springframework.boot:spring-boot-starter-web has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-security] `<dependency>` org.springframework.boot:spring-boot-starter-security has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-security] `<dependency>` org.springframework.boot:spring-boot-starter-data-redis has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-demo] `<dependency>` com.macro.mall:mall-mbg has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-demo] `<dependency>` org.springframework.boot:spring-boot-starter-thymeleaf has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-demo] `<dependency>` org.springframework.boot:spring-boot-starter-security has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-demo] `<dependency>` net.logstash.logback:logstash-logback-encoder has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-demo] `<plugin>spring-boot-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [mall-admin] `<dependency>` com.macro.mall:mall-mbg has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-admin] `<dependency>` com.macro.mall:mall-security has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-admin] `<exclusions>` on com.aliyun.oss:aliyun-sdk-oss — exclusion support lands in a later slice; exclusions were dropped.
- [mall-admin] `<dependency>` com.aliyun.oss:aliyun-sdk-oss has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-admin] `<exclusions>` on io.minio:minio — exclusion support lands in a later slice; exclusions were dropped.
- [mall-admin] `<dependency>` io.minio:minio has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-admin] `<dependency>` com.squareup.okhttp3:okhttp-jvm has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-admin] `<plugin>spring-boot-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [mall-admin] `<plugin>docker-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [mall-search] `<exclusions>` on com.macro.mall:mall-mbg — exclusion support lands in a later slice; exclusions were dropped.
- [mall-search] `<dependency>` com.macro.mall:mall-mbg has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-search] `<dependency>` org.springframework.boot:spring-boot-starter-data-elasticsearch has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-search] `<plugin>spring-boot-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [mall-search] `<plugin>docker-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [mall-portal] `<dependency>` com.macro.mall:mall-mbg has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-portal] `<dependency>` com.macro.mall:mall-security has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-portal] `<dependency>` org.springframework.boot:spring-boot-starter-data-mongodb has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-portal] `<dependency>` org.springframework.boot:spring-boot-starter-data-redis has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-portal] `<dependency>` org.springframework.boot:spring-boot-starter-amqp has no resolved `<version>`; jk wrote `=unresolved`. Run `mvn help:effective-pom` and re-import.
- [mall-portal] `<plugin>spring-boot-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [mall-portal] `<plugin>docker-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


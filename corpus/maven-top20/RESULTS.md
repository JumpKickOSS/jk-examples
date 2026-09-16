# Maven top-20 corpus — results

Generated 2026-09-16 05:54 by `run.py`. Host: AMD Ryzen 9 7900X 12-Core Processor (24 threads, 30 GB RAM), Linux-7.1.12-200.fc44.x86_64-x86_64-with-glibc2.43.

Maven ran through the launcher `jk mvn` provisions (or the repo's `mvnw`) with `MAVEN_OPTS=-Xmx3g`, `JAVA_HOME` = the Temurin matching the declared level (or `maven_jdk`), and a corpus-private local repo (`/home/bsant/src/scratch/maven-corpus/.m2`); jk ran with defaults. Wall = seconds. `pass/total` from surefire XML (Maven) and the `Tests:` line of `target/jk-results.md` (jk). Per-step logs and each import report live under `results/<repo>/` (latest run).

- **run1**: `jk 0.13.7` commit `1ff15e0913162010190280e652f99d4826986efd (tag v0.13.7 in /home/bsant/src/oss/jk)` — /home/bsant/.jk/bin/jk sha256:553c5ef2bd4f1c32; jk-engine-0.13.7.jar sha256:d3265c6c678f93a4
- **run2**: `jk 0.13.7` commit `main 528e12916` — /home/bsant/.jk/bin/jk sha256:0f8725c06c9004ba; jk-engine-0.13.7.jar sha256:62366867cbcc55ee; `jk 0.13.7` commit `main 528e12916` — /home/bsant/.jk/bin/jk sha256:98f2558c2e5a3e72; jk-engine-0.13.7.1789538521832.jar sha256:55655016143b5433; `jk 0.13.7` commit `main c190940e0` — /home/bsant/.jk/bin/jk sha256:0f8725c06c9004ba; jk-engine-0.13.7.jar sha256:62366867cbcc55ee
- **run3**: `jk 0.13.7` commit `3192eb8fd` — /home/bsant/.jk/bin/jk sha256:82da87bceb3d3366; jk-engine-0.13.7.1789552009776.jar sha256:05f5c1c67aabdf8d

## Side by side (the ratchet delta)

Maven column = warm-clean wall / tests pass·total as the reference; each run column = jk import E/W · lock · build · tests jk · jk cold/no-op/touch/test walls.

| # | repo | mvn build / tests | run1: import E/W · lock · build · tests jk · jk cold/no-op/touch/test | run2: import E/W · lock · build · tests jk · jk cold/no-op/touch/test | run3: import E/W · lock · build · tests jk · jk cold/no-op/touch/test |
|--:|------|-------------------|----|----|----|
| 1 | iluwatar/java-design-patterns | 116s / 79/82 (fail) | 0/825 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/1324 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 1/1151 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 2 | macrozheng/mall | skipped / — | 0/45 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/58 · ok · FAIL · — · fail (5s)/skipped/skipped/skipped | 0/54 · ok · FAIL · — · fail (9s)/skipped/skipped/skipped |
| 3 | TheAlgorithms/Java | 6s / 9742/9742 | 0/6 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/6 · ok · ok · 9744/9745 (fail) · 2s/0s/2s/fail (13s) | 0/5 · ok · ok · 9744/9745 (fail) · 5s/0s/6s/fail (21s) |
| 4 | eugenp/tutorials | 1s / no tests ran | 1/29 · ok · built nothing (0 modules) · no tests ran · 0s/0s/0s/0s | 1/49 · ok · built nothing (0 modules) · no tests ran · 0s/0s/0s/0s | 2/49 · ok · built nothing (0 modules) · no tests ran · 0s/0s/0s/0s |
| 5 | keycloak/keycloak | 218s / 2619/2694 (fail) | 0/176 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/138 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/140 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 6 | alibaba/nacos | skipped / — | 2/328 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 2/645 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 2/611 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 7 | xuxueli/xxl-job | 16s / no tests ran | 0/20 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/16 · ok · FAIL · — · fail (2s)/skipped/skipped/skipped | 0/15 · ok · FAIL · — · fail (5s)/skipped/skipped/skipped |
| 8 | apolloconfig/apollo | 33s / 463/465 (fail) | 0/114 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/181 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 9/156 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 9 | alibaba/spring-cloud-alibaba | skipped / — | 0/74 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 5/28 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 5/26 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 10 | jenkinsci/jenkins | 35s / 21480/21503 (fail) | 0/226 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 6/214 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 9/210 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 11 | dataease/dataease | 9s / no tests ran | 0/4 · ok · ok (1/15 modules) · no tests ran · 0s/0s/0s/0s | 0/7 · ok · ok (1/15 modules) · no tests ran · 0s/0s/0s/0s | 0/7 · ok · ok (1/15 modules) · no tests ran · 0s/0s/0s/0s |
| 12 | floci-io/floci | 37s / fail, no results | 0/39 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/13 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/14 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 13 | thingsboard/thingsboard | skipped / — | 2/253 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 13/113 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 13/118 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 14 | infinilabs/analysis-ik | 3s / 22/22 | 0/7 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/10 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/9 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 15 | openzipkin/zipkin | 64s / 1108/1111 | 0/52 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/89 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/91 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 16 | questdb/questdb | skipped / — | 0/32 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/32 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/33 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 17 | neo4j/neo4j | skipped / — | 0/26 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/48 · ok · ok (3/181 modules) · 0/1 (fail) · 1s/0s/0s/fail (1s) | 0/53 · ok · ok (3/181 modules) · fail, no results · 1s/0s/0s/fail (1s) |
| 18 | cryptomator/cryptomator | 6s / 188/193 (timeout) | 0/16 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/16 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 0/16 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 19 | quarkusio/quarkus | skipped / — | 1/1730 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 13/781 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 13/779 · FAIL · skipped · — · skipped/skipped/skipped/skipped |
| 20 | apache/hadoop | 511s / capped | 0/0 · skipped · skipped · — · skipped/skipped/skipped/skipped | 16/138 · FAIL · skipped · — · skipped/skipped/skipped/skipped | 16/139 · FAIL · skipped · — · skipped/skipped/skipped/skipped |

| count | run1 | run2 | run3 |
|-------|--:|--:|--:|
| repos measured | 20 | 20 | 20 |
| import with zero Tier-3 errors | 15 | 13 | 11 |
| `jk lock` ok | 2 | 6 | 6 |
| `jk build --skip-tests` ok (compiled something) | 1 | 3 | 3 |
| `jk test` ran and passed | 0 | 0 | 0 |
| jk test total == Maven total | 0 | 0 | 0 |

## run1

| # | repo | stars | modules | java | import E/W | lock | build | tests jk | tests mvn | mvn cold | mvn warm-clean | mvn no-op | mvn touch | mvn test | jk cold | jk no-op | jk touch | jk test | first failure |
|--:|------|------:|--------:|:----:|:---------:|:----:|:-----:|:--------:|:---------:|--------:|---------------:|----------:|----------:|---------:|--------:|---------:|---------:|--------:|---------------|
| 1 | [iluwatar/java-design-patterns](https://github.com/iluwatar/java-design-patterns) | 94693 | 211 | 21 | 0/825 | FAIL | skipped | — | 79/82 (fail)* | 163s | 116s | 81s | 94s | fail (52s) | skipped | skipped | skipped | skipped | [resolve-deps — java-design-patterns] Cannot resolve dependencies: No versions of ch.qos.logback:logback-classic match unresolved |
| 2 | [macrozheng/mall](https://github.com/macrozheng/mall) | 84774 | 8 | 17 | 0/45 | FAIL | skipped | — | —* | fail (274s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — mall] Cannot resolve dependencies: No versions of org.codehaus.janino:janino match unresolved |
| 3 | [TheAlgorithms/Java](https://github.com/TheAlgorithms/Java) | 66254 | 1 | 21 | 0/6 | FAIL | skipped | — | 9742/9742* | 8s | 6s | 7s | 6s | 37s | skipped | skipped | skipped | skipped | [resolve-deps — TheAlgorithms-Java] Cannot resolve dependencies: No versions of org.junit.jupiter:junit-jupiter match unresolved |
| 4 | [eugenp/tutorials](https://github.com/eugenp/tutorials) | 37322 | 1727 | 17 | 1/29 | ok | built nothing (0 modules) | no tests ran | no tests ran* | 4s | 1s | 1s | 1s | 1s | 0s | 0s | 0s | 0s | pom skips tests under Maven |
| 5 | [keycloak/keycloak](https://github.com/keycloak/keycloak) | 36795 | 191 | 17 | 0/176 | FAIL | skipped | — | 2619/2694 (fail)* | 459s | 218s | 136s | 134s | fail (142s) | skipped | skipped | skipped | skipped | jdk = 8 is not supported — jk targets JDK 17 and above (LTS: 17, 21, 25, … plus the latest release). |
| 6 | [alibaba/nacos](https://github.com/alibaba/nacos) | 33372 | 62 | 17 | 2/328 | FAIL | skipped | — | —* | fail (56s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}); dependencies.nacos-control-plugin.version (${revision}); test-dependencies.nacos-datasource-plugin-derby.version (${revision}); test-dependencies.nacos- |
| 7 | [xuxueli/xxl-job](https://github.com/xuxueli/xxl-job) | 30557 | 7 | 17 | 0/20 | FAIL | skipped | — | no tests ran* | 21s | 16s | 21s | 29s | 11s | skipped | skipped | skipped | skipped | [resolve-deps — xxl-job] Cannot resolve dependencies: No versions of org.apache.groovy:groovy match unresolved; pom skips tests under Maven |
| 8 | [apolloconfig/apollo](https://github.com/apolloconfig/apollo) | 29807 | 14 | 17 | 0/114 | FAIL | skipped | — | 463/465 (fail)* | 58s | 33s | 35s | 35s | fail (96s) | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}). ${VAR} is honoured only in repository credentials, repository object-store keys, and [test] env — anything that feeds a compile or package cache key mus |
| 9 | [alibaba/spring-cloud-alibaba](https://github.com/alibaba/spring-cloud-alibaba) | 29171 | 70 | 17 | 0/74 | FAIL | skipped | — | —* | fail (8s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}). ${VAR} is honoured only in repository credentials, repository object-store keys, and [test] env — anything that feeds a compile or package cache key mus |
| 10 | [jenkinsci/jenkins](https://github.com/jenkinsci/jenkins) | 26546 | 9 | 17 | 0/226 | FAIL | skipped | — | 21480/21503 (fail) | 76s | 35s | 22s | 22s | fail (271s) | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}); version (${changelist}). ${VAR} is honoured only in repository credentials, repository object-store keys, and [test] env — anything that feeds a compile; pom skips tests under Maven |
| 11 | [dataease/dataease](https://github.com/dataease/dataease) | 24435 | 15 | 21 | 0/4 | ok | ok (1/15 modules) | no tests ran | no tests ran* | 111s | 9s | 3s | 4s | 3s | 0s | 0s | 0s | 0s |  |
| 12 | [floci-io/floci](https://github.com/floci-io/floci) | 24322 | 3 | 25 | 0/39 | FAIL | skipped | — | fail, no results* | 95s | 37s | 6s | 6s | fail (720s) | skipped | skipped | skipped | skipped | [resolve-deps — floci] Cannot resolve dependencies: No versions of org.apache.james:apache-mime4j-dom match unresolved |
| 13 | [thingsboard/thingsboard](https://github.com/thingsboard/thingsboard) | 22423 | 60 | 25 | 2/253 | FAIL | skipped | — | —* | fail (428s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — thingsboard] Cannot resolve dependencies: No versions of com.google.guava:guava match unresolved |
| 14 | [infinilabs/analysis-ik](https://github.com/infinilabs/analysis-ik) | 17521 | 4 | 17 | 0/7 | FAIL | skipped | — | 22/22 | 12s | 3s | 2s | 3s | 2s | skipped | skipped | skipped | skipped | [resolve-deps — analysis-ik] Cannot resolve dependencies: Org.elasticsearch:elasticsearch 9.4.0 depends on org.apache.lucene:lucene-core [10.4.0,+∞) |
| 15 | [openzipkin/zipkin](https://github.com/openzipkin/zipkin) | 17460 | 19 | 17 | 0/52 | FAIL | skipped | — | 1108/1111 | 83s | 64s | 53s | 60s | 126s | skipped | skipped | skipped | skipped | jdk = 8 is not supported — jk targets JDK 17 and above (LTS: 17, 21, 25, … plus the latest release). |
| 16 | [questdb/questdb](https://github.com/questdb/questdb) | 17323 | 5 | 25 | 0/32 | FAIL | skipped | — | —* | fail (2s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — questdb] Cannot resolve dependencies: No versions of org.questdb:questdb-client match 1.3.10-SNAPSHOT |
| 17 | [neo4j/neo4j](https://github.com/neo4j/neo4j) | 17228 | 181 | 21 | 0/26 | FAIL | skipped | — | —* | fail (7s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — neo4j] Cannot resolve dependencies: No versions of com.google.testing.compile:compile-testing match unresolved |
| 18 | [cryptomator/cryptomator](https://github.com/cryptomator/cryptomator) | 16142 | 1 | 26 | 0/16 | FAIL | skipped | — | 188/193 (timeout)* | 18s | 6s | 2s | 6s | timeout (1200s) | skipped | skipped | skipped | skipped | [resolve-deps — cryptomator] Cannot resolve dependencies: Org.cryptomator:cryptofs 2.10.0 depends on jakarta.inject:jakarta.inject-api [2.0.1.MR,+∞) |
| 19 | [quarkusio/quarkus](https://github.com/quarkusio/quarkus) | 15888 | 1906 | 21 | 1/1730 | FAIL | skipped | — | —* | fail (1055s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | jdk = 11 is not supported — jk targets JDK 17 and above (LTS: 17, 21, 25, … plus the latest release). |
| 20 | [apache/hadoop](https://github.com/apache/hadoop) | 15661 | 121 | 17 | 0/0 | skipped | skipped | — | capped | 826s | 511s | 903s | capped | capped | skipped | skipped | skipped | skipped | timed out at mvn_test; jk import failed; pom skips tests under Maven |

Measured 20 of 20 selected repos.

\* Maven numbers reused from an earlier row (jk-only re-measurement): TheAlgorithms-Java, apollo, cryptomator, dataease, floci, java-design-patterns, keycloak, mall, nacos, neo4j, quarkus, questdb, spring-cloud-alibaba, thingsboard, tutorials, xxl-job

## run2

| # | repo | stars | modules | java | import E/W | lock | build | tests jk | tests mvn | mvn cold | mvn warm-clean | mvn no-op | mvn touch | mvn test | jk cold | jk no-op | jk touch | jk test | first failure |
|--:|------|------:|--------:|:----:|:---------:|:----:|:-----:|:--------:|:---------:|--------:|---------------:|----------:|----------:|---------:|--------:|---------:|---------:|--------:|---------------|
| 1 | [iluwatar/java-design-patterns](https://github.com/iluwatar/java-design-patterns) | 94693 | 211 | 21 | 0/1324 | FAIL | skipped | — | 79/82 (fail)* | 163s | 116s | 81s | 94s | fail (52s) | skipped | skipped | skipped | skipped | [resolve-deps — java-design-patterns] Cannot resolve dependencies: · Com.fasterxml.jackson.core:jackson-databind 2.22.1 depends on com.fasterxml.jackson.core:jackson-core [2.22.1,+∞) · The project depends on com.fasterxml.jackson.core:jackson-databind 2.22.1 |
| 2 | [macrozheng/mall](https://github.com/macrozheng/mall) | 84774 | 8 | 17 | 0/58 | ok | FAIL | — | —* | fail (274s) | skipped | skipped | skipped | skipped | fail (5s) | skipped | skipped | skipped | [compile-java — mall-common] /home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:68: error: cannot find symbol · symbol:   method setDescription(java.lang.String) · location: variable webLog of type com.macro.mall.common.domain.W |
| 3 | [TheAlgorithms/Java](https://github.com/TheAlgorithms/Java) | 66254 | 1 | 21 | 0/6 | ok | ok | 9744/9745 (fail) | 9742/9742* | 8s | 6s | 7s | 6s | 37s | 2s | 0s | 2s | fail (13s) | test failure: com.thealgorithms.sorts.SelectionSortRecursiveTest#shouldAcceptWhenRandomListIsPassed() — java.lang.StackOverflowError |
| 4 | [eugenp/tutorials](https://github.com/eugenp/tutorials) | 37322 | 1727 | 17 | 1/49 | ok | built nothing (0 modules) | no tests ran | no tests ran* | 4s | 1s | 1s | 1s | 1s | 0s | 0s | 0s | 0s | pom skips tests under Maven |
| 5 | [keycloak/keycloak](https://github.com/keycloak/keycloak) | 36795 | 191 | 17 | 0/138 | FAIL | skipped | — | 2619/2694 (fail)* | 459s | 218s | 136s | 134s | fail (142s) | skipped | skipped | skipped | skipped | [resolve-deps — keycloak] platform BOM conflict on `com.github.ben-manes.caffeine:caffeine`: org.apache.directory.api:api-parent:2.1.8 constrains to 2.9.3, but io.quarkus.platform:quarkus-bom:3.39.2 constrains to 3.2.4. Pick one BOM or pin the coord explicitly. |
| 6 | [alibaba/nacos](https://github.com/alibaba/nacos) | 33372 | 62 | 17 | 2/645 | FAIL | skipped | — | —* | fail (56s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — nacos] Cannot resolve dependencies: · Ch.qos.logback:logback-classic 1.5.32 depends on org.slf4j:slf4j-api [2.0.17,+∞) · The project depends on org.slf4j:slf4j-api 2.0.13 |
| 7 | [xuxueli/xxl-job](https://github.com/xuxueli/xxl-job) | 30557 | 7 | 17 | 0/16 | ok | FAIL | — | no tests ran* | 21s | 16s | 21s | 29s | 11s | fail (2s) | skipped | skipped | skipped | [package-javadoc — xxl-job-admin] /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/trigger/JobTrigger.java:52: error: malformed HTML; pom skips tests under Maven |
| 8 | [apolloconfig/apollo](https://github.com/apolloconfig/apollo) | 29807 | 14 | 17 | 0/181 | FAIL | skipped | — | 463/465 (fail)* | 58s | 33s | 35s | 35s | fail (96s) | skipped | skipped | skipped | skipped | [resolve-deps — apollo] Cannot resolve dependencies: · Package com.ctrip.framework.apollo:apollo-audit-api was not found in any repository · The project depends on com.ctrip.framework.apollo:apollo-audit-api 3.0.0-SNAPSHOT |
| 9 | [alibaba/spring-cloud-alibaba](https://github.com/alibaba/spring-cloud-alibaba) | 29171 | 70 | 17 | 5/28 | FAIL | skipped | — | —* | fail (8s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}). ${VAR} is honoured only in repository credentials, repository object-store keys, and [test] env — anything that feeds a compile or package cache key mus |
| 10 | [jenkinsci/jenkins](https://github.com/jenkinsci/jenkins) | 26546 | 9 | 17 | 6/214 | FAIL | skipped | — | 21480/21503 (fail)* | 76s | 35s | 22s | 22s | fail (271s) | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}); version (${changelist}). ${VAR} is honoured only in repository credentials, repository object-store keys, and [test] env — anything that feeds a compile; pom skips tests under Maven |
| 11 | [dataease/dataease](https://github.com/dataease/dataease) | 24435 | 15 | 21 | 0/7 | ok | ok (1/15 modules) | no tests ran | no tests ran* | 111s | 9s | 3s | 4s | 3s | 0s | 0s | 0s | 0s |  |
| 12 | [floci-io/floci](https://github.com/floci-io/floci) | 24322 | 3 | 25 | 0/13 | FAIL | skipped | — | fail, no results* | 95s | 37s | 6s | 6s | fail (720s) | skipped | skipped | skipped | skipped | [resolve-deps — floci] java.util.NoSuchElementException |
| 13 | [thingsboard/thingsboard](https://github.com/thingsboard/thingsboard) | 22423 | 60 | 25 | 13/113 | FAIL | skipped | — | —* | fail (428s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — thingsboard] POM not found in any declared repo: org.thingsboard.langchain4j:langchain4j-bom:1.16.1-TB1 |
| 14 | [infinilabs/analysis-ik](https://github.com/infinilabs/analysis-ik) | 17521 | 4 | 17 | 0/10 | FAIL | skipped | — | 22/22* | 12s | 3s | 2s | 3s | 2s | skipped | skipped | skipped | skipped | [resolve-deps — analysis-ik] Cannot resolve dependencies: · Org.elasticsearch:elasticsearch 9.4.0 depends on org.apache.lucene:lucene-core [10.4.0,+∞) · The project depends on org.apache.lucene:lucene-core 10.2.2 |
| 15 | [openzipkin/zipkin](https://github.com/openzipkin/zipkin) | 17460 | 19 | 17 | 0/89 | FAIL | skipped | — | 1108/1111* | 83s | 64s | 53s | 60s | 126s | skipped | skipped | skipped | skipped | [resolve-deps — zipkin] Cannot resolve dependencies: · No versions of io.zipkin.zipkin2:zipkin-collector match 3.6.2-SNAPSHOT · The project depends on io.zipkin.zipkin2:zipkin-collector 3.6.2-SNAPSHOT |
| 16 | [questdb/questdb](https://github.com/questdb/questdb) | 17323 | 5 | 25 | 0/32 | FAIL | skipped | — | —* | fail (2s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — questdb] Cannot resolve dependencies: · No versions of org.questdb:questdb-client match 1.3.10-SNAPSHOT · The project depends on org.questdb:questdb-client 1.3.10-SNAPSHOT |
| 17 | [neo4j/neo4j](https://github.com/neo4j/neo4j) | 17228 | 181 | 21 | 0/48 | ok | ok (3/181 modules) | 0/1 (fail) | —* | fail (7s) | skipped | skipped | skipped | skipped | 1s | 0s | 0s | fail (1s) | [run-tests — annotations] 1 test failure |
| 18 | [cryptomator/cryptomator](https://github.com/cryptomator/cryptomator) | 16142 | 1 | 26 | 0/16 | FAIL | skipped | — | 188/193 (timeout)* | 18s | 6s | 2s | 6s | timeout (1200s) | skipped | skipped | skipped | skipped | [resolve-deps — cryptomator] Cannot resolve dependencies: · Org.cryptomator:cryptofs 2.10.0 depends on jakarta.inject:jakarta.inject-api [2.0.1.MR,+∞) · The project depends on jakarta.inject:jakarta.inject-api 2.0.1 |
| 19 | [quarkusio/quarkus](https://github.com/quarkusio/quarkus) | 15888 | 1906 | 21 | 13/781 | FAIL | skipped | — | —* | fail (1055s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — quarkus] POM not found in any declared repo: tools.jackson:jackson-bom:unresolved |
| 20 | [apache/hadoop](https://github.com/apache/hadoop) | 15661 | 121 | 17 | 16/138 | FAIL | skipped | — | capped* | 826s | 511s | 903s | capped | capped | skipped | skipped | skipped | skipped | [resolve-deps — hadoop] Cannot resolve dependencies: · No versions of commons-io:commons-io match unresolved · The project depends on commons-io:commons-io unresolved; pom skips tests under Maven |

Measured 20 of 20 selected repos.

\* Maven numbers reused from an earlier row (jk-only re-measurement): TheAlgorithms-Java, analysis-ik, apollo, cryptomator, dataease, floci, hadoop, java-design-patterns, jenkins, keycloak, mall, nacos, neo4j, quarkus, questdb, spring-cloud-alibaba, thingsboard, tutorials, xxl-job, zipkin

## run3

| # | repo | stars | modules | java | import E/W | lock | build | tests jk | tests mvn | mvn cold | mvn warm-clean | mvn no-op | mvn touch | mvn test | jk cold | jk no-op | jk touch | jk test | first failure |
|--:|------|------:|--------:|:----:|:---------:|:----:|:-----:|:--------:|:---------:|--------:|---------------:|----------:|----------:|---------:|--------:|---------:|---------:|--------:|---------------|
| 1 | [iluwatar/java-design-patterns](https://github.com/iluwatar/java-design-patterns) | 94693 | 211 | 21 | 1/1151 | FAIL | skipped | — | 79/82 (fail)* | 163s | 116s | 81s | 94s | fail (52s) | skipped | skipped | skipped | skipped | [resolve-deps — java-design-patterns] Cannot resolve dependencies: · Com.fasterxml.jackson.core:jackson-databind 2.22.1 depends on com.fasterxml.jackson.core:jackson-core [2.22.1,+∞) · The project depends on com.fasterxml.jackson.core:jackson-databind 2.22.1 |
| 2 | [macrozheng/mall](https://github.com/macrozheng/mall) | 84774 | 8 | 17 | 0/54 | ok | FAIL | — | —* | fail (274s) | skipped | skipped | skipped | skipped | fail (9s) | skipped | skipped | skipped | [compile-java — mall-common] /home/bsant/src/scratch/maven-corpus/mall/mall-common/src/main/java/com/macro/mall/common/log/WebLogAspect.java:68: error: cannot find symbol · symbol:   method setDescription(java.lang.String) · location: variable webLog of type com.macro.mall.common.domain.W |
| 3 | [TheAlgorithms/Java](https://github.com/TheAlgorithms/Java) | 66254 | 1 | 21 | 0/5 | ok | ok | 9744/9745 (fail) | 9742/9742* | 8s | 6s | 7s | 6s | 37s | 5s | 0s | 6s | fail (21s) | test failure: com.thealgorithms.sorts.BubbleSortRecursiveTest#shouldAcceptWhenRandomArrayIsPassed() — java.lang.StackOverflowError |
| 4 | [eugenp/tutorials](https://github.com/eugenp/tutorials) | 37322 | 1727 | 17 | 2/49 | ok | built nothing (0 modules) | no tests ran | no tests ran* | 4s | 1s | 1s | 1s | 1s | 0s | 0s | 0s | 0s | pom skips tests under Maven |
| 5 | [keycloak/keycloak](https://github.com/keycloak/keycloak) | 36795 | 191 | 17 | 0/140 | FAIL | skipped | — | 2619/2694 (fail)* | 459s | 218s | 136s | 134s | fail (142s) | skipped | skipped | skipped | skipped | [resolve-deps — keycloak] platform BOM conflict on `com.github.ben-manes.caffeine:caffeine`: org.apache.directory.api:api-parent:2.1.8 constrains to 2.9.3, but io.quarkus.platform:quarkus-bom:3.39.2 constrains to 3.2.4. Pick one BOM or pin the coord explicitly. |
| 6 | [alibaba/nacos](https://github.com/alibaba/nacos) | 33372 | 62 | 17 | 2/611 | FAIL | skipped | — | —* | fail (56s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — nacos] Cannot resolve dependencies: · Ch.qos.logback:logback-classic 1.5.32 depends on org.slf4j:slf4j-api [2.0.17,+∞) · The project depends on org.slf4j:slf4j-api 2.0.13 |
| 7 | [xuxueli/xxl-job](https://github.com/xuxueli/xxl-job) | 30557 | 7 | 17 | 0/15 | ok | FAIL | — | no tests ran* | 21s | 16s | 21s | 29s | 11s | fail (5s) | skipped | skipped | skipped | [package-javadoc — xxl-job-admin] /home/bsant/src/scratch/maven-corpus/xxl-job/xxl-job-admin/src/main/java/com/xxl/job/admin/business/scheduler/trigger/JobTrigger.java:52: error: malformed HTML; pom skips tests under Maven |
| 8 | [apolloconfig/apollo](https://github.com/apolloconfig/apollo) | 29807 | 14 | 17 | 9/156 | FAIL | skipped | — | 463/465 (fail)* | 58s | 33s | 35s | 35s | fail (96s) | skipped | skipped | skipped | skipped | [resolve-deps — apollo] Cannot resolve dependencies: · Package com.ctrip.framework.apollo:apollo-audit-api was not found in any repository · The project depends on com.ctrip.framework.apollo:apollo-audit-api 3.0.0-SNAPSHOT |
| 9 | [alibaba/spring-cloud-alibaba](https://github.com/alibaba/spring-cloud-alibaba) | 29171 | 70 | 17 | 5/26 | FAIL | skipped | — | —* | fail (8s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}). ${VAR} is honoured only in repository credentials, repository object-store keys, and [test] env — anything that feeds a compile or package cache key mus |
| 10 | [jenkinsci/jenkins](https://github.com/jenkinsci/jenkins) | 26546 | 9 | 17 | 9/210 | FAIL | skipped | — | 21480/21503 (fail)* | 76s | 35s | 22s | 22s | fail (271s) | skipped | skipped | skipped | skipped | environment references are not allowed here: version (${revision}); version (${changelist}). ${VAR} is honoured only in repository credentials, repository object-store keys, and [test] env — anything that feeds a compile; pom skips tests under Maven |
| 11 | [dataease/dataease](https://github.com/dataease/dataease) | 24435 | 15 | 21 | 0/7 | ok | ok (1/15 modules) | no tests ran | no tests ran* | 111s | 9s | 3s | 4s | 3s | 0s | 0s | 0s | 0s |  |
| 12 | [floci-io/floci](https://github.com/floci-io/floci) | 24322 | 3 | 25 | 0/14 | FAIL | skipped | — | fail, no results* | 95s | 37s | 6s | 6s | fail (720s) | skipped | skipped | skipped | skipped | [resolve-deps — floci] java.util.NoSuchElementException |
| 13 | [thingsboard/thingsboard](https://github.com/thingsboard/thingsboard) | 22423 | 60 | 25 | 13/118 | FAIL | skipped | — | —* | fail (428s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — thingsboard] POM not found in any declared repo: org.thingsboard.langchain4j:langchain4j-bom:1.16.1-TB1 |
| 14 | [infinilabs/analysis-ik](https://github.com/infinilabs/analysis-ik) | 17521 | 4 | 17 | 0/9 | FAIL | skipped | — | 22/22* | 12s | 3s | 2s | 3s | 2s | skipped | skipped | skipped | skipped | [resolve-deps — analysis-ik] Cannot resolve dependencies: · Org.elasticsearch:elasticsearch 9.4.0 depends on org.apache.lucene:lucene-core [10.4.0,+∞) · The project depends on org.apache.lucene:lucene-core 10.2.2 |
| 15 | [openzipkin/zipkin](https://github.com/openzipkin/zipkin) | 17460 | 19 | 17 | 0/91 | FAIL | skipped | — | 1108/1111* | 83s | 64s | 53s | 60s | 126s | skipped | skipped | skipped | skipped | [resolve-deps — zipkin] platform BOM conflict on `io.micrometer:micrometer-registry-stackdriver`: io.micrometer:micrometer-bom:1.16.4 constrains to 1.16.4, but org.springframework.boot:spring-boot-dependencies:3.5.12 constrains to 1.15.10. Pick one BOM or pin the coord explicitly. |
| 16 | [questdb/questdb](https://github.com/questdb/questdb) | 17323 | 5 | 25 | 0/33 | FAIL | skipped | — | —* | fail (2s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — questdb] Cannot resolve dependencies: · No versions of org.questdb:questdb-client match 1.3.10-SNAPSHOT · The project depends on org.questdb:questdb-client 1.3.10-SNAPSHOT |
| 17 | [neo4j/neo4j](https://github.com/neo4j/neo4j) | 17228 | 181 | 21 | 0/53 | ok | ok (3/181 modules) | fail, no results | —* | fail (7s) | skipped | skipped | skipped | skipped | 1s | 0s | 0s | fail (1s) | [run-tests — org.neo4j:annotations] test discovery exited 70 before any test ran — test discovery failed under /home/bsant/src/scratch/maven-corpus/neo4j/target/annotations/classes/test: PreconditionViolationException: Cannot create Launcher without at least one TestEngine; consider adding an en |
| 18 | [cryptomator/cryptomator](https://github.com/cryptomator/cryptomator) | 16142 | 1 | 26 | 0/16 | FAIL | skipped | — | 188/193 (timeout)* | 18s | 6s | 2s | 6s | timeout (1200s) | skipped | skipped | skipped | skipped | [resolve-deps — cryptomator] Cannot resolve dependencies: · Org.cryptomator:cryptofs 2.10.0 depends on jakarta.inject:jakarta.inject-api [2.0.1.MR,+∞) · The project depends on jakarta.inject:jakarta.inject-api 2.0.1 |
| 19 | [quarkusio/quarkus](https://github.com/quarkusio/quarkus) | 15888 | 1906 | 21 | 13/779 | FAIL | skipped | — | —* | fail (1055s) | skipped | skipped | skipped | skipped | skipped | skipped | skipped | skipped | [resolve-deps — quarkus] POM not found in any declared repo: tools.jackson:jackson-bom:unresolved |
| 20 | [apache/hadoop](https://github.com/apache/hadoop) | 15661 | 121 | 17 | 16/139 | FAIL | skipped | — | capped* | 826s | 511s | 903s | capped | capped | skipped | skipped | skipped | skipped | [resolve-deps — hadoop] Cannot resolve dependencies: · No versions of commons-io:commons-io match unresolved · The project depends on commons-io:commons-io unresolved; pom skips tests under Maven |

Measured 20 of 20 selected repos.

\* Maven numbers reused from an earlier row (jk-only re-measurement): TheAlgorithms-Java, analysis-ik, apollo, cryptomator, dataease, floci, hadoop, java-design-patterns, jenkins, keycloak, mall, nacos, neo4j, quarkus, questdb, spring-cloud-alibaba, thingsboard, tutorials, xxl-job, zipkin

`built nothing` / `ok (n/m modules)` = `jk build` exited 0 but the imported workspace covers none / only n of the m poms; a build of nothing does not count in the ratchet.

`no tests ran` = the step exited 0 but no test result was produced (e.g. an aggregator root imported with no sources, or a pom that sets `maven.test.skip`); it never counts as a pass.

`capped` = killed by the 45-minute repo cap; `timeout` = the step's own 20-minute test timeout.

## Skipped (in the same star range, root pom.xml present)

| repo | stars | reason | detail |
|------|------:|--------|--------|
| google/guava | 51904 | needs-jdk-below-17 | source 1.8 |
| dbeaver/dbeaver | 51769 | not-plain-maven | Tycho/Eclipse RCP p2 build |
| apache/dubbo | 41569 | needs-jdk-below-17 | release 8 |
| YunaiV/ruoyi-vue-pro | 39297 | needs-jdk-below-17 | java.version 1.8 |
| alibaba/arthas | 37540 | needs-jdk-below-17 | 1.8 |
| netty/netty | 35058 | needs-jdk-below-17 | maven.compiler.release 8 |
| zxing/zxing | 34093 | needs-jdk-below-17 | java.version 1.8 |
| xkcoding/spring-boot-demo | 34074 | needs-jdk-below-17 | 1.8; demo collection |
| alibaba/easyexcel | 33637 | needs-jdk-below-17 | jdk.version 1.8 |
| binarywang/WxJava | 33092 | needs-jdk-below-17 | 1.8 |
| chinabugotech/hutool | 30271 | needs-jdk-below-17 | compile.version 8 |
| alibaba/canal | 29731 | needs-jdk-below-17 | java_source_version 1.8 |
| alibaba/druid | 28175 | needs-jdk-below-17 | maven.compiler.source 8 |
| crossoverJie/JCSprout | 26837 | needs-jdk-below-17 | java.version 11; notes collection |
| OpenAPITools/openapi-generator | 26739 | needs-jdk-below-17 | 11 |
| qiurunze123/miaosha | 26588 | needs-jdk-below-17 | 1.8 |
| apache/flink | 26335 | needs-jdk-below-17 | target.java.version 11 by default |
| apache/incubator-seata | 26001 | needs-jdk-below-17 | base release 8 with JDK17+/21+/25+ profiles |
| alibaba/fastjson | 25590 | needs-jdk-below-17 | jdk.version 1.5 |
| apache/skywalking | 24953 | needs-jdk-below-17 | maven.compiler.release 11 |
| proxyee-down-org/proxyee-down | 24658 | needs-jdk-below-17 | 1.8 |
| redisson/redisson | 24395 | needs-jdk-below-17 | maven.compiler.release 8 |
| google/gson | 24236 | needs-jdk-below-17 | maven.compiler.release 8 |
| EnterpriseQualityCoding/FizzBuzzEnterpriseEdition | 23855 | needs-jdk-below-17 | source 1.7; parody project |
| alibaba/Sentinel | 23144 | needs-jdk-below-17 | plugin-level release 8 (default-compile 17 for module-info only) |
| apache/rocketmq | 22600 | needs-jdk-below-17 | 1.8 |
| elunez/eladmin | 21911 | needs-jdk-below-17 | java.version 1.8 |
| brettwooldridge/HikariCP | 21216 | needs-jdk-below-17 | compiler source 11 |
| apache/shardingsphere | 20797 | needs-jdk-below-17 | maven.compiler.release=java.version=8 (compiles with JDK 21) |
| mybatis/mybatis-3 | 20443 | needs-jdk-below-17 | java.version 11 |
| linlinjava/litemall | 20358 | needs-jdk-below-17 | java.version 1.8 |
| yudaocode/SpringBoot-Labs | 20128 | tutorial-collection | no declared level; tutorial repo |
| YunaiV/yudao-cloud | 19534 | needs-jdk-below-17 | java.version 1.8 |
| dromara/Sa-Token | 19044 | needs-jdk-below-17 | maven.compiler.release 8 |
| antlr/antlr4 | 19003 | needs-jdk-below-17 | 11 |
| dianping/cat | 18936 | needs-jdk-below-17 | 1.8 |
| justauth/JustAuth | 17519 | needs-jdk-below-17 | 1.8 |
| alibaba/DataX | 17356 | needs-jdk-below-17 | jdk-version 1.8 |
| shuzheng/zheng | 16644 | needs-jdk-below-17 | 1.7 |
| JeffLi1993/springboot-learning-example | 16559 | tutorial-collection | no declared level; tutorial repo |
| dyc87112/SpringBoot-Learning | 15699 | tutorial-collection | tutorial repo |
| Konloch/bytecode-viewer | 15644 | needs-jdk-below-17 | java.version 1.8 |
| zhisheng17/flink-learning | 15097 | tutorial-collection | tutorial repo |

## Ratchet

Current bar (run3):

- repos importing with zero Tier-3 errors: **11** / 20
- repos whose `jk lock` succeeds: **6** / 20
- repos whose `jk build --skip-tests` compiles something: **3** / 20
- repos whose `jk test` runs and passes: **0** / 20
- repos whose jk test total equals Maven's: **0** / 20

Delta vs run2: import_clean 13→11, lock 6→6, build 3→3, tests_ran 0→0, tests_equal 0→0

Rule: a run that lowers any of these counts is a regression; a run that raises one moves the bar.

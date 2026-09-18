# Tier-3 reasons (import ERRORs and jk failures), grouped — run14

Generated 2026-09-18 07:46. Module prefixes, coordinates, versions and paths are normalized so one line = one distinct cause = one ticket candidate.

## import Tier 3 (not imported)

- (2) packaging `war` (`maven-war-plugin`) is not supported: jk builds jars, Boot jars and native images. Keep building this module with `jk mvn package`.  
  repos: java-design-patterns, jenkins
- (1) G:A N.Final` is pinned by the POM, and no repository the lock reads (jboss-public-repository, central, jumpkick, google) lists G:A at all; `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N.Final in `[repositories]`.  
  repos: keycloak
- (1) G:A N.Final` is pinned by the POM, and no repository the lock reads lists that version (jboss-public-repository lists N.Beta2, N.Beta1, N.Final, N.Final, N.Beta5, N.Beta4, N.Beta3, N.Beta2 and 282 older; central lists N.Beta2, N.Beta1, N.Final, N.Final, N.Beta5, N.Beta4, N.Beta3, N.Beta2 and 277 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N.Final in `[repositories]`.  
  repos: keycloak
- (1) G:A N.Final` is pinned by the POM, and no repository the lock reads lists that version (jboss-public-repository lists N.Beta5, N.Beta4, N.Beta3, N.Beta2, N.Beta1, N.Final, N.Final, N.Beta5 and 11 older; central lists N.Beta5, N.Beta4, N.Beta3, N.Beta2, N.Beta1, N.Final, N.Final, N.Beta5 and 10 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N.Final in `[repositories]`.  
  repos: keycloak
- (1) G:A N.Final` is pinned by the POM, and no repository the lock reads lists that version (jboss-public-repository lists N.Beta8, N.Beta7, N.Beta6, N.Beta5, N.Beta4, N.Beta3, N.Beta2, N.Beta1 and 64 older; central lists N.Beta8, N.Beta7, N.Beta6, N.Beta5, N.Beta4, N.Beta3, N.Beta2, N.Beta1 and 64 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N.Final in `[repositories]`.  
  repos: keycloak
- (1) G:A N.v20250814` is pinned by the POM, and no repository the lock reads lists that version (central lists N.M2, N.M1, N.M0); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N.v20250814 in `[repositories]`.  
  repos: hadoop
- (1) G:A N` is pinned by the POM, and no repository the lock reads (repository.jboss.org, central, jumpkick, google) lists G:A at all; `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`. (2 modules: hadoop-project, hadoop-client-modules/hadoop-client-minicluster)  
  repos: hadoop
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: java-design-patterns
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N, N, N, N, N, N, N and 1 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: thingsboard
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N, N, N, N, N, N, N and 43 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: quarkus
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N, N, N, N, N, N, N and 56 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N, N, N, N, N-rc-3, N-rc-2, N-rc-1 and 56 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, Nrc1, N, N, 20030911, N, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: thingsboard
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N-alpha-2, N-alpha-1, N, N, N, N, N, N and 101 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N-beta-3, N-beta-2, N-beta-1, N, N, N, N, N and 23 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: quarkus
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (repository.jboss.org lists N.v_883_R34x, N.v_686_R32x; central lists N-v_677_R32x, N-v_771, N, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: hadoop
- (1) G:A Nc` is pinned by the POM, and no repository the lock reads lists that version (central lists N.O, N-RC8, N-RC3, N_min, N, Na_min, Na); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes Nc in `[repositories]`.  
  repos: jenkins
- (1) G:A names `testsuite/integration-arquillian/servers/app-server/jboss/galleon/pom.xml`, which only profile `app-server-eap8` of `testsuite/integration-arquillian/servers/app-server/jboss` lists, and that profile is not active on this machine (activate it with Maven's `-P` and re-import, or list the module at the top level); the lock would fetch it from a repository, where a reactor module is not published, so the dependency was not written.  
  repos: keycloak
- (1) G:A names `testsuite/integration-arquillian/servers/app-server/jboss/wildfly/pom.xml`, which only profile `app-server-wildfly` of `testsuite/integration-arquillian/servers/app-server/jboss` lists, and that profile is not active on this machine (activate it with Maven's `-P` and re-import, or list the module at the top level); the lock would fetch it from a repository, where a reactor module is not published, so the dependency was not written.  
  repos: keycloak
- (1) `<build><extensions>` G:A is the `maven-archetype` packaging, a project template jk does not build; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`. (4 modules: extensions/amazon-lambda/maven-archetype, extensions/amazon-lambda-http/maven-archetype, extensions/amazon-lambda-rest/maven-archetype, …)  
  repos: quarkus
- (1) `<modules>` are declared only in profiles that are not active on this machine (default-jdk8, default-heavy, integration-jdk8, integration-heavy, default-jdk17, default, default-jdk22, default-jdk23, default-jdk24, default-jdk25, default-jdk26, integration-jdk17, integration, integration-jdk22, integration-jdk23, integration-jdk24, integration-jdk25, integration-jdk26, live-all, parents, default-disabled, integration-disabled); no module was imported, so the workspace builds nothing. Activate one with Maven's `-P` and re-import, or list the modules at the top level.  
  repos: tutorials
- (1) `<plugin>` G:A is the `bundle` packaging: an OSGi manifest bnd computes, which jk's jar step does not write; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`. Declared by the root pom.xml, inherited by 1 module.  
  repos: zipkin
- (1) `<plugin>` G:A is the `bundle` packaging: an OSGi manifest bnd computes, which jk's jar step does not write; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`. Declared by the root pom.xml, inherited by 101 modules.  
  repos: hadoop
- (1) `<type>exe</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: quarkus
- (1) `<type>exe</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`. (7 modules: edqs, transport/http, transport/mqtt, …)  
  repos: thingsboard
- (1) `<type>executable-war</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: jenkins
- (1) `<type>war</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: hadoop
- (1) `<type>zip</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`. (2 modules: quarkus/dist, testsuite/integration-arquillian/tests/base)  
  repos: keycloak
- (1) `<type>zip</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`. (3 modules: test-framework/core, quarkus/tests/junit5, testsuite/integration-arquillian/servers/auth-server/quarkus)  
  repos: keycloak
- (1) `maven-shade-plugin` relocates org.apache.lucene → org.neo4j.shaded.lucene9; jk relocates packages only in the fat jar of an `[application]`, and a workspace sibling compiles against this module's classes tree, which carries no shaded package, yet `community/lucene-index` (31 files), `community/neo4j` (1 file), `community/dbms` (1 file), `community/community-it/index-it` (1 file) import them. Keep building this module with Maven — `jk mvn -pl community/lucene9-shaded install` publishes G:A into `~/.m2/repository` — and depend on that artifact from a `file://` repository over `~/.m2/repository` in place of the workspace edge.  
  repos: neo4j
- (1) packaging `war` (`maven-war-plugin`) is not supported: jk builds jars, Boot jars and native images. Keep building this module with `jk mvn package`. (2 modules: hadoop-common-project/hadoop-auth-examples, hadoop-yarn-project/hadoop-yarn/hadoop-yarn-applications/hadoop-yarn-applications-catalog/hadoop-yarn-applications-catalog-webapp)  
  repos: hadoop

## jk lock failure

- (1) Cannot resolve dependencies: · No versions of G:A match N-SNAPSHOT · N-SNAPSHOT is a snapshot, and no repository G:A may resolve from serves snapshots: central (releases only), jumpkick (releases only), g  
  repos: questdb
- (1) [test-dependencies] G:A platform-managed cannot run under jk: its suites run through G:A, which needs G:A N or later — raise the pin to N  
  repos: spring-cloud-alibaba

## jk build failure

- (1) <path> Return an empty collection rather than null. [ReturnEmptyCollectionRatherThanNull]  
  repos: TheAlgorithms-Java
- (1) <path> error: duplicate class: org.neo4j.cypher.internal.parser.v5.Cypher5Lex  
  repos: neo4j
- (1) <path> error: package org.jboss.security does not exist  
  repos: keycloak
- (1) Error during the transformation of 'com.iluwatar.prototype.Prototype'; post-compiler 'lombok.bytecode.SneakyThrowsRemover' caused an exception: java.lang.IllegalArgumentException: Unsupported class file major version 69 · at org.lombokweb.asm.ClassReader.<init  
  repos: java-design-patterns
- (1) [generate.avro] inputs [src/main/avro/**/*.avsc, src/main/avro/**/*.avpr, src/main/avro/**/*.avdl] match no file under <path>  
  repos: hadoop
- (1) checkstyle failed (exit 255) before writing its report: · Could not find config XML file '<path>  
  repos: jenkins
- (1) checkstyle failed (exit 255) before writing its report: · Files to process must be specified, found 0.  
  repos: nacos
- (1) repository `apache.snapshots` at http://repository.apache.org/snapshots, declared by the POM of G:A, was not used: it is plaintext http, and a repository  
  repos: quarkus

## jk test failure

- (1) + [1 of 8] G:A - took 21ms  
  repos: dataease
- (1) 1 test failure  
  repos: thingsboard
- (1) Unable to open DISPLAY  
  repos: cryptomator
- (1) test discovery exited 3 before any test ran — java.lang.OutOfMemoryError: Metaspace · Fix: the test discovery JVM ran out of memory (Metaspace) before it ran a test; the frames above name the framework that filled it — a test framework that starts the applicat  
  repos: floci
- (1) test failure: com.ctrip.framework.apollo.build.sql.converter.ApolloSqlConverterH2Test — G:A#checkH2() — java.lang.IllegalStateException: illegal class path: <path>  
  repos: apollo
- (1) test failure: com.macro.mall.search.MallSearchApplicationTests — G:A#testGetAllEsProductList() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@446dacf9 testClass = com.macro.mall.se  
  repos: mall
- (1) test failure: com.xxl.job.executor.test.openclaw.OpenClawTest — G:A#test() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@4a00d591 testClass = com.xxl.job.ex  
  repos: xxl-job
- (1) test failure: zipkin2.collector.CollectorTest — G:A#accept_storageError() — java.lang.AssertionError:  
  repos: zipkin

## Maven-side failure (for context)

- (2) COMPILATION ERROR :  
  repos: spring-cloud-alibaba, thingsboard
- (1) Command execution failed.  
  repos: quarkus
- (1) DOCKER> Cannot create docker access object  [Connect to G:A [/N] failed: Connection timed out]  
  repos: mall
- (1) Failed to execute goal G:A:enforce (default) on project parent:  
  repos: neo4j
- (1) Failed to execute goal G:A:test (default-test) on project floci:  
  repos: floci
- (1) Failed to execute goal on project keycloak-quarkus-dist: Could not resolve dependencies for project G:A:N-SNAPSHOT  
  repos: keycloak
- (1) Failed to execute goal on project nacos-istio: Could not resolve dependencies for project G:A:N-SNAPSHOT  
  repos: nacos
- (1) Failed to execute goal on project questdb: Could not resolve dependencies for project G:A:N-SNAPSHOT  
  repos: questdb
- (1) Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: N s <<< FAILURE! -- in org.cryptomator.common.keychain.KeychainManagerTest  
  repos: cryptomator
- (1) Tests run: 3, Failures: 0, Errors: 3, Skipped: 0, Time elapsed: N s <<< FAILURE! -- in com.iluwatar.bloc.BlocUiTest  
  repos: java-design-patterns
- (1) com.ctrip.framework.apollo.biz.registry.DatabaseDiscoveryClientMemoryCacheDecoratorImpl - fail to read service instances from database  
  repos: apollo

# Tier-3 reasons (import ERRORs and jk failures), grouped — run15

Generated 2026-09-18 11:55. Module prefixes, coordinates, versions and paths are normalized so one line = one distinct cause = one ticket candidate.

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
- (1) `<modules>` are declared only in profiles that are not active on this machine (default-jdk8, default-heavy, integration-jdk8, integration-heavy, default-jdk17, default, default-jdk22, default-jdk23, default-jdk24, default-jdk25, default-jdk26, integration-jdk17, integration, integration-jdk22, integration-jdk23, integration-jdk24, integration-jdk25, integration-jdk26, live-all, parents, default-disabled, integration-disabled); no module was imported, so the workspace builds nothing. Activate one as Maven's `-P` does — `jk import pom.xml -P default-jdk8` — or list the modules at the top level.  
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

## jk build failure

- (1) <path> error: cannot access java.security.acl.Group · class file for java.security.acl.Group not found  
  repos: keycloak
- (1) <path> error: duplicate class: org.neo4j.cypher.internal.parser.v25.Cypher  
  repos: neo4j
- (1) javac returned non-zero exit code  
  repos: spring-cloud-alibaba
- (1) protoc failed (exit 1): · Security.proto: File not found. · G:A: Import "Security.proto" was not found or had errors.  
  repos: hadoop
- (1) repository `apache.snapshots` at http://repository.apache.org/snapshots, declared by the POM of G:A, was not used: it is plaintext http, and a repository  
  repos: quarkus
- (1) repository `apache.snapshots` at http://repository.apache.org/snapshots, declared by the POM of G:A, was not used: it is plaintext http, and a repository a POM declares has no table to  
  repos: floci
- (1) the compiler worker failed: java.lang.IllegalArgumentException: error: invalid flag: -J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED · at jdk.compiler/com.sun.tools.javac.main.Arguments.reportDiag(G:A) · at jdk.compiler/com.sun.  
  repos: zipkin

## jk test failure

- (1) + [1 of 8] G:A - took 33ms  
  repos: dataease
- (1) 1 test failure  
  repos: thingsboard
- (1) Unable to open DISPLAY  
  repos: cryptomator
- (1) `parse-build` src holds .groovy sources this module does not compile: jk.toml declares java and not groovy — add groovy = "<version>" to compile them  
  repos: jenkins
- (1) test failure: com.alibaba.nacos.logger.adapter.log4j2.Log4J2NacosLoggingAdapterTest — G:A#testIsNeedReloadConfiguration() — java.lang.ClassCastException: class org.apache.logging.slf4j.SLF4JLoggerContext cannot be cast to cla  
  repos: nacos
- (1) test failure: com.ctrip.framework.apollo.build.sql.converter.ApolloSqlConverterH2Test — G:A#checkH2() — java.lang.IllegalStateException: illegal class path: <path>  
  repos: apollo
- (1) test failure: com.iluwatar.corruption.system.AntiCorruptionLayerTest — G:A#antiCorruptionLayerWithExTest() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@15a8cebd testClass  
  repos: java-design-patterns
- (1) test failure: com.macro.mall.portal.MallPortalApplicationTests — G:A#contextLoads() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@337fb1a5 testClass = com.macro.mall.portal.MallPo  
  repos: mall
- (1) test failure: com.xxl.job.executor.test.BaseTests — G:A#test() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@5f32de90 testClass = com.xxl.job.executor.test.  
  repos: xxl-job

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

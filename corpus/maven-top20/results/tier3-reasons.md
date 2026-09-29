# Tier-3 reasons (import ERRORs and jk failures), grouped — run17

Generated 2026-09-28 20:15. Module prefixes, coordinates, versions and paths are normalized so one line = one distinct cause = one ticket candidate.

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
- (1) G:A N` is pinned by the POM, and no repository the lock reads (jboss-public-repository, central, jumpkick, google) lists G:A at all; `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
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
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N, N, N, N-M1, N, N, N-M3 and 4 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, Nrc1, N, N, 20030911, N, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: thingsboard
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N-alpha-2, N-alpha-1, N, N, N, N, N, N and 101 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N-beta-3, N-beta-2, N-beta-1, N, N, N, N, N and 23 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: quarkus
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (jboss-public-repository lists N-brew; central lists N, N, N-rc1, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (repository.jboss.org lists N.v_883_R34x, N.v_686_R32x; central lists N-v_677_R32x, N-v_771, N, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: hadoop
- (1) G:A Nc` is pinned by the POM, and no repository the lock reads lists that version (central lists N.O, N-RC8, N-RC3, N_min, N, Na_min, Na); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes Nc in `[repositories]`.  
  repos: jenkins
- (1) G:A names `testsuite/integration-arquillian/servers/app-server/jboss/galleon/pom.xml`, which only profile `app-server-eap8` of `testsuite/integration-arquillian/servers/app-server/jboss` lists, and that profile is not active on this machine (activate it with Maven's `-P` and re-import, or list the module at the top level); the lock would fetch it from a repository, where a reactor module is not published, so the dependency was not written.  
  repos: keycloak
- (1) G:A names `testsuite/integration-arquillian/servers/app-server/jboss/wildfly/pom.xml`, which only profile `app-server-wildfly` of `testsuite/integration-arquillian/servers/app-server/jboss` lists, and that profile is not active on this machine (activate it with Maven's `-P` and re-import, or list the module at the top level); the lock would fetch it from a repository, where a reactor module is not published, so the dependency was not written.  
  repos: keycloak
- (1) `<build><extensions>` G:A is the `maven-archetype` packaging, a project template jk does not build; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: tutorials
- (1) `<build><extensions>` G:A is the `maven-archetype` packaging, a project template jk does not build; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`. (4 modules: extensions/amazon-lambda/maven-archetype, extensions/amazon-lambda-http/maven-archetype, extensions/amazon-lambda-rest/maven-archetype, …)  
  repos: quarkus
- (1) `<plugin>` G:A is the `bundle` packaging: an OSGi manifest bnd computes, which jk's jar step does not write; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`. (3 modules: osgi/osgi-intro-sample-activator, osgi/osgi-intro-sample-service, osgi/osgi-intro-sample-client)  
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
- (1) `<type>zip</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`. (2 modules: libraries-apm/new-relic/currency-converter, libraries-apm/new-relic/newrelic-monitoring)  
  repos: tutorials
- (1) `<type>zip</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`. (2 modules: quarkus/dist, testsuite/integration-arquillian/tests/base)  
  repos: keycloak
- (1) `<type>zip</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`. (3 modules: test-framework/core, quarkus/tests/junit5, testsuite/integration-arquillian/servers/auth-server/quarkus)  
  repos: keycloak
- (1) packaging `war` (`maven-war-plugin`) is not supported: jk builds jars, Boot jars and native images. Keep building this module with `jk mvn package`. (136 modules: microservices-modules/microprofile, web-modules/restx, apache-cxf-modules/cxf-spring, …)  
  repos: tutorials
- (1) packaging `war` (`maven-war-plugin`) is not supported: jk builds jars, Boot jars and native images. Keep building this module with `jk mvn package`. (2 modules: hadoop-common-project/hadoop-auth-examples, hadoop-yarn-project/hadoop-yarn/hadoop-yarn-applications/hadoop-yarn-applications-catalog/hadoop-yarn-applications-catalog-webapp)  
  repos: hadoop

## jk lock failure

- (1) Cannot resolve dependencies: · No versions of G:A match N-SNAPSHOT · N-SNAPSHOT is a snapshot, and no repository G:A may resolve from serves snapshots: central (releases only), jumpkick (releases only), g  
  repos: questdb
- (1) repository nexus at http://G:A/repository/maven-releases/ is unreachable (ConnectException: the connection was not accepted); the resolve stops rather than lock without a configured repository — fix its url, start it, or remove it from [repositories  
  repos: tutorials

## jk build failure

- (1) <path> error: cannot access java.security.acl.Group · class file for java.security.acl.Group not found  
  repos: keycloak
- (1) <path> error: duplicate class: org.neo4j.cypher.internal.parser.v5.Cyph  
  repos: neo4j
- (1) repository `Codehaus Snapshots` at http://snapshots.repository.codehaus.org/, declared by the POM of G:A, was not used: it is plaintext http, and a repositor  
  repos: hadoop
- (1) repository `apache.snapshots` at http://repository.apache.org/snapshots, declared by the POM of G:A, was not used: it is plaintext http, and a repository  
  repos: quarkus

## jk test failure

- (1) <path> error: reference to markAllNotificationsAsRead is ambiguous · both method markAllNotificationsAsRead(org.thingsb  
  repos: thingsboard
- (1) CANCELLED test dataease · 273ms  
  repos: dataease
- (1) `jenkins-core` `compile-java` unknown enum constant javax.annotation.meta.When.MAYBE  
  repos: jenkins
- (1) test failure: com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointTests — G:A#contextLoads() — java.lang.IllegalStateException: Failed to load ApplicationContext for [MergedContextConfiguration@75dc1c1c testClass = com  
  repos: spring-cloud-alibaba
- (1) test failure: com.alibaba.nacos.logger.adapter.log4j2.Log4J2NacosLoggingAdapterTest — G:A#testIsNeedReloadConfiguration() — java.lang.ClassCastException: class org.apache.logging.slf4j.SLF4JLoggerContext cannot be cast to cla  
  repos: nacos
- (1) test failure: com.ctrip.framework.apollo.audit.controller.ApolloAuditControllerTest — G:A#testFindAllAuditLogsByOpNameAndTime() — java.lang.AssertionError: Status expected:<200> but was:<401>  
  repos: apollo
- (1) test failure: com.iluwatar.corruption.system.AntiCorruptionLayerTest — G:A#antiCorruptionLayerWithExTest() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@5546e754 testClass  
  repos: java-design-patterns
- (1) test failure: com.macro.mall.portal.PortalProductDaoTests — G:A#testGetPromotionProductList() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@57bfca3a testClass = com.macro.mall.por  
  repos: mall
- (1) test failure: com.xxl.job.admin.business.mapper.XxlJobLogReportMapperTest — G:A#test() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@4acb2510 testClass = com.xxl.job.admin.business  
  repos: xxl-job
- (1) test failure: io.github.hectorvent.floci.core.common.dns.EmbeddedDnsServerTest#forwardToUpstreams_returnsResponseLargerThan512BytesIntact() — java.net.SocketTimeoutException: Receive timed out  
  repos: floci
- (1) test failure: zipkin2.collector.CollectorTest — G:A#accept_storageError() — java.lang.AssertionError:  
  repos: zipkin

## Maven-side failure (for context)

- (3) Plugin G:A or one of its dependencies could not be resolved:  
  repos: analysis-ik, questdb, zipkin
- (2) Error executing Maven.  
  repos: neo4j, quarkus
- (2) Some problems were encountered while processing the POMs:  
  repos: hadoop, thingsboard
- (2) mvn package fail  
  repos: keycloak, tutorials
- (1) COMPILATION ERROR :  
  repos: spring-cloud-alibaba
- (1) DOCKER> Cannot create docker access object  [Connect to G:A [/N] failed: Connection timed out]  
  repos: mall
- (1) Failed to execute goal on project nacos-istio: Could not resolve dependencies for project G:A:N-SNAPSHOT  
  repos: nacos
- (1) Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0).  
  repos: java-design-patterns
- (1) TestEngine with ID 'junit-jupiter' encountered a critical issue during test discovery:  
  repos: floci
- (1) jdk-unavailable: temurin-26  
  repos: cryptomator

# Tier-3 reasons (import ERRORs and jk failures), grouped — run9

Generated 2026-09-16 20:31. Module prefixes, coordinates, versions and paths are normalized so one line = one distinct cause = one ticket candidate.

## import Tier 3 (not imported)

- (3) packaging `war` (`maven-war-plugin`) is not supported: jk builds jars, Boot jars and native images. Keep building this module with `jk mvn package`.  
  repos: hadoop, java-design-patterns, jenkins
- (2) `<type>exe</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: quarkus, thingsboard
- (1) G:A N` is pinned by the POM, and no repository the lock reads (jboss-public-repository, central, jumpkick, google) lists G:A at all; `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A N` is pinned by the POM, and no repository the lock reads (repo.jenkins-ci.org, central, jumpkick, google) lists G:A at all; `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: jenkins
- (1) G:A N` is pinned by the POM, and no repository the lock reads (repository.jboss.org, central, jumpkick, google) lists G:A at all; `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: hadoop
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: java-design-patterns
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (central lists N, N, N, N, N, N, N, N and 43 older); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: quarkus
- (1) G:A N` is pinned by the POM, and no repository the lock reads lists that version (jboss-public-repository lists N-brew; central lists N, N, N-rc1, N); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes N in `[repositories]`.  
  repos: keycloak
- (1) G:A Nc` is pinned by the POM, and no repository the lock reads lists that version (central lists N.O, N-RC8, N-RC3, N_min, N, Na_min, Na); `jk lock` refuses it. Pin a version a repository lists, or declare the repository that publishes Nc in `[repositories]`.  
  repos: jenkins
- (1) G:A names `testsuite/integration-arquillian/servers/app-server/jboss/galleon/pom.xml`, which only profile `app-server-eap8` of `testsuite/integration-arquillian/servers/app-server/jboss` lists, and that profile is not active on this machine (activate it with Maven's `-P` and re-import, or list the module at the top level); the lock would fetch it from a repository, where a reactor module is not published, so the dependency was not written.  
  repos: keycloak
- (1) G:A names `testsuite/integration-arquillian/servers/app-server/jboss/wildfly/pom.xml`, which only profile `app-server-wildfly` of `testsuite/integration-arquillian/servers/app-server/jboss` lists, and that profile is not active on this machine (activate it with Maven's `-P` and re-import, or list the module at the top level); the lock would fetch it from a repository, where a reactor module is not published, so the dependency was not written.  
  repos: keycloak
- (1) `<build><extensions>` G:A builds only the modules git shows as changed, a selection `jk build` makes from its own action cache; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: tutorials
- (1) `<build><extensions>` G:A is the `maven-archetype` packaging, a project template jk does not build; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: quarkus
- (1) `<build><extensions>` G:A is the ssh transport of `mvn deploy`; `jk publish` speaks HTTP(S), `s3://` and `gs://`; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: thingsboard
- (1) `<build><extensions>` G:A reports the Maven build to CI; the build's outputs do not depend on it; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`. Declared by the root pom.xml, inherited by 1,057 modules.  
  repos: quarkus
- (1) `<modules>` are declared only in profiles that are not active on this machine (default-jdk8, default-heavy, integration-jdk8, integration-heavy, default-jdk17, default, default-jdk22, default-jdk23, default-jdk24, default-jdk25, default-jdk26, integration-jdk17, integration, integration-jdk22, integration-jdk23, integration-jdk24, integration-jdk25, integration-jdk26, live-all, parents, default-disabled, integration-disabled); no module was imported, so the workspace builds nothing. Activate one with Maven's `-P` and re-import, or list the modules at the top level.  
  repos: tutorials
- (1) `<type>executable-war</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: jenkins
- (1) `<type>war</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: hadoop
- (1) `<type>zip</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: keycloak
- (1) `localizer-maven-plugin` generates `hudson.Messages`, `hudson.cli.Messages`, `hudson.diagnosis.Messages`, `hudson.fsp.Messages`, `hudson.init.impl.Messages` and 45 more from `Messages.properties` files under src/main/resources, src/filter/resources into `target/generated-sources/localizer`, and the plugin has no `main` a `[generate]` entry could run (a Maven mojo and an Ant task only), so a source importing those classes does not compile under jk. Run `jk mvn generate-sources` once, move `target/generated-sources/localizer` to a source root of its own (`src/generated/java`, listed in `[build] extra-src`) and check it in, or keep the module under `jk mvn`.  
  repos: jenkins
- (1) `localizer-maven-plugin` generates `hudson.cli.client.Messages` from `Messages.properties` files under src/main/resources, src/filter/resources into `target/generated-sources/localizer`, and the plugin has no `main` a `[generate]` entry could run (a Maven mojo and an Ant task only), so a source importing those classes does not compile under jk. Run `jk mvn generate-sources` once, move `target/generated-sources/localizer` to a source root of its own (`src/generated/java`, listed in `[build] extra-src`) and check it in, or keep the module under `jk mvn`.  
  repos: jenkins
- (1) `maven-shade-plugin` relocates org.apache.lucene → org.neo4j.shaded.lucene9; jk has no package relocation, so no jar this workspace builds carries the shaded packages, and `community/lucene-index` (31 files), `community/neo4j` (1 file), `community/dbms` (1 file), `community/community-it/index-it` (1 file) import them. Keep building this module with Maven — `jk mvn -pl community/lucene9-shaded install` publishes G:A into `~/.m2/repository` — and depend on that artifact from a `file://` repository over `~/.m2/repository` in place of the workspace edge.  
  repos: neo4j
- (1) `protobuf-maven-plugin` runs `grpc-java` protoc plugin (G:A:G:A) through `compile-custom`; `[protobuf]` has no key for a protoc plugin, so its output is not generated under jk. `src/main/proto/edge.proto` declare a `service`, so a source importing the generated stubs does not compile; check the stubs in under a source root of their own, or keep the module under `jk mvn`.  
  repos: thingsboard
- (1) `protobuf-maven-plugin` runs `grpc-java` protoc plugin (G:A:G:A) through `compile-custom`; `[protobuf]` has no key for a protoc plugin, so its output is not generated under jk. `src/main/proto/health/v1/health.proto`, `src/main/proto/reflection/v1/reflection.proto`, `src/main/proto/reflection/v1alpha/reflection.proto` declare a `service`, so a source importing the generated stubs does not compile; check the stubs in under a source root of their own, or keep the module under `jk mvn`.  
  repos: quarkus
- (1) `protobuf-maven-plugin` runs `grpc-java` protoc plugin (G:A:G:A) through `compile-custom`; `[protobuf]` has no key for a protoc plugin, so its output is not generated under jk. `src/main/resources/proto/mcp/v1alpha1/mcp.proto` declare a `service`, so a source importing the generated stubs does not compile; check the stubs in under a source root of their own, or keep the module under `jk mvn`.  
  repos: nacos

## jk lock failure

- (2) Resolving dependency graph...  
  repos: hadoop, thingsboard
- (1) Cannot resolve dependencies: · No versions of G:A match N · The project depends on G:A N  
  repos: java-design-patterns
- (1) Cannot resolve dependencies: · No versions of G:A match N-SNAPSHOT · N-SNAPSHOT is a snapshot, and no repository G:A may resolve from serves snapshots: central (releases only), jumpkick (releases only), g  
  repos: questdb
- (1) GET https://repository.jboss.org/nexus/content/groups/public/junit/junit-dep/N/junit-dep-N.jar failed after 6 attempts  
  repos: keycloak
- (1) POM not found in any declared repo: G:A  
  repos: quarkus

## jk build failure

- (1) <path> error: cannot find symbol · symbol: method getClassFactory()  
  repos: nacos
- (1) <path> error: package hudson.cli.client does not exist  
  repos: jenkins
- (1) <path> error: package org.neo4j.shaded.lucene9.index does not exist  
  repos: neo4j

## jk test failure

- (1) + [1 of 8] G:A - took 63ms  
  repos: dataease
- (1) <path> error: [NullAway] returning  
  repos: spring-cloud-alibaba
- (1) <path> error: package zipkin2.proto3 does not exist  
  repos: zipkin
- (1) Unable to open DISPLAY  
  repos: cryptomator
- (1) java.io.IOException: zinc worker exited with status 3 · --- zinc worker output --- · Terminating due to java.lang.OutOfMemoryError: GC overhead limit exceeded  
  repos: floci
- (1) test failure: com.ctrip.framework.apollo.build.sql.converter.ApolloSqlConverterH2Test — G:A#checkH2() — java.lang.IllegalStateException: illegal class path: <path>  
  repos: apollo
- (1) test failure: com.macro.mall.search.MallSearchApplicationTests — G:A#testGetAllEsProductList() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@5b324447 testClass = com.macro.mall.se  
  repos: mall
- (1) test failure: com.xxl.job.openapi.ExecutorBizTest — G:A#trigger() — java.lang.RuntimeException: Http Request Error (Connection refused (connect failed)). for url : http://G:A/trigger  
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

# Tier-3 reasons (import ERRORs and jk failures), grouped — run5

Generated 2026-09-16 10:36. Module prefixes, coordinates, versions and paths are normalized so one line = one distinct cause = one ticket candidate.

## import Tier 3 (not imported)

- (6) `<build><extensions>` is not supported. Move build extensions to a custom jk task once tasks land.  
  repos: hadoop, nacos, neo4j, quarkus, thingsboard, tutorials
- (3) packaging `war` (`maven-war-plugin`) is not supported: jk builds jars, Boot jars and native images. Keep building this module with `jk mvn package`.  
  repos: hadoop, java-design-patterns, jenkins
- (2) `<parent>` G:A could not be resolved (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: hadoop, quarkus
- (1) `<modules>` are declared only in profiles that are not active on this machine (default-jdk8, default-heavy, integration-jdk8, integration-heavy, default-jdk17, default, default-jdk22, default-jdk23, default-jdk24, default-jdk25, default-jdk26, integration-jdk17, integration, integration-jdk22, integration-jdk23, integration-jdk24, integration-jdk25, integration-jdk26, live-all, parents, default-disabled, integration-disabled); no module was imported, so the workspace builds nothing. Activate one with Maven's `-P` and re-import, or list the modules at the top level.  
  repos: tutorials
- (1) the effective model could not be built (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: hadoop

## jk lock failure

- (3) Cannot resolve dependencies: · No versions of G:A match N · The project depends on G:A N  
  repos: dataease, java-design-patterns, nacos
- (1) Cannot resolve dependencies: · G:A N depends on G:A [N,+∞) · G:A N depends on G:A N  
  repos: zipkin
- (1) Cannot resolve dependencies: · No versions of G:A match N-SNAPSHOT · The project depends on G:A N-SNAPSHOT  
  repos: questdb
- (1) Cannot resolve dependencies: · No versions of G:A match unresolved · The project depends on G:A unresolved  
  repos: hadoop
- (1) Invalid key: Unexpected '+', expected end-of-input  
  repos: quarkus
- (1) POM not found in any declared repo: G:A  
  repos: jenkins
- (1) dependency resolution failed while reading the dependencies of G:A:@N.Final: java.lang.IllegalArgumentException: malformed Maven range: [N, 3[  
  repos: keycloak
- (1) workspace artifact collision: `edqs-N-SNAPSHOT.jar` would be produced by both `common/edqs` and `edqs`. Final artifacts share <workspaceRoot>/target/, so two modules can't emit the same `<artifact>-<version>.jar`. Di  
  repos: thingsboard

## jk build failure

- (1) <path> error: malformed HTML  
  repos: xxl-job
- (1) <path> error: module not found: org.jetbrains.annotations  
  repos: cryptomator
- (1) <path> error: package com.ctrip.framework.apollo.openapi.model does not exist  
  repos: apollo
- (1) java.lang.RuntimeException: java.lang.NoClassDefFoundError: org/apache/logging/log4j/util/Strings  
  repos: neo4j
- (1) java.util.ConcurrentModificationException  
  repos: spring-cloud-alibaba

## jk test failure

- (1) test discovery exited 3 before any test ran · Fix: the runner's full output is above; rerun with --verbose for the live stream.  
  repos: floci
- (1) test failure: com.macro.mall.search.MallSearchApplicationTests — G:A#testGetAllEsProductList() — java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@3b6c740b testClass = com.macro.mall.se  
  repos: mall

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

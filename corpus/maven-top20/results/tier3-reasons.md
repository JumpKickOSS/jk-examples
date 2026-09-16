# Tier-3 reasons (import ERRORs and jk failures), grouped — run2

Generated 2026-09-16 02:19. Module prefixes, coordinates, versions and paths are normalized so one line = one distinct cause = one ticket candidate.

## import Tier 3 (not imported)

- (5) `<build><extensions>` is not supported. Move build extensions to a custom jk task once tasks land.  
  repos: hadoop, nacos, quarkus, thingsboard, tutorials
- (3) `<parent>` G:A could not be resolved (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: hadoop, quarkus, spring-cloud-alibaba
- (1) `<parent>` G:A:${revision} could not be resolved (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: spring-cloud-alibaba
- (1) `<parent>` G:A:${revision}${changelist} could not be resolved (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: jenkins
- (1) the effective model could not be built (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: hadoop

## jk lock failure

- (4) Cannot resolve dependencies: · G:A N depends on G:A [N,+∞) · The project depends on G:A N  
  repos: analysis-ik, cryptomator, java-design-patterns, nacos
- (2) Cannot resolve dependencies: · No versions of G:A match N-SNAPSHOT · The project depends on G:A N-SNAPSHOT  
  repos: questdb, zipkin
- (2) POM not found in any declared repo: G:A  
  repos: quarkus, thingsboard
- (2) environment references are not allowed here: version (${revision}) … — CI-friendly ${revision}/${changelist} versions copied verbatim from the pom  
  repos: jenkins, spring-cloud-alibaba
- (1) Cannot resolve dependencies: · No versions of G:A match unresolved · The project depends on G:A unresolved  
  repos: hadoop
- (1) Cannot resolve dependencies: · Package G:A was not found in any repository · The project depends on G:A N-SNAPSHOT  
  repos: apollo
- (1) java.util.NoSuchElementException  
  repos: floci
- (1) platform BOM conflict on G:A: G:A constrains to N, but G:A constrains to N. Pick one BOM or pin the coord explicitly.  
  repos: keycloak

## jk build failure

- (1) <path> error: cannot find symbol · symbol:   method setDescription(java.lang.String) · location: variable webLog of type com.macro.mall.common.domain.W  
  repos: mall
- (1) <path> error: malformed HTML  
  repos: xxl-job

## jk test failure

- (1) 1 test failure  
  repos: neo4j
- (1) test failure: com.thealgorithms.sorts.SelectionSortRecursiveTest#shouldAcceptWhenRandomListIsPassed() — java.lang.StackOverflowError  
  repos: TheAlgorithms-Java

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

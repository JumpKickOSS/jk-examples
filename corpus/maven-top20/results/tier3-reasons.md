# Tier-3 reasons (import ERRORs and jk failures), grouped — run6

Generated 2026-09-16 12:15. Module prefixes, coordinates, versions and paths are normalized so one line = one distinct cause = one ticket candidate.

## import Tier 3 (not imported)

- (3) packaging `war` (`maven-war-plugin`) is not supported: jk builds jars, Boot jars and native images. Keep building this module with `jk mvn package`.  
  repos: hadoop, java-design-patterns, jenkins
- (2) `<parent>` G:A could not be resolved (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: hadoop, quarkus
- (2) `<type>exe</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: quarkus, thingsboard
- (1) `<build><extensions>` G:A builds only the modules git shows as changed, a selection `jk build` makes from its own action cache; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: tutorials
- (1) `<build><extensions>` G:A is the `maven-archetype` packaging, a project template jk does not build; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: quarkus
- (1) `<build><extensions>` G:A is the ssh transport of `mvn deploy`; `jk publish` speaks HTTP(S), `s3://` and `gs://`; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: thingsboard
- (1) `<build><extensions>` G:A reports the Maven build to CI; the build's outputs do not depend on it; nothing is written for it. Drop it from the POM when the jk build does not need it, or keep running that step with `jk mvn`.  
  repos: quarkus
- (1) `<modules>` are declared only in profiles that are not active on this machine (default-jdk8, default-heavy, integration-jdk8, integration-heavy, default-jdk17, default, default-jdk22, default-jdk23, default-jdk24, default-jdk25, default-jdk26, integration-jdk17, integration, integration-jdk22, integration-jdk23, integration-jdk24, integration-jdk25, integration-jdk26, live-all, parents, default-disabled, integration-disabled); no module was imported, so the workspace builds nothing. Activate one with Maven's `-P` and re-import, or list the modules at the top level.  
  repos: tutorials
- (1) `<type>executable-war</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: jenkins
- (1) `<type>war</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: hadoop
- (1) `<type>zip</type>` on G:A names an artifact jk has no manifest spelling for; the dependency was not written. A jar of the same module is `{ group, name, version }`, a classified jar adds `classifier`.  
  repos: keycloak
- (1) `common/edqs` and `edqs` and `msa/edqs` both carry the name `edqs` (G:A, G:A, G:A), and `application` depend on it. A workspace edge is spelled by module name alone, so the edge is written as `edqs.workspace = true` and jk refuses the workspace as ambiguous until one of the two modules is renamed.  
  repos: thingsboard
- (1) `common/transport/coap` and `transport/coap` and `msa/transport/coap` both carry the name `coap` (G:A, G:A, G:A), and `application` depend on it. A workspace edge is spelled by module name alone, so the edge is written as `coap.workspace = true` and jk refuses the workspace as ambiguous until one of the two modules is renamed.  
  repos: thingsboard
- (1) `common/transport/http` and `transport/http` and `msa/transport/http` both carry the name `http` (G:A, G:A, G:A), and `application` depend on it. A workspace edge is spelled by module name alone, so the edge is written as `http.workspace = true` and jk refuses the workspace as ambiguous until one of the two modules is renamed.  
  repos: thingsboard
- (1) `common/transport/lwm2m` and `transport/lwm2m` and `msa/transport/lwm2m` both carry the name `lwm2m` (G:A, G:A, G:A), and `application` depend on it. A workspace edge is spelled by module name alone, so the edge is written as `lwm2m.workspace = true` and jk refuses the workspace as ambiguous until one of the two modules is renamed.  
  repos: thingsboard
- (1) `common/transport/mqtt` and `transport/mqtt` and `msa/transport/mqtt` both carry the name `mqtt` (G:A, G:A, G:A), and `application` depend on it. A workspace edge is spelled by module name alone, so the edge is written as `mqtt.workspace = true` and jk refuses the workspace as ambiguous until one of the two modules is renamed.  
  repos: thingsboard
- (1) `common/transport/snmp` and `transport/snmp` and `msa/transport/snmp` both carry the name `snmp` (G:A, G:A, G:A), and `application` depend on it. A workspace edge is spelled by module name alone, so the edge is written as `snmp.workspace = true` and jk refuses the workspace as ambiguous until one of the two modules is renamed.  
  repos: thingsboard
- (1) the effective model could not be built (…); nothing was inherited, and a dependency whose version the parent managed is written as `=unresolved`.  
  repos: hadoop

## jk lock failure

- (2) POM not found in any declared repo: G:A  
  repos: keycloak, quarkus
- (1) Cannot resolve dependencies: · G:A N depends on G:A [N,+∞) · G:A N depends on G:A N  
  repos: zipkin
- (1) Cannot resolve dependencies: · No versions of G:A match N · The project depends on G:A N  
  repos: nacos
- (1) Cannot resolve dependencies: · No versions of G:A match N-SNAPSHOT · N-SNAPSHOT is a snapshot, and no repository G:A may resolve from serves snapshots: central (releases only), jumpkick (releases only), g  
  repos: questdb
- (1) Cannot resolve dependencies: · No versions of G:A match N.ALL · The project depends on G:A N.ALL  
  repos: mall
- (1) Cannot resolve dependencies: · No versions of G:A match unresolved · The project depends on G:A unresolved  
  repos: hadoop
- (1) Cannot resolve dependencies: · Resolution budget exceeded: exceeded resolve time budget; set JK_RESOLVE_TIMEOUT_MS to raise (0 = unlimited)  
  repos: java-design-patterns
- (1) dependency resolution failed while reading the dependencies of G:A:@N: java.lang.IllegalArgumentException: malformed Maven range: [N, 3[  
  repos: jenkins
- (1) no upstream checksum for G:A from central (org/jetbrains/kotlin/kotlin-bom/N/kotlin-bom-N.pom): the repository publishes neither a .sha256 nor a .sha1 sidecar, so the bytes cannot be verified before they are pinned.  
  repos: dataease
- (1) workspace edge `edqs` is ambiguous: `edqs` depends on it, and `common/edqs` and `edqs` and `msa/edqs` both carry that name. A workspace edge is spelled by module name alone, so rename one of them.  
  repos: thingsboard

## jk build failure

- (1) <path> error: module not found: org.jetbrains.annotations  
  repos: cryptomator
- (1) <path> error: package com.ctrip.framework.apollo.openapi.model does not exist  
  repos: apollo
- (1) <path> error: package org.neo4j.shaded.lucene9.index does not exist  
  repos: neo4j
- (1) Spring AOT processing failed (exit 1): · at com.google.inject.internal.ConstructorInjectorStore.get(G:A) · at com.google.inject.internal.ConstructorBindingImpl.initialize(G:A)  
  repos: spring-cloud-alibaba

## jk test failure

- (1) test discovery exited 3 before any test ran · Fix: the runner's full output is above; rerun with --verbose for the live stream.  
  repos: floci
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

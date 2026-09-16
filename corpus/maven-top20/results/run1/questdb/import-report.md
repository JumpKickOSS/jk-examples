# jk import report

Source: `/home/bsant/src/scratch/maven-corpus/questdb/pom.xml`

## Tier 2 — imported with best-effort

These were mapped but you should review the result.

- Maven profile `local-client`: properties=[questdb.client.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- `<plugin>maven-deploy-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>jacoco-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- `<plugin>maven-release-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [core] Maven profile `per-fork-test-log`: activation=property=env.QDB_LOG_W_FILE_LOCATION (no jk equivalent — replace with an explicit jk profile or feature); plugins=[maven-surefire-plugin] (plugin mapping is not yet implemented).
- [core] Maven profile `local-client`: properties=[questdb.client.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `javadoc`: plugins=[maven-javadoc-plugin] (plugin mapping is not yet implemented).
- [core] Maven profile `maven-central-release`: plugins=[maven-javadoc-plugin,maven-source-plugin,maven-gpg-plugin,central-publishing-maven-plugin] (plugin mapping is not yet implemented).
- [core] Maven profile `build-web-console`: plugins=[download-maven-plugin,maven-assembly-plugin] (plugin mapping is not yet implemented).
- [core] Maven profile `build-binaries`: plugins=[exec-maven-plugin,maven-assembly-plugin] (plugin mapping is not yet implemented).
- [core] Maven profile `build-rust-library`: plugins=[rust-maven-plugin] (plugin mapping is not yet implemented).
- [core] Maven profile `platform-windows-x86-64`: activation=os=windows (use jk target predicates per dep); properties=[archive.name,runtime.name,runtime.assembly] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `platform-linux-x86-64`: activation=os=unix (use jk target predicates per dep); properties=[archive.name,runtime.name,runtime.assembly,platform.dir.name,jemalloc.so] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `platform-freebsd-x86-64`: activation=os=FreeBSD (use jk target predicates per dep); properties=[archive.name,runtime.name,runtime.assembly] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `platform-linux-aarch64`: activation=os=unix (use jk target predicates per dep); properties=[archive.name,runtime.name,runtime.assembly,platform.dir.name,jemalloc.so] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `platform-osx-aarch64`: activation=os=Mac (use jk target predicates per dep); properties=[archive.name,runtime.name,runtime.assembly,platform.dir.name] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `jacoco`: plugins=[jacoco-maven-plugin] (plugin mapping is not yet implemented).
- [core] Maven profile `java25+`: activation=jdk=(24,); 1 dependency (convert to a jk feature `java25+` if opt-in, or move into the main deps list); properties=[jdk.version,java.enforce.version,questdb.artifactid,excludePattern1,excludeTestPattern1,javac.compile.source,javac.compile.target] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `qdbr-release`: properties=[qdbr.release] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [core] Maven profile `qdbr-coverage`: properties=[qdr.release,qdbr.rustflags] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest); plugins=[rust-maven-plugin] (plugin mapping is not yet implemented).
- [core] `<plugin>maven-jar-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [core] `<plugin>maven-enforcer-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [core] `<plugin>maven-clean-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [core] `<plugin>maven-deploy-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [core] `<plugin>buildnumber-maven-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [core] `<plugin>maven-surefire-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [benchmarks] Maven profile `local-client`: contained no convertible payload; dropped.
- [benchmarks] Maven profile `javadoc`: plugins=[maven-javadoc-plugin] (plugin mapping is not yet implemented).
- [benchmarks] `<plugin>maven-shade-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.
- [utils] Maven profile `local-client`: properties=[questdb.client.version] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [utils] Maven profile `java17+`: activation=jdk=(17,); properties=[questdb.artifactid] (no jk equivalent — fold maven.compiler.* into project.jdk; drop the rest).
- [utils] `<plugin>maven-shade-plugin</plugin>` was not imported. Plugin-aware mappings (Spotless, JaCoCo, Spring Boot, ...) arrive in slice D.


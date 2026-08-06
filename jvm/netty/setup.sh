#!/usr/bin/env bash
# Clone Netty at the pinned tag and overlay JumpKick manifests (Mill-style thirdparty port).
# Checkout lands in ./checkout (gitignored). Re-running refreshes the overlay + codegen.
set -euo pipefail
cd "$(dirname "$0")"

# Align with Mill's Netty case study: large 4.1 multi-module tree (~47 Maven modules).
NETTY_TAG="${NETTY_TAG:-netty-4.1.115.Final}"
NETTY_REPO="${NETTY_REPO:-https://github.com/netty/netty.git}"

if [ ! -d checkout/.git ]; then
  git clone --filter=blob:none "$NETTY_REPO" checkout
fi
git -C checkout fetch -q --tags origin "refs/tags/${NETTY_TAG}:refs/tags/${NETTY_TAG}" 2>/dev/null \
  || git -C checkout fetch -q origin tag "$NETTY_TAG" --no-tags
# Force clean tree: soft checkout of the same tag leaves prior curate deletions in place.
git -C checkout checkout -q -f "$NETTY_TAG"
git -C checkout clean -fdq
SHA=$(git -C checkout rev-parse HEAD)

# Overlay: root workspace + per-module jk.toml (hand-maintained / generated from Mill graph).
cp -a overlay/. checkout/

# common/ requires Groovy template codegen (Maven gmaven / Mill GroovyShell).
./scripts/generate-common.sh checkout

# transport-sctp: Maven excludes com/** stubs (see transport-sctp/pom.xml compiler excludes).
# Those stubs conflict with JDK module jdk.sctp on Java 9+; remove them so compile matches Maven.
if [ -d checkout/transport-sctp/src/main/java/com ]; then
  rm -rf checkout/transport-sctp/src/main/java/com
  echo "stripped transport-sctp com.sun.nio.sctp stubs (Maven compiler exclude; JDK module conflict)"
fi

# handler: OpenJdkSelfSignedCertGenerator uses sun.security.x509 which needs
# --add-exports java.base/sun.security.x509=ALL-UNNAMED at compile (Maven argLine / Mill forkArgs).
# jk has no project-level javac export flag yet — drop this optional JDK-internal helper
# (BouncyCastleSelfSignedCertGenerator remains). Track as product gap: compiler-args in jk.toml.
OJSSCG=checkout/handler/src/main/java/io/netty/handler/ssl/util/OpenJdkSelfSignedCertGenerator.java
if [ -f "$OJSSCG" ]; then
  rm -f "$OJSSCG"
  echo "stripped OpenJdkSelfSignedCertGenerator (needs javac --add-exports; see PARITY.md)"
fi

# BC-only self-signed path (companion to OpenJdkSelfSignedCertGenerator removal)
if [ -f overlay/patches/SelfSignedCertificate.java ]; then
  cp overlay/patches/SelfSignedCertificate.java \
    checkout/handler/src/main/java/io/netty/handler/ssl/util/SelfSignedCertificate.java
  echo "applied SelfSignedCertificate BC-only patch"
fi
# Record pin for docs/CI
printf '%s\n' "$NETTY_TAG" > checkout/.jk-netty-tag
printf '%s\n' "$SHA" > checkout/.jk-netty-sha

# ChannelHandlerMetadataUtil stays under transport/src/test (upstream layout).
# Consumers declare: transport = { workspace = true, kind = "tests" } (Mill testModuleDeps /
# Maven test-jar). No synthetic nativeimage-testutil module and no source promotion.

# Optional / platform-specific tests that need classifiers or native libs (not on default Mill smoketest either).
for f in \
  checkout/handler/src/test/java/io/netty/handler/ssl/AmazonCorrettoSslEngineTest.java \
  checkout/resolver-dns/src/test/java/io/netty/resolver/dns/TestDnsServer.java \
  checkout/resolver-dns/src/test/java/io/netty/resolver/dns/DnsNameResolverTest.java \
  checkout/resolver-dns/src/test/java/io/netty/resolver/dns/SearchDomainTest.java \
    checkout/common/src/test/java/io/netty/util/internal/NativeLibraryLoaderTest.java
 do
  if [ -f "$f" ]; then
    rm -f "$f"
    echo "excluded optional test $(basename "$f")"
  fi
done

# AdaptivePoolingAllocator: JCTools MpmcArrayQueue requires capacity >= 2; 4.1.115 used
# availableProcessors() raw (fails on 1-core hosts — Netty #14579). Backport Math.max(2, …)
# from 4.1.116 so Adaptive* unit tests stay in the suite.
if [ -f overlay/patches/AdaptivePoolingAllocator.java ]; then
  cp overlay/patches/AdaptivePoolingAllocator.java \
    checkout/buffer/src/main/java/io/netty/buffer/AdaptivePoolingAllocator.java
  echo "applied AdaptivePoolingAllocator central-queue floor patch (Netty #14579)"
fi

./scripts/curate-tests.sh checkout

echo "netty ready at checkout/ (tag $NETTY_TAG, SHA $SHA, overlay + common codegen applied)"
echo "Next: cd checkout && jk lock && jk build --skip-tests"
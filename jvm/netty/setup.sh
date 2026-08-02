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
git -C checkout checkout -q "$NETTY_TAG"
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


# ChannelHandlerMetadataUtil lives under transport/src/test in upstream; Mill uses testModuleDeps.
# Promote into nativeimage-testutil main sources so sibling modules can compile *MetadataTest.
UTIL_SRC=checkout/transport/src/test/java/io/netty/nativeimage
UTIL_DST=checkout/nativeimage-testutil/src/main/java/io/netty/nativeimage
if [ -d "$UTIL_SRC" ]; then
  mkdir -p "$UTIL_DST"
  cp -a "$UTIL_SRC/." "$UTIL_DST/"
  # Drop javadoc-only import of transport's test class (breaks out-of-module compile).
  sed -i '/import io.netty.channel.NativeImageHandlerMetadataTest;/d' \
    "$UTIL_DST/ChannelHandlerMetadataUtil.java" 2>/dev/null || true
  echo "promoted ChannelHandlerMetadataUtil → nativeimage-testutil"
fi


# Optional / platform-specific tests that need classifiers or native libs (not on default Mill smoketest either).
for f in \
  checkout/handler/src/test/java/io/netty/handler/ssl/AmazonCorrettoSslEngineTest.java \
  checkout/resolver-dns/src/test/java/io/netty/resolver/dns/TestDnsServer.java \
  checkout/resolver-dns/src/test/java/io/netty/resolver/dns/DnsNameResolverTest.java \
  checkout/resolver-dns/src/test/java/io/netty/resolver/dns/SearchDomainTest.java \
    checkout/common/src/test/java/io/netty/util/internal/NativeLibraryLoaderTest.java \
  checkout/buffer/src/test/java/io/netty/buffer/AdaptiveBigEndianHeapByteBufTest.java \
  checkout/buffer/src/test/java/io/netty/buffer/AdaptiveLittleEndianHeapByteBufTest.java \
  checkout/buffer/src/test/java/io/netty/buffer/AdaptiveByteBufAllocatorTest.java
 do
  if [ -f "$f" ]; then
    rm -f "$f"
    echo "excluded optional test $(basename "$f")"
  fi
done

echo "netty ready at checkout/ (tag $NETTY_TAG, SHA $SHA, overlay + common codegen applied)"
echo "Next: cd checkout && jk lock && jk build --skip-tests"
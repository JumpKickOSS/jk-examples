#!/usr/bin/env bash
# Clone Netty at the pinned tag and overlay JumpKick manifests (Mill-style thirdparty port).
# Checkout lands in ./checkout (gitignored). Re-running refreshes the overlay + codegen.
set -euo pipefail
cd "$(dirname "$0")"

# Align with Mill's Netty case study: large 4.1 multi-module tree (~47 Maven modules).
NETTY_TAG="${NETTY_TAG:-netty-4.1.139.Final}"
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

# common/ collection templates: preferred path is .jk BEFORE_COMPILE on `jk build -m common`.
# Keep shell codegen for offline/setup-without-jk and Mill bench prep when tools already cached.
if [ "${NETTY_SKIP_SHELL_CODEGEN:-}" != "1" ]; then
  ./scripts/generate-common.sh checkout || {
    echo "shell codegen failed — will rely on .jk BEFORE_COMPILE during jk build" >&2
  }
fi

# transport-sctp: Maven excludes com/** stubs (see transport-sctp/pom.xml compiler excludes).
# Those stubs conflict with JDK module jdk.sctp on Java 9+; remove them so compile matches Maven.
if [ -d checkout/transport-sctp/src/main/java/com ]; then
  rm -rf checkout/transport-sctp/src/main/java/com
  echo "stripped transport-sctp com.sun.nio.sctp stubs (Maven compiler exclude; JDK module conflict)"
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

./scripts/curate-tests.sh checkout

echo "netty ready at checkout/ (tag $NETTY_TAG, SHA $SHA, overlay + common codegen applied)"
echo "Next: ./run.sh   (or: cd checkout && jk build --skip-tests)"
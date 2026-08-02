#!/usr/bin/env bash
# Curate Netty unit tests for a green jk build --redo on Linux/JDK 17+.
# Mill also runs a curated subset (not the full multi-hour suite). See PARITY.md.
set -euo pipefail
cd "$(dirname "$0")/.."
ROOT="${1:-checkout}"

# Modules whose tests need OpenSSL/tcnative, JNI .so, blockhound agent, OS-specific
# natives, network OCSP (hangs), or multi-hour suites.
for mod in \
  handler \
  handler-ssl-ocsp \
  transport-native-epoll \
  transport-native-kqueue \
  transport-blockhound-tests \
  transport-native-unix-common-tests \
  resolver-dns-native-macos \
  microbench \
  testsuite \
  testsuite-http2 \
  testsuite-autobahn \
  testsuite-native \
  testsuite-native-image \
  testsuite-native-image-client \
  testsuite-native-image-client-runtime-init
do
  if [ -d "$ROOT/$mod/src/test" ]; then
    rm -rf "$ROOT/$mod/src/test"
    echo "curate: drop tests for $mod (native/openssl/ocsp/network/suite)"
  fi
done

# Compression codec tests pull optional natives (brotli/zstd/lz4) with platform classifiers.
# codec-http brotli is handled separately (native-linux-x86_64 dep + isAvailable gate).
if [ -d "$ROOT/codec/src/test/java/io/netty/handler/codec/compression" ]; then
  rm -rf "$ROOT/codec/src/test/java/io/netty/handler/codec/compression"
  echo "curate: drop codec compression tests (optional natives)"
fi

# codec-http: Brotli tests use @DisabledIf(isNotSupported) that only skips macOS aarch64 —
# on Linux without the platform native jar, Brotli.ensureAvailability() still throws.
# Gate on Brotli.isAvailable() as a safety net (overlay also pulls native-linux-x86_64).
DECODER_TEST="$ROOT/codec-http/src/test/java/io/netty/handler/codec/http/HttpContentDecoderTest.java"
if [ -f "$DECODER_TEST" ]; then
  sed -i 's|return PlatformDependent.isOsx() && "aarch_64".equals(PlatformDependent.normalizedArch());|return !Brotli.isAvailable();|' \
    "$DECODER_TEST"
  echo "curate: HttpContentDecoderTest.isNotSupported → !Brotli.isAvailable()"
fi

# Individual flaky / environment-specific unit tests.
for f in \
  "$ROOT/transport/src/test/java/io/netty/bootstrap/BootstrapTest.java" \
  "$ROOT/transport/src/test/java/io/netty/bootstrap/ServerBootstrapTest.java" \
  "$ROOT/transport/src/test/java/io/netty/channel/AbstractChannelTest.java" \
  "$ROOT/common/src/test/java/io/netty/util/internal/NativeLibraryLoaderTest.java" \
  "$ROOT/handler/src/test/java/io/netty/handler/ssl/AmazonCorrettoSslEngineTest.java" \
  "$ROOT/resolver-dns/src/test/java/io/netty/resolver/dns/TestDnsServer.java" \
  "$ROOT/resolver-dns/src/test/java/io/netty/resolver/dns/DnsNameResolverTest.java" \
  "$ROOT/resolver-dns/src/test/java/io/netty/resolver/dns/SearchDomainTest.java"
do
  [ -f "$f" ] && rm -f "$f" && echo "curate: drop $(basename "$f")"
done

# Native-image metadata tests (need test-jar util + generate-and-compare resources).
find "$ROOT" -path '*/src/test/java/*' -name 'NativeImage*.java' -delete 2>/dev/null || true
find "$ROOT" -path '*/src/test/java/*' -name '*IntegrationTest.java' -delete 2>/dev/null || true
# ChannelHandlerMetadataUtil under transport/test imports NativeImageHandlerMetadataTest (deleted).
# setup.sh promotes a clean copy to nativeimage-testutil; ensure no stale test copy remains.
rm -rf "$ROOT/transport/src/test/java/io/netty/nativeimage" 2>/dev/null || true
echo "curate: drop NativeImage* / *IntegrationTest / transport nativeimage util"

echo "curate-tests done"

# Module map — Maven ↔ JumpKick

Pinned: **netty-4.1.115.Final**. Graph aligned with Mill `example/thirdparty/netty/build.mill`.

| Maven `artifactId` | Directory | jk `[project].name` | Notes |
|--------------------|-----------|---------------------|-------|
| `netty-buffer` | `buffer/` | `buffer` | workspace member |
| `netty-codec` | `codec/` | `codec` | workspace member |
| `netty-codec-dns` | `codec-dns/` | `codec-dns` | workspace member |
| `netty-codec-haproxy` | `codec-haproxy/` | `codec-haproxy` | workspace member |
| `netty-codec-http` | `codec-http/` | `codec-http` | workspace member |
| `netty-codec-http2` | `codec-http2/` | `codec-http2` | workspace member |
| `netty-codec-memcache` | `codec-memcache/` | `codec-memcache` | workspace member |
| `netty-codec-mqtt` | `codec-mqtt/` | `codec-mqtt` | workspace member |
| `netty-codec-redis` | `codec-redis/` | `codec-redis` | workspace member |
| `netty-codec-smtp` | `codec-smtp/` | `codec-smtp` | workspace member |
| `netty-codec-socks` | `codec-socks/` | `codec-socks` | workspace member |
| `netty-codec-stomp` | `codec-stomp/` | `codec-stomp` | workspace member |
| `netty-codec-xml` | `codec-xml/` | `codec-xml` | workspace member |
| `netty-common` | `common/` | `common` | workspace member |
| `netty-example` | `example/` | `example` | workspace member |
| `netty-handler` | `handler/` | `handler` | workspace member |
| `netty-handler-proxy` | `handler-proxy/` | `handler-proxy` | workspace member |
| `netty-handler-ssl-ocsp` | `handler-ssl-ocsp/` | `handler-ssl-ocsp` | workspace member |
| `netty-microbench` | `microbench/` | `microbench` | workspace member |
| `netty-resolver` | `resolver/` | `resolver` | workspace member |
| `netty-resolver-dns` | `resolver-dns/` | `resolver-dns` | workspace member |
| `netty-resolver-dns-classes-macos` | `resolver-dns-classes-macos/` | `resolver-dns-classes-macos` | workspace member |
| `netty-resolver-dns-native-macos` | `resolver-dns-native-macos/` | `resolver-dns-native-macos` | workspace member |
| `netty-testsuite` | `testsuite/` | `testsuite` | workspace member |
| `netty-testsuite-autobahn` | `testsuite-autobahn/` | `testsuite-autobahn` | workspace member |
| `netty-testsuite-http2` | `testsuite-http2/` | `testsuite-http2` | workspace member |
| `netty-testsuite-native-image` | `testsuite-native-image/` | `testsuite-native-image` | workspace member |
| `netty-testsuite-native-image-client` | `testsuite-native-image-client/` | `testsuite-native-image-client` | workspace member |
| `netty-testsuite-native-image-client-runtime-init` | `testsuite-native-image-client-runtime-init/` | `testsuite-native-image-client-runtime-init` | workspace member |
| `netty-transport` | `transport/` | `transport` | workspace member |
| `netty-transport-blockhound-tests` | `transport-blockhound-tests/` | `transport-blockhound-tests` | workspace member |
| `netty-transport-classes-epoll` | `transport-classes-epoll/` | `transport-classes-epoll` | workspace member |
| `netty-transport-classes-kqueue` | `transport-classes-kqueue/` | `transport-classes-kqueue` | workspace member |
| `netty-transport-native-epoll` | `transport-native-epoll/` | `transport-native-epoll` | workspace member |
| `netty-transport-native-kqueue` | `transport-native-kqueue/` | `transport-native-kqueue` | workspace member |
| `netty-transport-native-unix-common` | `transport-native-unix-common/` | `transport-native-unix-common` | workspace member |
| `netty-transport-native-unix-common-tests` | `transport-native-unix-common-tests/` | `transport-native-unix-common-tests` | workspace member |
| `netty-transport-rxtx` | `transport-rxtx/` | `transport-rxtx` | workspace member |
| `netty-transport-sctp` | `transport-sctp/` | `transport-sctp` | workspace member |
| `netty-transport-udt` | `transport-udt/` | `transport-udt` | workspace member |

| `netty-parent` | (repo root) | `netty-parent` | workspace root only |

Not modelled as compile members (no Java main surface / packaging-only): `bom`, `all` (aggregator), `dev-tools`, `docker`, `license`.

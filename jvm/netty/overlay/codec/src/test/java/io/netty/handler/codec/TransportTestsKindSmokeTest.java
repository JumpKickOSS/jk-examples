/*
 * JumpKick dogfood: prove workspace kind = "tests" puts transport's test helpers
 * (ChannelHandlerMetadataUtil) on this module's test classpath without promoting
 * those sources to a synthetic main module.
 */
package io.netty.handler.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.netty.nativeimage.ChannelHandlerMetadataUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("kind-smoke")
public class TransportTestsKindSmokeTest {

    @Test
    void transport_tests_kind_is_on_the_test_classpath() {
        // Load the class from transport's test product (kind = "tests").
        assertNotNull(ChannelHandlerMetadataUtil.class);
        assertEquals("io.netty.nativeimage.ChannelHandlerMetadataUtil",
                ChannelHandlerMetadataUtil.class.getName());
    }
}

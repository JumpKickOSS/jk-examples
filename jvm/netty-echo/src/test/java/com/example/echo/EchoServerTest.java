package com.example.echo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class EchoServerTest {
    @Test
    void handlerWritesBackWhatItReads() {
        EmbeddedChannel channel = new EmbeddedChannel(new EchoServerHandler());

        channel.writeInbound(Unpooled.copiedBuffer("hello, netty", StandardCharsets.UTF_8));

        ByteBuf echoed = channel.readOutbound();
        try {
            assertEquals("hello, netty", echoed.toString(StandardCharsets.UTF_8));
        } finally {
            echoed.release();
        }
        assertNull(channel.readOutbound());
        channel.finishAndReleaseAll();
    }
}

package dev.jkexamples.links.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.jkexamples.links.store.MemoryLinkStore;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void add_then_open() {
        var store = new MemoryLinkStore(Clock.systemUTC());
        var out = new ByteArrayOutputStream();
        var print = new PrintStream(out, true, StandardCharsets.UTF_8);
        assertEquals(0, Main.run(store, new String[] {"add", "https://jumpkick.build"}, print, System.err));
        String code = out.toString(StandardCharsets.UTF_8).strip();
        out.reset();
        assertEquals(0, Main.run(store, new String[] {"open", code}, print, System.err));
        assertEquals("https://jumpkick.build", out.toString(StandardCharsets.UTF_8).strip());
    }

    @Test
    void bad_usage_is_exit_2() {
        assertEquals(2, Main.run(new MemoryLinkStore(Clock.systemUTC()), new String[0], System.out, System.err));
    }
}

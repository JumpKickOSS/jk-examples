package dev.jkexamples.links.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Many writers on one file: slower than a unit test, so it runs in the integration tier. */
@Tag("integration")
class FileLinkStoreConcurrencyTest {

    @Test
    void concurrent_adds_write_one_line_each(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("links.tsv");
        var store = new FileLinkStore(file, Clock.systemUTC());
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 2_000).forEach(i -> pool.submit(() -> store.add(URI.create("https://example.com/" + i))));
        }
        assertEquals(2_000, Files.readAllLines(file).size());
        assertEquals(2_000, new FileLinkStore(file, Clock.systemUTC()).all().size());
    }
}

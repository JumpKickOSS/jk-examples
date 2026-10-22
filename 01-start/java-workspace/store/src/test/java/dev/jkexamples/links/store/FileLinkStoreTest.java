package dev.jkexamples.links.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.jkexamples.links.LinkStore;
import dev.jkexamples.links.LinkStoreContract;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileLinkStoreTest extends LinkStoreContract {

    @TempDir
    Path dir;

    @Override
    protected LinkStore newStore() throws Exception {
        return new FileLinkStore(Files.createTempFile(dir, "links", ".tsv"), Clock.systemUTC());
    }

    @Test
    void links_survive_a_reopen() throws Exception {
        Path file = dir.resolve("links.tsv");
        var first = new FileLinkStore(file, Clock.systemUTC()).add(URI.create("https://example.com/kept"));
        assertEquals(first, new FileLinkStore(file, Clock.systemUTC()).find(first.code()).orElseThrow());
    }
}

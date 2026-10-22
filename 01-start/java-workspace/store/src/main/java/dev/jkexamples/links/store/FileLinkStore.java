package dev.jkexamples.links.store;

import dev.jkexamples.links.ShortLink;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.Instant;

/** A store backed by a tab-separated file ({@code code, created, url} per line), appended on add. */
public final class FileLinkStore extends MemoryLinkStore {

    private final Path file;

    public FileLinkStore(Path file, Clock clock) throws IOException {
        super(clock);
        this.file = file;
        if (Files.exists(file)) {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] cells = line.split("\t", 3);
                if (cells.length == 3) remember(new ShortLink(cells[0], URI.create(cells[2]), Instant.parse(cells[1])));
            }
        }
    }

    @Override
    public synchronized ShortLink add(URI target) {
        int before = all().size();
        ShortLink link = super.add(target);
        if (all().size() > before) {
            try {
                Files.writeString(
                        file,
                        link.code() + "\t" + link.created() + "\t" + link.target() + "\n",
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return link;
    }
}

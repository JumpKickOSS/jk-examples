package dev.jkexamples.links.store;

import dev.jkexamples.links.Codes;
import dev.jkexamples.links.LinkStore;
import dev.jkexamples.links.ShortLink;
import java.net.URI;
import java.time.Clock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** A store that forgets everything on exit. Thread-safe. */
public class MemoryLinkStore implements LinkStore {

    private final Map<String, ShortLink> byCode = new LinkedHashMap<>();
    private final Map<URI, ShortLink> byTarget = new LinkedHashMap<>();
    private final Clock clock;

    public MemoryLinkStore(Clock clock) {
        this.clock = clock;
    }

    @Override
    public synchronized ShortLink add(URI target) {
        ShortLink existing = byTarget.get(target);
        if (existing != null) return existing;
        ShortLink link = new ShortLink(Codes.encode(byCode.size() + 1L), target, clock.instant());
        remember(link);
        return link;
    }

    @Override
    public synchronized Optional<ShortLink> find(String code) {
        return Optional.ofNullable(byCode.get(code));
    }

    @Override
    public synchronized List<ShortLink> all() {
        return List.copyOf(new ArrayList<>(byCode.values()));
    }

    /** Load a link read back from elsewhere, keeping its code. */
    protected synchronized void remember(ShortLink link) {
        byCode.put(link.code(), link);
        byTarget.put(link.target(), link);
    }
}

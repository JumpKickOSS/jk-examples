package dev.jkexamples.links;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/** Where short links live. Adding the same URL twice returns the first link. */
public interface LinkStore {

    ShortLink add(URI target);

    Optional<ShortLink> find(String code);

    /** Every link, oldest first. */
    List<ShortLink> all();
}

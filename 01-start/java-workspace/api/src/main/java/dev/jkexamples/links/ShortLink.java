package dev.jkexamples.links;

import java.net.URI;
import java.time.Instant;

/** A code and the URL it opens. */
public record ShortLink(String code, URI target, Instant created) {

    public ShortLink {
        if (!Codes.isValid(code)) throw new IllegalArgumentException("not a short code: " + code);
        if (!target.isAbsolute()) throw new IllegalArgumentException("not an absolute URL: " + target);
    }
}

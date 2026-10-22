package dev.jkexamples.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import org.junit.jupiter.api.Test;

/**
 * What every {@link LinkStore} must do. An implementation's test extends this and supplies a fresh
 * store; it reaches this class through {@code api = { workspace = true, kind = "tests" }}.
 */
public abstract class LinkStoreContract {

    protected abstract LinkStore newStore() throws Exception;

    @Test
    void a_new_link_can_be_found_by_its_code() throws Exception {
        LinkStore store = newStore();
        ShortLink link = store.add(URI.create("https://example.com/a"));
        assertEquals(link, store.find(link.code()).orElseThrow());
    }

    @Test
    void the_same_url_gets_the_same_code_and_different_urls_differ() throws Exception {
        LinkStore store = newStore();
        ShortLink a = store.add(URI.create("https://example.com/a"));
        assertEquals(a.code(), store.add(URI.create("https://example.com/a")).code());
        assertNotEquals(a.code(), store.add(URI.create("https://example.com/b")).code());
        assertEquals(2, store.all().size());
    }

    @Test
    void an_unknown_code_is_empty() throws Exception {
        assertTrue(newStore().find("zz9").isEmpty());
    }
}

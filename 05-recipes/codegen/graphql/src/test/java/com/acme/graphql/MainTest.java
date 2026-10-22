package com.acme.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acme.graphql.types.Show;
import org.junit.jupiter.api.Test;

/** The generated type and client compile and behave as the schema says. */
class MainTest {

    @Test
    void the_generated_type_carries_the_schema_fields() {
        Show show = Show.newBuilder().title("Dark").releaseYear(2017).build();
        assertEquals("Dark", show.getTitle());
        assertEquals(2017, show.getReleaseYear());
    }

    @Test
    void the_generated_client_serializes_the_query() {
        String document = Main.showsQuery("Dark");
        assertTrue(document.contains("shows"), document);
        assertTrue(document.contains("titleFilter"), document);
        assertTrue(document.contains("releaseYear"), document);
    }
}

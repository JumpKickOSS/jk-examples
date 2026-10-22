package dev.jkexamples.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class CodesTest {

    @Test
    void round_trips() {
        for (long n : new long[] {0, 1, 61, 62, 3843, 3844, Long.MAX_VALUE}) {
            assertEquals(n, Codes.decode(Codes.encode(n)));
        }
        assertEquals("10", Codes.encode(62));
    }

    @Test
    void rejects_foreign_characters() {
        assertFalse(Codes.isValid("ab-c"));
        assertFalse(Codes.isValid(""));
    }
}

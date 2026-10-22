package dev.jkexamples.csvjson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void rows_become_objects_keyed_by_the_header() throws Exception {
        String json = Main.convert(new StringReader("name, nights\nAda,3\nBruno,\n"));
        assertEquals("""
                [ {
                  "name" : "Ada",
                  "nights" : "3"
                }, {
                  "name" : "Bruno",
                  "nights" : ""
                } ]""", json);
    }

    @Test
    void the_fixture_reads_through_commons_io() throws Exception {
        String csv = IOUtils.toString(MainTest.class.getResourceAsStream("/guests.csv"), StandardCharsets.UTF_8);
        assertEquals(3, csv.lines().count());
    }
}

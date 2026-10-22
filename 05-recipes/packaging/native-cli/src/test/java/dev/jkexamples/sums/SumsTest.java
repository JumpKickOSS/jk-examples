package dev.jkexamples.sums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

class SumsTest {

    @TempDir
    Path dir;

    private record Run(int exit, String out) {}

    private Run run(String... args) {
        StringWriter out = new StringWriter();
        int exit = new CommandLine(new Sums()).setOut(new PrintWriter(out)).setErr(new PrintWriter(new StringWriter())).execute(args);
        return new Run(exit, out.toString());
    }

    @Test
    void prints_sha256_like_sha256sum() throws Exception {
        Path f = Files.writeString(dir.resolve("hello.txt"), "hello\n");
        assertEquals("5891b5b522d5df086d0ff0b110fbd9d21bb4fc7163af34d08286a2e846f6be03  " + f + "\n", run(f.toString()).out());
    }

    @Test
    void a_check_list_passes_until_a_file_changes() throws Exception {
        Path f = Files.writeString(dir.resolve("a.txt"), "a");
        Path list = Files.writeString(dir.resolve("SUMS"), run(f.toString()).out());
        assertEquals(0, run("-c", list.toString()).exit());
        Files.writeString(f, "changed");
        Run after = run("-c", list.toString());
        assertEquals(1, after.exit());
        assertTrue(after.out().endsWith("FAILED\n"));
    }

    @Test
    void no_files_is_a_usage_error() {
        assertEquals(2, run().exit());
    }
}

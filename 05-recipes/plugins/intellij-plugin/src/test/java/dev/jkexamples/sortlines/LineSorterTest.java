package dev.jkexamples.sortlines;

import com.intellij.testFramework.UsefulTestCase;

/** The text rule alone; no editor needed, but it runs in the same IDE test JVM as the rest. */
public class LineSorterTest extends UsefulTestCase {

    public void testCaseInsensitiveAndKeepsTheTrailingNewline() {
        assertEquals("a\nB\nc\n", LineSorter.sort("c\nB\na\n"));
        assertEquals("a\nb", LineSorter.sort("b\na"));
    }
}

package dev.jkexamples.sortlines;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/** The action inside a headless IDE: a real editor, the registered action, an undoable edit. */
public class SortLinesActionTest extends BasePlatformTestCase {

    public void testSortsTheWholeFileWithoutASelection() {
        myFixture.configureByText("names.txt", "pear\nApple\nbanana\n");
        myFixture.performEditorAction("dev.jkexamples.SortLines");
        myFixture.checkResult("Apple\nbanana\npear\n");
    }

    public void testSortsOnlyTheSelectedLines() {
        myFixture.configureByText("names.txt", "zebra\n<selection>pear\nApple</selection>\nant\n");
        myFixture.performEditorAction("dev.jkexamples.SortLines");
        myFixture.checkResult("zebra\nApple\npear\nant\n");
    }
}

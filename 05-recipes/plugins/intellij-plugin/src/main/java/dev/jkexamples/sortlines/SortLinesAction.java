package dev.jkexamples.sortlines;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.jetbrains.annotations.NotNull;

/** Edit ▸ Sort Lines: the selected lines (whole lines), or the whole document. One undo step. */
public final class SortLinesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        Document document = editor.getDocument();
        SelectionModel selection = editor.getSelectionModel();
        int start = 0;
        int end = document.getTextLength();
        if (selection.hasSelection()) {
            start = document.getLineStartOffset(document.getLineNumber(selection.getSelectionStart()));
            end = document.getLineEndOffset(document.getLineNumber(selection.getSelectionEnd()));
        }
        int from = start;
        int to = end;
        String sorted = LineSorter.sort(document.getText().substring(from, to));
        WriteCommandAction.runWriteCommandAction(
                e.getProject(), "Sort Lines", null, () -> document.replaceString(from, to, sorted));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getData(CommonDataKeys.EDITOR) != null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}


/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Michael A Updike
 * Copyright (c) 2013 Hugo Campos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.weebly.opus1269.smoothscroller;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileEditorListener implements FileEditorManagerListener {
    private final Map<FileEditor, SmoothScrollerMouseWheelListener> mListeners = new HashMap<FileEditor, SmoothScrollerMouseWheelListener>();

    @Override
    public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
        Set<FileEditor> destroyedEditors = new HashSet<FileEditor>(mListeners.keySet());

        // Remove all the editors that are still active from the set of destroyed editors.
        for (FileEditor editor : fileEditorManager.getAllEditors()) {
            if (destroyedEditors.contains(editor)) {
                destroyedEditors.remove(editor);
            }
        }

        // Remove the wheel listener from all the destroyed editors.
        for (FileEditor fileEditor : destroyedEditors) {
            SmoothScrollerMouseWheelListener listener = mListeners.get(fileEditor);

            if (listener != null) {
                listener.stopAnimating();

                Editor editor = ((TextEditor) fileEditor).getEditor();
                editor.getContentComponent().removeMouseWheelListener(listener);
            }
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
        FileEditor oldEditor = fileEditorManagerEvent.getOldEditor();
        FileEditor newEditor = fileEditorManagerEvent.getNewEditor();

        // stop animating old editor
        SmoothScrollerMouseWheelListener listener = mListeners.get(oldEditor);
        if (listener != null) {
            listener.stopAnimating();
        }

        // start animating new editor, creating listener if needed
        if (newEditor instanceof TextEditor) {
            listener = mListeners.get(newEditor);
            if (listener == null) {
                listener = new SmoothScrollerMouseWheelListener(newEditor);
                mListeners.put(newEditor, listener);

                Editor editor = ((TextEditor) newEditor).getEditor();
                editor.getContentComponent().addMouseWheelListener(listener);
            }

            listener.startAnimating();
        }
    }
}

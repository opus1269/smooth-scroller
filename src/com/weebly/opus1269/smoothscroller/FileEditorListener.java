
/*
 * Copyright 2016 Michael A Updike
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.weebly.opus1269.smoothscroller;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FileEditorListener implements FileEditorManagerListener {
    private final Map<FileEditor, SmoothScrollerMouseWheelListener> _listeners =
            new HashMap<FileEditor, SmoothScrollerMouseWheelListener>();

    public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
        FileEditor[] editors = fileEditorManager.getAllEditors();

        // Add a wheel listener to all text editors.
        for (FileEditor fileEditor : editors) {
            if (fileEditor instanceof TextEditor) {
                SmoothScrollerMouseWheelListener listener = new SmoothScrollerMouseWheelListener(fileEditor);
                _listeners.put(fileEditor, listener);

                Editor editor = ((TextEditor) fileEditor).getEditor();
                editor.getContentComponent().addMouseWheelListener(listener);

                listener.startAnimating();
            }
        }
    }

    public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
        Set<FileEditor> destroyedEditors = new HashSet<FileEditor>(_listeners.keySet());

        // Remove all the editors that are still active from the set of destroyed editors.
        for (FileEditor editor : fileEditorManager.getAllEditors()) {
            if (destroyedEditors.contains(editor)) {
                destroyedEditors.remove(editor);
            }
        }

        // Remove the wheel listener from all the destroyed editors.
        for (FileEditor fileEditor : destroyedEditors) {
            SmoothScrollerMouseWheelListener listener = _listeners.get(fileEditor);

            if (listener != null) {
                listener.stopAnimating();

                Editor editor = ((TextEditor) fileEditor).getEditor();
                editor.getContentComponent().removeMouseWheelListener(listener);
            }
        }
    }

    public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
    }
}

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

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Top level component for the plugin
 */
public class SmoothScrollerPlugin extends AbstractProjectComponent {
    private final Project mProject;
    //private final Logger mLogger = Logger.getInstance(getClass());

    public SmoothScrollerPlugin(Project project) {
        super(project);

        mProject = project;

        // Initialize scroll parameters
        OptionsAction.loadValues();

    }

    @Override
    public void initComponent() {
        mProject.getMessageBus()
                .connect()
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorListener());

        //mLogger.debug("SmoothScroller initialized");
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "SmoothScrollerProjectComponent";
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }
}
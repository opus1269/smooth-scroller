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

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;

/**
 * Created by mike on 3/7/2016.
 */
public class OptionsAction extends AnAction {

    private static final String THRESHOLD = "SmoothScrollerThreshold";
    private static final String FRIC_CONST = "SmoothScrollerFricConst";
    private static final String FRIC_EXP = "SmoothScrollerFricExp";

    private OptionsForm mOptionsForm = new OptionsForm();

    private static int mThreshold = 50;
    private static int mFricConst = 50;
    private static int mFricExp = 50;

    @Override
    public void actionPerformed(AnActionEvent e) {
        loadValues();

        showDialog();
    }

    private boolean showDialog() {
        DialogBuilder builder = new DialogBuilder();
        builder.setCenterPanel(mOptionsForm.getRoot());
        //builder.setDimensionServiceKey("GrepConsoleSound");
        builder.setTitle("Smooth Scroller Options");
        builder.removeAllActions();
        builder.addOkAction();
        builder.addCancelAction();

        mOptionsForm.setData(this);

        boolean isOk = builder.show() == DialogWrapper.OK_EXIT_CODE;
        if (isOk) {
            if (mOptionsForm.isModified(this)) {
                mOptionsForm.getData(this);
                saveValues();
            }
        }

        return isOk;
    }

    public static void loadValues() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        final float speedThreshold = props.getFloat(THRESHOLD, SmoothScrollerMouseWheelListener.DEF_SPEED_THESHOLD);
        final float fricConst = props.getFloat(FRIC_CONST, SmoothScrollerMouseWheelListener.DEF_FRIC_CONST);
        final float fricExp = props.getFloat(FRIC_EXP, SmoothScrollerMouseWheelListener.DEF_FRIC_EXP);

        mThreshold = Math.round(100 * speedThreshold / SmoothScrollerMouseWheelListener.MAX_SPEED_THESHOLD);
        mFricConst = Math.round(100 * fricConst / SmoothScrollerMouseWheelListener.MAX_FRIC_CONST);
        mFricExp =   Math.round(100 * fricExp / SmoothScrollerMouseWheelListener.MAX_FRIC_EXP);

        SmoothScrollerMouseWheelListener.setSpeedThreshold(speedThreshold);
        SmoothScrollerMouseWheelListener.setFricConst(fricConst);
        SmoothScrollerMouseWheelListener.setFricExp(fricExp);
    }

    private void saveValues() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        final float speedThreshold = (mThreshold / 100.0F) * SmoothScrollerMouseWheelListener.MAX_SPEED_THESHOLD;
        final float fricConst = (mFricConst / 100.0F) * SmoothScrollerMouseWheelListener.MAX_FRIC_CONST;
        final float fricExp = (mFricExp / 100.0F) * SmoothScrollerMouseWheelListener.MAX_FRIC_EXP;

        props.setValue(THRESHOLD, speedThreshold, SmoothScrollerMouseWheelListener.DEF_SPEED_THESHOLD);
        props.setValue(FRIC_CONST, fricConst, SmoothScrollerMouseWheelListener.DEF_FRIC_CONST);
        props.setValue(FRIC_EXP, fricExp, SmoothScrollerMouseWheelListener.DEF_FRIC_EXP);

        SmoothScrollerMouseWheelListener.setSpeedThreshold(speedThreshold);
        SmoothScrollerMouseWheelListener.setFricConst(fricConst);
        SmoothScrollerMouseWheelListener.setFricExp(fricExp);
    }

    public int getThreshold() {
        return mThreshold;
    }

    public void setThreshold(int mThreshold) {
        this.mThreshold = mThreshold;
    }

    public int getFricConst() {
        return mFricConst;
    }

    public void setFricConst(int mFricConst) {
        this.mFricConst = mFricConst;
    }

    public int getFricExp() {
        return mFricExp;
    }

    public void setFricExp(int mFricExp) {
        this.mFricExp = mFricExp;
    }

}

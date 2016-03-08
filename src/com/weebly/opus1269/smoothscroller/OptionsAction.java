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
 * Action to allow users to change the scroll settings
 */
public class OptionsAction extends AnAction {

    private static final String THRESHOLD = "SmoothScrollerThreshold";
    private static final String FRIC_CONST = "SmoothScrollerFricConst";
    private static final String FRIC_EXP = "SmoothScrollerFricExp";

    private OptionsForm mOptionsForm;

    private static int mThreshold = 50;
    private static int mFricConst = 50;
    private static int mFricExp = 50;

    private static float mThresholdVal = SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD;
    private static float mFricConstVal = SmoothScrollerMouseWheelListener.DEF_FRIC_CONST;
    private static float mFricExpVal = SmoothScrollerMouseWheelListener.DEF_FRIC_EXP;

    @Override
    public void actionPerformed(AnActionEvent e) {
        loadValues();

        showDialog();
    }

    private boolean showDialog() {
//        DialogBuilder builder = new DialogBuilder();
//        builder.setCenterPanel(mOptionsForm.getRoot());
//        //builder.setDimensionServiceKey("GrepConsoleSound");
//        builder.setTitle("Smooth Scroller Options");
//        builder.removeAllActions();
//        builder.addOkAction();
//        builder.addCancelAction();

        OptionsDialog dialog = new OptionsDialog();
        mOptionsForm = dialog.getOptionsForm();
        mOptionsForm.setData(this);
        dialog.show();

        boolean isOk = dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE;
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

        mThresholdVal = props.getFloat(THRESHOLD, SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD);
        mFricConstVal = props.getFloat(FRIC_CONST, SmoothScrollerMouseWheelListener.DEF_FRIC_CONST);
        mFricExpVal = props.getFloat(FRIC_EXP, SmoothScrollerMouseWheelListener.DEF_FRIC_EXP);

        mThreshold = Math.round(100 * mThresholdVal / SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD);
        mFricConst = Math.round(100 * mFricConstVal / SmoothScrollerMouseWheelListener.MAX_FRIC_CONST);
        mFricExp =   Math.round(100 * mFricExpVal / SmoothScrollerMouseWheelListener.MAX_FRIC_EXP);

        SmoothScrollerMouseWheelListener.setSpeedThreshold(mThresholdVal);
        SmoothScrollerMouseWheelListener.setFricConst(mFricConstVal);
        SmoothScrollerMouseWheelListener.setFricExp(mFricExpVal);
    }

    private static void saveValues() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        mThresholdVal = (mThreshold / 100.0F) * SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD;
        mFricConstVal = (mFricConst / 100.0F) * SmoothScrollerMouseWheelListener.MAX_FRIC_CONST;
        mFricExpVal = (mFricExp / 100.0F) * SmoothScrollerMouseWheelListener.MAX_FRIC_EXP;

        props.setValue(THRESHOLD, String.valueOf(mThresholdVal));
        props.setValue(FRIC_CONST, String.valueOf(mFricConstVal));
        props.setValue(FRIC_EXP, String.valueOf(mFricExpVal));

        SmoothScrollerMouseWheelListener.setSpeedThreshold(mThresholdVal);
        SmoothScrollerMouseWheelListener.setFricConst(mFricConstVal);
        SmoothScrollerMouseWheelListener.setFricExp(mFricExpVal);
    }

    public static void resetDefaults() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        mThresholdVal = SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD;
        mFricConstVal = SmoothScrollerMouseWheelListener.DEF_FRIC_CONST;
        mFricExpVal = SmoothScrollerMouseWheelListener.DEF_FRIC_EXP;

        props.setValue(THRESHOLD, String.valueOf(mThresholdVal));
        props.setValue(FRIC_CONST, String.valueOf(mFricConstVal));
        props.setValue(FRIC_EXP, String.valueOf(mFricExpVal));

        SmoothScrollerMouseWheelListener.setSpeedThreshold(mThresholdVal);
        SmoothScrollerMouseWheelListener.setFricConst(mFricConstVal);
        SmoothScrollerMouseWheelListener.setFricExp(mFricExpVal);

        mThreshold = Math.round(100 * mThresholdVal / SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD);
        mFricConst = Math.round(100 * mFricConstVal / SmoothScrollerMouseWheelListener.MAX_FRIC_CONST);
        mFricExp =   Math.round(100 * mFricExpVal / SmoothScrollerMouseWheelListener.MAX_FRIC_EXP);
    }

    public static int getThreshold() {
        return mThreshold;
    }

    public void setThreshold(int mThreshold) {
        OptionsAction.mThreshold = mThreshold;
    }

    public static int getFricConst() {
        return mFricConst;
    }

    public void setFricConst(int mFricConst) {
        OptionsAction.mFricConst = mFricConst;
    }

    public static int getFricExp() {
        return mFricExp;
    }

    public void setFricExp(int mFricExp) {
        OptionsAction.mFricExp = mFricExp;
    }

}

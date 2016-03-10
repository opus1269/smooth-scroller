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
import com.intellij.openapi.ui.DialogWrapper;

/**
 * Action to allow users to change the scroll settings
 */
public class OptionsAction extends AnAction {

    private static final String THRESHOLD = "SmoothScrollerThreshold";
    private static final String SPEED_LMT = "SmoothScrollerSpeedLmt";
    private static final String ACC_LMT = "SmoothScrollerAccLmt";
    private static final String FRIC_EXP = "SmoothScrollerFricExp";

    private static int sThreshold = 50;
    private static int sSpeedLmt = 50;
    private static int sAccLmt = 50;
    private static int sFricExp = 50;

    private static float sThresholdVal = SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD;
    private static float sSpeedLmtVal = SmoothScrollerMouseWheelListener.DEF_SPEED_LMT;
    private static float sAccLmtVal = SmoothScrollerMouseWheelListener.DEF_ACC_LMT;
    private static float sFricExpVal = SmoothScrollerMouseWheelListener.DEF_FRIC_EXP;

    @Override
    public void actionPerformed(AnActionEvent e) {
        loadValues();

        showDialog();
    }

    private boolean showDialog() {
        OptionsDialog dialog = new OptionsDialog();
        OptionsForm optionsForm = dialog.getOptionsForm();
        optionsForm.setData();
        dialog.show();

        boolean isOk = dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE;
        if (isOk) {
            if (optionsForm.isModified()) {
                optionsForm.getData();
                saveValues();
            }
        }

        return isOk;
    }

    public static void loadValues() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        sThresholdVal = props.getFloat(THRESHOLD, SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD);
        sSpeedLmtVal = props.getFloat(SPEED_LMT, SmoothScrollerMouseWheelListener.DEF_SPEED_LMT);
        sAccLmtVal = props.getFloat(ACC_LMT, SmoothScrollerMouseWheelListener.DEF_ACC_LMT);
        sFricExpVal = props.getFloat(FRIC_EXP, SmoothScrollerMouseWheelListener.DEF_FRIC_EXP);

        sThreshold = Math.round(100.0F * sThresholdVal / SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD);
        sSpeedLmt = Math.round(100.0F * sSpeedLmtVal / SmoothScrollerMouseWheelListener.MAX_SPEED_LMT);
        sAccLmt = Math.round(100.0F * sAccLmtVal / SmoothScrollerMouseWheelListener.MAX_ACC_LMT);
        sFricExp =   Math.round(100.0F * sFricExpVal / SmoothScrollerMouseWheelListener.MAX_FRIC_EXP);

        SmoothScrollerMouseWheelListener.setSpeedThreshold(sThresholdVal);
        SmoothScrollerMouseWheelListener.setSpeedLmt(sSpeedLmtVal);
        SmoothScrollerMouseWheelListener.setAccLmt(sAccLmtVal);
        SmoothScrollerMouseWheelListener.setFricExp(sFricExpVal);
    }

    private static void saveValues() {
        sThresholdVal = (sThreshold / 100.0F) * SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD;
        sSpeedLmtVal = (sSpeedLmt / 100.0F) * SmoothScrollerMouseWheelListener.MAX_SPEED_LMT;
        sAccLmtVal = (sAccLmt / 100.0F) * SmoothScrollerMouseWheelListener.MAX_ACC_LMT;
        sFricExpVal = (sFricExp / 100.0F) * SmoothScrollerMouseWheelListener.MAX_FRIC_EXP;

        storeProperties();
    }

    public static void resetDefaults() {
        sThresholdVal = SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD;
        sSpeedLmtVal = SmoothScrollerMouseWheelListener.DEF_SPEED_LMT;
        sAccLmtVal = SmoothScrollerMouseWheelListener.DEF_ACC_LMT;
        sFricExpVal = SmoothScrollerMouseWheelListener.DEF_FRIC_EXP;

        storeProperties();

        sThreshold = Math.round(100.0F * sThresholdVal / SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD);
        sSpeedLmt = Math.round(100.0F * sSpeedLmtVal / SmoothScrollerMouseWheelListener.MAX_SPEED_LMT);
        sAccLmt = Math.round(100.0F * sAccLmtVal / SmoothScrollerMouseWheelListener.MAX_ACC_LMT);
        sFricExp = Math.round(100.0F * sFricExpVal / SmoothScrollerMouseWheelListener.MAX_FRIC_EXP);
    }

    private static void storeProperties() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        props.setValue(THRESHOLD, String.valueOf(sThresholdVal));
        props.setValue(SPEED_LMT, String.valueOf(sSpeedLmtVal));
        props.setValue(ACC_LMT, String.valueOf(sAccLmtVal));
        props.setValue(FRIC_EXP, String.valueOf(sFricExpVal));

        SmoothScrollerMouseWheelListener.setSpeedThreshold(sThresholdVal);
        SmoothScrollerMouseWheelListener.setSpeedLmt(sSpeedLmtVal);
        SmoothScrollerMouseWheelListener.setAccLmt(sAccLmtVal);
        SmoothScrollerMouseWheelListener.setFricExp(sFricExpVal);
    }

    public static int getThreshold() {
        return sThreshold;
    }

    public static void setThreshold(int threshold) {
        sThreshold = threshold;
    }

    public static int getSpeedLmt() {
        return sSpeedLmt;
    }

    public static void setSpeedLmt(int speedLmt) {
        sSpeedLmt = speedLmt;
    }

    public static int getAccLmt() {
        return sAccLmt;
    }

    public static void setAccLmt(int accLmt) {
        sAccLmt = accLmt;
    }

    public static int getFricExp() {
        return sFricExp;
    }

    public static void setFricExp(int fricExp) {
        sFricExp = fricExp;
    }
}

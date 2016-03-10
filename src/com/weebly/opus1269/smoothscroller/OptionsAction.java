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
    private static final String FRIC = "SmoothScrollerFric";

    private static int sThreshold = 50;
    private static int sSpeedLmt = 50;
    private static int sAccLmt = 50;
    private static int sFric = 50;

    private static float sThresholdVal = SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD;
    private static float sSpeedLmtVal = SmoothScrollerMouseWheelListener.DEF_SPEED_LMT;
    private static float sAccLmtVal = SmoothScrollerMouseWheelListener.DEF_ACC_LMT;
    private static float sFricVal = SmoothScrollerMouseWheelListener.DEF_FRIC;

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
        sFricVal = props.getFloat(FRIC, SmoothScrollerMouseWheelListener.DEF_FRIC);

        sThreshold = Math.round(100.0F * sThresholdVal / SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD);
        sSpeedLmt = Math.round(100.0F * sSpeedLmtVal / SmoothScrollerMouseWheelListener.MAX_SPEED_LMT);
        sAccLmt = Math.round(100.0F * sAccLmtVal / SmoothScrollerMouseWheelListener.MAX_ACC_LMT);
        sFric =   Math.round(100.0F * sFricVal / SmoothScrollerMouseWheelListener.MAX_FRIC);

        SmoothScrollerMouseWheelListener.setSpeedThreshold(sThresholdVal);
        SmoothScrollerMouseWheelListener.setSpeedLmt(sSpeedLmtVal);
        SmoothScrollerMouseWheelListener.setAccLmt(sAccLmtVal);
        SmoothScrollerMouseWheelListener.setFricExp(sFricVal);
    }

    private static void saveValues() {
        sThresholdVal = (sThreshold / 100.0F) * SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD;
        sSpeedLmtVal = (sSpeedLmt / 100.0F) * SmoothScrollerMouseWheelListener.MAX_SPEED_LMT;
        sAccLmtVal = (sAccLmt / 100.0F) * SmoothScrollerMouseWheelListener.MAX_ACC_LMT;
        sFricVal = (sFric / 100.0F) * SmoothScrollerMouseWheelListener.MAX_FRIC;

        storeProperties();
    }

    public static void resetDefaults() {
        sThresholdVal = SmoothScrollerMouseWheelListener.DEF_SPEED_THRESHOLD;
        sSpeedLmtVal = SmoothScrollerMouseWheelListener.DEF_SPEED_LMT;
        sAccLmtVal = SmoothScrollerMouseWheelListener.DEF_ACC_LMT;
        sFricVal = SmoothScrollerMouseWheelListener.DEF_FRIC;

        storeProperties();

        sThreshold = Math.round(100.0F * sThresholdVal / SmoothScrollerMouseWheelListener.MAX_SPEED_THRESHOLD);
        sSpeedLmt = Math.round(100.0F * sSpeedLmtVal / SmoothScrollerMouseWheelListener.MAX_SPEED_LMT);
        sAccLmt = Math.round(100.0F * sAccLmtVal / SmoothScrollerMouseWheelListener.MAX_ACC_LMT);
        sFric = Math.round(100.0F * sFricVal / SmoothScrollerMouseWheelListener.MAX_FRIC);
    }

    private static void storeProperties() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        props.setValue(THRESHOLD, String.valueOf(sThresholdVal));
        props.setValue(SPEED_LMT, String.valueOf(sSpeedLmtVal));
        props.setValue(ACC_LMT, String.valueOf(sAccLmtVal));
        props.setValue(FRIC, String.valueOf(sFricVal));

        SmoothScrollerMouseWheelListener.setSpeedThreshold(sThresholdVal);
        SmoothScrollerMouseWheelListener.setSpeedLmt(sSpeedLmtVal);
        SmoothScrollerMouseWheelListener.setAccLmt(sAccLmtVal);
        SmoothScrollerMouseWheelListener.setFricExp(sFricVal);
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

    public static int getFric() {
        return sFric;
    }

    public static void setFric(int fric) {
        sFric = fric;
    }
}

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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI for the SmoothScroller option
 */
public class OptionsForm {
    private JSlider thresholdSlider;
    private JSlider speedLmtSlider;
    private JSlider accLmtSlider;
    private JSlider fricSlider;
    private JPanel panel;
    private JPanel itemPanel;
    private JButton resetDefaultsButton;

    public OptionsForm() {
        resetDefaultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OptionsAction.resetDefaults();
                thresholdSlider.setValue(OptionsAction.getThreshold());
                speedLmtSlider.setValue(OptionsAction.getSpeedLmt());
                accLmtSlider.setValue(OptionsAction.getAccLmt());
                fricSlider.setValue(OptionsAction.getFric());
            }
        });
    }

    public JComponent getRoot() {
        return panel;
    }

    public void setData() {
        thresholdSlider.setValue(OptionsAction.getThreshold());
        speedLmtSlider.setValue(OptionsAction.getSpeedLmt());
        accLmtSlider.setValue(OptionsAction.getAccLmt());
        fricSlider.setValue(OptionsAction.getFric());
    }

    public void getData() {
        OptionsAction.setThreshold(thresholdSlider.getValue());
        OptionsAction.setSpeedLmt(speedLmtSlider.getValue());
        OptionsAction.setAccLmt(accLmtSlider.getValue());
        OptionsAction.setFric(fricSlider.getValue());
    }

    public boolean isModified() {
        return thresholdSlider.getValue() != OptionsAction.getThreshold() ||
                speedLmtSlider.getValue() != OptionsAction.getSpeedLmt() ||
                accLmtSlider.getValue() != OptionsAction.getAccLmt() ||
                fricSlider.getValue() != OptionsAction.getFric();
    }
}

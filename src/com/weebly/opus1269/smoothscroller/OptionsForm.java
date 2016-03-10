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

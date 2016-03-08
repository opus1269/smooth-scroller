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
    private JSlider fricConstSlider;
    private JSlider fricExpSlider;
    private JPanel panel;
    private JPanel itemPanel;
    private JButton resetDefaultsButton;

    public OptionsForm() {
        resetDefaultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OptionsAction.resetDefaults();
                thresholdSlider.setValue(OptionsAction.getThreshold());
                fricConstSlider.setValue(OptionsAction.getFricConst());
                fricExpSlider.setValue(OptionsAction.getFricExp());
            }
        });
    }

    public JComponent getRoot() {
        return panel;
    }

    public void setData(OptionsAction data) {
        thresholdSlider.setValue(OptionsAction.getThreshold());
        fricConstSlider.setValue(OptionsAction.getFricConst());
        fricExpSlider.setValue(OptionsAction.getFricExp());
    }

    public void getData(OptionsAction data) {
        data.setThreshold(thresholdSlider.getValue());
        data.setFricConst(fricConstSlider.getValue());
        data.setFricExp(fricExpSlider.getValue());
    }

    public boolean isModified(OptionsAction data) {
        if (thresholdSlider.getValue() != OptionsAction.getThreshold() ||
                fricConstSlider.getValue() != OptionsAction.getFricConst() ||
                fricExpSlider.getValue() != OptionsAction.getFricExp()) {
            return true;
        }
        return false;
    }
}

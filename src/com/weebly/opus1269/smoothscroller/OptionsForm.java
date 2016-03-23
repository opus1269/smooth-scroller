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
import java.util.ArrayList;

/**
 * GUI for the SmoothScroller options
 */
public class OptionsForm implements ActionListener {
    private JSlider thresholdSlider;
    private JSlider speedLmtSlider;
    private JSlider accLmtSlider;
    private JSlider fricSlider;
    private JSlider multSlider;
    private JPanel panel;
    private JPanel itemPanel;
    private JButton resetDefaultsButton;

    private final ArrayList<JSlider> mList = new ArrayList<JSlider>();

    public OptionsForm() {
        mList.add(thresholdSlider);
        mList.add(speedLmtSlider);
        mList.add(accLmtSlider);
        mList.add(fricSlider);
        mList.add(multSlider);

        resetDefaultsButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Props.resetDefaults();
        setFromProps();
    }

    public JComponent getRoot() {
        return panel;
    }

    public void setToProps() {
        int i = 0;
        for (JSlider slider : mList) {
            getProp(i).setPos(slider.getValue());
            i++;
        }
    }

    public boolean isModified() {
        int i = 0;
        for (JSlider slider : mList) {
            if (slider.getValue() != getProp(i).POS) {
                return true;
            }
            i++;
        }
        return false;
    }

    public void setFromProps() {
        int i = 0;
        for (JSlider slider : mList) {
            slider.setValue(getProp(i).POS);
            i++;
        }
    }

    private Prop getProp(int pos) {
        return Props.get(pos);
    }

}

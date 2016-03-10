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

import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

class SmoothScrollerMouseWheelListener implements MouseWheelListener, ActionListener {
    private static final int FPS = 20;
    private static final int MILLIS_PER_FRAME = 1000 / FPS;

    public static final float MAX_SPEED_THRESHOLD = 0.001F;
    public static final float MAX_SPEED_LMT = 100.0F;
    public static final float MAX_ACC_LMT = 10.0F;
    public static final float MAX_FRIC = .015F;

    public static final float DEF_SPEED_THRESHOLD = 0.0005F;
    public static final float DEF_SPEED_LMT = 25.0F;
    public static final float DEF_ACC_LMT = 5.0F;
    public static final float DEF_FRIC = 0.005F;

    private static float sSpeedThreshold = DEF_SPEED_THRESHOLD;
    private static float sSpeedLmt = DEF_SPEED_LMT;
    private static float sAccLmt = DEF_ACC_LMT;
    private static float sFric = DEF_FRIC;

    private final ScrollingModel mScrollingModel;
    private Timer mTimer = null;

    private double mLastWheelDelta = 0.0D;
    private boolean mScrolling = false;

    private double mVelocity = 0.0D;
    private ArrayList<Double> mVelocities = new ArrayList<Double>();
    private static final int MAX_VELOCITIES = 10;

    /**
     * Constructor for our MouseWheelListener.
     *
     * @param editor The file editor to which smooth scrolling is to be added.
     */
    public SmoothScrollerMouseWheelListener(FileEditor editor) {
        mScrollingModel = ((TextEditor) editor).getEditor().getScrollingModel();
        mScrollingModel.disableAnimation();

        mTimer = new Timer(MILLIS_PER_FRAME, this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        // don't want to apply any easing to velocity while scrolling
        mScrolling = true;
        mScrollingModel.runActionOnScrollingFinished(new Runnable() {
            @Override
            public void run() {
                mScrolling = false;
                mVelocities.clear();
            }
        });

        // track wheel motion delta
        final double wheelDelta = e.getPreciseWheelRotation();
        final boolean sameDirection = mLastWheelDelta * wheelDelta > 0.0D;
        mLastWheelDelta = wheelDelta;

        if (!sameDirection) {
            // changed direction
            mVelocity = 0.0D;
            mVelocities.clear();
            return;
        }

        final double scrollDelta = e.getScrollAmount() * wheelDelta;
        final double deltaV = scrollDelta / MILLIS_PER_FRAME;

        if (Math.abs(deltaV) < sSpeedThreshold) {
            // skip small movements
            return;
        }

        final double newVelocity = mVelocity + deltaV;

        // calculate average velocity over last several mouse wheel events
        if (mVelocities.size() == MAX_VELOCITIES) {
            mVelocities.remove(0);
        }
        mVelocities.add(newVelocity);

        final double oldVelocity = mVelocity;
        mVelocity = getAverage(mVelocities);

        // limit acceleration
        final double acc = (mVelocity - oldVelocity) / MILLIS_PER_FRAME;
        if (Math.abs(acc) > sAccLmt) {
            mVelocity = oldVelocity + sAccLmt * MILLIS_PER_FRAME * Math.signum(acc);
        }

        // limit speed
        if (Math.abs(mVelocity) > sSpeedLmt) {
            mVelocity = sSpeedLmt * Math.signum(mVelocity);
        }
        if (Math.abs(mVelocity) < sSpeedThreshold) {
            mVelocity = 0.0D;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    /**
     * Starts animating the scroll offset.
     */
    public void startAnimating() {
        mTimer.start();
    }

    /**
     * Stops animating the scroll offset.
     */
    public void stopAnimating() {
        mTimer.stop();
    }

    /**
     * Updates the velocity acting on the scroll offset and then updates
     * the scroll offset.
     */
    private void update() {
         if (!mScrolling) {
            // Basic kinetic scrolling, exponential decay vel_new = vel * e^-lambda*deltaT
            mVelocity = mVelocity * Math.exp(-sFric * MILLIS_PER_FRAME);
        }

        if (Math.abs(mVelocity) >= sSpeedThreshold) {
            // reposition cursor offset based on current velocity
            final int currentOffset = mScrollingModel.getVerticalScrollOffset();
            final long offset = Math.round((currentOffset + mVelocity * MILLIS_PER_FRAME));
            mScrollingModel.scrollVertically(Math.max(0, (int) offset));
        } else {
            // bring to stop below threshold
            mVelocity = 0.0D;
            mVelocities.clear();
        }
    }

    private double getAverage(ArrayList<Double> array) {
        double sum = 0.0D;
        if (!array.isEmpty()) {
            for (Double item : array) {
                sum = sum + item;
            }
            return sum / array.size();
        }
        return sum;
    }

    public static void setSpeedThreshold(float speedThreshold) {
        sSpeedThreshold = speedThreshold;
    }

    public static void setSpeedLmt(float speedLmt) {
        sSpeedLmt = speedLmt;
    }

    public static void setAccLmt(float accLmt) {
        sAccLmt = accLmt;
    }

    public static void setFricExp(float fricExp) {
        sFric = fricExp;
    }
}

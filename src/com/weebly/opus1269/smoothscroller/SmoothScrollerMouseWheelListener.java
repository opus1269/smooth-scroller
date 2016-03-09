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

import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class SmoothScrollerMouseWheelListener implements MouseWheelListener, ActionListener {
    private static final int FPS = 50;
    private static final int MILLIS_PER_FRAME = 1000 / FPS;

    public static final float MAX_SPEED_THRESHOLD = 0.001F;
    public static final float MAX_FRIC_CONST = 1.0F;
    public static final float MAX_FRIC_EXP = 10.0F;

    public static final float DEF_SPEED_THRESHOLD = 0.0005F;
    public static final float DEF_FRIC_CONST = 0.9F;
    public static final float DEF_FRIC_EXP = 1.1F;

    private static float sSpeedThreshold = DEF_SPEED_THRESHOLD;
    private static float sFricConst = DEF_FRIC_CONST;
    private static float sFricExp = DEF_FRIC_CONST;

    private final ScrollingModel mScrollingModel;
    private Timer mTimer = null;

    private long mLastTime = 0;

    private double mLastWheelDelta = 0.0D;
    private long mLastScrollTime = 0;
    private boolean mScrolling = false;
    private Timer mScrollTimer = null;

    private double mVelocity = 0.0D;

    /**
     * Constructor for our MouseWheelListener.
     *
     * @param editor The file editor to which smooth scrolling is to be added.
     */
    public SmoothScrollerMouseWheelListener(FileEditor editor) {
        mScrollingModel = ((TextEditor) editor).getEditor().getScrollingModel();
        mScrollingModel.disableAnimation();
        mTimer = new Timer(MILLIS_PER_FRAME, this);
        mScrollTimer = new Timer(MILLIS_PER_FRAME, this);
        mScrollTimer.setRepeats(false);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mScrolling = true;
        mScrollTimer.restart();

        final double wheelDelta = e.getPreciseWheelRotation();
        final boolean sameDirection = mLastWheelDelta * wheelDelta >= 0.0D;
        mLastWheelDelta = wheelDelta;

        final long currentTime = System.nanoTime();
        final long elapsedMillis = (currentTime - mLastScrollTime) / 1000000;
        mLastScrollTime = currentTime;

        if (!sameDirection) {
            // changed direction
            mVelocity = 0.0D;
            return;
        }

        if (elapsedMillis > 0) {
            // update velocity
            if (mVelocity * wheelDelta >= 0.0D) {
                mVelocity = mVelocity + wheelDelta;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Timer timer = (Timer) e.getSource();
        if (timer.isRepeats()) {
            update();
        } else {
            mScrolling = false;
        }
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
        if (mLastTime == 0) {
            mLastTime = System.nanoTime();
            return;
        }

        final long currentTime = System.nanoTime();
        final long elapsedMillis = (currentTime - mLastTime) / 1000000;
        mLastTime = currentTime;

        //final double exponent = elapsedMillis / (MILLIS_PER_FRAME / sFricExp);

        //final double deceleration = 0.1D * mVelocity * Math.signum(mVelocity);
        //final double deceleration = Math.pow(mVelocity, -exponent * elapsedMillis) * Math.signum(mVelocity);

        //mVelocity = mVelocity - deceleration * elapsedMillis;

        if (!mScrolling) {
            mVelocity = mVelocity * Math.exp(-sFricExp * elapsedMillis);
        }

        final double speed = Math.abs(mVelocity);
        if (speed >= sSpeedThreshold) {
            final int currentOffset = mScrollingModel.getVerticalScrollOffset();
            final long offset = Math.round((currentOffset + mVelocity));
            mScrollingModel.scrollVertically(Math.max(0, (int) offset));
        } else {
            mVelocity = 0.0D;
        }
    }

    public static float getFricConst() {
        return sFricConst;
    }

    public static void setFricConst(float mFricConst) {
        SmoothScrollerMouseWheelListener.sFricConst = mFricConst;
    }

    public static float getFricExp() {
        return sFricExp;
    }

    public static void setFricExp(float mFricExp) {
        SmoothScrollerMouseWheelListener.sFricExp = mFricExp;
    }

    public static float getSpeedThreshold() {
        return sSpeedThreshold;
    }

    public static void setSpeedThreshold(float mSpeedThreshold) {
        SmoothScrollerMouseWheelListener.sSpeedThreshold = mSpeedThreshold;
    }
}

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
import java.util.ArrayList;

class SmoothScrollerMouseWheelListener implements MouseWheelListener, ActionListener {
    private static final int FPS = 50;
    private static final int MILLIS_PER_FRAME = 1000 / FPS;

    public static final float MAX_SPEED_THRESHOLD = 0.001F;
    public static final float MAX_SPEED_LMT = 100.0F;
    public static final float MAX_ACC_LMT = 10.0F;
    public static final float MAX_FRIC_EXP = .015F;

    public static final float DEF_SPEED_THRESHOLD = 0.0005F;
    public static final float DEF_SPEED_LMT = 25.0F;
    public static final float DEF_ACC_LMT = 5.0F;
    public static final float DEF_FRIC_EXP = 0.005F;

    private static float sSpeedThreshold = DEF_SPEED_THRESHOLD;
    private static float sSpeedLmt = DEF_SPEED_LMT;
    private static float sAccLmt = DEF_SPEED_LMT;
    private static float sFricExp = DEF_SPEED_LMT;

    private final ScrollingModel mScrollingModel;
    private Timer mTimer = null;

    private long mLastTime = 0;

    private double mLastWheelDelta = 0.0D;
    private long mLastScrollTime = 0;
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
        if (mLastScrollTime == 0) {
            mLastScrollTime = System.nanoTime();
            return;
        }

        // don't want to apply any easing to velocity while scrolling
        mScrolling = true;
        mScrollingModel.runActionOnScrollingFinished(new Runnable() {
            @Override
            public void run() {
                mScrolling = false;
            }
        });

        // track wheel motion delta
        final double wheelDelta = e.getPreciseWheelRotation();
        final boolean sameDirection = mLastWheelDelta * wheelDelta > 0.0D;
        mLastWheelDelta = wheelDelta;

        // track time delta
        final long currentTime = System.nanoTime();
        final long elapsedMillis = (currentTime - mLastTime) / 1000000;
        mLastScrollTime = currentTime;

        if (elapsedMillis == 0) {
            // it do happen
            return;
        }

        if (!sameDirection) {
            // changed direction
            mVelocity = 0.0D;
            mVelocities.clear();
            return;
        }

        double scrollDelta = e.getScrollAmount() * wheelDelta;
        double newVelocity = mVelocity + scrollDelta / elapsedMillis;

        // calculate average velocity over last several mouse wheel events
        if (Math.abs(newVelocity) > sSpeedThreshold) {
            // skip small movements
            if (mVelocities.size() == MAX_VELOCITIES) {
                mVelocities.remove(0);
            }
            mVelocities.add(newVelocity);
        }

        double oldVelocity = mVelocity;
        mVelocity = getAverage(mVelocities);

        // limit acceleration
        final double acc = (mVelocity - oldVelocity) / elapsedMillis;
        if (Math.abs(acc) > sAccLmt) {
            mVelocity = oldVelocity + sAccLmt * elapsedMillis * Math.signum(acc);
        }

        // limit speed
        if (Math.abs(mVelocity) > sSpeedLmt) {
            mVelocity = sSpeedLmt * Math.signum(mVelocity);
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
        if (mLastTime == 0) {
            mLastTime = System.nanoTime();
            return;
        }

        // track time delta
        final long currentTime = System.nanoTime();
        final long elapsedMillis = (currentTime - mLastTime) / 1000000;
        mLastTime = currentTime;

        if (!mScrolling) {
            // Basic kinetic scrolling, exponential decay vel_new = vel * e^-lambda*deltaT
             mVelocity = mVelocity * Math.exp(-sFricExp * elapsedMillis);
            // basic kinetic scrolling, linear easing
            //mVelocity = mVelocity - sFricExp * mVelocity;
        }

        if (Math.abs(mVelocity) >= sSpeedThreshold) {
            // reposition cursor offset based on current velocity
            final int currentOffset = mScrollingModel.getVerticalScrollOffset();
            final long offset = Math.round((currentOffset + mVelocity * elapsedMillis));
            mScrollingModel.scrollVertically(Math.max(0, (int) offset));
        } else {
            // bring to stop below threshold
            mVelocity = 0.0D;
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

    public static float getSpeedThreshold() {
        return sSpeedThreshold;
    }

    public static void setSpeedThreshold(float speedThreshold) {
        sSpeedThreshold = speedThreshold;
    }

    public static float getSpeedLmt() {
        return sSpeedLmt;
    }

    public static void setSpeedLmt(float speedLmt) {
        sSpeedLmt = speedLmt;
    }

    public static float getAccLmt() {
        return sAccLmt;
    }

    public static void setAccLmt(float accLmt) {
        sAccLmt = accLmt;
    }

    public static float getFricExp() {
        return sFricExp;
    }

    public static void setFricExp(float fricExp) {
        sFricExp = fricExp;
    }
 }

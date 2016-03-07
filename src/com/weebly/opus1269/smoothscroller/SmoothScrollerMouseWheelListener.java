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

    private static final double PSEUDO_FRICTION = 0.95d;
    private static final double PSEUDO_FRICTION_EXP = 1.25d;
    private static final double SPEED_THRESHOLD = 0.0005d;

    private final ScrollingModel mScrollingModel;
    private Timer mTimer = null;

    private long mLastTime = 0;

    private double mLastWheelDelta = 0.0d;

    private double mVelocity = 0.0d;

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
        final double wheelDelta = e.getPreciseWheelRotation();
        final boolean sameDirection = mLastWheelDelta * wheelDelta >= 0;
        mLastWheelDelta = wheelDelta;

        if (!sameDirection) {
            // changed direction
            mVelocity = 0.0d;
        } else {
            // update velocity
            mVelocity = mVelocity + wheelDelta;
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

        final long currentTime = System.nanoTime();
        final long elapsedMillis = (currentTime - mLastTime) / 1000000;
        mLastTime = currentTime;

        final double exponent = elapsedMillis / (MILLIS_PER_FRAME / PSEUDO_FRICTION_EXP);

        mVelocity = mVelocity * Math.pow(PSEUDO_FRICTION, exponent);

        final double speed = Math.abs(mVelocity);
        if (speed >= SPEED_THRESHOLD) {
            final int currentOffset = mScrollingModel.getVerticalScrollOffset();
            final int offset = (int) Math.round((currentOffset + mVelocity * elapsedMillis));
            mScrollingModel.scrollVertically(Math.max(0, offset));
        } else {
            mVelocity = 0.0d;
        }
    }
}

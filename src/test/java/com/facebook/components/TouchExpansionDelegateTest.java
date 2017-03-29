/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho;

import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.litho.testing.testrunner.ComponentsTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(ComponentsTestRunner.class)
public class TouchExpansionDelegateTest {
  private TouchExpansionDelegate mTouchDelegate;

  @Before
  public void setup() {
    mTouchDelegate = new TouchExpansionDelegate(new ComponentHost(RuntimeEnvironment.application));
  }

  @Test
  public void testEmptyOnTouchEvent() {
    mTouchDelegate.onTouchEvent(
        MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            MotionEvent.ACTION_DOWN,
            0,
            0,
            0));
  }

  @Test
  public void testTouchWithinBounds() {
    final View view = mock(View.class);
    when(view.getContext()).thenReturn(RuntimeEnvironment.application);
    when(view.getWidth()).thenReturn(4);
    when(view.getHeight()).thenReturn(6);

    mTouchDelegate.registerTouchExpansion(0, view, new Rect(0, 0, 10, 10));

    MotionEvent event = MotionEvent.obtain(
        SystemClock.uptimeMillis(),
        SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN,
        5,
        5,
        0);

    mTouchDelegate.onTouchEvent(event);

    verify(view, times(1)).dispatchTouchEvent(event);
    assertEquals(2f, event.getX());
    assertEquals(3f, event.getY());
  }

  @Test
  public void testTouchOutsideBounds() {
    final View view = mock(View.class);
    when(view.getContext()).thenReturn(RuntimeEnvironment.application);

    mTouchDelegate.registerTouchExpansion(0, view, new Rect(0, 0, 10, 10));

    MotionEvent event = MotionEvent.obtain(
        SystemClock.uptimeMillis(),
        SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN,
        100,
        100,
        0);

    mTouchDelegate.onTouchEvent(event);

    verify(view, never()).dispatchTouchEvent(event);
  }

  @Test
  public void testUnregister() {
    final View view = mock(View.class);
    when(view.getContext()).thenReturn(RuntimeEnvironment.application);

    mTouchDelegate.registerTouchExpansion(0, view, new Rect(0, 0, 10, 10));
    mTouchDelegate.unregisterTouchExpansion(0);

    MotionEvent event = MotionEvent.obtain(
        SystemClock.uptimeMillis(),
        SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN,
        5,
        5,
        0);

    mTouchDelegate.onTouchEvent(event);

    verify(view, never()).dispatchTouchEvent(event);
  }

  @Test

/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mattcarroll.hover.window;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Controls {@code View}s' positions, visibility, etc within a {@code Window}.
 */
public class ViewGroupController implements ViewController {

    private ViewGroup mViewGroup;
    private static int sActionBarHeight = -1;

    public ViewGroupController(@NonNull ViewGroup mViewGroup) {
        this.mViewGroup = mViewGroup;
    }

    public void addView(int width, int height, boolean isTouchable, @NonNull View view) {
        // If this view is untouchable then add the corresponding flag, otherwise set to zero which
        // won't have any effect on the OR'ing of flags.
        int touchableFlag = isTouchable ? 0 : WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        int windowType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                windowType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | touchableFlag,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

	    mViewGroup.addView(view, params);
    }

    public void removeView(@NonNull View view) {
        if (null != view.getParent()) {
	        mViewGroup.removeView(view);
        }
    }

    public Point getViewPosition(@NonNull View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);

        int offset = 0;
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            offset = view.getRootView().getHeight() - ((View) parent).getHeight() - getSoftButtonsBarHeight(view.getContext());
        }
        return new Point(rect.left, rect.top - offset);
    }

    public void moveViewTo(View view, int x, int y) {
        view.setX(x);
        view.setY(y);
    }

    public void showView(View view) {
        try {
            ViewGroup.LayoutParams params = view.getLayoutParams();
	        mViewGroup.addView(view, params);
        } catch (IllegalStateException e) {
            // The view is already visible.
        }
    }

    public void hideView(View view) {
        try {
	        mViewGroup.removeView(view);
        } catch (IllegalArgumentException e) {
            // The View wasn't visible to begin with.
        }
    }

    public void makeTouchable(View view) {
       //do nothing
    }

    public void makeUntouchable(View view) {
        //do nothing
    }

    @SuppressLint("NewApi")
    private int getSoftButtonsBarHeight(Context context) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            if (wm != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(metrics);
                int usableHeight = metrics.heightPixels;
                wm.getDefaultDisplay().getRealMetrics(metrics);
                int realHeight = metrics.heightPixels;
                if (realHeight > usableHeight)
                    return realHeight - usableHeight;
                else
                    return 0;
            }
        }
        return 0;
    }
}

package io.mattcarroll.hover.window;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;

public interface ViewController {

	void addView(int width, int height, boolean isTouchable, @NonNull View view);

	void removeView(@NonNull View view);

	Point getViewPosition(@NonNull View view);

	void moveViewTo(View view, int x, int y);

	void showView(View view);

	void hideView(View view);

	void makeTouchable(View view);

	void makeUntouchable(View view);
}

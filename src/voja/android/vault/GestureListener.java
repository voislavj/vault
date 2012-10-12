package voja.android.vault;

import android.view.MotionEvent;
import android.view.GestureDetector;

public class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	GlideLock view;
	
	public static final String DEBUG_TAG = "voja";

	public GestureListener(GlideLock view) {
		this.view = view;
	}

	public boolean onDown(MotionEvent e) {
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, final float velocityY) {
		final float distanceTimeFactor = 0.4f;
		final float totalDx = (distanceTimeFactor * velocityX / 2);
		final float totalDy = (distanceTimeFactor * velocityY / 2);

		//view.onAnimateMove(totalDx, totalDy, (long) (1000 * distanceTimeFactor));
		view.onMove(totalDx, totalDy);
		return true;
	}

	public boolean onDoubleTap(MotionEvent e) {
		//view.onResetLocation();
		return true;
	}

	public void onLongPress(MotionEvent e) {
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		//view.onMove(-distanceX, -distanceY);
		return true;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

}

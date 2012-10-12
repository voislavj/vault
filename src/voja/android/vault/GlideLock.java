package voja.android.vault;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GlideLock extends LinearLayout {

    private GestureDetector gestures;
    private Matrix translate;

    private Matrix animateStart;
    private OvershootInterpolator animateInterpolator;
    private long startTime;
    private long endTime;
    private float totalAnimDx;
    private float totalAnimDy;
    
    String value="";

    public void onAnimateMove(float dx, float dy, long duration) {
        animateStart = new Matrix(translate);
        animateInterpolator = new OvershootInterpolator();
        startTime = System.currentTimeMillis();
        endTime = startTime + duration;
        totalAnimDx = dx;
        totalAnimDy = dy;
        post(new Runnable() {
            public void run() {
                onAnimateStep();
            }
        });
    }

    private void onAnimateStep() {
        long curTime = System.currentTimeMillis();
        float percentTime = (float) (curTime - startTime)
                / (float) (endTime - startTime);
        float percentDistance = animateInterpolator
                .getInterpolation(percentTime);
        float curDx = percentDistance * totalAnimDx;
        float curDy = percentDistance * totalAnimDy;
        translate.set(animateStart);
        onMove(curDx, curDy);

        if (percentTime < 1.0f) {
            post(new Runnable() {
                public void run() {
                    onAnimateStep();
                }
            });
        }
    }

    public void onMove(float dx, float dy) {
        //translate.postTranslate(dx, dy);
    	
    	TextView tv = (TextView)findViewById(R.id.tv);
    	int val = Integer.parseInt(tv.getText().toString());
    	val = val>0 ? val : 0;
    	
    	if(dy>0) {
    		val -= 1;
    	}else{
    		val += 1;
    	}
    	val = val>=0 ? (val>9 ? 0 : val) : 9;
    	
    	value = ""+val;
    	tv.setText(value);
    	
        invalidate();
    }

    public void onResetLocation() {
        translate.reset();
        invalidate();
    }

    public void onSetLocation(float dx, float dy) {
        translate.postTranslate(dx, dy);
    }
    
    public GlideLock(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        translate = new Matrix();
        gestures = new GestureDetector(new GestureListener(this));
        
        setOrientation(LinearLayout.VERTICAL);
        ((Activity)getContext()).getLayoutInflater().inflate(R.layout.locker, this, true);
        /*TextView tv;
        for(int i=0; i<10; i++) {
        	tv = new TextView(context);
        	tv.setText(""+i);
        	tv.setTextSize(20);
        	addView(tv);
        }*/
    }

    public GlideLock(Context context) {
        super(context);
        translate = new Matrix();
        gestures = new GestureDetector(new GestureListener(this));
        
        setOrientation(LinearLayout.VERTICAL);
        
        /*TextView tv;
        for(int i=0; i<10; i++) {
        	tv = new TextView(context);
        	tv.setText(""+i);
        	tv.setTextSize(30);
        	addView(tv);
        }*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawBitmap(droid, translate, null);
        Matrix m = canvas.getMatrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestures.onTouchEvent(event);
    }

}

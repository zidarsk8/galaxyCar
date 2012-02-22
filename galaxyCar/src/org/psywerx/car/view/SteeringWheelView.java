package org.psywerx.car.view;

import org.psywerx.car.DataListener;
import org.psywerx.car.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class SteeringWheelView extends View implements DataListener{

	private static final float ANGLE_FACTOR = 5;
	private float mRotate = 0.0f;
	private float mToAngle = 0.0f;
	private float mAlpha = 0.1f;

	public SteeringWheelView(Context context) {
		super(context);
	}
	public SteeringWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SteeringWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.rotate(mRotate,128,128);
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.steering_wheel_small), 0, 0, null);
	}

	public synchronized void rotateWheel(){
		mRotate = (1f-mAlpha) * mRotate + mAlpha * mToAngle;
	}

	public synchronized void updateData(float[] data) {
		mToAngle = data[4]*ANGLE_FACTOR;
		rotateWheel();
	}
	public void setAlpha(float alpha) {
		this.mAlpha = alpha;
	}

}

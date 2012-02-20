package org.psywerx.car.view;

import java.text.DecimalFormat;

import org.psywerx.car.DataListener;
import org.psywerx.car.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class StevecView extends View implements DataListener{
	private final int MIN_ROTATE = -30;
	private final int MAX_ROTATE = 180;
	private final DecimalFormat FORMATTER = new DecimalFormat("0000");

	private Paint mTextPaint = null;
	private float mRotate = 0;
	private float mSpeed = 0;
	private float mAlpha = 0.3f;
	private double mDistance = 0;
	private long mTimestamp = 0;

	public boolean setSpeed(float speed) {
		long ct = System.nanoTime();
		mDistance += speed*(ct-mTimestamp)/1e10;
		mTimestamp = ct;
		
		mSpeed = (1.0f-mAlpha)*mSpeed + mAlpha * speed;
		float temp = -30+2.3f*mSpeed;
		if (temp < MIN_ROTATE || temp > MAX_ROTATE)
			return false;
		this.mRotate = temp;
		this.postInvalidate();
		return true;
	}
	
	public StevecView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTimestamp = System.nanoTime();
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.LTGRAY);
		mTextPaint.setTextSize(29);
		mTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Digitaldream.ttf"));
		setSpeed(0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.speedgaugeclean), 0, 0, null);
		canvas.drawText(FORMATTER.format(mDistance), 85, 187, mTextPaint);
		canvas.save();
		canvas.rotate(mRotate,130,129);
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cagr), 45, 124, null);
		canvas.restore();
	}

	public void updateData(float[] data) {
		//D.dbgv("updating stevec view with speed: "+data[3]);
		setSpeed(data[3]);
		
	}

	public void setAlpha(float alpha) {
		this.mAlpha = alpha;
	}
}

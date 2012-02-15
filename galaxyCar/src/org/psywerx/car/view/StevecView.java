package org.psywerx.car.view;

import org.psywerx.car.R;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class StevecView extends View {
	private final int MIN_ROTATE = -30;
	private final int MAX_ROTATE = 180;
	private final DecimalFormat FORMATTER = new DecimalFormat("0000");

	private Paint mTextPaint = null;
	private float mRotate = 0;
	private int mDistance = 0;

	public boolean setSpeed(float speed) {
		float temp = -30+2.3f*speed;
		if (temp < MIN_ROTATE || temp > MAX_ROTATE)
			return false;
		this.mRotate = temp;
		this.postInvalidate();
		return true;
	}

	public boolean setDistance(int x) {
		if (x < 0 || x > 9999)
			return false;
		this.mDistance = x;
		this.postInvalidate();
		return true;
	}

	public StevecView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.LTGRAY);
		mTextPaint.setTextSize(29);
		mTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Digitaldream.ttf"));
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
}

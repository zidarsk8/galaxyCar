package org.psywerx.car.view;

import org.psywerx.car.DataListener;
import org.psywerx.car.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PospeskiView extends View implements DataListener{

	private final float SIZE_FACTOR = 3f;
	private Paint mPaint = null;
	private Bitmap mPic = null;
	private float mX = 0;
	private float mY = 0;
	private float mZ = 0;
	private int mWidth = 0;
	private int mHeigh = 0;
	private static float ALPHA = 0.1f;

	public PospeskiView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPic = BitmapFactory.decodeResource(getResources(), R.drawable.gmeter);
		mWidth = mPic.getWidth();
		mHeigh = mPic.getHeight();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		setXYZ(0,0,0);
	}

	public boolean setXYZ(float x, float y, float z) {
		if ( x < -1 || x > 1 || y < -1 || y > 1 || z < -1 || z > 1 ){
			return false;
		}

		mX = (1f-ALPHA) * mX + ALPHA* (((x+1) * mWidth ) / 2);
		mY = (1f-ALPHA) * mY + ALPHA* (((y+1) * mHeigh ) / 2);
		mZ = (1f-ALPHA) * mZ + ALPHA* (Math.abs(z*SIZE_FACTOR) + 4);

		postInvalidate();
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mPic, 0, 0, null);
		canvas.drawCircle(mX, mY, mZ, mPaint);
	}

	public void updateData(float[] data) {
		setXYZ(data[0], data[1], data[2]);
	}
}

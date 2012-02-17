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

	private Paint mPaint = null;
	private Bitmap mPic = null;
	private float mX = 0;
	private float mY = 0;
	private float mZ = 0;
	private int mWidth = 0;
	private int mHeigh = 0;

	public PospeskiView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPic = BitmapFactory.decodeResource(getResources(), R.drawable.gmeter);
		mWidth = mPic.getWidth() / 2;
		mHeigh = mPic.getHeight() / 2;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		setXYZ(0,0,0);
	}

	public boolean setXYZ(float x, float y, float z) {
		if ( x < -1 || x > 1 || y < -1 || y > 1 || z < -1 || z > 1 )
			return false;

		this.mX = ( (x+1) * mWidth ) / 2;
		this.mY = ( (y+1) * mHeigh ) / 2;
		this.mZ = z*20 + 5;
		this.postInvalidate();
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mPic, 0, 0, null);
		canvas.drawCircle(this.mX, this.mY, this.mZ, mPaint);
	}

	public void updateData(float[] data) {
		setXYZ(data[0], data[1], data[2]);
	}
}

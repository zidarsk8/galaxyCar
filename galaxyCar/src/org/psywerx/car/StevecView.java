package org.psywerx.car;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class StevecView extends View {

	private float mRotate = 0;
	private final int minRotate = -30;
	private final int maxRotate = 180;
	
	public boolean setSpeed(float speed) {
		float temp = -30+2.3f*speed;
		if (temp < minRotate || temp > maxRotate)
			return false;
		this.mRotate = temp;
		this.postInvalidate();
		return true;
		
	}
	
	public StevecView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.speedgaugeclean), 0, 0, null);
		/*
		Paint paint = new Paint(); 

		paint.setColor(Color.WHITE); 
		paint.setTextSize(20); 
		canvas.drawText(Float.toString((Math.round(mSpeed*100))/100), 80, 300, paint);
		*/
		canvas.save();
		canvas.rotate(mRotate,130,129);
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cagr), 45, 124, null);
		canvas.restore();
	}
}

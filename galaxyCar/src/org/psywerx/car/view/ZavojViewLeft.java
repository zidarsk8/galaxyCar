package org.psywerx.car.view;

import org.psywerx.car.Car;
import org.psywerx.car.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class ZavojViewLeft extends View{
	private Bitmap mImage = null;
	
	public ZavojViewLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
		mImage = BitmapFactory.decodeResource(getResources(), R.drawable.turn_meter_palcka);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.rotate((float)Car.turnLeft /*/(float)Math.PI * 180 */, mImage.getWidth()/2, mImage.getHeight()/2);
		canvas.drawBitmap(mImage, 0, 0, null);
		canvas.restore();
	}
}

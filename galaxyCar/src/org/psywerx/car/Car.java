package org.psywerx.car;


import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Car implements Drawable{
	
	ModelLoader models;
	
	@Override
	public void draw(GL10 gl) {
		Log.v("smotko", "AAsdfafasdfAAA");
		Model car = models.GetModel("car");
		car.draw(gl);
	}

	@Override
	public void init(ModelLoader m) {
		models = m;
		if(models.models.containsKey("car"))
			Log.v("smotko","KEY");
		else
			Log.v("smotko", "@@@");
	}

}

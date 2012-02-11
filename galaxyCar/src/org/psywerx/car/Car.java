package org.psywerx.car;


import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Car implements Drawable{
	
	ModelLoader models;
	
	@Override
	public void draw(GL10 gl) {
		Model car = models.GetModel("car");
		gl.glTranslatef(pos[0], pos[1], pos[2]);
		car.draw(gl);
	}

	@Override
	public void init(ModelLoader m) {
		models = m;
	}

}

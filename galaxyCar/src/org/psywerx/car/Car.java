package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

public class Car extends Model implements Drawable{
	

	ModelLoader models;
	
	
	
	public void draw(GL10 gl) {
		Model car = models.GetModel("car");
		
		car.draw(gl);
	}

	public void init(ModelLoader m) {
		models = m;
	}

	public void move(float[] p) {
		
	}

}

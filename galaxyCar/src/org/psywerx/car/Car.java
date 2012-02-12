package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

public class Car implements Drawable{
	

	ModelLoader models;
	private float rot;
	
	
	
	public void draw(GL10 gl) {
		Model car = models.GetModel("car");
		rot -= 0.005f;
		car.rotate(gl, rot, 0f, 1f, 0f);
		//car.move(gl, 0, 0, 0.01f);
		car.draw(gl);
	}

	public void init(ModelLoader m) {
		models = m;
	}



}

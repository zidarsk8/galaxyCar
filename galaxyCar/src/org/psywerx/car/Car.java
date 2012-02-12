package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

public class Car implements Drawable{
	
	public float[] pos = {0f, 0f, 0f};
	public float[] rot = {0f, 0f, 0f};
	ModelLoader models;
	
	
	
	public void draw(GL10 gl) {
		Model car = models.GetModel("car");
		gl.glTranslatef(pos[0], pos[1], pos[2]);
		car.draw(gl);
	}

	public void init(ModelLoader m) {
		models = m;
	}

}

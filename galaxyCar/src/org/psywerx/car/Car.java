package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

import org.psywerx.car.bluetooth.BtHelper;

public class Car implements Drawable{
	

	ModelLoader models;
	private float rot;
	private BtHelper b;
	float angle = 0;
	
	
	
	public void draw(GL10 gl) {
		Model car = models.GetModel("car");
		rot -= 0.005f;
		float[] m = b.getLastData();
		car.rotate(gl, (m[5]*m[4]/1000f)-5, 0f, 1f, 0f);
		//car.move(gl, , 0, 0.01f);
		car.draw(gl);
	}

	public Car(ModelLoader m, BtHelper md) {
		models = m;
		b = md;
	}



}

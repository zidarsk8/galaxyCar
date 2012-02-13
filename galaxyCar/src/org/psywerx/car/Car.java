package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

import org.psywerx.car.bluetooth.BtHelper;

public class Car{
	

	ModelLoader models;
	private float rot;
	private BtHelper b;
	float angle = 0;
	private Model car;
	protected float x = 0f,y = 0f,z = -10f;
	protected float yaw = 0, pitch = 0, skew = 0;
	
	public void update(){
		float[] m = b.getLastData();
		m[5] = 5;
		yaw += m[5]*0.5;
		skew += Math.log(m[5]);
		if(skew > 1) skew = 1;
		else if(skew < -1) skew = -1;
		
		z += Math.sin(angle)*m[5]*0.5;
		x += Math.cos(angle)*m[5]*0.5;
		
		angle += m[5]*0.05;
		//pitch += 6f;
		//skew += 5f;
	}
	
	public void draw(GL10 gl) {
		update();
		gl.glPushMatrix();
//		gl.glTranslatef(-x, y, -z);
		//car.rotate(gl, -pitch, yaw, skew);
		car.draw(gl);
		gl.glPopMatrix();
	}

	public Car(ModelLoader m, BtHelper md) {
		models = m;
		b = md;
		car = models.GetModel("car");
	}



}

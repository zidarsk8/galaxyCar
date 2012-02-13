package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

import org.psywerx.car.bluetooth.BtHelper;

public class Car{
	

	ModelLoader models;
	Camera camera;
	private float rot;
	private BtHelper b;
	float angle = 0;
	private Model car;
	protected float x = 0f,y = 0f,z = -10f;
	protected float yaw = 260, pitch = 0, skew = 0;
	
	public void update(){
		float[] m = b.getLastData();
//		x = camera.x;
		//y = camera.y;
//		z = -camera.z - 15;
		m[5] = 5;
//		z += -0.1f;
		yaw += m[5]*0.03;
		skew += Math.log(m[5]);
		if(skew > 1) skew = 1;
		else if(skew < -1) skew = -1;
		
		z += Math.sin(angle)*m[5]*0.008;
		x += Math.cos(angle)*m[5]*0.008;
		
		angle += m[5]*0.0005;
		//pitch += 6f;
		//skew += 5f;
	}
	
	public void draw(GL10 gl) {
		update();
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		car.rotate(gl, pitch, yaw, skew);
		car.draw(gl);
		gl.glPopMatrix();
	}

	public Car(ModelLoader m, BtHelper md, Camera c) {
		models = m;
		b = md;
		car = models.GetModel("car");
		camera = c;
	}



}

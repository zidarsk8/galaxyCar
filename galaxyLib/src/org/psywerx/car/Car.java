package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

public class Car implements DataListener{
	

	ModelLoader models;
	Camera camera;
	//private float rot;
	float angle = 0;
	private Model car;
	protected float x = 0f,y = 0f,z = -10f;
	protected float yaw = 180, pitch = 0, skew = 0;
	private float alpha = 0;
	
	public void update(){
		float[] m = new float[6];
		m[5] =10;
		m[4] = 5;
		
		// TODO: This sort of works for wheel turn 5, make it work better and for more values
		
		//yaw += m[5]*0.0285;
		skew += Math.log(m[5]);
		if(skew > 1) skew = 1;
		else if(skew < -1) skew = -1;
		
		double tmpZ = Math.sin(5)*m[5];
		double tmpX = Math.cos(5)*m[5];
		
		x = (float) (tmpZ * Math.cos(alpha) - tmpX * Math.sin(alpha));
		z = (float) (tmpX * Math.cos(alpha) + tmpZ * Math.sin(alpha));
		
		float speed = (float) Math.pow(m[5],-3)*10;
		
		alpha += speed;
		
		yaw += 57*speed;
		
		//angle += m[5]*0.0005;
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

	public Car(ModelLoader m, Camera c) {
		models = m;
		car = models.GetModel("car");
		camera = c;
	}

	public void updateData(float[] data) {
		// TODO Auto-generated method stub
		
	}



}

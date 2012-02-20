package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3d;

public class SteeringWheel implements DataListener{

	// change this if the turning circle radius is not linear function of the wheel turn value
	//private final float LINEAR = 1;

	private ModelLoader models;
	//private Camera camera;

	private Model mSteeringWheel;

	protected Vector3d mPosition = null;
	protected float yaw = 0, pitch = -90, skew = 0;

	public void update(){
		yaw += 0.001f;
	}

	//	private void printV(String ime,Vector3d mPosition){
	//		D.dbgv(String.format(ime+" :  %.4f  %.4f  %.4f ", mPosition.x, mPosition.y, mPosition.z));
	//	}

	public void draw(GL10 gl) {
		update();
		gl.glPushMatrix();
		gl.glTranslatef((float)mPosition.x, (float)mPosition.y, (float)mPosition.z);
		mSteeringWheel.rotate(gl, pitch, yaw, skew);
		mSteeringWheel.draw(gl);
		gl.glPopMatrix();
	}
	
	public SteeringWheel(ModelLoader m, Camera c) {
		// use the device hardware's native byte order
		mPosition = new Vector3d(-1, 0, -4);
		models = m;
		//mSteeringWheel = models.GetModel("mSteeringWheel");
		mSteeringWheel = models.GetModel("steering_wheel");
		//camera = c;
	}

	private synchronized void setDirection(float speed, float s){
		
	}

	public void updateData(float[] data) {
		//D.dbgv("updating mSteeringWheel data "+data[4]);
		setDirection(data[3],data[4]);
	}

	public void setAlpha(float alpha) {
	}

}

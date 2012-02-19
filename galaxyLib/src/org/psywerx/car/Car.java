package org.psywerx.car;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3d;

public class Car implements DataListener{

	private final float SPEED_FACTOR = 0.7f;
	private final float TURN_FACTOR = 10;
	private final float MAX_RADIUS = 100;

	private final int HISTORY_SIZE = 10000*9;

	// change this if the turning circle radius is not linear function of the wheel turn value
	//private final float LINEAR = 1;

	private ModelLoader models;
	//private Camera camera;

	private Model car;

	Vector3d mDirVec = null;
	Vector3d mPosition = null;
	protected float yaw = 180, pitch = 0, skew = 0;
	//private float alpha = 0;

	private float mSpeed = 0;
	private float mTurn = 0;
	private long mTimestamp = 0;

	private float[] mHistorArr = new float[HISTORY_SIZE];
	private int mHistoryPosition = 0;
	private FloatBuffer history;


	public void update(){
		long time = System.nanoTime();
		double elapsed = (time - mTimestamp)/ 1e9f;
		mTimestamp = time;
		double dDistance = mSpeed * elapsed;

		if (mSpeed == 0){
			return;
		}
		
		Vector3d newDirection = new Vector3d(mDirVec);
		Vector3d perpendicular = new Vector3d(); // perpendicular to direction vector
		perpendicular.cross(mDirVec, new Vector3d(0, 1, 0)); 
		perpendicular.normalize();
		mDirVec.normalize();
		
		Vector3d t1 = new Vector3d();
		Vector3d t2 = new Vector3d();
		Vector3d t3 = new Vector3d();
		t1.add(mPosition,perpendicular);
		t2.sub(mPosition,perpendicular);
		t2.add(mPosition,mDirVec);
		
		//dodamo histor trikotnike
		// TODO: smotko .. dej nared da se bojo se te trikotniki izrisoval
		mHistorArr[mHistoryPosition]   = (float) t1.x;
		mHistorArr[mHistoryPosition+1] = (float) t1.y;
		mHistorArr[mHistoryPosition+2] = (float) t1.z;
		mHistorArr[mHistoryPosition+3] = (float) t2.x;
		mHistorArr[mHistoryPosition+4] = (float) t2.y;
		mHistorArr[mHistoryPosition+5] = (float) t2.z;
		mHistorArr[mHistoryPosition+6] = (float) t3.x;
		mHistorArr[mHistoryPosition+7] = (float) t3.y;
		mHistorArr[mHistoryPosition+8] = (float) t3.z;
		
		mHistoryPosition = (mHistoryPosition+9) % HISTORY_SIZE;
		
		if (mTurn != 0){
			//double radious = (MAX_RADIUS - Math.pow(mTurn * TURN_FACTOR, LINEAR));
			double radious = MAX_RADIUS - mTurn * TURN_FACTOR;
			double alpha = Math.PI*2*dDistance/radious; // distance of the driven arc in angle degrees
			// no need for translation rotation translation, cause of the small angles

			//the new direction is a linear combination of old direction and the perpendicular vector
			//according to the calculated angle	

			newDirection.scale(Math.cos(alpha));
			perpendicular.scale(Math.sin(alpha));
			newDirection.add(perpendicular);
			newDirection.normalize();
			newDirection.scale(dDistance*SPEED_FACTOR);
			//D.dbgv(String.format("alpha:  %.5f     pos:  %.4f  %.4f  %.4f ", alpha, mDirVec.x, mDirVec.y, mDirVec.z));
		}else{
			newDirection.normalize();
			newDirection.scale(dDistance*SPEED_FACTOR);
		}
		
		mPosition.add(newDirection);
		
		double xArc = newDirection.angle(new Vector3d(1, 0, 0));
		double zArc = newDirection.angle(new Vector3d(0, 0, -1));

		if (xArc <= Math.PI/2){
			yaw = (float) Math.toDegrees(Math.PI + zArc);
		}else{
			yaw = (float) Math.toDegrees(Math.PI - zArc);
		}

		mDirVec = newDirection;

	}

	public void draw(GL10 gl) {
		update();
		gl.glPushMatrix();
		gl.glTranslatef((float)mPosition.x, (float)mPosition.y, (float)mPosition.z);
		car.rotate(gl, pitch, yaw, skew);
		car.draw(gl);
		
		drawHistory(gl);
		
		
		gl.glPopMatrix();
	}

	private void drawHistory(GL10 gl) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 4 bytes per float)
				mHistorArr.length * 4);
		// use the device hardware's native byte order
		vbb.order(ByteOrder.nativeOrder());
		// create a floating point buffer from the ByteBuffer
		history = vbb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		history.put(mHistorArr);
		// set the buffer to read the first coordinate
		history.position(0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, history);
//		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer[i]);
		gl.glColor4f(0f, 0f, 255f, 1);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (history.capacity()) / 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}

	public Car(ModelLoader m, Camera c) {
		mDirVec = new Vector3d(0, 0, -1);
		mPosition = new Vector3d(0, 0, -10);
		models = m;
		car = models.GetModel("car");
		//camera = c;
		mTimestamp = System.nanoTime();
	}

	private synchronized void setDirection(float speed, float s){
		mSpeed = speed;
		mTurn = s;
	}

	public void updateData(float[] data) {
		//D.dbgv("updating car data "+data[4]);
		setDirection(data[3],data[4]);
	}



}

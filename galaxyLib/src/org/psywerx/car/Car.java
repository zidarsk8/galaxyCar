package org.psywerx.car;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3d;

import android.view.View;

public class Car implements DataListener{

	private final float SPEED_FACTOR = 1f;
	private final float TURN_FACTOR = 2;
	private final float MAX_RADIUS = 20;

	private final float SIZE = 0.5f;
	private final int HISTORY_SIZE = 200*18;

	private final int HISTORY_SKIP = 5;
	private int mhistpos = 0;
	
	// change this if the turning circle radius is not linear function of the wheel turn value
	//private final float LINEAR = 1;

	private ModelLoader models;
	//private Camera camera;

	private Model car;

	protected Vector3d mDirVec = null;
	protected Vector3d mPosition = null;
	private LinkedList<Vector3d> mHistoryPos = new LinkedList<Vector3d>();
	private LinkedList<Vector3d> mHistoryLook = new LinkedList<Vector3d>();
	protected float yaw = 180, pitch = 0, skew = 0;

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
		double dDistance = mSpeed * elapsed * SPEED_FACTOR; // m/s

		if (mSpeed == 0){
			return;
		}
		Vector3d norm = new Vector3d(0,(mTurn<0? -1:1),0);
		Vector3d newDirection = new Vector3d(mDirVec);
		Vector3d perpendicular = new Vector3d(); // perpendicular to direction vector
		perpendicular.cross(newDirection, norm);
		perpendicular.normalize();
		mDirVec.normalize();
		if(mhistpos++%HISTORY_SKIP == 0){


			mDirVec.scale(SIZE*2);
			perpendicular.scale(SIZE);

			Vector3d t1 = new Vector3d();
			Vector3d t2 = new Vector3d();
			Vector3d t3 = new Vector3d();
			Vector3d t4 = new Vector3d();

			Vector3d center = new Vector3d((float)car.center[0],0,(float)car.center[2]);
			center.add(mPosition);

			t1.add(center,perpendicular);
			t2.sub(center,perpendicular);
			t3.add(t1, mDirVec);
			t4.add(t2, mDirVec);

			//dodamo histor trikotnike
			mHistorArr[mHistoryPosition]   = (float) t1.x;
			mHistorArr[mHistoryPosition+1] = 0.2f;
			mHistorArr[mHistoryPosition+2] = (float) t1.z;
			mHistorArr[mHistoryPosition+3] = (float) t2.x;
			mHistorArr[mHistoryPosition+4] = 0.2f;
			mHistorArr[mHistoryPosition+5] = (float) t2.z;
			mHistorArr[mHistoryPosition+6] = (float) t3.x;
			mHistorArr[mHistoryPosition+7] = 0.2f;
			mHistorArr[mHistoryPosition+8] = (float) t3.z;
			mHistoryPosition+=9;
			mHistorArr[mHistoryPosition]   = (float) t2.x;
			mHistorArr[mHistoryPosition+1] = 0.2f;
			mHistorArr[mHistoryPosition+2] = (float) t2.z;
			mHistorArr[mHistoryPosition+3] = (float) t4.x;
			mHistorArr[mHistoryPosition+4] = 0.2f;
			mHistorArr[mHistoryPosition+5] = (float) t4.z;
			mHistorArr[mHistoryPosition+6] = (float) t3.x;
			mHistorArr[mHistoryPosition+7] = 0.2f;
			mHistorArr[mHistoryPosition+8] = (float) t3.z;


			mHistoryPosition = (mHistoryPosition+9) % HISTORY_SIZE;

		}

		if (mTurn != 0){
			//double radious = (MAX_RADIUS - Math.pow(mTurn * TURN_FACTOR, LINEAR));
			double radious = MAX_RADIUS - Math.abs(mTurn) * TURN_FACTOR ;
			double alpha = dDistance/radious; // distance of the driven arc in angle degrees
			perpendicular.normalize();
			perpendicular.scale(radious);
			newDirection.normalize();
			newDirection.scale(radious);

			mPosition.add(perpendicular);

			newDirection.scale(Math.sin(alpha));

			perpendicular.scale(-Math.cos(alpha));

			perpendicular.add(newDirection);

			mPosition.add(perpendicular);
			mDirVec.cross(perpendicular, norm);

		}else{
			newDirection.normalize();
			newDirection.scale(dDistance);
			mPosition.add(newDirection);
		}

		double xArc = newDirection.angle(new Vector3d(1, 0, 0));
		double zArc = newDirection.angle(new Vector3d(0, 0, -1));

		if (xArc <= Math.PI/2){
			yaw = (float) Math.toDegrees(Math.PI + zArc);
		}else{
			yaw = (float) Math.toDegrees(Math.PI - zArc);
		}
		
		mHistoryPos.add(new Vector3d(mPosition));
		mHistoryLook.add(new Vector3d(mDirVec));
		
		while (mHistoryPos.size() > 20){
			mHistoryPos.removeFirst();
			mHistoryLook.removeFirst();
		}
	}

	//	private void printV(String ime,Vector3d mPosition){
	//		D.dbgv(String.format(ime+" :  %.4f  %.4f  %.4f ", mPosition.x, mPosition.y, mPosition.z));
	//	}

	public void draw(GL10 gl) {
		update();
		gl.glPushMatrix();
		gl.glTranslatef((float)mPosition.x, (float)mPosition.y, (float)mPosition.z);
		car.rotate(gl, pitch, yaw, skew);
		car.draw(gl);



		gl.glPopMatrix();
		drawHistory(gl);
	}

	private void drawHistory(GL10 gl) {
		// add the coordinates to the FloatBuffer
		history.put(mHistorArr);
		// set the buffer to read the first coordinate
		history.position(0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, history);
		//		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer[i]);
		gl.glColor4f(0f, 0f, 0f, 0.5f);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (history.capacity()) / 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}

	public Car(ModelLoader m) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
				mHistorArr.length * 4);
		// use the device hardware's native byte order
		vbb.order(ByteOrder.nativeOrder());
		// create a floating point buffer from the ByteBuffer
		history = vbb.asFloatBuffer();
		mDirVec = new Vector3d(0, 0, -1);
		mPosition = new Vector3d(0, 0.2f, -10);
		models = m;
		//car = models.GetModel("car");
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

	public void setAlpha(float alpha) {
	}

	protected float[] getLookAtVector(){
		Vector3d a = mHistoryPos.getFirst();
		Vector3d b = mHistoryLook.getFirst();
		return new float[]{(float)a.x,(float)a.y,(float)a.z, (float)a.x+(float)b.x, (float)a.y+(float)b.y, (float)a.z+(float)b.z};
	}
	
}

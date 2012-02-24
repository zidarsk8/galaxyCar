package org.psywerx.car;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3d;

public class Car implements DataListener{

	/**
	 * Car parameters used for calibration
	 */
	//4  metre na minuto damo v m/s
	//brez *10 naj bi blo prou, samo tko leps zgleda
	public static final float METRI_NA_OBRAT = 0.0402123859659494f;
	private final float SPEED_FACTOR = METRI_NA_OBRAT /60 *500; 
	private final float TURN_FACTOR = 4;
	private final float MAX_RADIUS = 30;

	private final float SIZE = 0.5f;
	private final int HISTORY_SIZE = 500 * 18;
	private final int HISTORY_SKIP = 8;

	private int mHistPos = 0;

	private ModelLoader models;
	private Model car;

	protected Vector3d mDirVec = null;
	protected Vector3d mPosition = null;

	protected float yaw = 180, pitch = 0, skew = 0;

	public float mSpeed = 0;
	public float mTurn = 0;
	private long mTimestamp = 0;

	private float[] mHistorArr = new float[HISTORY_SIZE];
	private int mHistoryPosition = 0;
	private FloatBuffer mHistoryBuffer;
	
	public static double turnLeft = 0;
	public static double turnRight = 0;
	public long mStartTimer = 0;
	public double mPrevozeno = 0;
	public double avgSpeed = 0;
	public double maxSpeed = 0;
	public double avgSpeedCounter = 0;
	
	public Car(ModelLoader m) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 4 bytes per float)
				mHistorArr.length * 4);
		// use the device hardware's native byte order
		vbb.order(ByteOrder.nativeOrder());
		// create a floating point buffer from the ByteBuffer
		mHistoryBuffer = vbb.asFloatBuffer();
		mDirVec = new Vector3d(0, 0, -1);
		mPosition = new Vector3d(0, 0.2f, -10);
		models = m;
		car = models.GetModel("car");
		mTimestamp = System.nanoTime();
		mStartTimer = mTimestamp;
	}

	/**
	 * Called on each draw call, updates the car position
	 */
	public void update() {
		long time = System.nanoTime();
		double elapsed = (time - mTimestamp) / 1e9f;
		mTimestamp = time;
		// (float)( ((ct-mTimestamp)/1e9)   *    cur[3]/60)        * METRI_NA_OBRAT ;
		double dDistance = mSpeed * elapsed * SPEED_FACTOR; // m/s

		if (mSpeed == 0) { return; }
		
		/**
		 * racuanje stvari za prikaz
		 */
		mPrevozeno += dDistance/500*60;
		avgSpeedCounter += elapsed;
		avgSpeed += mSpeed*elapsed;
		
		if (mSpeed > maxSpeed){
			maxSpeed = mSpeed;
		}
		
		Vector3d norm = new Vector3d(0, (mTurn < 0 ? -1 : 1), 0);
		Vector3d newDirection = new Vector3d(mDirVec);
		Vector3d perpendicular = new Vector3d(); // perpendicular to direction
													// vector
		perpendicular.cross(newDirection, norm);
		perpendicular.normalize();
		mDirVec.normalize();
		if (mHistPos++ % HISTORY_SKIP == 0) {
			
			float sc = SIZE * (40/(mSpeed+15));
			mDirVec.scale(sc*2);
			perpendicular.scale(sc);

			Vector3d t1 = new Vector3d();
			Vector3d t2 = new Vector3d();
			Vector3d t3 = new Vector3d();
			Vector3d t4 = new Vector3d();

			Vector3d center = new Vector3d((float) car.mCenter[0], 0,
					(float) car.mCenter[2]);
			center.add(mPosition);

			t1.add(center, perpendicular);
			t2.sub(center, perpendicular);
			t3.add(t1, mDirVec);
			t4.add(t2, mDirVec);

			// add the history triangles:
			mHistorArr[mHistoryPosition] = (float) t1.x;
			mHistorArr[mHistoryPosition + 1] = 0.2f;
			mHistorArr[mHistoryPosition + 2] = (float) t1.z;
			mHistorArr[mHistoryPosition + 3] = (float) t2.x;
			mHistorArr[mHistoryPosition + 4] = 0.2f;
			mHistorArr[mHistoryPosition + 5] = (float) t2.z;
			mHistorArr[mHistoryPosition + 6] = (float) t3.x;
			mHistorArr[mHistoryPosition + 7] = 0.2f;
			mHistorArr[mHistoryPosition + 8] = (float) t3.z;
			mHistoryPosition += 9;
			mHistorArr[mHistoryPosition] = (float) t2.x;
			mHistorArr[mHistoryPosition + 1] = 0.2f;
			mHistorArr[mHistoryPosition + 2] = (float) t2.z;
			mHistorArr[mHistoryPosition + 3] = (float) t4.x;
			mHistorArr[mHistoryPosition + 4] = 0.2f;
			mHistorArr[mHistoryPosition + 5] = (float) t4.z;
			mHistorArr[mHistoryPosition + 6] = (float) t3.x;
			mHistorArr[mHistoryPosition + 7] = 0.2f;
			mHistorArr[mHistoryPosition + 8] = (float) t3.z;

			mHistoryPosition = (mHistoryPosition + 9) % HISTORY_SIZE;

		}

		if (mTurn != 0) {
			double radious = MAX_RADIUS - Math.abs(mTurn) * TURN_FACTOR;
			double alpha = dDistance / radious; // distance of the driven arc in
												// angle degrees
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
			mDirVec.normalize();
			mDirVec.scale(10);
			if (mTurn < 0){
				turnRight += alpha;
			}else{
				turnLeft += alpha;
			}
		} else {
			newDirection.normalize();
			newDirection.scale(dDistance);
			mPosition.add(newDirection);
			mDirVec.normalize();
			mDirVec.scale(10);
		}


		double xArc = newDirection.angle(new Vector3d(1, 0, 0));
		double zArc = newDirection.angle(new Vector3d(0, 0, -1));

		if (xArc <= Math.PI / 2) {
			yaw = (float) Math.toDegrees(Math.PI + zArc);
		} else {
			yaw = (float) Math.toDegrees(Math.PI - zArc);
		}
	}

	/**
	 * Draws the car
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		update();
		gl.glPushMatrix();
		gl.glTranslatef((float) mPosition.x, (float) mPosition.y,
				(float) mPosition.z);
		car.rotate(gl, pitch, yaw, skew);
		car.draw(gl);

		gl.glPopMatrix();
		drawHistory(gl);
	}

	/**
	 * Draws the car history
	 * 
	 * @param gl
	 */
	private void drawHistory(GL10 gl) {
		// add the coordinates to the FloatBuffer
		mHistoryBuffer.put(mHistorArr);
		// set the buffer to read the first coordinate
		mHistoryBuffer.position(0);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mHistoryBuffer);
		// gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer[i]);
		gl.glColor4f(0f, 0f, 0f, 0.5f);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (mHistoryBuffer.capacity()) / 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}

	/**
	 * Called when bluetooth receives new data and updates the car position
	 */
	public synchronized void updateData(float[] data) {
		mSpeed = data[3];
		mTurn = data[4];
	}

	/**
	 * Returns the lookAtVector used in setting up the camera
	 * 
	 * @return float[] lookAtVector
	 */
	protected synchronized float[] getLookAtVector() {
		
		return new float[] { 
				(float) mPosition.x - (float) mDirVec.x , 
				(float) mPosition.y + 2, 
				(float) mPosition.z - (float) mDirVec.z ,
				(float) mPosition.x + (float) mDirVec.x, 
				(float) mPosition.y + (float) mDirVec.y,
				(float) mPosition.z + (float) mDirVec.z };
	}

}

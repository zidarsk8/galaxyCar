package org.psywerx.car;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

public class CarSurfaceViewRenderer implements GLSurfaceView.Renderer {

	private ModelLoader mModelLoader;

	/**
	 * Initialize the light parameters
	 */
	private final float[] mLightAmbient = { 0.8f, 0.8f, 0.8f, 1.0f };
	private final float[] mLightDiffuse = { 0.9f, 0.9f, 0.9f, 1.0f };
	private final float[] mLightPosition = { 0.0f, 0.0f, 2.0f, 1.0f };

	/**
	 * Camera vector used for animation and positioning of the camera
	 */
	private float[] mCameraVector = new float[6];
	private int mCameraPosition = 0;

	/**
	 * Objects to be drawn
	 */
	private Car mCar;
	private Model mRoad;

	/**
	 * Texture pointer
	 */
	private int[] mTexture = new int[1];

	public CarSurfaceViewRenderer(ModelLoader m) {
		mModelLoader = m;
		// initShapes() needs to be here so we can add listeners to models (car)
		initShapes();
	}

	private void initShapes() {
		mCar = new Car(mModelLoader);
		mRoad = mModelLoader.GetModel("cesta2");
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		setCameraVector();
		GLU.gluLookAt(gl, mCameraVector[0], mCameraVector[1] + 4f,
				mCameraVector[2], mCameraVector[3], mCameraVector[4],
				mCameraVector[5], 0, 1, 0);

		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glEnable(GL10.GL_LIGHTING);

		// Draw the objects
		mRoad.draw(gl, mTexture);
		mCar.draw(gl);

		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) { // Prevent A Divide By Zero By
			height = 1; // Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); // Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
				1000.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The Modelview Matrix

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(255.0f, 255.0f, 255.0f, 1.0f); // White Background
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, mLightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, mLightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, mLightPosition, 0);
		gl.glEnable(GL10.GL_LIGHT1); // Enable Light One

		initTextures(gl);

	}
	public void setNextCamerPosition(){
		cameraPosition = ++cameraPosition%4;
	}

	private void initTextures(GL10 gl) {
		// Generate one texture pointer...
		gl.glGenTextures(1, mTexture, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_EXP);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR_MIPMAP_NEAREST);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP,
				GL11.GL_TRUE);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mRoad.mBitmap, 0);
		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mRoad.mBitmap, 0);
	}

	/**
	 * Internal function that sets the camera position
	 */
	private void setCameraVector() {
		float alpha = 0.05f;
		float[] newCameraVectpor = new float[6];
		switch (mCameraPosition) {
		case 0:
			newCameraVectpor[0] = 0f;
			newCameraVectpor[1] = 45f;
			newCameraVectpor[2] = 10f;

			newCameraVectpor[3] = (float) mCar.mPosition.x;
			newCameraVectpor[4] = 0f;
			newCameraVectpor[5] = (float) mCar.mPosition.z;

			break;
		case 1:
			newCameraVectpor[0] = (float) mCar.mPosition.x;
			newCameraVectpor[1] = 2f;
			newCameraVectpor[2] = (float) mCar.mPosition.z - 14;

			newCameraVectpor[3] = (float) mCar.mPosition.x;
			newCameraVectpor[4] = 0f;
			newCameraVectpor[5] = (float) mCar.mPosition.z;
			break;
		case 2:
			// GLU.gluLookAt(gl, (float)car.mPosition.x-10, 45f,
			// (float)car.mPosition.z-10, (float)car.mPosition.x, 0,
			// (float)car.mPosition.z, 0, 1, 0);
			newCameraVectpor[0] = (float) mCar.mPosition.x - 10;
			newCameraVectpor[1] = 45f;
			newCameraVectpor[2] = (float) mCar.mPosition.z - 10;

			newCameraVectpor[3] = (float) mCar.mPosition.x;
			newCameraVectpor[4] = 0f;
			newCameraVectpor[5] = (float) mCar.mPosition.z;

			break;
		case 3:
			// float[] v = car.getLookAtVector();
			newCameraVectpor[0] = (float) (mCar.mPosition.x - mCar.mDirVec.x);
			newCameraVectpor[1] = (float) mCar.mPosition.y;
			newCameraVectpor[2] = (float) (mCar.mPosition.z - mCar.mDirVec.z);
			newCameraVectpor[3] = (float) (mCar.mPosition.x + mCar.mDirVec.x);
			newCameraVectpor[4] = (float) (mCar.mPosition.y + mCar.mDirVec.y);
			newCameraVectpor[5] = (float) (mCar.mPosition.z + mCar.mDirVec.z);

		}
		for (int i = 0; i < 6; i++) {
			mCameraVector[i] = mCameraVector[i] * (1 - alpha)
					+ newCameraVectpor[i] * alpha;
		}

	}

	/**
	 * Cycle between camera positions the camera position
	 * 
	 * 0 - tower 1 - on car 2 - moving tower 3 - behind car
	 */
	public void nextCameraPosition() {
		mCameraPosition = (mCameraPosition + 1) % 4;
	}

	public Car getCar() {
		return mCar;
	}

}

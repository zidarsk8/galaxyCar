package org.psywerx.car;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class CarSurfaceViewRenderer implements GLSurfaceView.Renderer {

	private ModelLoader mModelLoader;
	
	float[] LightAmbient = { 0.8f, 0.8f, 0.8f, 1.0f };
	float[] LightDiffuse = { 0.9f, 0.9f, 0.9f, 1.0f };
	float[] LightPosition = { 0.0f, 0.0f, 2.0f, 1.0f };

	private Car car;
	private Model cesta;
	private Camera camera;

	public CarSurfaceViewRenderer(AssetManager asm, ModelLoader m) {
		mModelLoader = m;
		//init shapes needs to be here so we can add listeners to models (car)
		initShapes();
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glLoadIdentity();
		//camera.setView(gl);
		//camera.set((float)-car.mPosition.x, -4, (float) (-car.mPosition.z-12f));
		//camera.set(0, -5, -20f);
		
		// Fixed camera looking at car:
		GLU.gluLookAt(gl, 0, 45f, 10f, (float)car.mPosition.x, 0, (float)car.mPosition.z, 0, 1, 0);
		
		
		
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glEnable(GL10.GL_LIGHTING);
		
		car.draw(gl);
		cesta.draw(gl);

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
				100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The Modelview Matrix

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(255.0f, 255.0f, 255.0f, 1.0f); //hite  WgBackground
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, LightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, LightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, LightPosition, 0);
		gl.glEnable(GL10.GL_LIGHT1); // Enable Light One

	}

	private void initShapes() {
		camera = new Camera();
		car = new Car(mModelLoader, camera);
		cesta = mModelLoader.GetModel("cesta");
	}
	public Car getCar() {
		return car;
	}

}

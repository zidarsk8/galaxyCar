package org.psywerx.car;

import java.security.InvalidParameterException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

public class CarSurfaceViewRenderer implements GLSurfaceView.Renderer {

	private ModelLoader mModelLoader;
	
	float[] LightAmbient = { 0.8f, 0.8f, 0.8f, 1.0f };
	float[] LightDiffuse = { 0.9f, 0.9f, 0.9f, 1.0f };
	float[] LightPosition = { 0.0f, 0.0f, 2.0f, 1.0f };

	private Car car;
	//private Model cesta;
	private Camera camera;

	private Model cesta2;
	private SteeringWheel mSteeringWheel;
	private int[] textures = new int[1];
	
	public int cameraPosition = 0;

	public CarSurfaceViewRenderer(AssetManager asm, ModelLoader m) {
		mModelLoader = m;
		//init shapes needs to be here so we can add listeners to models (car)
		initShapes();
		
	}

	private void initTextures(GL10 gl) {
		//Generate one texture pointer...
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_EXP);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, cesta2.mBitmap, 0);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, cesta2.mBitmap, 0);

		// Clean up
		try {
			//cesta2.mBitmap.recycle();
		} catch (IllegalArgumentException e) {
		}
	}
	float[] v = new float[6];
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glLoadIdentity();
		float alpha = 0.05f;
		float[] newV = new float[6];
		switch(cameraPosition){
		case 0:
			newV[0] = 0f;
			newV[1] = 45f;
			newV[2] = 10f;
			
			newV[3] = (float)car.mPosition.x;
			newV[4] = 0f;
			newV[5] = (float)car.mPosition.z;
			
			break;
		case 1:
			newV[0] = (float)car.mPosition.x;
			newV[1] = 2f;
			newV[2] = (float)car.mPosition.z-14;
			
			newV[3] = (float)car.mPosition.x;
			newV[4] = 0f;
			newV[5] = (float)car.mPosition.z;
			break;
		case 2:
			//GLU.gluLookAt(gl, (float)car.mPosition.x-10, 45f, (float)car.mPosition.z-10, (float)car.mPosition.x, 0, (float)car.mPosition.z, 0, 1, 0);
			newV[0] =  (float)car.mPosition.x-10;
			newV[1] = 45f;
			newV[2] = (float)car.mPosition.z-10;
			
			newV[3] = (float)car.mPosition.x;
			newV[4] = 0f;
			newV[5] = (float)car.mPosition.z;
			
			break;
		case 3:
			//float[] v = car.getLookAtVector();
			newV[0] = (float)(car.mPosition.x-car.mDirVec.x);
			newV[1] = (float)car.mPosition.y;
			newV[2] = (float)(car.mPosition.z-car.mDirVec.z);
			newV[3] = (float)(car.mPosition.x+car.mDirVec.x);
			newV[4] = (float)(car.mPosition.y+car.mDirVec.y);
			newV[5] = (float)(car.mPosition.z+car.mDirVec.z);
			
		}
		for(int i=0; i<6; i++){
			v[i] = v[i]*(1-alpha) + newV[i]*alpha;
		}
		GLU.gluLookAt(gl, v[0], v[1]+4f, v[2], v[3], v[4], v[5], 0, 1, 0);
		//camera.set(0, -5, -20f);
		
		// Fixed camera looking at car:
		
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glEnable(GL10.GL_LIGHTING);
		
		cesta2.draw(gl, textures);
		car.draw(gl);
		
		gl.glLoadIdentity();

		//mSteeringWheel.draw(gl);

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
		gl.glClearColor(255.0f, 255.0f, 255.0f, 1.0f); //hite  WgBackground
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do
		
		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, LightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, LightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, LightPosition, 0);
		gl.glEnable(GL10.GL_LIGHT1); // Enable Light One
		
		initTextures(gl);

	}

	private void initShapes() {
		camera = new Camera();
		car = new Car(mModelLoader, camera);
		//cesta = mModelLoader.GetModel("cesta");
		cesta2 = mModelLoader.GetModel("cesta2");
		mSteeringWheel = new SteeringWheel(mModelLoader, camera);
	}
	public Car getCar() {
		return car;
	}

}

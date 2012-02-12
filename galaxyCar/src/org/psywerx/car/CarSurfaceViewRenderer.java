package org.psywerx.car;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class CarSurfaceViewRenderer implements GLSurfaceView.Renderer {

	private ModelLoader mModelLoader;
	private float rot = 0.0f;
	
    float[] LightAmbient=		{ 0.1f, 0.1f, 0.1f, 1.0f };
    float[] LightDiffuse=		{ 0.9f, 0.9f, 0.9f, 1.0f };
    float[] LightPosition=	    { 0.0f, 0.0f, 2.0f, 1.0f };

	private Car car;

	public CarSurfaceViewRenderer(AssetManager asm, ModelLoader m) {
		mModelLoader= m;
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glTranslatef(0.0f, 0f, -9.0f);
		
		gl.glRotatef(rot , 1.0f, 1.0f, 1.0f);
		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		rot += 0.8f;
		// Point to our buffers
		
		// Enable the vertex and color state

		car.draw(gl);
		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);

	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, LightAmbient, 0);		// Setup The Ambient Light
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, LightDiffuse, 0);		// Setup The Diffuse Light
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION,LightPosition, 0);	// Position The Light
        gl.glEnable(GL10.GL_LIGHT1);								// Enable Light One
		

		initShapes();
	}

	private void initShapes() {
		
		car = new Car();
		car.init(mModelLoader);

	}

}

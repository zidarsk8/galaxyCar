package org.psywerx.car;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.psywerx.car.collada.ColladaHandler;
import org.psywerx.car.collada.ColladaObject;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class CarSurfaceViewRenderer implements GLSurfaceView.Renderer {

	private AssetManager mAssets;
	private FloatBuffer triangleVB[];
	private FloatBuffer triangleNB[];
	private Models mModels;
	private float rot = 0.0f;
	
    float[] LightAmbient=		{ 0.1f, 0.1f, 0.1f, 1.0f };
    float[] LightDiffuse=		{ 0.9f, 0.9f, 0.9f, 1.0f };
    float[] LightPosition=	    { 0.0f, 0.0f, 2.0f, 1.0f };

	public CarSurfaceViewRenderer(AssetManager asm, Models m) {
		mModels = m;
		mAssets = asm;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glTranslatef(0.0f, 0f, -7.0f);
		gl.glRotatef(rot , 1.0f, 1.0f, 1.0f);
		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		rot += 0.8f;
		// Point to our buffers
		
		// Enable the vertex and color state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		for (int i=0; i<triangleVB.length;i++) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleVB[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, triangleNB[i]);
			gl.glColor4f(mModels.mColors[i][0],
					mModels.mColors[i][1],
					mModels.mColors[i][2],1);
			// Draw the vertices as triangles
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mModels.mModels[i].length  / 3);
		}

		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);

	}

	@Override
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

	@Override
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

		int modelsLen = mModels.mModels.length;
		triangleVB = new FloatBuffer[modelsLen];
		triangleNB = new FloatBuffer[modelsLen];
		for (int i=0; i < modelsLen; i++) {
			ByteBuffer vbb = ByteBuffer.allocateDirect(
					// (# of coordinate values * 4 bytes per float)
					mModels.mModels[i].length * 4);
			// use the device hardware's native byte order
			vbb.order(ByteOrder.nativeOrder());
			// create a floating point buffer from the ByteBuffer
			triangleVB[i] = vbb.asFloatBuffer(); 
			// add the coordinates to the FloatBuffer
			triangleVB[i].put(mModels.mModels[i]); 
			// set the buffer to read the first coordinate
			triangleVB[i].position(0); 
			
			vbb = ByteBuffer.allocateDirect(
					// (# of coordinate values * 4 bytes per float)
					mModels.mNormals[i].length * 4);
			// use the device hardware's native byte order
			vbb.order(ByteOrder.nativeOrder());
			
			triangleNB[i] = vbb.asFloatBuffer(); 
			// add the coordinates to the FloatBuffer
			triangleNB[i].put(mModels.mNormals[i]); 
			// set the buffer to read the first coordinate
			triangleNB[i].position(0); 
			
		}

	}

}

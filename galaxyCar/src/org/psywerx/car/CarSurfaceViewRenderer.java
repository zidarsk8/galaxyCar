package org.psywerx.car;

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
	private FloatBuffer triangleCB;
	private ColladaHandler mHandler;
	private ArrayList<ColladaObject> mObjectArray;
	private float triangleCoords[];
	private float rot = 0.0f;
	private float colors[] = {
    		1.0f, 0.0f, 0.0f, 1.0f, //Red
    		0.0f, 1.0f, 0.0f, 1.0f, //Green
    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
    		1.0f, 0.0f, 0.0f, 1.0f, //Red
    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
    		0.0f, 1.0f, 0.0f, 1.0f, //Green
    		1.0f, 0.0f, 0.0f, 1.0f, //Red
    		0.0f, 1.0f, 0.0f, 1.0f, //Green
    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
    		1.0f, 0.0f, 0.0f, 1.0f, //Red
    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
    		0.0f, 1.0f, 0.0f, 1.0f 	//Green
			    					};
	

	public CarSurfaceViewRenderer(AssetManager asm) {
		this.mAssets = asm;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
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
			gl.glColor4f(0.3f,0.99f,0.0f,1);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleVB[i]);
			
			// Draw the vertices as triangles
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, triangleCoords.length  / 3);
		}

		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

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

		initShapes();
	}

	private void initShapes() {

		triangleCoords = new float[] { 0.0f, 1.0f, 0.0f, // Top Of Triangle (Front)
				-1.0f, -1.0f, 1.0f, // Left Of Triangle (Front)
				1.0f, -1.0f, 1.0f, // Right Of Triangle (Front)
				0.0f, 1.0f, 0.0f, // Top Of Triangle (Right)
				1.0f, -1.0f, 1.0f, // Left Of Triangle (Right)
				1.0f, -1.0f, -1.0f, // Right Of Triangle (Right)
				0.0f, 1.0f, 0.0f, // Top Of Triangle (Back)
				1.0f, -1.0f, -1.0f, // Left Of Triangle (Back)
				-1.0f, -1.0f, -1.0f, // Right Of Triangle (Back)
				0.0f, 1.0f, 0.0f, // Top Of Triangle (Left)
				-1.0f, -1.0f, -1.0f, // Left Of Triangle (Left)
				-1.0f, -1.0f, 1.0f // Right Of Triangle (Left)
		};

		triangleVB = new FloatBuffer[1];
		//for (int i=0; i < Colors.v.length; i++) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
				triangleCoords.length * 4);
		vbb.order(ByteOrder.nativeOrder());// use the device hardware's native
		// byte order
		triangleVB[0] = vbb.asFloatBuffer(); // create a floating point buffer from
		// the ByteBuffer
		triangleVB[0].put(triangleCoords); // add the coordinates to the
		// FloatBuffer
		triangleVB[0].position(0); // set the buffer to read the first coordinate
		//}
		// float triangleCoords[] = {
		// // X, Y, Z
		// -0.5f, -0.25f, 0,
		// 0.5f, -0.25f, 0,
		// 0.0f, 0.559016994f, 0
		// };

		// initialize vertex Buffer for triangle
		
		// initialize vertex Buffer for triangle
//		vbb = ByteBuffer.allocateDirect(
//		// (# of coordinate values * 4 bytes per float)
//				colors.length * 4);
//		vbb.order(ByteOrder.nativeOrder());// use the device hardware's native
//											// byte order
//		triangleCB = vbb.asFloatBuffer(); // create a floating point buffer from
//											// the ByteBuffer
//		triangleCB.put(triangleCoords); // add the coordinates to the
//										// FloatBuffer
//		triangleCB.position(0); // set the buffer to read the first coordinate
		/**/
	}

}

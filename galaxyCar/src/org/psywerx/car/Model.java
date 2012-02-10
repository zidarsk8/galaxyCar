package org.psywerx.car;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Model {

	public FloatBuffer vertexBuffer[];
	public FloatBuffer normalBuffer[];
	public float colors[][];
	public int count;

	
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		for (int i=0; i<vertexBuffer.length;i++) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer[i]);
			gl.glColor4f(colors[i][0],
					colors[i][1],
					colors[i][2],1);
			// Draw the vertices as triangles
			
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (vertexBuffer[i].capacity()/4)  / 3);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
//	@Override
//	public void init(ModelLoader m) {
//		int modelsLen = m.mModels.length;
//		vertexBuffer = new FloatBuffer[modelsLen];
//		normalBuffer = new FloatBuffer[modelsLen];
//		for (int i=0; i < modelsLen; i++) {
//			ByteBuffer vbb = ByteBuffer.allocateDirect(
//					// (# of coordinate values * 4 bytes per float)
//					m.mModels[i].length * 4);
//			// use the device hardware's native byte order
//			vbb.order(ByteOrder.nativeOrder());
//			// create a floating point buffer from the ByteBuffer
//			vertexBuffer[i] = vbb.asFloatBuffer(); 
//			// add the coordinates to the FloatBuffer
//			vertexBuffer[i].put(m.mModels[i]); 
//			// set the buffer to read the first coordinate
//			vertexBuffer[i].position(0); 
//			
//			vbb = ByteBuffer.allocateDirect(
//					// (# of coordinate values * 4 bytes per float)
//					m.mNormals[i].length * 4);
//			// use the device hardware's native byte order
//			vbb.order(ByteOrder.nativeOrder());
//			
//			normalBuffer[i] = vbb.asFloatBuffer(); 
//			// add the coordinates to the FloatBuffer
//			normalBuffer[i].put(m.mNormals[i]); 
//			// set the buffer to read the first coordinate
//			normalBuffer[i].position(0); 			
//		}		
//	}	
}

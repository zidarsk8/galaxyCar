package org.psywerx.car;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Model {

	public FloatBuffer vertexBuffer[];
	public FloatBuffer normalBuffer[];
	public float[] pos = {0f, 0f, 0f};
	public float[] rot = {0f, 0f, 0f};
	public float[] center = {0f, 0f, 0f};
	public float colors[][];
	public int count;

	
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTranslatef(pos[0], pos[1], pos[2]);
		//gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		for (int i=0; i<vertexBuffer.length;i++) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer[i]);
			gl.glColor4f(colors[i][0],
					colors[i][1],
					colors[i][2],1);
			// Draw the vertices as triangles
			
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (vertexBuffer[i].capacity())  / 3);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	public void rotate(GL10 gl, float angle, float x, float y, float z){
		
		gl.glTranslatef(center[0], center[1], center[2]);
		gl.glRotatef(angle, x, y, z);
		gl.glTranslatef(-center[0], -center[1], -center[2]);
		
	}
}

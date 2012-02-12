package org.psywerx.car;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

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
			
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (vertexBuffer[i].capacity())  / 3);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}

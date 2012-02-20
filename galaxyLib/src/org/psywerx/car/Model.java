package org.psywerx.car;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

public class Model {

	public FloatBuffer vertexBuffer[];
	public FloatBuffer normalBuffer[];
	public FloatBuffer textureBuffer;
	public float textureScale = 1;
	protected float[] center = {0f, 0f, 0f};
	//private float angle = 0;
	public float colors[][];
	public int count;
	public Bitmap mBitmap = null;
	//private float[] rot = {0f, 0f, 0f};
	public float[] vertices;

	public void draw(GL10 gl){
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		for (int i=0; i<vertexBuffer.length;i++) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer[i]);
			gl.glColor4f(colors[i][0],
					colors[i][1],
					colors[i][2],1);			
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (vertexBuffer[i].capacity())  / 3);
		}
	}
	
	public void draw(GL10 gl, int[] textures){
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		for (int i=0; i<vertexBuffer.length;i++) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer[i]);
			gl.glColor4f(colors[i][0],
					colors[i][1],
					colors[i][2],1);			
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (vertexBuffer[i].capacity())  / 3);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	public void rotate(GL10 gl, float pitch, float yaw, float skew){
		gl.glTranslatef(center[0], center[1], center[2]);
		gl.glRotatef(-pitch, 1, 0, 0);
		gl.glRotatef(-yaw, 0, 1, 0);
		gl.glRotatef(-skew, 0, 0, 1);
		gl.glTranslatef(-center[0], -center[1], -center[2]);
	}

}

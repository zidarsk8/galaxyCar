package org.psywerx.car;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

/**
 * 
 * Model with basic functions to draw a shape the screen
 * 
 * @author smotko
 * 
 */
public class Model {

	protected FloatBuffer mVertexBuffer[];
	protected FloatBuffer mNormalBuffer[];
	protected FloatBuffer mTextureBuffer;
	protected float mTextureScale = 1;
	protected float[] mCenter = { 0f, 0f, 0f };
	protected float mColors[][];
	protected int mCount;
	protected Bitmap mBitmap = null;
	protected float[] mVertices;

	/**
	 * Draw a model
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		for (int i = 0; i < mVertexBuffer.length; i++) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer[i]);
			gl.glColor4f(mColors[i][0], mColors[i][1], mColors[i][2], 1);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0,
					(mVertexBuffer[i].capacity()) / 3);
		}
	}

	/**
	 * Draw a model with textures
	 * 
	 * @param gl
	 * @param textures
	 */
	public void draw(GL10 gl, int[] textures) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
		for (int i = 0; i < mVertexBuffer.length; i++) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer[i]);
			gl.glColor4f(mColors[i][0], mColors[i][1], mColors[i][2], 1);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0,
					(mVertexBuffer[i].capacity()) / 3);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	/**
	 * Rotate model based on pitch, jaw and skew
	 * 
	 * @param gl
	 * @param pitch
	 * @param yaw
	 * @param skew
	 */
	public void rotate(GL10 gl, float pitch, float yaw, float skew) {
		gl.glTranslatef(mCenter[0], mCenter[1], mCenter[2]);
		gl.glRotatef(-pitch, 1, 0, 0);
		gl.glRotatef(-yaw, 0, 1, 0);
		gl.glRotatef(-skew, 0, 0, 1);
		gl.glTranslatef(-mCenter[0], -mCenter[1], -mCenter[2]);
	}

}

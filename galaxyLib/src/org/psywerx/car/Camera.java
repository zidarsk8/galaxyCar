package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

public class Camera {
	protected float x = 0, y = -5, z = -10f;
	protected float yaw = 0, pitch = -0f, skew = 0;

	public void setView(GL10 gl) {
		gl.glTranslatef(x, y, z);
		gl.glRotatef(-yaw, 1, 0, 0);
		gl.glRotatef(-pitch, 0, 1, 0);
		gl.glRotatef(-skew, 0, 0, 1);
	}

	public void move(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

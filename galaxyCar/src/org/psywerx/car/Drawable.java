package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

public interface Drawable {

	public float[] pos = { 0f, 0f, 0f };
	public float[] rot = { 0f, 0f, 0f };

	public void draw(GL10 gl);

	public void init();
}

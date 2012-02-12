package org.psywerx.car;

import javax.microedition.khronos.opengles.GL10;

public interface Drawable {



	public void draw(GL10 gl);

	public void init(ModelLoader m);

}

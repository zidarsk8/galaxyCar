package org.psywerx.car;

import java.io.IOException;
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
	private FloatBuffer triangleVB;
	private ColladaHandler mHandler;
	private ArrayList<ColladaObject> mObjectArray;

	public CarSurfaceViewRenderer(AssetManager asm) {
		this.mAssets = asm;
		mHandler = new ColladaHandler();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		if (mObjectArray != null && mObjectArray.size() > 0) {
			for (int i = 0; i < mObjectArray.size(); i++) {
				mObjectArray.get(i).draw(gl);
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		final float zNear = 1.0f, zFar = 100.0f, fieldOfView = 45.0f;
		float ratio = (float) width / (float) height;

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, width, height);
		GLU.gluPerspective(gl, fieldOfView, ratio, zNear, zFar);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0, 0, 0, 0);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glClearDepthf(1.0f);

		initShapes();
	}

	private void initShapes() {

		try {
			mObjectArray = mHandler.parseFile(mAssets.open("blend.dae"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

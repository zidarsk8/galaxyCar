package org.psywerx.car;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

import org.psywerx.car.collada.ColladaHandler;
import org.psywerx.car.collada.ColladaObject;

public class CarSurfaceViewRenderer implements GLSurfaceView.Renderer {


    private AssetManager mAssets;
	private FloatBuffer triangleVB;
	private ColladaHandler mHandler;
    private ArrayList<ColladaObject> mObjectArray;

	public CarSurfaceViewRenderer(AssetManager asm){
		this.mAssets = asm;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Draw the triangle
		gl.glColor4f(0.63671875f, 0.76953125f, 0.22265625f, 0.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleVB);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(0,0,0,0);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glClearDepthf(1.0f);
		
		initShapes();
	}

	private void initShapes(){
		
		
        try {
			mObjectArray = mHandler.parseFile(mAssets.open("model.dae"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		float triangleCoords[] = Cone.v[0][0];
		
//		float triangleCoords[] = {
//				// X, Y, Z
//				-0.5f, -0.25f, 0,
//				0.5f, -0.25f, 0,
//				0.0f,  0.559016994f, 0
//			}; 

		// initialize vertex Buffer for triangle  
		ByteBuffer vbb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
				triangleCoords.length * 4); 
		vbb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
		triangleVB = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
		triangleVB.put(triangleCoords);    // add the coordinates to the FloatBuffer
		triangleVB.position(0);            // set the buffer to read the first coordinate
		/**/
	}

}

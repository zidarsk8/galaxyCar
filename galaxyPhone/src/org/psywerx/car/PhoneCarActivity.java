package org.psywerx.car;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;

/**
 * Activity for testing the OpenGl part of the main application works on a
 * mobile phones as well as on a tablet
 */
public class PhoneCarActivity extends Activity {
	/** Called when the activity is first created. */
	private WakeLock mWakeLock;
	private GLSurfaceView mGlView;
	private CarSurfaceViewRenderer mCarSurface;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Sandboc lock");
		mWakeLock.acquire();

		mGlView = (GLSurfaceView) findViewById(R.id.glSurface);

		if (mGlView != null) {
			mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			mCarSurface = new CarSurfaceViewRenderer(new ModelLoader(this));
			mGlView.setRenderer(mCarSurface);
			mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			mGlView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mCarSurface.setNextCameraPosition();
				}
			});
		}
	}
}
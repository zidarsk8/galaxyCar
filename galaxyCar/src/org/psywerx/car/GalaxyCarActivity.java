package org.psywerx.car;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public class GalaxyCarActivity extends Activity {

	private GLSurfaceView mGlView;
	private WakeLock mWakeLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getApplicationContext();
		
		// Enable full screen mode:
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Get wake lock:
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Sandboc lock");
		mWakeLock.acquire();

		setContentView(R.layout.main);
		this.mGlView = (GLSurfaceView) this.findViewById(R.id.glSurface);
		if (this.mGlView != null) {
			this.mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			this.mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			this.mGlView.setRenderer(new CarSurfaceViewRenderer(getResources()
					.getAssets(),new Models(this)));
			this.mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();
		mGlView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWakeLock.release();
		mGlView.onPause();
	}

}

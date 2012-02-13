package org.psywerx.car;

import org.psywerx.car.bluetooth.BtHelper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PhoneCarActivity extends Activity {
    /** Called when the activity is first created. */
	private WakeLock mWakeLock;
	private GLSurfaceView mGlView;
	private BtHelper mBtHelper;
	private BluetoothAdapter mBluetoothAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Sandboc lock");
		mWakeLock.acquire();
        
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		mBtHelper = new BtHelper(getApplicationContext());
		
        mGlView = (GLSurfaceView) findViewById(R.id.glSurface);
        
		if (mGlView != null) {
			mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			mGlView.setRenderer(new CarSurfaceViewRenderer(getResources()
					.getAssets(), new ModelLoader(this), mBtHelper));
			mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
    }
}
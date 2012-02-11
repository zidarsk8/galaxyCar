package org.psywerx.car;


import org.psywerx.car.bluetooth.BluetoothChatService;
import org.psywerx.car.bluetooth.BluetoothHandler;
import org.psywerx.car.bluetooth.DeviceListActivity;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GalaxyCarActivity extends Activity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    
	private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService = null;

    private BluetoothHandler mHandler = null;
	private GLSurfaceView mGlView;
	private WakeLock mWakeLock;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		// Get wake lock:
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Sandboc lock");
		mWakeLock.acquire();

        init();
    }
    
    private void init() {
		mGlView = (GLSurfaceView) findViewById(R.id.glSurface);
		if (mGlView != null) {
			mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			mGlView.setRenderer(new CarSurfaceViewRenderer(getResources()
					.getAssets(),new ModelLoader(this)));
			mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
    	mHandler = new BluetoothHandler(getApplicationContext());
        Button b = (Button) findViewById(R.id.bluetoothButton);
        b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enableBluetooth();
			}
		});
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getApplicationContext(), mHandler);
	}

	private void enableBluetooth(){
    	D.dbgv("starting bluetooth thingy");
    	if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		D.dbgv("on result from : "+requestCode+"   resultCode "+resultCode);
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				if (resultCode == Activity.RESULT_OK) {
					String address = data.getExtras()
							.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
					mChatService.connect(device, false);
				}
				break;
			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					// Bluetooth is now enabled, so set up a chat session
					enableBluetooth();
				} else {
					// User did not enable Bluetooth or an error occurred
					//Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				}
				break;
			default: 
				super.onActivityResult(requestCode, resultCode, data);
		}
	}
}

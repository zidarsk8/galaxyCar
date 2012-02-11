package org.psywerx.car;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.psywerx.car.bluetooth.BluetoothChatService;
import org.psywerx.car.bluetooth.DeviceListActivity;

public class GalaxyCarActivity extends Activity {

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

	private BluetoothChatService mBluetoothService = null;
	private GLSurfaceView mGlView;
	private WakeLock mWakeLock;
	
	private String mLastData = null;
	//private BtHelper mBluetooth;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothChatService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                mLastData = new String(readBuf, 0, msg.arg1);
                break;
            }
        }
    };

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

	private void init(){
		//mBluetooth = BtHelper.getBtHelper(this);

		this.mGlView = (GLSurfaceView) this.findViewById(R.id.glSurface);
		if (this.mGlView != null) {
			this.mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			this.mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			this.mGlView.setRenderer(new CarSurfaceViewRenderer(getResources()
					.getAssets(),new ModelLoader(this)));
			this.mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
		turnOnBluetooth();
	}

	private void turnOnBluetooth(){
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if(ba == null || !ba.isEnabled()){
			Toast.makeText(this , "We need ModriZob!", Toast.LENGTH_LONG).show();
			return;
		}
		mBluetoothService = new BluetoothChatService(this, mHandler);
		mBluetoothService.start();
		Intent data = new Intent(this , DeviceListActivity.class);
        startActivityForResult(data,REQUEST_CONNECT_DEVICE_INSECURE);
	}

	protected void onActvityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE_SECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					connectDevice(data, true);
				}
				break;
			case REQUEST_CONNECT_DEVICE_INSECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					connectDevice(data, false);
				}
				break;
		}	
	}

	private void connectDevice(Intent data, boolean secure){
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        mBluetoothService.connect(device, secure);
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

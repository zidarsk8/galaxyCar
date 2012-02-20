package org.psywerx.car;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.psywerx.car.bluetooth.BtHelper;
import org.psywerx.car.bluetooth.DeviceListActivity;
import org.psywerx.car.bluetooth.BtListener;
import org.psywerx.car.seekbar.VerticalSeekBar;
import org.psywerx.car.view.PospeskiView;
import org.psywerx.car.view.SteeringWheelView;
import org.psywerx.car.view.StevecView;

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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GalaxyCarActivity extends Activity implements BtListener{

	// Intent request codes
	public static final long THREAD_REFRESH_PERIOD = 50;
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	public static final String DEVICE_NAME = "device_name";

	private int mViewMode = 0; // 0 = normal view, 1 = Gl view, 2 = graph view

	private BluetoothAdapter mBluetoothAdapter;

	private GLSurfaceView mGlView;
	private WakeLock mWakeLock;
	private BtHelper mBtHelper;
	private Object mChartView;
	private VerticalSeekBar mAlphaBar;
	private ToggleButton mBluetoothButton;
	private ToggleButton mStartButton;
	private DataHandler mDataHandler;
	private Graph mGraph;
	private boolean mRefreshThread = true;
	private SteeringWheelView mSteeringWheelView;
	private PospeskiView mPospeskiView;
	private StevecView mStevecView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get wake lock:
		PowerManager pm = (PowerManager)
				getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"Sandboc lock");
		mWakeLock.acquire();

		init();
	}

	private void init() {
		mDataHandler = new DataHandler();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mGraph = new Graph();

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		mChartView = ChartFactory.getLineChartView(this,
				mGraph.getDemoDataset(), mGraph.getDemoRenderer());

		mGraph.start((GraphicalView) mChartView);

		layout.addView((View) mChartView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		mBtHelper = new BtHelper(getApplicationContext(), this, mDataHandler);
		mGlView = (GLSurfaceView) findViewById(R.id.glSurface);
		if (mGlView == null) {
			finish();// cant show stuff if you cant show stuff right :P
			return;
		}
		CarSurfaceViewRenderer svr = new CarSurfaceViewRenderer(getResources()
				.getAssets(), new ModelLoader(this));
		mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mGlView.setRenderer(svr);
		mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);


		((Button) findViewById(R.id.expandGlButton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mViewMode == 0){
					toGLView();
				}else{
					toNormalView();
				}
			}
		});
		((Button) findViewById(R.id.expandGraphButton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mViewMode == 0){
					toGraphView();
				}else{
					toNormalView();
				}
			}
		});

		mAlphaBar = (VerticalSeekBar) findViewById(R.id.alphaBar);
		mAlphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				float p = progress/100f;
				mDataHandler.setAlpha(p*p); 
			}
			public void onStartTrackingTouch(SeekBar seekBar){}
			public void onStopTrackingTouch(SeekBar seekBar){}
		});

		mBluetoothButton = (ToggleButton) findViewById(R.id.bluetoothButton);
		mBluetoothButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				enableBluetooth();
			}
		});

		mStartButton = (ToggleButton) findViewById(R.id.powerButton);
		mStartButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleStart();
			}
		});

		mSteeringWheelView = (SteeringWheelView) findViewById(R.id.steeringWheel);
		mPospeskiView = (PospeskiView) findViewById(R.id.pospeski);
		mStevecView = (StevecView) findViewById(R.id.stevec);
		// add data handlers to each view or class
		mDataHandler.registerListener(svr.getCar());
		mDataHandler.registerListener(mStevecView);
		mDataHandler.registerListener(mSteeringWheelView);
		mDataHandler.registerListener(mPospeskiView);
		mDataHandler
		.registerListener(mGraph);

		new Thread(){
			public void run() {
				while(mRefreshThread){
					try {
						Thread.sleep(100);
						mPospeskiView.postInvalidate();
						mSteeringWheelView.postInvalidate();
						mStevecView.postInvalidate();
					} catch (InterruptedException e) {
					}
				}
			};
		}.start();
	}

	private void toggleStart(){
		if (!mBluetoothButton.isChecked()) {
			mStartButton.setChecked(false);
			return;
		}
		if (mStartButton.isChecked()){
			mBtHelper.sendStart();
		}else{
			mBtHelper.sendStop();
		}
	}

	private void enableBluetooth() {
		if (mBluetoothButton.isChecked()) {
			D.dbgv("starting bluetooth thingy");
			if (mBluetoothAdapter == null) {
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not available", Toast.LENGTH_LONG).show();
				mBluetoothButton.setChecked(false);
			} else if (!mBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			} else {
				Intent serverIntent = new Intent(getApplicationContext(),
						DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		} else {
			D.dbgv("turn off bluetoot");
			btUnaviable();
			//mBtHelper.reset();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		D.dbgv("on result from : " + requestCode + "   resultCode "
				+ resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				mBtHelper.connect(device, false);
				new Thread(mBtHelper).start();
			}
			else {
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not available", Toast.LENGTH_LONG).show();
				mBluetoothButton.setChecked(false);				
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				enableBluetooth();
			} else {
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not available", Toast.LENGTH_LONG).show();
				mBluetoothButton.setChecked(false);
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		((GraphicalView) mChartView).repaint();
	}

	private void toNormalView(){
		D.dbgv("switching to normal view");
		mViewMode = 0;
	}
	private void toGLView(){
		D.dbgv("switching to gl view");
		mViewMode = 1;
	}
	private void toGraphView(){
		D.dbgv("switching to graph view");
		mViewMode = 2;
	}

	public void btUnaviable() {
		mBluetoothButton.setChecked(false);
		mBtHelper.reset();
		mStartButton.setChecked(false);
		mRefreshThread  = false;
		//TODO stop all threads
	}

}

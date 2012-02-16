package org.psywerx.car;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.psywerx.car.bluetooth.BtHelper;
import org.psywerx.car.bluetooth.DeviceListActivity;
import org.psywerx.car.view.StevecView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GalaxyCarActivity extends Activity {

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

    public static final String DEVICE_NAME = "device_name";
    
	private BluetoothAdapter mBluetoothAdapter;

	private GLSurfaceView mGlView;
	private WakeLock mWakeLock;
	private BtHelper mBtHelper;
	private Object mChartView;
	private ToggleButton mBluetoothButton;
	private ToggleButton mStartButton;
	private DataHandler mDataHandler;

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
		mDataHandler = new DataHandler();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		StevecView sv = (StevecView) findViewById(R.id.stevec);
		mDataHandler.setStevec(sv);
		
		mBtHelper = new BtHelper(getApplicationContext(),mDataHandler);
		mGlView = (GLSurfaceView) findViewById(R.id.glSurface);
		if (mGlView != null) {
			mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			mGlView.setRenderer(new CarSurfaceViewRenderer(getResources()
					.getAssets(), new ModelLoader(this)));
			mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
    	
		mBluetoothButton = (ToggleButton) findViewById(R.id.bluetoothButton);
		mBluetoothButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				enableBluetooth();
			}
		});

        mStartButton = (ToggleButton) findViewById(R.id.powerButton);
		mStartButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBtHelper.sendStart();
			}
		});
	}

	private void enableBluetooth() {
		if (mBluetoothButton.isChecked()){
			D.dbgv("starting bluetooth thingy");
			if (mBluetoothAdapter == null) {
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not available", Toast.LENGTH_LONG).show();
			} else if (!mBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			} else {
				Intent serverIntent = new Intent(getApplicationContext(),
						DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		}else{
			D.dbgv("turn off bluetoot");
			mBtHelper.reset();
			
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		D.dbgv("on result from : " + requestCode + "   resultCode "
				+ resultCode);
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				if (resultCode == Activity.RESULT_OK) {
					String address = data.getExtras()
							.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
					mBtHelper.connect(device, false);
					new Thread(mBtHelper).start();
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

	private XYMultipleSeriesRenderer getDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.BLUE);
		r.setPointStyle(PointStyle.SQUARE);
		r.setFillBelowLine(true);
		r.setFillBelowLineColor(Color.WHITE);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.GREEN);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.LTGRAY);
		return renderer;
	}

	private static final int SERIES_NR = 2;

	private XYMultipleSeriesDataset getDemoDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = 10;
		Random r = new Random();
		for (int i = 0; i < SERIES_NR; i++) {
			XYSeries series = new XYSeries("Demo series " + (i + 1));
			for (int k = 0; k < nr; k++) {
				series.add(k, 20 + r.nextInt() % 100);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) { 
		    LinearLayout layout = (LinearLayout)findViewById(R.id.chart); 
		    mChartView = ChartFactory.getLineChartView(this, getDemoDataset(), 
		getDemoRenderer()); 
			layout.addView((View) mChartView, new LayoutParams		(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); 
		  } else { 
		    ((GraphicalView) mChartView).repaint(); 
		  } 
	}

}

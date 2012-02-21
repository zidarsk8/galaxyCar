package org.psywerx.car;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.psywerx.car.bluetooth.BtHelper;
import org.psywerx.car.bluetooth.BtListener;
import org.psywerx.car.bluetooth.DeviceListActivity;
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
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GalaxyCarActivity extends Activity implements BtListener {

	// Intent request codes
	public static final long THREAD_REFRESH_PERIOD = 50;
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	public static final String DEVICE_NAME = "device_name";

	private BluetoothAdapter mBluetoothAdapter;

	private GLSurfaceView mGlView;
	private WakeLock mWakeLock;
	private Vibrator mVib = null;
	private BtHelper mBtHelper;
	private VerticalSeekBar mAlphaBar;
	private ToggleButton mBluetoothButton;
	private ToggleButton mStartButton;
	private DataHandler mDataHandler;
	private Graph mGraph;
	private boolean mRefreshThread = true;
	private SteeringWheelView mSteeringWheelView;
	private PospeskiView mPospeskiView;
	private StevecView mStevecView;
	private int mViewMode; // 0 normal 1 gl 2 graph
	private GraphicalView mChartViewAll;
	private GraphicalView mChartViewTurn;
	private GraphicalView mChartViewRevs;
	private GraphicalView mChartViewG;
	private CarSurfaceViewRenderer svr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get wake lock:
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Sandboc lock");
		mWakeLock.acquire();

		mVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		init();
	}

	private void init() {
		mViewMode = 0;
		mDataHandler = new DataHandler();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mGraph = new Graph();

		LinearLayout graphAll = (LinearLayout) findViewById(R.id.chart);
		mChartViewAll = ChartFactory.getLineChartView(this,
				mGraph.getDatasetAll(), mGraph.getRendererAll());

		LinearLayout graphTurn = (LinearLayout) findViewById(R.id.chartGL2);
		mChartViewTurn = ChartFactory.getLineChartView(this,
				mGraph.getDatasetTurn(), mGraph.getRendererTurn());

		LinearLayout graphRevs = (LinearLayout) findViewById(R.id.chartGL3);
		mChartViewRevs = ChartFactory.getLineChartView(this,
				mGraph.getDatasetRevs(), mGraph.getRendererRevs());

		LinearLayout graphG = (LinearLayout) findViewById(R.id.chartGL4);
		mChartViewG = ChartFactory.getLineChartView(this, mGraph.getDatasetG(),
				mGraph.getRendererG());

		//mGraph.start();

		graphAll.addView((View) mChartViewAll, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		graphTurn.addView((View) mChartViewTurn, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		graphRevs.addView((View) mChartViewRevs, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		graphG.addView((View) mChartViewG, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		mBtHelper = new BtHelper(getApplicationContext(), this, mDataHandler);

		svr = new CarSurfaceViewRenderer(new ModelLoader(this));

		mGlView = (GLSurfaceView) findViewById(R.id.glSurface);
		if (mGlView == null) {
			finish();
			return;
		}

		mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mGlView.setRenderer(svr);
		mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		mGlView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				svr.nextCameraPosition();

			}
		});

		((Button) findViewById(R.id.expandGlButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						toGLView();
					}
				});
		((Button) findViewById(R.id.expandGraphButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						toGraphView();
					}
				});
		((Button) findViewById(R.id.normalViewButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						toNormalView();
					}
				});
		((Button) findViewById(R.id.normalViewButton2))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						toNormalView();
					}
				});

		mAlphaBar = (VerticalSeekBar) findViewById(R.id.alphaBar);
		mAlphaBar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						float p = progress / 100f;
						mDataHandler.setAlpha(p * p);
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
					}
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
		mDataHandler.registerListener(mGraph);

		new Thread() {
			public void run() {
				while (mRefreshThread) {
					try {
						switch (mViewMode) {
						case 0:
							Thread.sleep(100);
							mPospeskiView.postInvalidate();
							mSteeringWheelView.postInvalidate();
							mStevecView.postInvalidate();
							mChartViewAll.repaint();
							break;
						case 1:
							Thread.sleep(100);
							// mPospeskiView.postInvalidate();
							// mStevecView.postInvalidate();
							break;
						case 2:
							Thread.sleep(50);
							mChartViewRevs.repaint();
							mChartViewTurn.repaint();
							mChartViewG.repaint();
							break;
						default:
							Thread.sleep(100);
						}

					} catch (InterruptedException e) {
					}
				}
			};
		}.start();
	}

	private void toggleStart() {
		if (!mBluetoothButton.isChecked()) {
			mStartButton.setChecked(false);
			return;
		}
		if (mStartButton.isChecked()) {
			mBtHelper.sendStart();
			mVib.vibrate(200);
		} else {
			mBtHelper.sendStop();
			mVib.vibrate(200);
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
			// mBtHelper.reset();
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
			} else {
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
	}

	private void toNormalView() {
		D.dbgv("switching to normal view");
		switch (mViewMode) {
		case 1:
			RelativeLayout glViewLayou = ((RelativeLayout) findViewById(R.id.glViewLayou));
			RelativeLayout normalViewLayou = ((RelativeLayout) findViewById(R.id.normalViewLayou));
			glViewLayou.removeView(mGlView);
			// glViewLayou.removeView(mPospeskiView);
			// glViewLayou.removeView(mStevecView);
			normalViewLayou.addView(mGlView);
			// normalViewLayou.addView(mPospeskiView);
			// normalViewLayou.addView(mStevecView);
			findViewById(R.id.expandGlButton).bringToFront();
			normalViewLayou.setVisibility(View.VISIBLE);
			glViewLayou.setVisibility(View.INVISIBLE);

			RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(
					730, 400);
			l.setMargins(500, 40, 0, 0);
			mGlView.setLayoutParams(l);
			break;
		case 2:
			((RelativeLayout) findViewById(R.id.normalViewLayou))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.glViewLayou))
					.setVisibility(View.INVISIBLE);
			break;
		}
		((RelativeLayout) findViewById(R.id.graphViewLayou))
				.setVisibility(View.INVISIBLE);
		mViewMode = 0;
	}

	private void toGraphView() {
		D.dbgv("switching to graph view");
		((RelativeLayout) findViewById(R.id.normalViewLayou))
				.setVisibility(View.INVISIBLE);
		((RelativeLayout) findViewById(R.id.graphViewLayou))
				.setVisibility(View.VISIBLE);
		((RelativeLayout) findViewById(R.id.glViewLayou))
				.setVisibility(View.INVISIBLE);
		mViewMode = 2;
	}

	private void toGLView() {
		D.dbgv("switching to gl view  " + mGlView.getHeight() + "  "
				+ mGlView.getWidth());
		((RelativeLayout) findViewById(R.id.graphViewLayou))
				.setVisibility(View.INVISIBLE);

		RelativeLayout glViewLayou = ((RelativeLayout) findViewById(R.id.glViewLayou));
		RelativeLayout normalViewLayou = ((RelativeLayout) findViewById(R.id.normalViewLayou));
		normalViewLayou.removeView(mGlView);
		// normalViewLayou.removeView(mPospeskiView);
		// normalViewLayou.removeView(mStevecView);
		glViewLayou.addView(mGlView);
		// glViewLayou.addView(mPospeskiView);
		// glViewLayou.addView(mStevecView);
		findViewById(R.id.normalViewButton2).bringToFront();
		normalViewLayou.setVisibility(View.INVISIBLE);
		glViewLayou.setVisibility(View.VISIBLE);

		RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(1170,
				670);
		l.setMargins(60, 40, 0, 0);
		mGlView.setLayoutParams(l);
		mViewMode = 1;
	}

	@Override
	protected void onStop() {
		super.onStop();
		btUnaviable();
	}

	public void btUnaviable() {
		mBluetoothButton.setChecked(false);
		mBtHelper.reset();
		mStartButton.setChecked(false);
		mRefreshThread = false;
		// TODO stop all threads
	}

}

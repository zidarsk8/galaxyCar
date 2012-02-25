package org.psywerx.car;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.psywerx.car.bluetooth.BtHelper;
import org.psywerx.car.bluetooth.BtListener;
import org.psywerx.car.bluetooth.DeviceListActivity;
import org.psywerx.car.seekbar.VerticalSeekBar;
import org.psywerx.car.view.PospeskiView;
import org.psywerx.car.view.SteeringWheelView;
import org.psywerx.car.view.StevecView;
import org.psywerx.car.view.ZavojViewLeft;
import org.psywerx.car.view.ZavojViewRight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Main application activity
 * 
 */
public class GalaxyCarActivity extends Activity implements BtListener{

	// Intent request codes
	public static final long THREAD_REFRESH_PERIOD = 50;
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	//private BluetoothAdapter mBluetoothAdapter;

	private GLSurfaceView mGlView;
	private WakeLock mWakeLock;
	private Vibrator mVibrator = null;
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
	private CarSurfaceViewRenderer mCarSurfaceView;
	private RelativeLayout mGraphViewLayout;
	private RelativeLayout mGlViewLayout;
	private RelativeLayout mNormalViewLayout;
	private ToggleButton mAvarageButton;
	private ZavojViewLeft mZavjoLevo;
	private ZavojViewRight mZavjoDesno;
	private TextView mZavjoLevoText;
	private TextView mZavjoDesnoText;
	private TextView mTextMSn;
	private TextView mTextMn;
	private TextView mTextRPMn;
	private Car mCar;
	private TextView mTextAvgSpeed;
	private TextView mTextMaxSpeed;
	private TextView mTextDrivenM;
	private TextView mTextTimeDriven;

	private long mTimeDriven;
	private long mTimeDrivenEnd;

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

	/**
	 * Inits all the components of the application
	 */
	private void init() {
		mViewMode = 0;

		setGraphViews();

		mAlphaBar = (VerticalSeekBar) findViewById(R.id.alphaBar);
		mBluetoothButton = (ToggleButton) findViewById(R.id.bluetoothButton);
		mGraphViewLayout = ((RelativeLayout) findViewById(R.id.graphViewLayou));
		mGlViewLayout = ((RelativeLayout) findViewById(R.id.glViewLayou));
		mNormalViewLayout = ((RelativeLayout) findViewById(R.id.normalViewLayou));
		mPospeskiView = (PospeskiView) findViewById(R.id.pospeski);
		mStartButton = (ToggleButton) findViewById(R.id.powerButton);
		mSteeringWheelView = (SteeringWheelView) findViewById(R.id.steeringWheel);
		mStevecView = (StevecView) findViewById(R.id.stevec);
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mZavjoLevo = (ZavojViewLeft) findViewById(R.id.zavojLevo);
		mZavjoDesno = (ZavojViewRight) findViewById(R.id.zavojDesno);
		mZavjoLevoText = (TextView) findViewById(R.id.textNrLeft);
		mZavjoDesnoText = (TextView) findViewById(R.id.textNrRight);
		mTextMSn = (TextView) findViewById(R.id.textMSn);
		mTextMn = (TextView) findViewById(R.id.textMn);
		mTextRPMn = (TextView) findViewById(R.id.textRPMn);
		mTextAvgSpeed = (TextView) findViewById(R.id.textAvgSpeed);
		mTextMaxSpeed = (TextView) findViewById(R.id.textMaxSpeed);
		mTextDrivenM = (TextView) findViewById(R.id.textDrivenM);
		mTextTimeDriven = (TextView) findViewById(R.id.textTimeDriven);

		//mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		mDataHandler = new DataHandler();
		mDataSimulator = new SimulationDataGenerator();
		mDataSimulator.setCtx(getApplicationContext());
		mBtHelper = new BtHelper(getApplicationContext(), this, mDataHandler);

		mCarSurfaceView = new CarSurfaceViewRenderer(new ModelLoader(this));

		mGlView = (GLSurfaceView) findViewById(R.id.glSurface);
		if (mGlView == null) {
			finish();
			return;
		}

		mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mGlView.setRenderer(mCarSurfaceView);
		mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		// add data handlers to each view or class
		mCar = mCarSurfaceView.getCar();
		mDataHandler.registerListener(mCar);
		mDataHandler.registerListener(mStevecView);
		mDataHandler.registerListener(mSteeringWheelView);
		mDataHandler.registerListener(mPospeskiView);
		mDataHandler.registerListener(mGraph);

		setButtonListeners();

	}

	/**
	 * Sends start and stop signals
	 */
	private void toggleStart() {
		if (!mBluetoothButton.isChecked()) {
			mStartButton.setChecked(false);
			return;
		}
		if (mStartButton.isChecked()) {
			mBtHelper.sendStart();
			mTimeDriven = System.nanoTime();
			mTimeDrivenEnd = 0;
			mVibrator.vibrate(200);
		} else {
			mTimeDrivenEnd = System.nanoTime();
			mBtHelper.sendStop();
			mVibrator.vibrate(200);
		}
	}

	/**
	 * Enables bluetooth if available
	 */
	private void enableBluetooth() {
		if (mBluetoothButton.isChecked()) {

			Intent serverIntent = new Intent(getApplicationContext(),
					DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

			//			if (mBluetoothAdapter == null) {
			//				Toast.makeText(getApplicationContext(),
			//						"Bluetooth is not available", Toast.LENGTH_LONG).show();
			//				mBluetoothButton.setChecked(false);
			//			} else if (!mBluetoothAdapter.isEnabled()) {
			//				Intent enableIntent = new Intent(
			//						BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			//			} else {
			//				Intent serverIntent = new Intent(getApplicationContext(),
			//						DeviceListActivity.class);
			//				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			//			}
		} else {
			btUnaviable();

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
				//BluetoothDevice device = mBluetoothAdapter
				//		.getRemoteDevice(address);
				//mBtHelper.connect(device, false);

				Toast.makeText(getApplicationContext(),
						"Reading data from \n"+address, Toast.LENGTH_LONG).show();

				mDataSimulator.setDataType(Integer.parseInt(address.substring(0, 1)));
				new Thread(mDataSimulator).start();

				//timestamp = System.nanoTime();
				startRepaintingThread();
			} else {
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not available", Toast.LENGTH_LONG).show();
				mBluetoothButton.setChecked(false);
				mBtHelper.stopCar();
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


	private SimulationDataGenerator mDataSimulator;

	private class SimulationDataGenerator implements Runnable{
		private int dataType = 0;
		private boolean run = false;
		private Context ctx;

		public void run() {
			try {
				run = true;
				while (run){
					switch (dataType) {
					case 1:
						if (mStartButton.isChecked()){
							Thread.sleep(35);
							mBtHelper.recieveData(getTestLoopData());
						}else{
							mBtHelper.stopCar();
							Thread.sleep(500);
						}
						break;
					case 2:
						while (run){
							if (mStartButton.isChecked()){
								InputStreamReader is = new InputStreamReader(ctx.getAssets().open("circlelog.csv"));
								BufferedReader br = new BufferedReader(is);
								String line;
								while ((line = br.readLine()) != null){
									Thread.sleep(350);
									mBtHelper.recieveData(line);
								}
							}else {
								Thread.sleep(500);
							}
						}
						break;
					case 3:
						while (run){
							if (mStartButton.isChecked()){
								InputStreamReader is = new InputStreamReader(ctx.getAssets().open("circlelog35ms.csv"));
								BufferedReader br = new BufferedReader(is);
								String line;
								while ((line = br.readLine()) != null){
									Thread.sleep(35);
									mBtHelper.recieveData(line);
								}
							}else {
								Thread.sleep(500);
							}
						}
						break;

					default:

						break;
					}

				}
			} catch (Exception e) {
				D.dbge(e.toString(),e);
			}
		}
		public synchronized void stop(){
			run = false;
		}
		public void setDataType(int dataType) {
			this.dataType = dataType;
		}
		public void setCtx(Context ctx) {
			this.ctx = ctx;
		}
	};


	private long timestamp = 0;

	protected String getTestLoopData(){
		if (timestamp == 0) {
			//return "0,0,0,5,0";
			timestamp = System.nanoTime();
		}
		int speed = 30;
		long cur = System.nanoTime()-timestamp;
		double[] a = {
				5,
				5  +  Math.PI*10,
				5  +  Math.PI*10  +  15,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2  +  46,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2  +  46  +  Math.PI*16/2,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2  +  46  +  Math.PI*16/2  +  Math.PI*12,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2  +  46  +  Math.PI*16/2  +  Math.PI*12  +  Math.PI*1,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2  +  46  +  Math.PI*16/2  +  Math.PI*12  +  Math.PI*1 + 8,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2  +  46  +  Math.PI*16/2  +  Math.PI*12  +  Math.PI*1 + 8 + 5.7,
				5  +  Math.PI*10  +  15  +  3*Math.PI*18/2  +  46  +  Math.PI*16/2  +  Math.PI*12  +  Math.PI*1 + 8 + 6 + 9,
		};

		cur /= 1e8;
		cur %= a[a.length-1];
		double ff = Math.sin(2*Math.PI*((cur-a[3])/(a[4]-a[3])))*15;
		float x,y,z;
		x = -0.0430172172f;
		y = -0.0880380459f;
		z = 0.4048961114f;
		String ss = ""+(x+Math.random()*0.4-0.2)+","+
				(y+Math.random()*0.4-0.2)+","+
				(z+Math.random()*0.4-0.2)+",";
		if       (cur < a[0]){ return ss+speed+",5" ;
		}else if (cur < a[1]){ return ss+speed+",10" ;
		}else if (cur < a[2]){ return ss+speed+",5" ;
		}else if (cur < a[3]){ return ss+speed+",2" ;
		}else if (cur < a[4]){ return ss+(speed+ff)+",5" ;
		}else if (cur < a[5]){ return ss+speed+",3" ;
		}else if (cur < a[6]){ return ss+speed+",1" ;
		}else if (cur < a[7]){ return ss+speed+",0" ;
		}else if (cur < a[8]){ return ss+speed+",5" ;
		}else if (cur < a[9]){ return ss+speed+",0" ;
		}else if (cur < a[10]){return ss+speed+",5" ;
		}else {                return ss+speed+",0" ;
		}
	}




	/**
	 * Switches from full screen opengl/graph views
	 * to the normal "all in one" view
	 */
	private void toNormalView() {
		D.dbgv("switching to normal view");
		switch (mViewMode) {
		case 1:
			mGlViewLayout.removeView(mGlView);
			mNormalViewLayout.addView(mGlView);

			findViewById(R.id.backGroundOkvir).bringToFront();
			findViewById(R.id.expandGlButton).bringToFront();
			findViewById(R.id.expandGraphButton).bringToFront();
			findViewById(R.id.alphaBar).bringToFront();
			findViewById(R.id.averageButton).bringToFront();
			mPospeskiView.bringToFront();
			mNormalViewLayout.setVisibility(View.VISIBLE);
			mGlViewLayout.setVisibility(View.INVISIBLE);

			RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(
					720, 400);
			l.setMargins(530, 40, 0, 0);
			mGlView.setLayoutParams(l);
			break;
		case 2:
			mNormalViewLayout.setVisibility(View.VISIBLE);
			mGlViewLayout.setVisibility(View.INVISIBLE);
			break;
		}
		mGraphViewLayout.setVisibility(View.INVISIBLE);
		mViewMode = 0;
	}
	/**
	 * Shows graphs in fullscreen
	 */
	private void toGraphView() {
		D.dbgv("switching to graph view");
		mNormalViewLayout.setVisibility(View.INVISIBLE);
		mGraphViewLayout.setVisibility(View.VISIBLE);
		mGlViewLayout.setVisibility(View.INVISIBLE);
		mViewMode = 2;
		if(!mBluetoothButton.isChecked()){
			Toast.makeText(getApplicationContext(),
					"Calculating history...", Toast.LENGTH_SHORT).show();
			mDataHandler.setAlpha(mAlphaBar.getProgress());
			mDataHandler.setSmoothMode(mAvarageButton.isChecked());
			float[][] history;
			if(mAvarageButton.isChecked()){
				history = mDataHandler.getWholeHistoryRolingAvg();
			} else {
				history = mDataHandler.getWholeHistoryAlpha();
			}

			mGraph.insertWholeHistory(history);
		}
	}
	/**
	 * Shows opengl view in fullscreen
	 */
	private void toGLView() {
		D.dbgv("switching to gl view  " + mGlView.getHeight() + "  "
				+ mGlView.getWidth());
		mGraphViewLayout.setVisibility(View.INVISIBLE);
		mNormalViewLayout.removeView(mGlView);
		mGlViewLayout.addView(mGlView);
		findViewById(R.id.backGroundOkvirGl).bringToFront();
		mPospeskiView.bringToFront();
		findViewById(R.id.normalViewButton2).bringToFront();
		findViewById(R.id.textM).bringToFront();
		findViewById(R.id.textMS).bringToFront();
		findViewById(R.id.textRPM).bringToFront();
		findViewById(R.id.textMn).bringToFront();
		findViewById(R.id.textMSn).bringToFront();
		findViewById(R.id.textRPMn).bringToFront();
		mNormalViewLayout.setVisibility(View.INVISIBLE);
		mGlViewLayout.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(1230,
				650);
		l.setMargins(20, 40, 0, 0);
		mGlView.setLayoutParams(l);
		mViewMode = 1;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(mViewMode != 0){
				toNormalView();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		btUnaviable();
	}

	/**
	 * Called when bluetooth connection is lost
	 */
	public void btUnaviable() {
		mBluetoothButton.setChecked(false);
		mBtHelper.reset();
		mStartButton.setChecked(false);
		mRefreshThread = false;
		mDataSimulator.stop();
	}

	/**
	 * Adds the graph renderers to their views
	 */
	private void setGraphViews() {
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

		graphAll.addView((View) mChartViewAll, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		graphTurn.addView((View) mChartViewTurn, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		graphRevs.addView((View) mChartViewRevs, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		graphG.addView((View) mChartViewG, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	/**
	 * Initializes all the button listeners
	 */
	private void setButtonListeners() {
		mGlView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mCarSurfaceView.setNextCameraPosition();
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
		mAvarageButton = ((ToggleButton) findViewById(R.id.averageButton));
		mAvarageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mDataHandler.setSmoothMode(((ToggleButton) v).isChecked());
				mChartViewAll.repaint();
			}
		});

		mStartButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleStart();
			}
		});
		mBluetoothButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				enableBluetooth();
			}
		});
		mAlphaBar
		.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar,
					int progress, boolean fromUser) {
				mDataHandler.setAlpha(progress);
				mChartViewAll.repaint();
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

	}

	/**
	 * Repaint thread repaints the pospeski, steeringWheel, stevec and chart
	 * views
	 */


	private void startRepaintingThread() {
		mRefreshThread = true;

		new Thread() {
			public void run() {
				int skip = 0;
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
							Thread.sleep(300);
							runOnUiThread(new Runnable() {
								public void run() {
									/* na ui threadu mormo samo text stimat*/
									mTextRPMn.setText(String.format("%.1f",mCar.mSpeed));
									mTextMn.setText(String.format("%.0f",Car.mPrevozeno));
									mTextMSn.setText(String.format("%.1f",mCar.mSpeed*Car.METRI_NA_OBRAT));
								}
							});
							break;
						case 2:
							Thread.sleep(50);
							mChartViewRevs.repaint();
							mChartViewTurn.repaint();
							mChartViewG.repaint();
							if (++skip%3 == 0){
								mZavjoDesno.postInvalidate();
								mZavjoLevo.postInvalidate();
								runOnUiThread(new Runnable() {
									public void run() {
										/* na ui threadu mormo samo text stimat*/
										mZavjoDesnoText.setText(""+((int)Car.turnRight/360));
										mZavjoLevoText.setText(""+((int)Car.turnLeft/360));
										mTextAvgSpeed.setText(String.format("%.1f",(float)(mCar.avgSpeed/mCar.avgSpeedCounter)));
										mTextMaxSpeed.setText(String.format("%.1f", (float)mCar.maxSpeed));
										mTextDrivenM.setText(String.format("%.0f",(float)Car.mPrevozeno));
										float tDrvb = 0;
										if(mTimeDriven > 0) 
											tDrvb = ((mTimeDrivenEnd > 0 ? mTimeDrivenEnd : System.nanoTime())-mTimeDriven)/1e9f;
										mTextTimeDriven.setText(String.format("%.1f", tDrvb));
										//TODO: izpisi
										// povprecna hitrost = mCar.avgSpeed/mCar.avgSpeedCounter
										// prevozeno kilometrov = mCar.mPrevozeno
										// max hitrost = mCar.maxSpeed
										// se neki se spomn !

										//TODO: dodat staticni tekst zravn okn k pove kaj kera cifra pome (levi desni zavoj ... itd

									}
								});
							}
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

}

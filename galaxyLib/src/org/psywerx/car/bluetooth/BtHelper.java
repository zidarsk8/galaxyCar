package org.psywerx.car.bluetooth;

import org.psywerx.car.D;
import org.psywerx.car.DataHandler;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class BtHelper {

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_TOAST_FAIL = 6;
	public static final String TOAST = "toast";


	private static final float mMetriNaObrat = 0.0402123859659494f;
	private Context mContext = null;
	private BtListener mBtListener = null;
	private BluetoothChatService mBluetoothService = null;
	private DataHandler mDataHandler = null;

	private long mTimestamp;

	private String[] arr ;

	/**
	 * handle feedback from BluetoothChatService
	 */
	private final Handler mHandler = new Handler(){

		private String mConnectedDeviceName = null;

		public static final String TOAST = "toast";

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					break;
				case BluetoothChatService.STATE_CONNECTING:
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					stopCar();
					break;
				}
				break;
			case MESSAGE_WRITE:
				//D.dbgv("Me:  " + new String((byte[]) msg.obj));
				break;
			case MESSAGE_READ:
				recieveData(new String((byte[]) msg.obj, 0, msg.arg1));
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString("device_name");
				Toast.makeText(mContext, "Connected to "
						+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(mContext, msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST_FAIL:
				Toast.makeText(mContext, msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				stopCar();
				mBtListener.btUnaviable();
				break;
			}
		}
	};

	public BtHelper(Context ctx, BtListener btl, DataHandler dl){
		mContext = ctx;
		mBtListener = btl;
		mBluetoothService = new BluetoothChatService(ctx, mHandler);
		mDataHandler = dl;		
	}

	/**
	 * Connect device with bluetooth
	 * @param device target device to connect
	 * @param secure set security level of connection
	 */
	public void connect(BluetoothDevice device, boolean secure){
		mBluetoothService.connect(device,secure);
	}

	/**
	 * Stop bluetooth connection
	 * Stop car
	 */
	public void reset(){
		if (mBluetoothService != null){
			mBluetoothService.stop();
		}
		stopCar();
	}

	/**
	 * Send start signal
	 */
	public void sendStart(){
		if (mBluetoothService.getState() == BluetoothChatService.STATE_CONNECTED)
			mBluetoothService.write("start".getBytes());
	}

	/**
	 * Send stop signal
	 */
	public void sendStop(){
		if (mBluetoothService.getState() == BluetoothChatService.STATE_CONNECTED){
			mBluetoothService.write("stop".getBytes());
			stopCar();
		}
	}


	/**
	 * Send data signal and wait for feedback
	 */
	public void sendData(){
		mBluetoothService.write("podatki".getBytes());
	}

	/**
	 * The function checks if the csv is correct and splits the csv string into a float array, 
	 * which is passed to dataHandler. 
	 * 
	 * format of the data output is:
	 * accel x, 
	 * accel y, 
	 * accel z,
	 * revolutions per minute,
	 * wheel turn value,
	 * time since last data received,
	 * distance traveled since last data received,
	 * 
	 * @param data csv string received from bluetooth (x,y,z,speed,turn)
	 */
	public void recieveData(String data){
		try {
			long ct = System.nanoTime();
			final int len = 5;
			float[] cur = new float[7];
			if ("start pressed".equals(data)){
				mTimestamp = System.nanoTime();
				sendData();
			}else if ("stop pressed".equals(data)){

			}else{
				sendData();
				arr= data.split(",");
				if (arr.length != 5 ){
					D.dbge("wrong data set: "+data);
					return;
				}
				for (int i = 0; i < len; i++){
					cur[i] = Float.parseFloat(arr[i]);
				}
				cur[5] = (ct-mTimestamp)/1e9f; //cas od zadnje meritve
				//                    cas v sekundah       obratov na sekundo    koliko metrov naredi en obrat
				cur[6] = (float)( ((ct-mTimestamp)/1e9)   *    cur[3]/60)        * mMetriNaObrat ;
				D.dbgv(String.format("%.5f  %.5f  %.5f  %.5f  %.5f  ",cur[0],cur[1],cur[2],cur[3],cur[4]));
				mTimestamp = ct;
			}
			mDataHandler.updateData(cur);
		} catch (Exception e) {
			D.dbge("error recieving data fro bluetooth",e);
		}
	}

	public BluetoothChatService getChatService(){
		return mBluetoothService;
	}

	/**
	 * Stop the car
	 */
	public void stopCar(){
		mDataHandler.setAlpha(100);
		mDataHandler.setSmoothMode(true);
		mDataHandler.updateData(new float[]{0,0,0,0,0,System.nanoTime(),0});
	}

}

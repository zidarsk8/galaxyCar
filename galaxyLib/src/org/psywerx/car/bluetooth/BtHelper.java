package org.psywerx.car.bluetooth;

import org.psywerx.car.D;
import org.psywerx.car.DataListener;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class BtHelper implements Runnable{

	private static final long REQUEST_TIMEOUT = 100;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final String TOAST = "toast";

	private Context mContext = null;
	private BluetoothChatService mBluetoothService = null;
	private DataListener mDataListener = null;

	private boolean mWait = true;

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
			}
		}
	};

	public void run(){
		try{
			mWait = true;
			while (mWait){
				Thread.sleep(REQUEST_TIMEOUT);
				sendData();
			}
		}catch(Exception e){
			D.dbge("Bt helper running stuff ",e);
		}
	}

	public BtHelper(Context ctx, DataListener dl){
		mContext = ctx;
		mBluetoothService = new BluetoothChatService(ctx, mHandler);
		mDataListener = dl;
	}

	public void connect(BluetoothDevice device, boolean secure){
		mBluetoothService.connect(device,secure);
	}

	public void reset(){
		mBluetoothService.stop();
	}

	public synchronized void sendStart(){
		if (mBluetoothService.getState() == BluetoothChatService.STATE_CONNECTED)
			mBluetoothService.write("start".getBytes());
	}

	public synchronized void sendStop(){
		if (mBluetoothService.getState() == BluetoothChatService.STATE_CONNECTED)
			mBluetoothService.write("stop".getBytes());
	}

	public synchronized void sendData(){
		//D.dbgv("send data");
		if (mBluetoothService.getState() == BluetoothChatService.STATE_CONNECTED){
			mWait = false;
			mBluetoothService.write("podatki".getBytes());
		}
	}

	/**
	 * The function checks if the csv is correct and splits the csv string into a float array, which is passed to dataHandler
	 * 
	 * @param data csv string recieved from bluetooth (x,y,z,speed,turn)
	 */
	public synchronized void recieveData(String data){
		//D.dbge("recieved "+data);
		try {
			if ("start pressed".equals(data)){
				sendData();
				return;
			}
			String[] arr = data.split(",");
			if (arr.length != 5 ){
				D.dbge("wrong data set: "+data);
				return;
			}
			int len = arr.length;
			float[] cur = new float[len];
			for (int i = 0; i < len; i++){
				cur[i] = Float.parseFloat(arr[i]);
			}
			mDataListener.updateData(cur);

		} catch (Exception e) {
			D.dbge("error recieving data fro bluetooth",e);
		}finally{
			sendData();
		}
	}

	public BluetoothChatService getChatService(){
		return mBluetoothService;
	}

}

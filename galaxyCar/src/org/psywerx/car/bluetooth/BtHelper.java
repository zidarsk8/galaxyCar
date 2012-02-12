package org.psywerx.car.bluetooth;

import java.lang.Runnable;
import java.util.ArrayList;

import org.psywerx.car.D;
import org.psywerx.car.GalaxyCarActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class BtHelper implements Runnable{
	
	private static final int TIMEOUT = 300;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";

	private Context mContext = null;
	private BluetoothChatService mBluetoothService = null;
	
	
	private boolean dataLock = false;
	private boolean run = true;
	private ArrayList<float[]> mHistory = null;
	private int mReadIndex;

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
					D.dbgv("Me:  " + new String((byte[]) msg.obj));
					break;
				case MESSAGE_READ:
					recieveData(new String((byte[]) msg.obj, 0, msg.arg1));
					break;
				case MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(GalaxyCarActivity.DEVICE_NAME);
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
		while (run){
			Thread.sleep(TIMEOUT);
			sendData();
		}
		}catch(Exception e){
			D.dbge("Bt helper running stuff ",e);
		}
	}

	public BtHelper(Context ctx){
		mContext = ctx;
		mBluetoothService = new BluetoothChatService(ctx, mHandler);
		mHistory = new ArrayList<float[]>();
		mReadIndex = 0;
	}

	public void connect(BluetoothDevice device, boolean secure){
		mBluetoothService.connect(device,secure);
	}
	
	public void stopConnection(){
		run = false;
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
		if (!dataLock && mBluetoothService.getState() == BluetoothChatService.STATE_CONNECTED){
			dataLock = true;
			mBluetoothService.write("podatki".getBytes());			
		}
	}
	public synchronized void recieveData(String data){
		data = System.nanoTime()+","+data;
		D.dbgv(data);
		String[] arr = data.split(",");
		if (arr.length != 6 ){
			D.dbge("wrong data set");
			return;
		}
		int len = arr.length;
		float[] cur = new float[len];
		for (int i = 0; i < len; i++){
			cur[i] = Float.parseFloat(arr[i]);
		}
		mHistory.add(cur);
		dataLock = false;
	}
	
	public synchronized float[][] getUnreadData(){
		int histSize = mHistory.size();
		float[][] data = new float[histSize-mReadIndex][5];
		for (int i = 0; i < histSize-mReadIndex; i++ ){
			data[i]= mHistory.get(mReadIndex+i);
		}
		mReadIndex = histSize-1;
		return data;
	}
	
	public synchronized float[] getLastData(){
		if(mHistory.isEmpty())
			return new float[6];
		return mHistory.get(mHistory.size()-1);
	}

	public BluetoothChatService getChatService(){
		return mBluetoothService;
	}

}

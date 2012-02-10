package org.psywerx.car.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class BtHelper{
	
	private Context mContext = null;
	private BluetoothChatService mBluetoothService = null;
	private String mLastData = null;
	
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
	
	public static BtHelper getBtHelper(Context context){
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if(ba == null){
			Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			return null;
		}
		else if(!ba.isEnabled()){
			Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_LONG).show();
			return null;
		}
		else{
			return new BtHelper(context);
		}
	}
	
	public static void enableBluetooth(Context context){
		context.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
	}
	
	private BtHelper(Context context){
		mContext = context;
		
		mBluetoothService = new BluetoothChatService(mContext, mHandler);
		startConnection();
	}
	
	public void startConnection(){
		mBluetoothService.start();
		Intent data = new Intent(mContext, DeviceListActivity.class);
        mContext.startActivity(data);
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        mBluetoothService.connect(device, false);
	}
	
	public void stopConnection(){
		mBluetoothService.stop();
	}
	
	public synchronized boolean sendStart(){
		if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED)
            return false;
		mBluetoothService.write("start".getBytes());
		return true;
	}
	
	public synchronized boolean sendStop(){
		if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED)
            return false;
		mBluetoothService.write("stop".getBytes());
		return true;
	}
	
	public synchronized String[] sendData(){
		if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED)
            return null;
		mBluetoothService.write("podatki".getBytes());
		//TODO wait for response
		return mLastData.split(",");
	}
}
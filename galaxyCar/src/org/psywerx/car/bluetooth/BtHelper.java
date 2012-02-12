package org.psywerx.car.bluetooth;

import java.lang.Runnable;

import org.psywerx.car.D;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class BtHelper implements Runnable{
	
	private static final int TIMEOUT = 4000;

	private Context mContext = null;
	private BluetoothChatService mBluetoothService = null;
	private String mLastData = null;
	
	private boolean run = true;
	
	public void run(){
		try{
		while (run){
			Thread.sleep(TIMEOUT);
			D.dbgv("requesting");
			sendData();
		}
		}catch(Exception e){
			D.dbge("Bt helper running stuff ",e);
		}
	}

	public BtHelper(BluetoothChatService service){
		mBluetoothService = service;
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
		if (mBluetoothService.getState() == BluetoothChatService.STATE_CONNECTED)
			mBluetoothService.write("podatki".getBytes());
	}
}

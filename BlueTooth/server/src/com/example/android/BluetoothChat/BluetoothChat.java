/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.BluetoothChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity {

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private static final int REQUEST_ENABLE_BT = 3;
	private static int mTimeout = 100;

	// Layout Views
	private TextView mTitle;
	private ListView mConversationView;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	private Context mContext = null;
	private boolean mRun = true;
	private String mCurrentLine = "";

	private float[] mData = new float[5];
	private float alpha = 0.4f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		D.dbge("+++ ON CREATE +++");
		mContext = this;
		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		//readFileLoop();

		((Button) findViewById(R.id.button_dec)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setTimeout(0.8f);
			}
		});
		((Button) findViewById(R.id.button_inc)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setTimeout(1.3f);
			}
		});
		setTimeout(1f);
	}

	private synchronized void setTimeout(float i){
		mTimeout = Math.max(5, (int) (mTimeout * i));
		((TextView) findViewById(R.id.timout)).setText("timeout: "+mTimeout);
	}

	public void testLoop(){
		new Thread(){
			@Override
			public void run() {

			}
		}.start();
	}

	public void readFileLoop(){
		new Thread(){
			@Override
			public synchronized void run() {
				try {
					while (mRun){
						InputStreamReader is = new InputStreamReader(mContext.getAssets().open("circlelog35ms.csv"));
						BufferedReader br = new BufferedReader(is);
						String line;
						while ((line = br.readLine()) != null){
							mCurrentLine = line;
							//D.dbgv("Thread read line:", mCurrentLine);
							Thread.sleep(35);
						}
					}
				} catch (InterruptedException e) {
					D.dbge(e.toString());
				} catch (IOException e) {
					D.dbge(e.toString());
				}
			};
		}.start();
	}



	@Override
	public void onStart() {
		super.onStart();
		D.dbge("++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null) setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		D.dbge("+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		D.dbgd("setupChat()");

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		D.dbge("- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		D.dbge("-- ON STOP --");
		mRun = false;
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null) mChatService.stop();
		D.dbge("--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		D.dbgd("ensure discoverable");
		if (mBluetoothAdapter.getScanMode() !=
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				D.dbgi("MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				//majcn: tuki se posilja
				String[] d = writeMessage.split(",");

				//simple low pass
				for (int i=0; i<d.length && d.length==mData.length; i++){
					mData[i] = mData[i]*(1.0f-alpha) + alpha*Float.parseFloat(d[i]);
				}

				String podatki = String.format("%5.3f  %5.3f  %5.3f  %3.1f  %3.1f", 
						mData[0],
						mData[1],
						mData[2],
						mData[3],
						mData[4]);
				mConversationArrayAdapter.add( podatki);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				//majcn: tuki se prejema
				//D.dbgv(readMessage);

				if(readMessage.equals("start")){
					D.dbgv("sending start");
					mChatService.write("start pressed".getBytes());
					timestamp = System.nanoTime();
				}
				if(readMessage.equals("stop")){
					D.dbgv("sending stop");
					mChatService.write("stop pressed".getBytes());
					timestamp = 0;
				}
				if(readMessage.equals("podatki")){
					if ( mCurrentLine != null) {
						new Thread(){
							public void run() {
								try {
									Thread.sleep(mTimeout);
									writeTestLoop();
									//writeLineFromFile();
								} catch (Exception e) {
									// TODO: handle exception
								}
							};
						}.start();
					}else{
						D.dbge("Sending null !!!!!!!!!!");
					}
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to "
						+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private long timestamp = 0;

	protected void writeTestLoop(){
		if (timestamp == 0) return;
		long cur = System.nanoTime();
		if (cur-timestamp < 5){
			mChatService.write(("0.2332,0.1,0.41,10,0").getBytes());
		}else if (cur-timestamp < 5+Math.PI*10){
			mChatService.write(("0.2332,0.1,0.41,10,5").getBytes());
		}else  if (cur-timestamp < 5+Math.PI*10+5){
			mChatService.write(("0.2332,0.1,0.41,10,0").getBytes());
		}else if (cur-timestamp < 5+Math.PI*10+5+3*Math.PI*16/2){
			mChatService.write(("0.2332,0.1,0.41,10,-2").getBytes());
		}else if (cur-timestamp < 5+Math.PI*10+5+3*Math.PI*16/2+20){
			mChatService.write(("0.2332,0.1,0.41,10,0").getBytes());
		}
	}

	protected synchronized void writeLineFromFile(){
		D.dbgv("writing line: "+mCurrentLine);
		mChatService.write(mCurrentLine.getBytes());
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		D.dbgd("onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				D.dbgd("BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

}

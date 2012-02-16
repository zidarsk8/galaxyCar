package org.psywerx.car;

import java.util.ArrayList;

public class DataHandler implements DataListener{
	private int sillydebug = 0;
	private DataListener stevec;
	
	private ArrayList<float[]> mHistory;
	private float[] mLastData;
	
	private boolean mRunning;
	private class UpdateViews implements Runnable{
		public void run() {
			try {
				mLastData[4] -= 5f; //
				stevec.updateData(mLastData);
			} catch (Exception e) {
				D.dbge("updating views error", e);
			} finally{
				mRunning = false;
			}
		};
	};
	private final UpdateViews mUpdateViews = new UpdateViews();
		
	public DataHandler() {
		mHistory = new ArrayList<float[]>();
	}
	public void updateData(float[] data) {
		mLastData = data;
		mHistory.add(data);
		
		if (sillydebug++%100 ==0)
		D.dbgv(String.format("handling ALL the data (%5d):  %5.3f  %5.3f  %5.3f  %5.3f  %5.3f", 
				sillydebug, data[0], data[1], data[2], data[3], data[4]));
		
		if (!mRunning){
			mRunning = true;
			new Thread(mUpdateViews).start();
		}
	}
	
	public void setStevec(DataListener stevec) {
		this.stevec = stevec;
	}
}

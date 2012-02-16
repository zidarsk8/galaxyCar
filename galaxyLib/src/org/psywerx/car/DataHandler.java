package org.psywerx.car;

import java.util.ArrayList;

public class DataHandler implements DataListener{
	private int sillydebug = 0;
	
	private ArrayList<float[]> mHistory;
	
	private static Thread mUpdateViews = new Thread(){
		public void run() {
			
		};
	};
		
	public DataHandler() {
		mHistory = new ArrayList<float[]>();
	}
	public void addData(float[] data) {
		mHistory.add(data);
		
		if (sillydebug++%100 ==0)
		D.dbgv(String.format("handling ALL the data (%5d):  %5.3f  %5.3f  %5.3f  %5.3f  %5.3f", 
				sillydebug, data[0], data[1], data[2], data[3], data[4]));
		
		if (!mUpdateViews.isAlive())
			mUpdateViews.start();
	}
}

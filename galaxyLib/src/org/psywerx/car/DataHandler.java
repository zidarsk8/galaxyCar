package org.psywerx.car;

import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;


public class DataHandler implements DataListener{
	private int sillydebug = 0;

	private ArrayList<DataListener> mDataListeners = new ArrayList<DataListener>();
	
	private ArrayList<float[]> mHistory;
	private float[] mLastData;

	private final Vector3d mOfsetVec = new Vector3d(-0.0430172172f, -0.0880380459f, 0.4048961114f);
	private final Vector3d mDownVec = new Vector3d(0,0,-1);
	private final AxisAngle4d mGRotateVector;
	
	private boolean mRunning;
	private class UpdateViews implements Runnable{
		public void run() {
			try {
				mLastData[4] -= 5f; 
				// TODO: normalize g forces
				
				
				for (Iterator<DataListener> i = mDataListeners.iterator(); i.hasNext();){
					DataListener dl = i.next();
					if (dl != null){
						dl.updateData(mLastData);
					}else{
						D.dbge("iterator data is null");
					}
				}
				
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
		Vector3d axis = new Vector3d();
		axis.cross(mOfsetVec, new Vector3d(0,0,-1));
		
		mGRotateVector = new AxisAngle4d(axis, mDownVec.angle(mOfsetVec));
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
	
	public void registerListener(DataListener listener) {
		mDataListeners.add(listener);
	}
}

package org.psywerx.car;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;


public class DataHandler implements DataListener{

	private ArrayList<DataListener> mDataListeners = new ArrayList<DataListener>();

	private ArrayList<float[]> mHistory;
	private float[] mLastData;
	private float[] mLastAlpha;
	private float[] mLastRolingAvg;
	private double mAlpha;
	private int mRolingCount;
	private LinkedList<float[]> mAverageFilter;

	private final Vector3d mOfsetVec = new Vector3d(-0.0430172172f, -0.0880380459f, 0.4048961114f);
	private final Vector3d mDownVec = new Vector3d(0,0,-1);
	private final Matrix3d mRotMatrix;

	private boolean mRunning;
	private class UpdateViews {
		public void run() {
			try {
				mLastData[4] -= 5f; 

				Vector3d g = new Vector3d(mLastData[0],mLastData[1],mLastData[2]);
				Vector3d result = new Vector3d();
				mRotMatrix.transform(g, result);

				mLastData[0] = (float) result.x*10;
				mLastData[1] = (float) result.y*10;
				mLastData[2] = ((float) result.z + 0.3856666667f)*10; //stationary down vector

				float[] removeFromRolingAvg = mAverageFilter.getFirst();
				for (int i=0; i<5; i++){
					mLastAlpha[i] = (float) (mLastAlpha[i]* (1f-mAlpha)+mLastData[i] * mAlpha);
					mLastRolingAvg[i] = removeFromRolingAvg[i]/mRolingCount + mLastData[i]/mRolingCount;
				}
				mAverageFilter.add(mLastData);
				while (mAverageFilter.size()>mRolingCount){
					mAverageFilter.removeFirst();
				}
				
				
				for (Iterator<DataListener> i = mDataListeners.iterator(); i.hasNext();){
					DataListener dl = i.next();
					if (dl != null){
						dl.updateData(mLastAlpha);
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

		mRotMatrix = new Matrix3d();
		mRotMatrix.set(new AxisAngle4d(axis, mDownVec.angle(mOfsetVec))); //set rotation component
		mAlpha = 1;
	}

	public void registerListener(DataListener listener) {
		if (listener != null){
			mDataListeners.add(listener);
		}
	}
	public void setAlpha(int alpha) {
		mRolingCount = alpha/5;
		mAlpha = Math.pow(alpha/100.0f, 2);
	}
	
	
	public void updateData(float[] rawData) {
		mLastData = rawData;
		mHistory.add(rawData);
		
		if (!mRunning){
			mRunning = true;
			//new Thread(mUpdateViews).start();
			mUpdateViews.run();
		}
		
	}
}

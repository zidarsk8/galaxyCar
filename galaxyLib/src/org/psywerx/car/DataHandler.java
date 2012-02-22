package org.psywerx.car;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;


public class DataHandler implements DataListener{

	private ArrayList<DataListener> mDataListeners = new ArrayList<DataListener>();

	private static final int MAX_HISTORY_SIZE = 10000;

	private LinkedList<float[]> mHistory;
	private float[] mLastData;
	private float[] mLastAlpha;
	private float[] mLastRolingAvg;
	private double mAlpha;
	private int mRolingCount;
	private LinkedList<float[]> mAverageFilter;
	private boolean mSmoothMode;

	private final Vector3d mOfsetVec = new Vector3d(-0.0430172172f, -0.0880380459f, 0.4048961114f);
	private final Vector3d mDownVec = new Vector3d(0,0,-1);
	private final Matrix3d mRotMatrix;

	private boolean mRunning;

	/**
	 * This function takes in raw data and does the folowing:
	 * Cal
	 */
	public void updateViews() {
		try {
			mLastData[4] -= 5f; 

			//rotate the accelerometer so that the z access points down.
			Vector3d g = new Vector3d(mLastData[0],mLastData[1],mLastData[2]);
			Vector3d result = new Vector3d();
			mRotMatrix.transform(g, result);

			mLastData[0] = (float) result.x*10;
			mLastData[1] = (float) result.y*10;
			mLastData[2] = ((float) result.z + 0.3856666667f)*10; //stationary down vector

			
			//set Alpha filter values;
			for (int i=0; i<5; i++){
				mLastAlpha[i] = (float) (mLastAlpha[i]* (1f-mAlpha)+mLastData[i] * mAlpha);
			}
			
			//set rolling average values
			while (mAverageFilter.size()>=mRolingCount){
				float[] removeFromRolingAvg = mAverageFilter.removeFirst();
				for (int i=0; i<5; i++){
					mLastRolingAvg[i] -= removeFromRolingAvg[i];
				}
			}
			float[] addToRolingAvg = new float[5];
			for (int i=0; i<5; i++){
				addToRolingAvg[i] = mLastData[i] / mRolingCount;
				mLastRolingAvg[i] += addToRolingAvg[i] ;
			}
			mAverageFilter.add(addToRolingAvg);



			for (Iterator<DataListener> i = mDataListeners.iterator(); i.hasNext();){
				DataListener dl = i.next();
				if (dl != null){
					//dl.updateData(mLastData);
					if (mSmoothMode){
						dl.updateData(mLastAlpha);
					}else{
						dl.updateData(mLastRolingAvg);
					}
				}else{
					D.dbge("iterator data is null");
				}
			}

		} catch (Exception e) {
			D.dbge("updating views error", e);
		} finally{
			mRunning = false;
		}
	}

	public DataHandler() {
		mHistory = new LinkedList<float[]>();
		Vector3d axis = new Vector3d();
		axis.cross(mOfsetVec, new Vector3d(0,0,-1));

		mRotMatrix = new Matrix3d();
		mRotMatrix.set(new AxisAngle4d(axis, mDownVec.angle(mOfsetVec))); //set rotation component
		mAlpha = 1;
		mRolingCount = 1;
		mLastAlpha = new float[5];
		mLastRolingAvg = new float[5];
		mAverageFilter = new LinkedList<float[]>();
	}

	public void registerListener(DataListener listener) {
		if (listener != null){
			mDataListeners.add(listener);
		}
	}

	public void setAlpha(int alpha) {
		mRolingCount = Math.max(1,20-alpha/5); //raste obratno sorazmerno
		mAlpha = Math.pow(alpha/100.0f, 2);    //se premika po kvadratni skali
	}


	public void updateData(float[] rawData) {
		mLastData = rawData;
		mHistory.add(rawData);
		if (mHistory.size()>MAX_HISTORY_SIZE){
			mHistory.removeFirst();
		}

		if (!mRunning){
			mRunning = true;
			//new Thread(mUpdateViews).start();
			updateViews();
		}
	}

	public void setSmoothMode(boolean alpha){
		mSmoothMode = !alpha;
	}

	public float[][] getWholeHistoryAlpha(){
		float[][] h = new float[mHistory.size()][5];
		
		int lIndex = 0;
		for (Iterator<float[]> ii = mHistory.iterator(); ii.hasNext();){
			float[] dl = ii.next();
			//work on alpha filter;
			for (int i=0; i<5; i++){
				h[lIndex][i] = (float) (h[lIndex][i]* (1f-mAlpha)+dl[i] * mAlpha);
			}
			lIndex++;
		}
		return h;
	}

	public float[][] getWholeHistoryRolingAvg(){
		float[][] ra = new float[mHistory.size()][5]; //rolling average
		
		int li = 0; //list index
		for (Iterator<float[]> ii = mHistory.iterator(); ii.hasNext();){
			float[] dl = ii.next();
			//work on alpha filter;
			for (int i=0; i<5; i++){
				ra[li][i] = dl[i]; //TODO: calculate rolling average
			}
			li++;
		}
		return ra;
	}

}

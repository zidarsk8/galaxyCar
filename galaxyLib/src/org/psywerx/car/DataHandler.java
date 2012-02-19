package org.psywerx.car;

import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;


public class DataHandler implements DataListener{
	private int sillydebug = 0;

	private ArrayList<DataListener> mDataListeners = new ArrayList<DataListener>();

	private ArrayList<float[]> mHistory;
	private float[] mLastData;

	private final Vector3d mOfsetVec = new Vector3d(-0.0430172172f, -0.0880380459f, 0.4048961114f);
	private final Vector3d mDownVec = new Vector3d(0,0,-1);
	private final Matrix3d mRotMatrix;

	private boolean mRunning;
	private class UpdateViews implements Runnable{
		public void run() {
			try {
				mLastData[4] -= 5f; 

				Vector3d g = new Vector3d(mLastData[0],mLastData[1],mLastData[2]);
				Vector3d result = new Vector3d();
				mRotMatrix.transform(g, result);

				mLastData[0] = (float) result.x;
				mLastData[1] = (float) result.y;
				mLastData[2] = (float) result.z - 0.4156666667f; //stationary down vector

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

		mRotMatrix = new Matrix3d();
		mRotMatrix.set(new AxisAngle4d(axis, mDownVec.angle(mOfsetVec))); //set rotation component

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
		if (listener != null){
			mDataListeners.add(listener);
		}
	}
	public void setAlpha(float alpha) {
		for (Iterator<DataListener> i = mDataListeners.iterator(); i.hasNext();){
			DataListener dl = i.next();
			if (dl != null){
				dl.setAlpha(alpha);
			}else{
				D.dbge("iterator data is null");
			}
		}
	}
}

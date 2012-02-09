package org.psywerx.car;

public class Models{
	
	private float[] mTh; //track history
	private float mTrackWidth = 0.2f;

	/**
	 *	turn track history array into triangle array for OpenGL.
	 */
	protected float[] getTrackMesh(){
		return getTrackMesh(mTrackWidth);
	}
	
	/**
	 *	turn track history array into triangle array for OpenGL, 
	 *	and specify the displayed track width.
	 *	@param w track width
	 */
	protected float[] getTrackMesh(float w){
		//TODO: finish this, and make up a model for track history
		int size = mTh.length;
		float[] mesh = new float[size*3];
		//mesh[0] = mth.
		for (int i=1; i<size-1; i++){
			
		}
		return mesh;
	}
	
}

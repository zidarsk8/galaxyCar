package org.psywerx.car;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;

import org.psywerx.car.Debugger;

public class Models{
	
	private float[] mTh; //track history
	protected float mTrackWidth = 0.2f;
	protected float[][] mModels;
	protected float[][] mColors;


	public Models(Context ctx){
		
		initModelArrays(ctx,"car");
	}

	private void initModelArrays(Context ctx,String model){
		try{
			InputStreamReader src = new InputStreamReader(ctx.getAssets().open(model+".c.csv",AssetManager.ACCESS_STREAMING));
			BufferedReader brc = new BufferedReader(src);
			String line;
			String concat = "";
			int lineCount = 0;
			while ((line = brc.readLine()) != null){
				lineCount++;
				concat += line+";";
			}

			int i = 0;
			mColors = new float[lineCount][];
			String[] concatSplit = concat.split(";");
			for (String con : concatSplit){
				String[] modString = con.split(",");
				mColors[i] = new float[modString.length];
				int j = 0;
				for (String a : modString){
					mColors[i][j++] = Float.parseFloat(a);
				}
				i++;
			}

			src = new InputStreamReader(ctx.getAssets().open(model+".v.csv",AssetManager.ACCESS_STREAMING));
			brc = new BufferedReader(src);
			mModels = new float[lineCount][];
			i = 0;
			while((line = brc.readLine()) != null){
				String[] modString = line.split(",");
				mModels[i] = new float[modString.length];
				int j = 0;
				for (String a : modString){
					mModels[i][j++] = Float.parseFloat(a);
				}
				i++;
			}
		}catch(Exception e){
			Debugger.dbge(e.toString());
		}
	}


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

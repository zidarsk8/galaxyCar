package org.psywerx.car;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class ModelLoader{
	
	private float[] mTh; //track history
	protected float mTrackWidth = 0.2f;
	protected float[][] mModels;
	protected float[][] mNormals;
	public HashMap<String, Model> models = new HashMap<String, Model>();
	private int lineCount;
	private Context ctx;

	public ModelLoader(Context ctx){
		// TODO: go over all the .csv files in assets and generate models on based on csv file name
		this.ctx = ctx;
		models.put("car", InitModel("car"));
	}
	public Model GetModel(String model){
		return models.get(model);
	}

	private Model InitModel(String model){
		
		Model m = new Model();

		try{
			
			InputStreamReader src = new InputStreamReader(ctx.getAssets().open(model+".c.csv",AssetManager.ACCESS_STREAMING));
			BufferedReader brc = new BufferedReader(src);
			String line;
			String concat = "";
			lineCount = 0;
			while ((line = brc.readLine()) != null){
				lineCount++;
				concat += line+";";
			}
			//m.count = lineCount;
			int i = 0;
			
			m.colors = new float[lineCount][];
			String[] concatSplit = concat.split(";");
			
			for (String con : concatSplit){
				String[] modString = con.split(",");
				m.colors[i] = new float[modString.length];
				int j = 0;
				for (String a : modString){
					m.colors[i][j++] = Float.parseFloat(a);
				}
				i++;
			}
			
			
			m.vertexBuffer = InitBuffer(model, "v");
			m.normalBuffer = InitBuffer(model, "n");
			
		}catch(Exception e){
			D.dbge(e.toString());
		}
		return m;
	}


	private FloatBuffer[] InitBuffer(String model, String type) throws NumberFormatException, IOException {
		
		InputStreamReader src = new InputStreamReader(ctx.getAssets().open(model+"."+type+".csv",AssetManager.ACCESS_STREAMING));
		BufferedReader brc = new BufferedReader(src);
		
		String line;
		
		FloatBuffer[] vertexBuffer = new FloatBuffer[lineCount];
		int i = 0;
		
		
		while((line = brc.readLine()) != null){
			String[] modString = line.split(",");
			float buff[] = new float[modString.length];
			int j = 0;
			for (String a : modString){
				buff[j++] = Float.parseFloat(a);
			}
			ByteBuffer vbb = ByteBuffer.allocateDirect(
					// (# of coordinate values * 4 bytes per float)
					buff.length * 4);
			// use the device hardware's native byte order
			vbb.order(ByteOrder.nativeOrder());
			// create a floating point buffer from the ByteBuffer
			vertexBuffer[i] = vbb.asFloatBuffer(); 
			// add the coordinates to the FloatBuffer
			vertexBuffer[i].put(buff); 
			// set the buffer to read the first coordinate
			vertexBuffer[i].position(0); 
			i++;
			
		}
		return vertexBuffer;

	}

	/**
	 *	turn track history array into triangle array for OpenGL.
	 */
	protected float[] getTrackMesh(){
		// TODO: move somplace else :)
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

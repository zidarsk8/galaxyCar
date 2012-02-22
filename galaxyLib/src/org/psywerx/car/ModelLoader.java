package org.psywerx.car;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * Loads models from .csv source
 * 
 * @author smotko
 *
 */
public class ModelLoader{

	/**
	 * HashMap with all the models used
	 */
	public HashMap<String, Model> mModels = new HashMap<String, Model>();
	
	private Context mContext;
	protected float[][] mModel;
	protected float[][] mNormals;
	private int mLineCount;

	public ModelLoader(Context context){
		this.mContext = context;
		
		//add car model
		mModels.put("car", InitModel("car"));
		
		//add cesta model
		Model road = InitModel("world");
		road.mTextureScale = 2;
		addTexture("kvadrat.png", road);
		mModels.put("road", road);
	}
	/**
	 * Function used to add texture to the model
	 * 
	 * @param textureName name
	 * @param model to apply the texture to
	 */
	private void addTexture(String textureName, Model model) {
		try {
			InputStream is = mContext.getAssets().open(textureName,AssetManager.ACCESS_STREAMING);
			Bitmap bitmap = null;
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);
			model.mBitmap = bitmap;
			is.close();
			float[] texture = new float[model.mVertices.length*2/3];
			for (int i = 0,j = 0; i < model.mVertices.length; i++) {
				if(i%3==1) continue;
				texture[j++] = model.mVertices[i]/model.mTextureScale;
			}
			D.dbgd(Arrays.toString(texture));
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			model.mTextureBuffer = byteBuf.asFloatBuffer();
			
			
			model.mTextureBuffer.put(texture);
			model.mTextureBuffer.position(0);
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Function used to get the model
	 * 
	 * @param model name
	 * @return
	 */
	public Model GetModel(String model){
		return mModels.get(model);
	}

	/**
	 * Internal function used to initialize the model
	 * 
	 * @param model
	 * @return
	 */
	private Model InitModel(String model){
		
		Model m = new Model();

		try{
			InputStreamReader src = new InputStreamReader(mContext.getAssets().open(model+".c.csv",AssetManager.ACCESS_STREAMING));
			BufferedReader brc = new BufferedReader(src);
			String line;
			String concat = "";
			mLineCount = 0;
			while ((line = brc.readLine()) != null){
				mLineCount++;
				concat += line+";";
			}
			//m.count = lineCount;
			int i = 0;
			
			m.mColors = new float[mLineCount][];
			String[] concatSplit = concat.split(";");
			
			for (String con : concatSplit){
				String[] modString = con.split(",");
				m.mColors[i] = new float[modString.length];
				int j = 0;
				for (String a : modString){
					m.mColors[i][j++] = Float.parseFloat(a);
				}
				i++;
			}
			m.mVertexBuffer = InitBuffer(model, "v", m);
			m.mNormalBuffer = InitBuffer(model, "n", m);
			
		}catch(Exception e){
			D.dbge(e.toString());
		}
		return m;
	}

	/**
	 * Internal function to initialize buffers
	 * 
	 * @param model
	 * @param type of the buffer
	 * @param m
	 * @return initialized float buffer
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private FloatBuffer[] InitBuffer(String model, String type, Model m) throws NumberFormatException, IOException {
		
		InputStreamReader src = new InputStreamReader(mContext.getAssets().open(model+"."+type+".csv",AssetManager.ACCESS_STREAMING));
		BufferedReader brc = new BufferedReader(src);
		
		String line;
		
		FloatBuffer[] vertexBuffer = new FloatBuffer[mLineCount];
		int i = 0;
		
		float x = 0,y = 0,z = 0;
		ByteBuffer vbb = null;
		
		while((line = brc.readLine()) != null){
			String[] modString = line.split(",");
			float buff[] = new float[modString.length];
			int j = 0;
			for (String a : modString){
				buff[j++] = Float.parseFloat(a);
			}
			x += buff[0]; y += buff[1]; z += buff[2];
			if(type == "v")
				m.mVertices = buff;
			vbb = ByteBuffer.allocateDirect(
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
		if(type == "v"){
			m.mCenter = new float[]{x/i, y/i, z/i};
		}
		return vertexBuffer;
	}
}

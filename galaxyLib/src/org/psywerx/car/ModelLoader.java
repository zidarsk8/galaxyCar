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

public class ModelLoader{
	
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
		//models.put("cesta", InitModel("cesta"));
		Model cesta = InitModel("world");
		cesta.textureScale = 2;
		addTexture("kvadrat.png", cesta);
		models.put("cesta2", cesta);
	}
	private void addTexture(String tex, Model mo) {
		try {
			InputStream is = ctx.getAssets().open(tex,AssetManager.ACCESS_STREAMING);
			Bitmap bitmap = null;
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);
			mo.mBitmap = bitmap;
			is.close();
			float[] texture = new float[mo.vertices.length*2/3];
			for (int i = 0,j = 0; i < mo.vertices.length; i++) {
				if(i%3==1) continue;
				texture[j++] = mo.vertices[i]/mo.textureScale;
			}
			D.dbgd(Arrays.toString(texture));
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			mo.textureBuffer = byteBuf.asFloatBuffer();
			
			
			mo.textureBuffer.put(texture);
			mo.textureBuffer.position(0);
			

		} catch (IOException e) {
			e.printStackTrace();
		}
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
			
			
			m.vertexBuffer = InitBuffer(model, "v", m);
			m.normalBuffer = InitBuffer(model, "n", m);
			
		}catch(Exception e){
			D.dbge(e.toString());
		}
		return m;
	}


	private FloatBuffer[] InitBuffer(String model, String type, Model m) throws NumberFormatException, IOException {
		
		InputStreamReader src = new InputStreamReader(ctx.getAssets().open(model+"."+type+".csv",AssetManager.ACCESS_STREAMING));
		BufferedReader brc = new BufferedReader(src);
		
		String line;
		
		FloatBuffer[] vertexBuffer = new FloatBuffer[lineCount];
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
				m.vertices = buff;
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
			m.center = new float[]{x/i, y/i, z/i};
		}
		return vertexBuffer;

	}
}

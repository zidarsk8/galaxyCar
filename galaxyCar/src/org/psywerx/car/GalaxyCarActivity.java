package org.psywerx.car;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GalaxyCarActivity extends Activity {
	
	private GLSurfaceView mGlView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.main);
		this.mGlView = (GLSurfaceView) this.findViewById(R.id.glSurface);
		if (this.mGlView != null){
    		this.mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			this.mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			this.mGlView.setRenderer(new CarSurfaceViewRenderer(getResources().getAssets()));
			this.mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
	}

    @Override
    protected void onResume(){
    	super.onResume();

        mGlView.onResume();
    }
    
    @Override
    protected void onPause(){
    	super.onPause();

   		mGlView.onPause();
    }
    
}

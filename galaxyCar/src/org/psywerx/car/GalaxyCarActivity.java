package org.psywerx.car;


import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GalaxyCarActivity extends Activity {
	
	private GLSurfaceView glView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //glView = new GLSurfaceView(this);
    	//glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		//glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    	//glView.setRenderer(new CarSurfaceViewRenderer());
        //setContentView(glView);
		setContentView(R.layout.main);
		this.glView = (GLSurfaceView) this.findViewById(R.id.glSurface);
		if (this.glView != null)
		{
			this.glView.setRenderer(new CarSurfaceViewRenderer());
			this.glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
	}

    @Override
    protected void onResume(){
    	super.onResume();

        //glView.onResume();
    }
    
    @Override
    protected void onPause(){
    	super.onPause();

   		//glView.onPause();
    }
    
}

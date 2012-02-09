package ckt.projects.acl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

public class ColladaObject {
	private FloatBuffer   mVertexBuffer;
    private ByteBuffer  mIndexBuffer;
    private int[] upAxis;
	
    public ColladaObject(float[] vertices, byte[] indices, int[] upAxis){       //add colors eventually
    	this.upAxis = upAxis;
    	
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }
    
    public void draw(GL10 gl){
        GLU.gluLookAt(gl, 5, 0, 3, 0, 0, 0, upAxis[0], upAxis[1], upAxis[2]);
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    	gl.glColor4f(0, 0, 1.0f, 1.0f);
    	gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.limit(), GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
    }
	
}

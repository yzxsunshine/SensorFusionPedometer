package com.xyzstudio.chart;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.xyzstudio.badlogic.framework.FileIO;
import com.xyzstudio.badlogic.framework.gl.Model;
import com.xyzstudio.badlogic.framework.gl.Vertices3;
import com.xyzstudio.badlogic.framework.impl.AndroidFileIO;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;
import com.xyzstudio.math.Matrix3x3;
import com.xyzstudio.math.Vector3;
import com.xyzstudio.pedometer.SensorData;
import com.xyzstudio.pedometer.object.ArrowObj;
import com.xyzstudio.pedometer.object.MapObj;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class MapChart extends GLSurfaceView {
	private MapRenderer renderer;
	private GLGraphics glGraphics;
	private MapObj map;
	private ArrowObj arrow;
	private FileIO fileIO;
	private float stepLength;
	public MapChart(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		glGraphics = new GLGraphics(this);
		fileIO = new AndroidFileIO(context.getAssets());
		renderer = new MapRenderer();
		this.setRenderer(renderer);
		stepLength = 1.0f;
	}
	
	public void updateStep(Vector3 stepDir) {
		arrow.updateRelativeMotion(stepDir.mul(stepLength));
	}
	
	public void updateData(SensorData sd){
		Vector3 pos = Vector3.zeros();
		Matrix3x3 rot = Matrix3x3.eye();
		arrow.update(sd);
		//arrow.update(pos, rot);
	}
	
	public void setStepLength(float len) {
		stepLength = len;
	}
	
	private class MapRenderer implements GLSurfaceView.Renderer {

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// TODO Auto-generated method stub
			glGraphics.setGL(gl); 
			map = new MapObj(glGraphics);
			arrow = new ArrowObj(glGraphics, fileIO);
		}
		
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	        gl.glClearDepthf(1.0f);
	        gl.glMatrixMode(GL10.GL_PROJECTION);
	        gl.glLoadIdentity();
	        GLU.gluPerspective(gl, 67, 
	                           glGraphics.getWidth() / (float) glGraphics.getHeight(),
	                           0.1f, 1000.0f);
	        gl.glMatrixMode(GL10.GL_MODELVIEW);
	        gl.glLoadIdentity();
	        
	        arrow.draw();
		}
		
	}

}

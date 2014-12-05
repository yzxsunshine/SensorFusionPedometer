package com.xyzstudio.badlogic.framework.impl;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class GLGraphics {
    GLSurfaceView glView;
    GL10 gl;
    
    public GLGraphics(GLSurfaceView glView) {
        this.glView = glView;
    }
    
    public GL10 getGL() {
        return gl;
    }
    
    public void setGL(GL10 gl) {
        this.gl = gl;
    }
    
    public GLSurfaceView getGLSurfaceView() {
    	return glView;
    }
    
    public void setGLSurfaceView(GLSurfaceView glView) {
    	this.glView = glView;
    }
    
    public int getWidth() {
        return glView.getWidth();
    }
    
    public int getHeight() {
        return glView.getHeight();
    }
}

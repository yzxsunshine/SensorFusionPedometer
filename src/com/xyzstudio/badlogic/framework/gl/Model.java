package com.xyzstudio.badlogic.framework.gl;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

import com.xyzstudio.badlogic.framework.impl.GLGame;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;
import com.xyzstudio.common.CommonDefine;
import com.xyzstudio.math.Matrix3x3;
import com.xyzstudio.math.Matrix4x4;
import com.xyzstudio.math.Quaternion;
import com.xyzstudio.math.RotationSpace;
import com.xyzstudio.math.Vector3;
import com.xyzstudio.math.Vector4;

public class Model {	
	public Vertices3 vertices = null;
	public Texture texture = null;	// WARNING! only support meshes with one texture right now!
	private GLGraphics glGraphics = null;
	private Matrix4x4 rotMat = null;
	private Vector3 translation = null;
	private Vector3 scale = null;
	public Model(GLGraphics glGraphics) {
		this.glGraphics = glGraphics;
	}
	
	
	public void SetRotationFromEular(Vector3 vec)
	{
		rotMat = new Matrix4x4(RotationSpace.Eular2Matrix(vec));
	}
	
	public void SetRotationFromRodrigues(Vector3 vec)
	{
		rotMat = new Matrix4x4(RotationSpace.Rodrigues2Matrix(vec));
	}
	
	public void SetRotationMatrix(Matrix3x3 mat)
	{
		rotMat = new Matrix4x4(mat);
	}
	
	public void SetRotationFromQuaternion(Quaternion q)
	{
		rotMat = new Matrix4x4(RotationSpace.Quaternion2Matrix(q));
	}
	
	
	public Matrix4x4 GetRoationMatrix()
	{
		return rotMat;
	}
	
	
	public void SetTranslationVector(Vector3 vec)
	{
		translation = vec;
	}
	
	public Vector3 GetTranslationVector()
	{
		return translation;
	}
	
	public void SetScale(Vector3 vec)
	{
		scale = vec;
	}
	
	public void SetScale(float s)
	{
		scale = Vector3.ones().mul(s);
	}
	
	public Vector3 GetScale()
	{
		return scale;
	}
	
	public void draw() {
		GL10 gl = glGraphics.getGL();
        // WARNING! the code below is the real draw part, changes are needed in the future.
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        texture.bind();
        vertices.bind();    
        if(translation != null)
        	gl.glTranslatef(translation.x(), translation.y(), translation.z());
        
        //Matrix4x4 glRotMat = new Matrix4x4(rotMat);
        if(rotMat != null)
        	gl.glMultMatrixf(rotMat.getData(), 0);
        //gl.glRotatef(90.0f, 1, 0, 0);
        //gl.glRotatef(180.0f, 0, 0, 1);
        if(scale != null)
        	gl.glScalef(scale.x(), scale.y(), scale.z());
        vertices.draw(GL10.GL_TRIANGLES, 0, vertices.getNumVertices());
        vertices.unbind();            
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisable(GL10.GL_DEPTH_TEST);
	}
	
	public void reload()
	{
		texture.reload();
	}
}

package com.xyzstudio.math;

public class Vector4 {
	public float[] data = new float[4];	// x y z w
	// constructor
	public Vector4()
	{
		data[0] = 0;
		data[1] = 0;
		data[2] = 0;
		data[3] = 0;
	}
	
	public Vector4(float v0, float v1, float v2, float v3)
	{
		data[0] = v0;
		data[1] = v1;
		data[2] = v2;
		data[3] = v3;
	}
	
	public Vector4(Vector3 vec, float w)
	{
		x(vec.x());
		y(vec.y());
		z(vec.z());
		w(w);
	}
	
	public Vector4(Vector3 vec)
	{
		w(vec.magnitude());
		vec.normalize();
		x(vec.x());
		y(vec.y());
		z(vec.z());
	}
	
	public Vector4(float[] vdata)
	{
		System.arraycopy(vdata, 0, data, 0, 4);
	}
	
	public Vector4(Vector4 vec)
	{
		System.arraycopy(vec.data, 0, data, 0, 4);
	}
	
	public void copyData(float[] val)
	{
		System.arraycopy(val, 0, data, 0, 4);
	}
	
	public float[] getData()
	{
		return data;
	}
	
	public float x()
	{
		return data[0];
	}
	
	public void x(float val)
	{
		data[0] = val;
	}
	
	public float y()
	{
		return data[1];
	}
	
	public void y(float val)
	{
		data[1] = val;
	}
	
	public float z()
	{
		return data[2];
	}
	
	public void z(float val)
	{
		data[2] = val;
	}
	
	public float w()	// could be quaternion or axis- angle
	{
		return data[3];
	}
	
	public void w(float val)
	{
		data[3] = val;
	}
	
	// operations
	public Vector4 add(Vector4 vec)
	{
		data[0] += vec.data[0];
		data[1] += vec.data[1];
		data[2] += vec.data[2];
		data[3] += vec.data[3];
		return this;
	}
	
	public Vector4 minus(Vector4 vec)
	{
		data[0] -= vec.data[0];
		data[1] -= vec.data[1];
		data[2] -= vec.data[2];
		data[3] -= vec.data[3];
		return this;
	}
}

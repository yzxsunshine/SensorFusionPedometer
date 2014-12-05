package com.xyzstudio.math;

import com.xyzstudio.common.CommonDefine;

public class Vector3 {
	private float[] data = new float[3];
	
	// constructor
	public Vector3()
	{
		data[0] = 0;
		data[1] = 0;
		data[2] = 0;
	}
	
	public Vector3(float v0, float v1, float v2)
	{
		data[0] = v0;
		data[1] = v1;
		data[2] = v2;
	}
	
	public Vector3(float[] vdata)
	{
		System.arraycopy(vdata, 0, data, 0, 3);
	}
	
	public Vector3(Vector3 vec)
	{
		System.arraycopy(vec.data, 0, data, 0, 3);
	}
	
	public void copyData(float[] val)
	{
		System.arraycopy(val, 0, data, 0, 3);
	}
	
	public float[] getData()
	{
		return data;
	}
	
	public Vector3 clone()
	{
		return Vector3.zeros().add(this);
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
	
	// eular angle
	/**
	 * heading, the same as azimuth, theta or yaw
	 * */
	public float heading()
	{
		return data[0];
	}
	
	/**
	 * heading, the same as azimuth, theta or yaw
	 * */
	public void heading(float val)
	{
		data[0] = val;
	}
	
	/**
	 * attitude, the same as elevation, phi or pitch
	 * */
	public float attitude()
	{
		return data[1];
	}
	
	/**
	 * attitude, the same as elevation, phi or pitch
	 * */
	public void attitude(float val)
	{
		data[1] = val;
	}
	
	/**
	 * bank, the same as tilt, psi and roll
	 * */
	public float bank()
	{
		return data[2];
	}
	
	/**
	 * bank, the same as tilt, psi and roll
	 * */
	public void bank(float val)
	{
		data[2] = val;
	}
	
	// operations
	public Vector3 add(Vector3 vec)
	{
		return new Vector3(x()+vec.x(), y()+vec.y(), z()+vec.z());
	}
	
	public Vector3 addToSelf(Vector3 vec)
	{
		data[0] += vec.data[0];
		data[1] += vec.data[1];
		data[2] += vec.data[2];
		return this;
	}
	
	public Vector3 minus(Vector3 vec)
	{
		return new Vector3(x()-vec.x(), y()-vec.y(), z()-vec.z());
	}
	
	public Vector3 minusToSelf(Vector3 vec)
	{
		data[0] -= vec.data[0];
		data[1] -= vec.data[1];
		data[2] -= vec.data[2];
		return this;
	}
	
	public Vector3 mul(Vector3 vec)
	{
		return new Vector3(x()*vec.x(), y()*vec.y(), z()*vec.z());
	}
	
	public Vector3 mulToSelf(Vector3 vec)
	{
		data[0] *= vec.data[0];
		data[1] *= vec.data[1];
		data[2] *= vec.data[2];
		return this;
	}
	
	public Vector3 mul(float s)
	{
		return new Vector3(x()*s, y()*s, z()*s);
	}
	
	public Vector3 mulToSelf(float s)
	{
		data[0] *= s;
		data[1] *= s;
		data[2] *= s;
		return this;
	}
	
	public Vector3 divide(Vector3 vec)
	{
		return new Vector3(x()/vec.x(), y()/vec.y(), z()/vec.z());
	}
	
	public Vector3 divideToSelf(Vector3 vec)
	{
		data[0] /= vec.data[0];
		data[1] /= vec.data[1];
		data[2] /= vec.data[2];
		return this;
	}
	
	public Vector3 divide(float s)
	{
		return new Vector3(x()/s, y()/s, z()/s);
	}
	
	public Vector3 divideToSelf(float s)
	{
		data[0] /= s;
		data[1] /= s;
		data[2] /= s;
		return this;
	}
	
	public Vector3 negtive()
	{
		return new Vector3(-x(), -y(), -z());
	}
	
	public Vector3 negtiveSelf()
	{
		data[0] = -data[0];
		data[1] = -data[1];
		data[2] = -data[2];
		return this;
	}
	
	// dot product
	public float dot(Vector3 vec)
	{
		return data[0] * vec.data[0] + data[1] * vec.data[1] + data[2] * vec.data[2];
	}
	// cross product
	public Vector3 cross(Vector3 vec)
	{
		Vector3 ret = new Vector3();
		ret.data[0] = data[1] * vec.data[2] - data[2] * vec.data[1];
		ret.data[1] = data[2] * vec.data[0] - data[0] * vec.data[2];
		ret.data[2] = data[0] * vec.data[1] - data[1] * vec.data[0];
		return ret;
	}
	
	public float magnitude()
	{
		return (float) Math.sqrt(this.dot(this));
	}
	
	public Vector3 normalize()
	{
		float norm = magnitude();
		if(norm > CommonDefine.EPSILON)
		{
			divideToSelf(norm);
		}
		else
		{
			data[0] = 0;
			data[1] = 0;
			data[2] = 0;
		}
		return this;
	}
	
	/**
	 * 	Static functions begin
	 * 
	 * */
	// return a instance with all dimensions as one
	public static Vector3 ones()
	{
		Vector3 vec = new Vector3(1, 1, 1);
		return vec;
	}
	
	public static Vector3 zeros()
	{
		Vector3 vec = new Vector3(0, 0, 0);
		return vec;
	}
	
	// dot product
	public static float dot(Vector3 vec1, Vector3 vec2)
	{
		return vec1.data[0] * vec2.data[0] + vec1.data[1] * vec2.data[1] + vec1.data[2] * vec2.data[2];
	}
	// cross product
	public static Vector3 cross(Vector3 vec1, Vector3 vec2)
	{
		Vector3 ret = new Vector3();
		ret.data[0] = vec1.data[1] * vec2.data[2] - vec1.data[2] * vec2.data[1];
		ret.data[1] = vec1.data[2] * vec2.data[0] - vec1.data[0] * vec2.data[2];
		ret.data[2] = vec1.data[0] * vec2.data[1] - vec1.data[1] * vec2.data[0];
		return ret;
	}
	
}

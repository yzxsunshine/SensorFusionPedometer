package com.xyzstudio.math;

import android.util.Log;

public class VectorN {
	private float[] data = null;
	private int n;
	
	public VectorN(int n)
	{
		this.n = n;
		data = new float[n];
	}
	
	/**
	 * not safe
	 * @param vdata
	 */
	public VectorN(float[] vdata)
	{
		n = vdata.length;
		data = new float[n];
		System.arraycopy(vdata, 0, data, 0, n);
	}
	
	public VectorN(VectorN vec)
	{
		n = vec.n;
		data = new float[n];
		System.arraycopy(vec.data, 0, data, 0, vec.n);
	}
	
	public void copyData(float[] val)
	{
		n = val.length;
		data = new float[n];
		System.arraycopy(val, 0, data, 0, val.length);
	}
	
	public float[] getData()
	{
		return data;
	}
	
	public int getLength()
	{
		return n;
	}
	
	public float get(int id)
	{
		if(id < n)
			return data[id];
		else
		{
			Log.e("VectorN", "Inaccessible index for get function");
			return 0.0f;
		}
	}
	
	public void set(int id, float val)
	{
		if(id < n)
			data[id] = val;
		else
		{
			Log.e("VectorN", "Inaccessible index for set function");
		}
	}
	
	public VectorN add(VectorN vec)
	{
		if(n != vec.n)
		{
			Log.e("VectorN", "length of two vectors are not same.");
			return null;
		}
		VectorN ret = new VectorN(n);
		for(int i=0; i<n; i++)
			ret.set(i, data[i] + vec.get(i));
		return ret;
	}
	
	public VectorN addToSelf(VectorN vec)
	{
		if(n != vec.n)
		{
			Log.e("VectorN", "length of two vectors are not same.");
			return null;
		}
		for(int i=0; i<n; i++)
			data[i] += vec.get(i);
		return this;
	}
	
	public VectorN minus(VectorN vec)
	{
		if(n != vec.n)
		{
			Log.e("VectorN", "length of two vectors are not same.");
			return null;
		}
		VectorN ret = new VectorN(n);
		for(int i=0; i<n; i++)
			ret.set(i, data[i] - vec.get(i));
		return ret;
	}
	
	public VectorN minusToSelf(VectorN vec)
	{
		if(n != vec.n)
		{
			Log.e("VectorN", "length of two vectors are not same.");
			return null;
		}
		for(int i=0; i<n; i++)
			data[i] -= vec.get(i);
		return this;
	}
}

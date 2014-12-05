package com.xyzstudio.math;

public class Matrix4x4 {
	private float[] data = new float[16];
	// constructor
	public Matrix4x4()
	{
		data[0] = 0;	data[1] = 0;	data[2] = 0;	data[3] = 0;	
		data[4] = 0;	data[5] = 0;	data[6] = 0;	data[7] = 0;	
		data[8] = 0;	data[9] = 0;	data[10] = 0;	data[11] = 0;
		data[12] = 0;	data[13] = 0;	data[14] = 0;	data[15] = 0;
	}
	
	public Matrix4x4(float[] val)
	{
		System.arraycopy(val, 0, data, 0, 16);
	}
	
	public Matrix4x4(Matrix4x4 mat)
	{
		System.arraycopy(mat.data, 0, data, 0, 16);
	}
	
	public Matrix4x4(Matrix3x3 mat)
	{
		for(int rr=0; rr<3; rr++)
		{
			for(int cc=0; cc<3; cc++)
			{
				set(rr, cc, mat.get(rr, cc));
			}
			set(rr, 3, 0);
			set(3, rr, 0);
		}
		set(3, 3, 1);
	}
	
	public void copyData(float[] val)
	{
		System.arraycopy(val, 0, data, 0, 16);
	}
	
	public float[] getData()
	{
		return data;
	}
	
	public float get(int row, int col)
	{
		return data[row*4+col];
	}
	
	public void set(int row, int col, float val)
	{
		data[row*4+col] = val;
	}
}

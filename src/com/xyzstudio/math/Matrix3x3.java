package com.xyzstudio.math;

public class Matrix3x3 {
	private float[] data = new float[9];
	// constructor
	public Matrix3x3()
	{
		data[0] = 0;	data[1] = 0;	data[2] = 0;
		data[3] = 0;	data[4] = 0;	data[5] = 0;
		data[6] = 0;	data[7] = 0;	data[8] = 0;
	}
	
	public Matrix3x3(float[] val)
	{
		System.arraycopy(val, 0, data, 0, 9);
	}
	
	public Matrix3x3(Matrix3x3 mat)
	{
		System.arraycopy(mat.data, 0, data, 0, 9);
	}
	
	public void copyData(float[] val)
	{
		System.arraycopy(val, 0, data, 0, 9);
	}
	
	public float[] getData()
	{
		return data;
	}
	
	public float get(int row, int col)
	{
		return data[row*3+col];
	}
	
	public void set(int row, int col, float val)
	{
		data[row*3+col] = val;
	}
	
	public Matrix3x3 add(Matrix3x3 mat)
	{
		Matrix3x3 ret = new Matrix3x3();
		for(int i=0; i<9; i++)
			ret.data[i] = data[i] + mat.data[i];	
		return ret;
	}
	
	public Matrix3x3 addToSelf(Matrix3x3 mat)
	{
		for(int i=0; i<9; i++)
			data[i] += mat.data[i];	
		return this;
	}
	
	public Matrix3x3 minus(Matrix3x3 mat)
	{
		Matrix3x3 ret = new Matrix3x3();
		for(int i=0; i<9; i++)
			ret.data[i] = data[i] - mat.data[i];	
		return ret;
	}
	
	public Matrix3x3 minusToSelf(Matrix3x3 mat)
	{
		for(int i=0; i<9; i++)
			data[i] -= mat.data[i];	
		return this;
	}
	
	public Matrix3x3 mul(Matrix3x3 mat)
	{
		Matrix3x3 ret = new Matrix3x3();
		ret.set(0, 0, get(0, 0)*mat.get(0, 0) + get(0, 1)*mat.get(1, 0) + get(0, 2)*mat.get(2, 0));	
		ret.set(0, 1, get(0, 0)*mat.get(0, 1) + get(0, 1)*mat.get(1, 1) + get(0, 2)*mat.get(2, 1));	
		ret.set(0, 2, get(0, 0)*mat.get(0, 2) + get(0, 1)*mat.get(1, 2) + get(0, 2)*mat.get(2, 2));	
		ret.set(1, 0, get(1, 0)*mat.get(0, 0) + get(1, 1)*mat.get(1, 0) + get(1, 2)*mat.get(2, 0));	
		ret.set(1, 1, get(1, 0)*mat.get(0, 1) + get(1, 1)*mat.get(1, 1) + get(1, 2)*mat.get(2, 1));	
		ret.set(1, 2, get(1, 0)*mat.get(0, 2) + get(1, 1)*mat.get(1, 2) + get(1, 2)*mat.get(2, 2));	
		ret.set(2, 0, get(2, 0)*mat.get(0, 0) + get(2, 1)*mat.get(1, 0) + get(2, 2)*mat.get(2, 0));	
		ret.set(2, 1, get(2, 0)*mat.get(0, 1) + get(2, 1)*mat.get(1, 1) + get(2, 2)*mat.get(2, 1));	
		ret.set(2, 2, get(2, 0)*mat.get(0, 2) + get(2, 1)*mat.get(1, 2) + get(2, 2)*mat.get(2, 2));	
		return ret;
	}
	
	public Matrix3x3 mulToSelf(Matrix3x3 mat) {
		// TODO Auto-generated method stub
		float[] val = new float[9];
		val[0] = get(0, 0)*mat.get(0, 0) + get(0, 1)*mat.get(1, 0) + get(0, 2)*mat.get(2, 0);	
		val[1] = get(0, 0)*mat.get(0, 1) + get(0, 1)*mat.get(1, 1) + get(0, 2)*mat.get(2, 1);	
		val[2] = get(0, 0)*mat.get(0, 2) + get(0, 1)*mat.get(1, 2) + get(0, 2)*mat.get(2, 2);	
		val[3] = get(1, 0)*mat.get(0, 0) + get(1, 1)*mat.get(1, 0) + get(1, 2)*mat.get(2, 0);	
		val[4] = get(1, 0)*mat.get(0, 1) + get(1, 1)*mat.get(1, 1) + get(1, 2)*mat.get(2, 1);	
		val[5] = get(1, 0)*mat.get(0, 2) + get(1, 1)*mat.get(1, 2) + get(1, 2)*mat.get(2, 2);	
		val[6] = get(2, 0)*mat.get(0, 0) + get(2, 1)*mat.get(1, 0) + get(2, 2)*mat.get(2, 0);	
		val[7] = get(2, 0)*mat.get(0, 1) + get(2, 1)*mat.get(1, 1) + get(2, 2)*mat.get(2, 1);	
		val[8] = get(2, 0)*mat.get(0, 2) + get(2, 1)*mat.get(1, 2) + get(2, 2)*mat.get(2, 2);
		copyData(val);
		return null;
	}
	
	public Vector3 mul(Vector3 vec)
	{
		Vector3 retVec = new Vector3();
		retVec.x(get(0, 0)*vec.x() + get(0, 1)*vec.y() + get(0, 2)*vec.z());	
		retVec.y(get(1, 0)*vec.x() + get(1, 1)*vec.y() + get(1, 2)*vec.z());	
		retVec.z(get(2, 0)*vec.x() + get(2, 1)*vec.y() + get(2, 2)*vec.z());	
		return retVec;
	}
	
	public Matrix3x3 mul(float scale)
	{
		Matrix3x3 ret = new Matrix3x3();
		for(int i=0; i<9; i++)
			ret.data[i] = data[i] * scale;
		return ret;
	}
	
	public Matrix3x3 mulToSelf(float scale)
	{
		for(int i=0; i<9; i++)
			data[i] *= scale;
		return this;
	}
	
	public Matrix3x3 divide(float scale)
	{
		Matrix3x3 ret = new Matrix3x3();
		for(int i=0; i<9; i++)
			ret.data[i] = data[i] / scale;
		return ret;
	}
	
	public Matrix3x3 divideToSelf(float scale)
	{
		for(int i=0; i<9; i++)
			data[i] /= scale;
		return this;
	}
	
	public float det()
	{
		float ret = 0;
		ret += data[0]*data[4]*data[8] + data[1]*data[5]*data[6] + data[2]*data[3]*data[7];
		ret -= data[0]*data[5]*data[7] + data[1]*data[3]*data[8] + data[2]*data[4]*data[6];
		return ret;
	}
	
	public float trace()
	{
		return data[0] + data[4] + data[8];
	}
	
	public Matrix3x3 transpose()
	{
		Matrix3x3 mat = new Matrix3x3();
		mat.data[0] = data[0];
		mat.data[1] = data[3];
		mat.data[2] = data[6];
		mat.data[3] = data[1];
		mat.data[4] = data[4];
		mat.data[5] = data[7];
		mat.data[6] = data[2];
		mat.data[7] = data[5];
		mat.data[8] = data[8];
		return mat;
	}
	
	public Matrix3x3 inverse()
	{
		Matrix3x3 ret = new Matrix3x3();
		float detVal = det();
		ret.data[0] = (data[4]*data[8]-data[5]*data[7]) / detVal;
		ret.data[1] = (data[3]*data[8]-data[5]*data[6]) / detVal;
		ret.data[2] = (data[3]*data[7]-data[4]*data[6]) / detVal;
		ret.data[3] = (data[1]*data[8]-data[2]*data[7]) / detVal;
		ret.data[4] = (data[0]*data[8]-data[2]*data[6]) / detVal;
		ret.data[5] = (data[0]*data[7]-data[1]*data[6]) / detVal;
		ret.data[6] = (data[1]*data[5]-data[2]*data[4]) / detVal;
		ret.data[7] = (data[0]*data[5]-data[2]*data[3]) / detVal;
		ret.data[8] = (data[0]*data[4]-data[1]*data[3]) / detVal;
		return ret;
	}
	
	/**
	 * 	Static functions begin
	 * 
	 * */
	// return a instance with all dimensions as one
	public static Matrix3x3 ones()
	{
		Matrix3x3 mat = new Matrix3x3();
		for(int i=0; i<9; i++)
		{
			mat.data[i] = 1;
		}
		return mat;
	}
	
	public static Matrix3x3 zeros()
	{
		Matrix3x3 mat = new Matrix3x3();
		for(int i=0; i<9; i++)
		{
			mat.data[i] = 0;
		}
		return mat;
	}
	
	public static Matrix3x3 eye()
	{
		Matrix3x3 mat = new Matrix3x3();
		for(int i=0; i<9; i++)
		{
			mat.data[i] = 0;
		}
		mat.data[0] = 1;
		mat.data[4] = 1;
		mat.data[8] = 1;
		return mat;
	}
	
	public static Matrix3x3 skewSymm(Vector3 vec)
	{
		Matrix3x3 mat = Matrix3x3.zeros();
		mat.set(0, 1, -vec.z());
		mat.set(0, 2, vec.y());
		mat.set(1, 0, vec.z());
		mat.set(1, 2, -vec.x());
		mat.set(2, 0, -vec.y());
		mat.set(2, 1, vec.x());
		return mat;
	} 
}

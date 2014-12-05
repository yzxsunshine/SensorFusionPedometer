package com.xyzstudio.math;

import com.xyzstudio.common.CommonDefine;

import android.util.Log;

public class MatrixMxN {
	private float[] data = null;
	private int m;	//row
	private int n;	//col
	public MatrixMxN(int m, int n) {
		data = new float[m*n];
		this.m = m;
		this.n = n;
	}
	
	public MatrixMxN(float[] val)
	{
		System.arraycopy(val, 0, data, 0, m*n);
	}
	
	public MatrixMxN(MatrixMxN mat)
	{
		System.arraycopy(mat.data, 0, data, 0, m*n);
	}
	
	public MatrixMxN(VectorN vec)
	{
		m = vec.getLength();
		n = m;
		for(int i=0; i<n; i++)
		{
			for(int j=0;j<n; j++)
			{
				set(i, j, 0.0f);
			}
			set(i, i, vec.get(i));
		}
	}
	
	public void copyData(float[] val)
	{
		System.arraycopy(val, 0, data, 0, m*n);
	}
	
	public float[] getData()
	{
		return data;
	}
	
	public float get(int row, int col)
	{
		return data[row*n + col];
	}
	
	public void set(int row, int col, float val)
	{
		data[row*n + col] = val;
	}
	
	public int rows()
	{
		return m;
	}
	
	public int cols()
	{
		return n;
	}
	
	public MatrixMxN add(MatrixMxN mat)
	{
		MatrixMxN ret = new MatrixMxN(m, n);
		for(int i=0; i<m*n; i++)
			ret.data[i] = data[i] + mat.data[i];	
		return ret;
	}
	
	public MatrixMxN addToSelf(MatrixMxN mat)
	{
		for(int i=0; i<m*n; i++)
			data[i] += mat.data[i];	
		return this;
	}
	
	public MatrixMxN minus(MatrixMxN mat)
	{
		MatrixMxN ret = new MatrixMxN(m, n);
		for(int i=0; i<m*n; i++)
			ret.data[i] = data[i] - mat.data[i];	
		return ret;
	}
	
	public MatrixMxN minusToSelf(MatrixMxN mat)
	{
		for(int i=0; i<m*n; i++)
			data[i] -= mat.data[i];	
		return this;
	}
	
	public MatrixMxN mul(float s)
	{
		MatrixMxN mat = new MatrixMxN(m, n);
		for(int i=0; i<m; i++)
			for(int j=0; j<n; j++)
				mat.set(i, j, get(i, j)*s);
		return mat;
	}
	
	public MatrixMxN mul(MatrixMxN mat)
	{
		int m = 0;
		int n = 0;
		if(this.n == mat.m)
		{
			m = this.m;
			n = this.n;
		}
		MatrixMxN ret = new MatrixMxN(m, n);
		
		for(int i=0; i<m; i++)
		{
			for(int j=0; j<n; j++)
			{
				float val = 0;
				for(int k=0; k<this.n; k++)
					val += this.get(i, k) * mat.get(k, j);
				ret.set(i, j, val);
			}
		}
		
		return ret;
	}
	
	public VectorN mul(VectorN vec)
	{
		VectorN ret = new VectorN(m);
		for(int i=0; i<m; i++)
		{
			float val = 0;
			for(int j=0; j<n; j++)
				val += get(i, j)*vec.get(j);
			vec.set(i, val);
		}
		return ret;
	}
	
	/**
	 * based on Gaussian-Jodan method
	 * @return
	 */
	public MatrixMxN inverse()
	{
		if(m!=n)
		{
			Log.e("Matrix Inverse", "Not a square matrix!");
			return null;
		}
		MatrixMxN mat = new MatrixMxN(m, n);
		int is[] = new int[n];	// record max elements of rows
		int js[] = new int[n];	// record max elements of columns
		int i, j, k;
		float maxElem, absVal, tmpVal;
		
		for(k=0; k<n; k++)
		{
			// find max elements
			maxElem = 0.0f;
			for(i=k; i<n; i++)
			{
				for(j=k; j<n; j++)
				{
					absVal = Math.abs(get(i, j));
					if(absVal > maxElem)
					{
						maxElem = absVal;
						is[k] = i;
						js[k] = j;
					}
				}
			}
			
			// check if the matrix is valid
			if(maxElem < CommonDefine.EPSILON)
			{
				Log.e("Matrix Inverse", "Invalid matrix!");
			}
			// if the max row is not k, do switch
			if(is[k] != k)
			{
				for(j=0; j<n; j++)
				{
					tmpVal = get(k, j);
					set(k, j, get(is[k], j));
					set(is[k], j, tmpVal); 
				}
			}
			
			// if the max column is not k, do switch
			if(js[k] != k)
			{
				for(i=0; i<n; i++)
				{
					tmpVal = get(i, k);
					set(i, k, get(i, js[k]));
					set(i, js[k], tmpVal); 
				}
			}
			
			// deal with diagonal elements
			set(k, k, 1/get(k, k));
			for(j=0; j<n; j++)	// kth row 
			{
				if(j!=k)
				{
					set(k, j, get(k, j) * get(k, k));
				}
			}
			for(i=0; i<n; i++)	// elements except diagonal, kth row and kth column
			{
				if(i!=k)
				{
					for(j=0; j<n; j++)	 
					{
						if(j!=k)
						{
							set(i, j, get(i, j) - get(i, k)*get(k, j));
						}
					}
				}
			}
			// kth column
			for(i=0; i<n; i++)
			{
				if(i!=k)
				{
					set(i, k, -get(i, k) * get(k, k));
				}
			}
		}
		// recover switched row and column
		// for previously switched rows, use column to switch, vice versa
		for(k=n-1; k>=0; k--)
		{
			if(js[k]!=k)
			{
				for(j=0; j<n; j++)
				{
					tmpVal = get(k, j);
					set(k, j, get(js[k], j));
					set(js[k], j, tmpVal); 
				}
			}
			
			if(is[k]!=k)
			{
				for(i=0; i<n; i++)
				{
					tmpVal = get(i, k);
					set(i, k, get(i, is[k]));
					set(i, is[k], tmpVal); 
				}
			}
		}
		return mat;
	}
	
	public MatrixMxN transpose()
	{
		MatrixMxN mat = new MatrixMxN(n, m);
		for(int i=0; i<n; i++)
		{
			for(int j=0; j<m; j++)
			{
				mat.set(i, j, get(j, i));
			}
		}
		return mat;
	}
	
	// static methods
	public static MatrixMxN zeros(int m, int n)
	{
		MatrixMxN mat = new MatrixMxN(m, n);
		for(int i=0; i<m; i++)
		{
			for(int j=0; j<n; j++)
			{
				mat.set(i, j, 0.0f);
			}
		}
		return mat;
	}
	
	public static MatrixMxN ones(int m, int n)
	{
		MatrixMxN mat = new MatrixMxN(m, n);
		for(int i=0; i<m; i++)
		{
			for(int j=0; j<m; j++)
			{
				mat.set(i, j, 1.0f);
			}
		}
		return mat;
	}
	
	public static MatrixMxN eye(int m, int n)
	{
		MatrixMxN mat = new MatrixMxN(m, n);
		for(int i=0; i<m; i++)
		{
			for(int j=0; j<m; j++)
			{
				mat.set(i, j, 0.0f);
			}
			mat.set(i, i, 1.0f);
		}
		return mat;
	}
}

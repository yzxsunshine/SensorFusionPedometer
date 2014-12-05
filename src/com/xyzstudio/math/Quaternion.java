package com.xyzstudio.math;

public class Quaternion extends Vector4 {
	public Quaternion()
	{
		super();	// x, y, z, w
		data[3] = 1;
	}
	
	public Quaternion(float x, float y, float z, float w)
	{
		super(x, y, z, w);
	}
	
	public Quaternion(Quaternion q)
	{
		super(q.data);
	}
	
	public Quaternion mul(Quaternion q)
	{
		Quaternion p = new Quaternion();
		p.x( x()*q.w() + w()*q.x() + z()*q.y() - y()*q.z() );
		p.y( y()*q.w() + w()*q.y() + x()*q.z() - z()*q.x() );
		p.z( z()*q.w() + w()*q.z() + y()*q.x() - x()*q.y() );
		p.w( w()*q.w() - x()*q.x() - y()*q.y() - z()*q.z() );
		return this;
	}
	
	public float dot(Quaternion q)
	{
		return data[3]*q.data[3] - data[0]*q.data[0] - data[1]*q.data[1] - data[2]*q.data[2];
	}
}

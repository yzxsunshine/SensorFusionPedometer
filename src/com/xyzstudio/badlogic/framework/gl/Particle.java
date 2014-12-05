package com.xyzstudio.badlogic.framework.gl;

import com.xyzstudio.math.Vector3;

public class Particle {
	private Vector3 color = null;
	private Vector3 position = null;
	private Vector3 velocity = null;
	private Vector3 acceleration = null;
	private int state = 0;
	
	public Particle()
	{
		color = new Vector3();
		position = new Vector3();
		velocity = new Vector3();
		acceleration = new Vector3();
		state = 0;
	}
	
	// color
	public float r()
	{
		return color.x();
	}
	
	public void r(float val)
	{
		color.x(val);
	}
	
	public float g()
	{
		return color.y();
	}
	
	public void g(float val)
	{
		color.y(val);
	}
	
	public float b()
	{
		return color.z();
	}
	
	public void b(float val)
	{
		color.z(val);
	}
	
	public Vector3 getColor()
	{
		return color;
	}
	
	public void setColor(Vector3 vec)
	{
		color = vec;
	}
	
	// position
	public float x()
	{
		return position.x();
	}
	
	public void x(float val)
	{
		position.x(val);
	}
	
	public float y()
	{
		return position.y();
	}
	
	public void y(float val)
	{
		position.y(val);
	}
	
	public float z()
	{
		return position.z();
	}
	
	public void z(float val)
	{
		position.z(val);
	}
	
	public Vector3 getPosition()
	{
		return position;
	}
	
	public void setPosition(Vector3 vec)
	{
		position = vec;
	}
	
	// velocity
	public float vx()
	{
		return velocity.x();
	}
	
	public void vx(float val)
	{
		velocity.x(val);
	}
	
	public float vy()
	{
		return velocity.y();
	}
	
	public void vy(float val)
	{
		velocity.y(val);
	}
	
	public float vz()
	{
		return velocity.z();
	}
	
	public void vz(float val)
	{
		velocity.z(val);
	}
	
	public Vector3 getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(Vector3 vec)
	{
		velocity = vec;
	}
	
	// acceleration
	public float ax()
	{
		return acceleration.x();
	}
	
	public void ax(float val)
	{
		acceleration.x(val);
	}
	
	public float ay()
	{
		return acceleration.y();
	}
	
	public void ay(float val)
	{
		acceleration.y(val);
	}
	
	public float az()
	{
		return acceleration.z();
	}
	
	public void az(float val)
	{
		acceleration.z(val);
	}
	
	public Vector3 getAcceleration()
	{
		return acceleration;
	}
	
	public void setAcceleration(Vector3 vec)
	{
		acceleration = vec;
	}
	
	public int getState()
	{
		return state;
	}
	
	public void setState(int s)
	{
		state = s;
	}
}

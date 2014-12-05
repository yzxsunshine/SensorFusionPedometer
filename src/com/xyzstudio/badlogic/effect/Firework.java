package com.xyzstudio.badlogic.effect;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.xyzstudio.badlogic.framework.gl.Particle;
import com.xyzstudio.badlogic.framework.gl.Texture;
import com.xyzstudio.badlogic.framework.gl.Vertices3;
import com.xyzstudio.badlogic.framework.impl.GLGame;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;
import com.xyzstudio.math.Vector3;

public class Firework {
	public Particle[][] particles = null;
	private int maxParticles = 0;
	private float lifeTime;
	private float fadeTime;
	private int maxTail;
	private Vector3 initColor;
	private Vector3 color;
	private Vector3 position;
	private Vector3 velocity;
	private Vector3 acceleration;
	private Texture texture;
	private GLGraphics glGraphics;
	private Vertices3 firework;
	private float explosionLevel;
	public Firework()
	{
		
	}
	
	public Firework(int particleNum, int tailLen, float life
				  	, String texPath, GLGame glGame)
	{
		maxParticles = particleNum;
		maxTail = tailLen;
		particles = new Particle[maxParticles][maxTail];
		for(int i=0; i<maxParticles; i++)
		{
			for(int j=0; j<maxTail; j++)
			{
				particles[i][j] = new Particle();	
			}
		}
		lifeTime = life;
		Random fadeRand = new Random();
		fadeTime = fadeRand.nextInt(100)*0.00005f + 0.002f;
		texture = new Texture(glGame, texPath);
		glGraphics = glGame.getGLGraphics();
		firework = new Vertices3(glGraphics, maxParticles*maxTail*4, maxParticles*maxTail*6, true, true, false);
		int height = glGraphics.getHeight();
		Random expRand = new Random();
		explosionLevel = (expRand.nextInt(height/40) + height*0.05f ) / height;				
	}
	//
	public void initParticles(int particleNum, int tailLen, float life
							, String texPath, GLGame glGame)
	{
		maxParticles = particleNum;
		maxTail = tailLen;
		particles = new Particle[maxParticles][maxTail];
		lifeTime = life;
		Random fadeRand = new Random();
		fadeTime = fadeRand.nextInt(100)*0.00005f + 0.002f;
		texture = new Texture(glGame, texPath);
		glGraphics = glGame.getGLGraphics();
		//firework = new Vertices3(glGraphics, maxParticles*maxTail*6, 0, true, true, false);
		firework = new Vertices3(glGraphics, maxParticles*maxTail*4, maxParticles*maxTail*6, true, true, false);
		int height = glGraphics.getHeight();
		Random expRand = new Random();
		explosionLevel = (expRand.nextInt(height/40) + height*0.05f ) / height;				
	}
	
	public void initState(Vector3 p, Vector3 v, Vector3 a, Vector3 c, Vector3 ic)
	{
		position = p;
		velocity = v;
		acceleration = a;
		color = c;
		initColor = ic;
		reset();
	}
	
	public void reset()
	{
		for(int i=0; i<maxParticles; i++)
		{
			// head particle of firework
			particles[i][0].setPosition(position);
			particles[i][0].setVelocity(velocity);;
			particles[i][0].setAcceleration(acceleration);
			particles[i][0].setColor(initColor);
			for(int j=0; j<maxTail; j++)
			{
				particles[i][j].setPosition(position);
			}
		}
	}
	
	public void update(float deltaTime)
	{
		float factor = deltaTime * 30.0f;
		float degreeElem = maxParticles/360.0f;
		for(int i=0; i<maxParticles; i++)
		{
			for(int j=maxTail-1; j>0; j--)
			{
				particles[i][j].setPosition(particles[i][j-1].getPosition());
			}
			particles[i][0].setPosition(particles[i][0].getPosition().add(particles[i][0].getVelocity().mul(factor)));
			float yd = particles[i][0].ay()*factor;
			particles[i][0].vy(particles[i][0].vy() + yd);
			// explosion
			if(particles[i][0].getState() == 0 && particles[i][0].vy() < -yd)
			{
				particles[i][0].setState(1);
				particles[i][0].setColor(color);
				float radian = (float) Math.PI*i*degreeElem / 180;
				particles[i][0].vx((float) Math.sin(radian) * explosionLevel);
				particles[i][0].vz((float) Math.cos(radian) * explosionLevel);
			}
			
			lifeTime -= fadeTime;
			
			if(lifeTime < 0)
			{
				reset();
			}
		}
		
	}
	
	public void draw()
	{
		GL10 gl = glGraphics.getGL();
		texture.bind();
		//float[] vertices = new float[maxParticles*maxTail*6*9];
		float[] vertices = new float[maxParticles*maxTail*4*9];
		short[] indices = new short[maxParticles*maxTail*6];
		int count = 0;
		int indicesCount = 0;
		for(int i=0; i<maxParticles; i++)
		{
			for(int j=0; j<maxTail; j++)
			{
				float dt = 1 - j*1.0f/maxTail;
				float size = 0.5f * dt;
				//dt = dt*255;
				indices[indicesCount*6 + 0] = 0;
				indices[indicesCount*6 + 1] = 1;
				indices[indicesCount*6 + 2] = 3;
				indices[indicesCount*6 + 3] = 1;
				indices[indicesCount*6 + 4] = 2;
				indices[indicesCount*6 + 5] = 3;
				indicesCount++;
				// [1 2 3 2 3 4] 6 points
				vertices[count*9 + 0] = particles[i][j].x() - size;
				vertices[count*9 + 1] = particles[i][j].y() - size;
				vertices[count*9 + 2] = particles[i][j].z();
				vertices[count*9 + 3] = particles[i][0].r();
				vertices[count*9 + 4] = particles[i][0].g();
				vertices[count*9 + 5] = particles[i][0].b();
				vertices[count*9 + 6] = dt;
				vertices[count*9 + 7] = 0.0f;
				vertices[count*9 + 8] = 1.0f;
				count++;
				
				vertices[count*9 + 0] = particles[i][j].x() + size;
				vertices[count*9 + 1] = particles[i][j].y() - size;
				vertices[count*9 + 2] = particles[i][j].z();
				vertices[count*9 + 3] = particles[i][0].r();
				vertices[count*9 + 4] = particles[i][0].g();
				vertices[count*9 + 5] = particles[i][0].b();
				vertices[count*9 + 6] = dt;
				vertices[count*9 + 7] = 1.0f;
				vertices[count*9 + 8] = 1.0f;
				count++;
				
				vertices[count*9 + 0] = particles[i][j].x() + size;
				vertices[count*9 + 1] = particles[i][j].y() + size;
				vertices[count*9 + 2] = particles[i][j].z();
				vertices[count*9 + 3] = particles[i][0].r();
				vertices[count*9 + 4] = particles[i][0].g();
				vertices[count*9 + 5] = particles[i][0].b();
				vertices[count*9 + 6] = dt;
				vertices[count*9 + 7] = 1.0f;
				vertices[count*9 + 8] = 0.0f;
				count++;
				
				vertices[count*9 + 0] = particles[i][j].x() - size;
				vertices[count*9 + 1] = particles[i][j].y() + size;
				vertices[count*9 + 2] = particles[i][j].z();
				vertices[count*9 + 3] = particles[i][0].r();
				vertices[count*9 + 4] = particles[i][0].g();
				vertices[count*9 + 5] = particles[i][0].b();
				vertices[count*9 + 6] = dt;
				vertices[count*9 + 7] = 0.0f;
				vertices[count*9 + 8] = 0.0f;
				count++;
			}
		}
		
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		firework.setVertices(vertices, 0, vertices.length);
		firework.setIndices(indices, 0, indices.length);
		firework.bind();
		//gl.glColor4f(particles[0][0].r()/255, particles[0][0].g()/255, particles[0][0].b()/255, 1.0f);
		firework.draw(GL10.GL_TRIANGLES, 0, firework.getNumIndices());
		firework.unbind();
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_DEPTH_TEST);		
		gl.glDisable(GL10.GL_BLEND);
	}
	
	public void reload()
	{
		texture.reload();
	}
	
}

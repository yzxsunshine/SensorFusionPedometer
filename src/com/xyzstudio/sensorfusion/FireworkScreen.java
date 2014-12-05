package com.xyzstudio.sensorfusion;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

import com.xyzstudio.badlogic.effect.Firework;
import com.xyzstudio.badlogic.framework.Game;
import com.xyzstudio.badlogic.framework.gl.Particle;
import com.xyzstudio.badlogic.framework.gl.Vertices3;
import com.xyzstudio.badlogic.framework.impl.GLGame;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;
import com.xyzstudio.badlogic.framework.impl.GLScreen;
import com.xyzstudio.math.Vector3;

public class FireworkScreen extends GLScreen {
	private Firework[] fireworks = null;
	private final int fireworkNum = 10;
	private Vector3[] colors = null;
	private Vertices3 cube = null;
	
	public FireworkScreen(GLGame game) {
		super(game);
		// TODO Auto-generated constructor stub
		fireworks = new Firework[fireworkNum];
		initColor();
		float lifeTime = 5.5f;
		for(int i=0; i<fireworkNum; i++)
		{
			Random xRand = new Random();
			float x = xRand.nextInt(2)*1.0f - 4.0f;
			Random yRand = new Random();
			float y = -yRand.nextInt(4)*1.0f - 8.0f;
			Random zRand = new Random();
			float z = -zRand.nextInt(5)*1.0f - 25.0f;
			Random speedRand = new Random();
			GLGraphics glGraphics = game.getGLGraphics();
			int height = glGraphics.getHeight();
			float speed = (float) ((speedRand.nextInt(height/50) + height*0.2f) / height);
			fireworks[i] = new Firework(6, 6, lifeTime, "particle.bmp", game);	//24 30
			Random colorRand = new Random();
			int colorSelected = colorRand.nextInt(colors.length);
			fireworks[i].initState(new Vector3(x, y, z), new Vector3(0, speed, 0), new Vector3(0, -0.5f/height, 0)
								 , colors[colorSelected], getInitColor(colors[colorSelected]));
		}
		
	}

	private Vector3 getInitColor(Vector3 color)
	{
		Vector3 ret = color.add(Vector3.ones().mul(0.2f));
		if(ret.x() > 1.0f)
			ret.x(1.0f);
		if(ret.y() > 1.0f)
			ret.y(1.0f);
		if(ret.z() > 1.0f)
			ret.z(1.0f);
		return ret;
	}
	
	private void initColor() {
		// TODO Auto-generated method stub
		colors = new Vector3[12];
		colors[0] = new Vector3(1.0f, 0.5f, 0.5f);
		colors[1] = new Vector3(1.0f, 0.75f, 0.5f);
		colors[2] = new Vector3(1.0f, 1.0f, 0.5f);
		colors[3] = new Vector3(0.75f, 1.0f, 0.5f);
		colors[4] = new Vector3(0.5f, 1.0f, 0.5f);
		colors[5] = new Vector3(0.5f, 1.0f, 0.75f);
		colors[6] = new Vector3(0.5f, 1.0f, 1.0f);
		colors[7] = new Vector3(0.5f, 0.75f, 1.0f);
		colors[8] = new Vector3(0.5f, 0.5f, 1.0f);
		colors[9] = new Vector3(0.75f, 0.5f, 1.0f);
		colors[10] = new Vector3(1.0f, 0.5f, 1.0f);
		colors[11] = new Vector3(1.0f, 0.5f, 0.75f);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		for(int i=0; i<fireworkNum; i++)
		{
			//fireworks[i].update(deltaTime);
		}
	}

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
		GL10 gl = glGraphics.getGL();
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45, 
                           glGraphics.getWidth() / (float) glGraphics.getHeight(),
                           0.1f, 1000.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
		for(int i=0; i<fireworkNum; i++)
		{
			fireworks[i].draw();
			fireworks[i].update(deltaTime);
		}
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		for(int i=0; i<fireworkNum; i++)
		{
			fireworks[i].reload();
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
    
	private Vertices3 createCube(Particle vec) {
		float size = 0.5f;
        float[] vertices = { vec.x()-size, vec.y()-size,  vec.z(), 0, 1,
        					 vec.x()+size, vec.y()-size,  vec.z(), 1, 1,
        					 vec.x()+size, vec.y()+size,  vec.z(), 1, 0,
        					 vec.x()-size, vec.y()+size,  vec.z(), 0, 0/*,
                           
                              0.5f, -0.5f,  0.5f, 0, 1,
                              0.5f, -0.5f, -0.5f, 1, 1,
                              0.5f,  0.5f, -0.5f, 1, 0,
                              0.5f,  0.5f,  0.5f, 0, 0,
                            
                              0.5f, -0.5f, -0.5f, 0, 1,
                             -0.5f, -0.5f, -0.5f, 1, 1,
                             -0.5f,  0.5f, -0.5f, 1, 0,
                              0.5f,  0.5f, -0.5f, 0, 0,
                            
                             -0.5f, -0.5f, -0.5f, 0, 1, 
                             -0.5f, -0.5f,  0.5f, 1, 1,
                             -0.5f,  0.5f,  0.5f, 1, 0,
                             -0.5f,  0.5f, -0.5f, 0, 0,
                           
                             -0.5f,  0.5f,  0.5f, 0, 1,
                              0.5f,  0.5f,  0.5f, 1, 1,
                              0.5f,  0.5f, -0.5f, 1, 0,
                             -0.5f,  0.5f, -0.5f, 0, 0,
                           
                             -0.5f, -0.5f,  0.5f, 0, 1,
                              0.5f, -0.5f,  0.5f, 1, 1,
                              0.5f, -0.5f, -0.5f, 1, 0,
                             -0.5f, -0.5f, -0.5f, 0, 0*/
        };             
        
        short[] indices = { 0, 1, 3, 1, 2, 3/*,
                            4, 5, 7, 5, 6, 7,
                            8, 9, 11, 9, 10, 11,
                            12, 13, 15, 13, 14, 15,
                            16, 17, 19, 17, 18, 19,
                            20, 21, 23, 21, 22, 23,*/
        };
        
        Vertices3 cube = new Vertices3(glGraphics, 4, 6, false, true, false);
        cube.setVertices(vertices, 0, vertices.length);
        cube.setIndices(indices, 0, indices.length);
        return cube;
    }
}

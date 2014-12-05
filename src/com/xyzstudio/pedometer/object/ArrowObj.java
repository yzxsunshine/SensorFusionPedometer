package com.xyzstudio.pedometer.object;

import com.xyzstudio.badlogic.framework.FileIO;
import com.xyzstudio.badlogic.framework.gl.Model;
import com.xyzstudio.badlogic.framework.gl.Texture;
import com.xyzstudio.badlogic.framework.gl.Vertices3;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;
import com.xyzstudio.math.Matrix3x3;
import com.xyzstudio.math.RotationSpace;
import com.xyzstudio.math.Vector3;
import com.xyzstudio.pedometer.SensorData;

public class ArrowObj extends Model {
	public ArrowObj(GLGraphics glGraphics, FileIO fileIO) {
		super(glGraphics);
		// TODO Auto-generated constructor stub
		float size = 0.5f;
        float[] vertices = { -size, -size,  size, 0, 1,
		                size, -size,  size, 1, 1,
		                size,  size,  size, 1, 0,
		               -size,  size,  size, 0, 0,
		             
		                size, -size,  size, 0, 1,
		                size, -size, -size, 1, 1,
		                size,  size, -size, 1, 0,
		                size,  size,  size, 0, 0,
		              
		                size, -size, -size, 0, 1,
		               -size, -size, -size, 1, 1,
		               -size,  size, -size, 1, 0,
		                size,  size, -size, 0, 0,
		              
		               -size, -size, -size, 0, 1, 
		               -size, -size,  size, 1, 1,
		               -size,  size,  size, 1, 0,
		               -size,  size, -size, 0, 0,
		             
		               -size,  size,  size, 0, 1,
		                size,  size,  size, 1, 1,
		                size,  size, -size, 1, 0,
		               -size,  size, -size, 0, 0,
		             
		               -size, -size,  size, 0, 1,
		                size, -size,  size, 1, 1,
		                size, -size, -size, 1, 0,
		               -size, -size, -size, 0, 0
		};             
		
		short[] indices = { 0, 1, 3, 1, 2, 3,
		              4, 5, 7, 5, 6, 7,
		              8, 9, 11, 9, 10, 11,
		              12, 13, 15, 13, 14, 15,
		              16, 17, 19, 17, 18, 19,
		              20, 21, 23, 21, 22, 23,
		};
		
		this.vertices = new Vertices3(glGraphics, 24, 36, false, true, false);
	    this.vertices.setVertices(vertices, 0, vertices.length);
	    this.vertices.setIndices(indices, 0, indices.length);
			

        this.texture = new Texture(glGraphics, fileIO, "arrow.png");
        this.SetTranslationVector(new Vector3(0, 0, -10));
	}

	public void update(Vector3 pos, Matrix3x3 rot) {
		this.SetTranslationVector(pos);
		this.SetRotationMatrix(rot);
	}
	
	public void updateRelativeMotion(Vector3 pos) {
		this.SetTranslationVector(this.GetTranslationVector().add(pos));
	}
	
	public void update(SensorData sd) {
		Matrix3x3 rotMat = RotationSpace.Eular2Matrix(sd.GetOrient());
		Vector3 viewDir;
		Vector3 zAxis = new Vector3(0, 0, 1.0f);
		viewDir = rotMat.mul(zAxis.negtive());
		float theta = (float) Math.atan2( viewDir.y(),  viewDir.x() );
		Vector3 rodrigues = zAxis.mul(theta);
		Matrix3x3 rot2D = RotationSpace.Rodrigues2Matrix(rodrigues);
		this.SetRotationMatrix(rot2D.transpose());
	}
}

package com.xyzstudio.sensorfusion;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

import com.xyzstudio.algorithm.ComplementaryFilterFusion;
import com.xyzstudio.algorithm.FusionInterface;
import com.xyzstudio.badlogic.framework.Game;
import com.xyzstudio.badlogic.framework.gl.Font;
import com.xyzstudio.badlogic.framework.gl.Model;
import com.xyzstudio.badlogic.framework.gl.Texture;
import com.xyzstudio.badlogic.framework.gl.Vertices3;
import com.xyzstudio.badlogic.framework.impl.GLGame;
import com.xyzstudio.badlogic.framework.impl.GLScreen;
import com.xyzstudio.badlogic.framework.mesh.MeshIO;
import com.xyzstudio.badlogic.framework.mesh.ObjMeshIO;
import com.xyzstudio.badlogic.framework.mesh.PlyMeshIO;
import com.xyzstudio.common.CommonDefine;
import com.xyzstudio.math.Matrix3x3;
import com.xyzstudio.math.Matrix4x4;
import com.xyzstudio.math.RotationSpace;
import com.xyzstudio.math.Vector3;
import com.xyzstudio.math.Vector4;

public class CompassScreen extends GLScreen {
	
	protected FusionInterface fusion;
	protected Vector3 fusedOrientation;
	
	//Vertices3 cube;
	//Texture texture;
	Model model;
    float angle = 0;
    MeshIO meshIO;
	Font font;
    public CompassScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
		fusion = new ComplementaryFilterFusion();
		//cube = createCube();
        //texture = new Texture((GLGame)glGame, "crate.png");
		meshIO = new PlyMeshIO();//ObjMeshIO();
		model = meshIO.readMesh("compass.ply", null, glGraphics, glGame);
		//font = new Font(null, 0, 0, 0, 0, 0);
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
		model.draw();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		model.reload();
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		// get sensor data and do fusion
		Vector3 vec = new Vector3(0, 0, -5);
		model.SetTranslationVector(vec);
		model.SetScale(0.1f);
		Vector3 accMagOrientation = glGame.getInput().getAccMagOrientation();
		Vector3 gyroOrientation = glGame.getInput().getGyroOrientation();
		fusedOrientation = fusion.orientationFusion(accMagOrientation, gyroOrientation);
		Matrix3x3 rotMat = RotationSpace.Eular2Matrix(fusedOrientation);
		model.SetRotationMatrix(rotMat);
		glGame.getInput().setGyroOrientation(fusedOrientation);
	}

}

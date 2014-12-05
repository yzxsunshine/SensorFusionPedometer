package com.xyzstudio.badlogic.framework.mesh;

import com.xyzstudio.badlogic.framework.FileIO;
import com.xyzstudio.badlogic.framework.gl.Model;
import com.xyzstudio.badlogic.framework.gl.ObjLoader;
import com.xyzstudio.badlogic.framework.gl.Texture;
import com.xyzstudio.badlogic.framework.impl.GLGame;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;

public class ObjMeshIO implements MeshIO {
	@Override
	public Model readMesh(String fileName, String texName, GLGraphics glGraphics, GLGame glGame) {
		// TODO Auto-generated method stub
		Model model = new Model(glGraphics);
		model.vertices = ObjLoader.load(glGame, fileName);
		model.texture = new Texture(glGame, texName);
		return model;
	}

	@Override
	public Model readMesh(String fileName, String texName,
			GLGraphics glGraphics, FileIO fileIO) {
		// TODO Auto-generated method stub
		Model model = new Model(glGraphics);
		model.vertices = ObjLoader.load(glGraphics, fileIO, fileName);
		model.texture = new Texture(glGraphics, fileIO, texName);
		return model;
	}

}

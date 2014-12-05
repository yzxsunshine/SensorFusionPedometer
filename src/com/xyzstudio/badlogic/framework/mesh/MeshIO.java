package com.xyzstudio.badlogic.framework.mesh;

import com.xyzstudio.badlogic.framework.FileIO;
import com.xyzstudio.badlogic.framework.gl.Model;
import com.xyzstudio.badlogic.framework.impl.GLGame;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;

public interface MeshIO {
	public Model readMesh(String fileName, String texName, GLGraphics glGraphics, GLGame glGame);
	public Model readMesh(String fileName, String texName, GLGraphics glGraphics, FileIO fileIO);
}

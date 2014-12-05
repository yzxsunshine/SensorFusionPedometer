package com.xyzstudio.sensorfusion;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.xyzstudio.badlogic.framework.Screen;
import com.xyzstudio.badlogic.framework.impl.GLGame;

public class CompassGame extends GLGame {
	@Override
	public Screen getStartScreen() {
		// TODO Auto-generated method stub
		return new CompassScreen(this);
	}
}

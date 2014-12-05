package com.xyzstudio.sensorfusion;

import com.xyzstudio.badlogic.framework.Screen;
import com.xyzstudio.badlogic.framework.impl.GLGame;

public class FireworkGame extends GLGame {

	@Override
	public Screen getStartScreen() {
		// TODO Auto-generated method stub
		return new FireworkScreen(this);
	}

}

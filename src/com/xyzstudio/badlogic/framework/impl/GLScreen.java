package com.xyzstudio.badlogic.framework.impl;

import com.xyzstudio.badlogic.framework.Game;
import com.xyzstudio.badlogic.framework.Screen;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;

public abstract class GLScreen extends Screen{
	protected final GLGraphics glGraphics;
    protected final GLGame glGame;
    
    public GLScreen(Game game) {
    	super(game);
        glGame = (GLGame)game;
        glGraphics = ((GLGame)game).getGLGraphics();
    }
    
    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
}

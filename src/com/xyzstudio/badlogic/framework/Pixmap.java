package com.xyzstudio.badlogic.framework;

import com.xyzstudio.badlogic.framework.Graphics.PixmapFormat;

public interface Pixmap {
    public int getWidth();

    public int getHeight();

    public PixmapFormat getFormat();

    public void dispose();
}

package com.xyzstudio.pedometer;

import com.xyzstudio.math.Vector3;

public interface StepListener {
    public void onStep(Vector3 direction);
    public void onStep();
    public void passValue();
}
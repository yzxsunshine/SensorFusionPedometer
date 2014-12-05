package com.xyzstudio.algorithm;

import com.xyzstudio.math.Vector3;

public interface FusionInterface {
	// only interface to provide fusion function
	public Vector3 orientationFusion(Vector3 accMagOrientation, Vector3 gyroOrientation);
	
	public Vector3 translationFusion(Vector3 accel, Vector3 otherData, float accelWeight, float otherWeight);
}

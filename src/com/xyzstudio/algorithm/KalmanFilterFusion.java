package com.xyzstudio.algorithm;

import com.xyzstudio.math.MatrixMxN;
import com.xyzstudio.math.Vector3;

public class KalmanFilterFusion implements FusionInterface {
	private KalmanFilter kalmanFilter = null;
	
	public KalmanFilterFusion()
	{
		MatrixMxN sysMat = MatrixMxN.eye(6, 6);
		MatrixMxN meaMat = MatrixMxN.eye(6, 6);
		float sysNoise = 0.1f;
		float meaNoise = 0.1f;
		kalmanFilter = new KalmanFilter(sysMat, meaMat, sysNoise, meaNoise);
		
	}
	
	@Override
	public Vector3 orientationFusion(Vector3 accMagOrientation,
			Vector3 gyroOrientation) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	@Override
	public Vector3 translationFusion(Vector3 accel, Vector3 otherData,
			float accelWeight, float otherWeight) {
		// TODO Auto-generated method stub
		
		return null;
	}

}

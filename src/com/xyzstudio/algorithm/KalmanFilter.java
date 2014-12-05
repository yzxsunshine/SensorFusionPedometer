package com.xyzstudio.algorithm;

import com.xyzstudio.math.MatrixMxN;
import com.xyzstudio.math.VectorN;

public class KalmanFilter {
	private int systemSize;
	private int measureSize;
	private MatrixMxN systemMatrix;
	private MatrixMxN measureMatrix;
	private MatrixMxN systemNoise;
	private MatrixMxN measureNoise;
	//private MatrixMxN controlMatrix;
	private MatrixMxN covMatrix;
	
	private VectorN systemVec;
	private VectorN meaVec;
	
	public KalmanFilter(int ss, int ms, float sysNoise, float meaNoise)
	{
		systemSize = ss;
		measureSize = ms;
		systemMatrix = MatrixMxN.eye(ss, ss);
		measureMatrix = MatrixMxN.eye(ms, ms);
		systemNoise = MatrixMxN.eye(systemSize, systemSize).mul(sysNoise);
	}
	
	public KalmanFilter(MatrixMxN sysMat, MatrixMxN measureMat, float sysNoise, float meaNoise)
	{
		systemSize = sysMat.rows();
		measureSize = measureMat.rows();
		systemMatrix = sysMat;
		measureMatrix = measureMat;
		systemNoise = MatrixMxN.eye(systemSize, systemSize).mul(sysNoise);
		measureNoise = MatrixMxN.eye(measureSize, measureSize).mul(meaNoise);
	}
	
	public void initialize(VectorN vec)
	{
		MatrixMxN meaInvMat = measureMatrix.inverse();
		MatrixMxN meaInvTransposeMat = measureMatrix.transpose().inverse();
		systemVec = meaInvMat.mul(vec);
		meaVec = vec;
		covMatrix = meaInvMat.mul(measureNoise).mul(meaInvTransposeMat);
	}
	
	public void configure(VectorN vec, float dt)
	{
		meaVec = vec;
		
	}
	
	public void predict()
	{
		// x = F*x;
		systemVec = systemMatrix.mul(systemVec);
		// P = F*P*F' + Q
		covMatrix = (systemMatrix.mul(covMatrix).mul(systemMatrix.transpose())).add(systemNoise);
	}
	
	public void update()
	{
		// S = H*P*H' + R
		MatrixMxN meaCovMatrix = measureMatrix.mul(covMatrix).mul(measureMatrix.transpose()).add(measureNoise);
		// K = P*H'*inv(S)
		MatrixMxN kalmanGainFactor = covMatrix.mul(measureMatrix.transpose()).mul(meaCovMatrix.inverse());
		// e = z - H*X
		VectorN innovation = meaVec.minus(measureMatrix.mul(systemVec));
		// x = x + K*e
		systemVec.addToSelf(kalmanGainFactor.mul(innovation));
		// P = P - K*H*P
		covMatrix.minusToSelf(kalmanGainFactor.mul(measureMatrix).mul(covMatrix));
	}
}

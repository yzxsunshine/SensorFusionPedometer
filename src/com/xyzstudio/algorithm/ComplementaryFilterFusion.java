package com.xyzstudio.algorithm;

import com.xyzstudio.math.Vector3;

public class ComplementaryFilterFusion implements FusionInterface {

	public final static float FILTER_COEFFICIENT = 0.98f;
	
	@Override
	public Vector3 orientationFusion(Vector3 accMagOrientation,
			Vector3 gyroOrientation) {
		// TODO Auto-generated method stub
		float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
        Vector3 fusedOrientation = new Vector3();
        // azimuth
        if (gyroOrientation.heading() < -0.5 * Math.PI && accMagOrientation.heading() > 0.0) {
        	float val = (float) (FILTER_COEFFICIENT * (gyroOrientation.heading() + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation.heading());
    		val -= (val > Math.PI) ? 2.0 * Math.PI : 0;
    		fusedOrientation.heading(val);
        }
        else if (accMagOrientation.heading() < -0.5 * Math.PI && gyroOrientation.heading() > 0.0) {
        	float val = (float) (FILTER_COEFFICIENT * gyroOrientation.heading() + oneMinusCoeff * (accMagOrientation.heading() + 2.0 * Math.PI)); 
        	val -= (val > Math.PI)? 2.0 * Math.PI : 0;
        	fusedOrientation.heading(val);
        }
        else {
        	float val = FILTER_COEFFICIENT * gyroOrientation.heading() + oneMinusCoeff * accMagOrientation.heading();
        	fusedOrientation.heading(val);
        }
        
        // pitch
        if (gyroOrientation.attitude() < -0.5 * Math.PI && accMagOrientation.attitude() > 0.0) {
        	float val = (float) (FILTER_COEFFICIENT * (gyroOrientation.attitude() + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation.attitude());
    		val -= (val > Math.PI) ? 2.0 * Math.PI : 0;
    		fusedOrientation.attitude(val);
        }
        else if (accMagOrientation.attitude() < -0.5 * Math.PI && gyroOrientation.attitude() > 0.0) {
        	float val = (float) (FILTER_COEFFICIENT * gyroOrientation.attitude() + oneMinusCoeff * (accMagOrientation.attitude() + 2.0 * Math.PI));
        	val -= (val > Math.PI)? 2.0 * Math.PI : 0;
        	fusedOrientation.attitude(val);
        }
        else {
        	float val = FILTER_COEFFICIENT * gyroOrientation.attitude() + oneMinusCoeff * accMagOrientation.attitude();
        	fusedOrientation.attitude(val);
        }
        
        // roll
        if (gyroOrientation.bank() < -0.5 * Math.PI && accMagOrientation.bank() > 0.0) {
        	float val = (float) (FILTER_COEFFICIENT * (gyroOrientation.bank() + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation.bank());
    		val -= (val > Math.PI) ? 2.0 * Math.PI : 0;
    		fusedOrientation.bank(val);
        }
        else if (accMagOrientation.bank() < -0.5 * Math.PI && gyroOrientation.bank() > 0.0) {
        	float val = (float) (FILTER_COEFFICIENT * gyroOrientation.bank() + oneMinusCoeff * (accMagOrientation.bank() + 2.0 * Math.PI));
        	val -= (val > Math.PI)? 2.0 * Math.PI : 0;
        	fusedOrientation.bank(val);
        }
        else {
        	float val = FILTER_COEFFICIENT * gyroOrientation.bank() + oneMinusCoeff * accMagOrientation.bank();
        	fusedOrientation.bank(val);
        }
        
        return fusedOrientation;
	}

	@Override
	public Vector3 translationFusion(Vector3 accel, Vector3 otherData,
			float accelWeight, float otherWeight) {
		// TODO Auto-generated method stub
		return null;
	}

}

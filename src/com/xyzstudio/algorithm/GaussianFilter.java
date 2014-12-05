package com.xyzstudio.algorithm;

import java.util.Iterator;
import java.util.Queue;

/**
 * This is a half side Gaussian Filter, may sound not very make sense
 * , but hope it could work for a incrementally input system.
 * */
public class GaussianFilter {
	private float[] weightTable = null;
	private int radius = 0;
	private final static float factor = -0.5f;  
	private float ds; // distance sigma  
	
	public GaussianFilter( int r, float ds ) {  
	    this.ds = ds;  
	    radius = r;
	    BuildDistanceWeightTable();
	} 
	
	private void BuildDistanceWeightTable() {
		int size = radius + 1;
		weightTable = new float[size];
		float weightSum = 0.0f;
		for(int i = -radius; i <= 0; i++) {
			weightTable[i+radius] = (float) Math.exp( i*i / ds * factor);
			weightSum += weightTable[i+radius];
		}
		
		for(int i = -radius; i <= 0; i++) {
			weightTable[i+radius] /= weightSum;
		}
	}
	
	public float DoFilter(Queue<Float> queue)
	{
		float result = 0.0f;
		Iterator<Float> it = queue.iterator();
		int i=0;
		
		while(it.hasNext())
		{
			result += weightTable[i] * (Float)it.next();
			i++;
		}
		
		return result;
	}
}

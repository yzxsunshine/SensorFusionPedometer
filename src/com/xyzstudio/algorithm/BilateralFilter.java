package com.xyzstudio.algorithm;

/*
 * Warning
 * */
public class BilateralFilter {
	private double[] cWeightTable = null;
	private double[] sWeightTable = null;
	private int radius = 0;
	private final static double factor = -0.5d;  
	private double ds; // distance sigma  
	private double rs; // range sigma  
	private float minAcceMagnitude;
	private float maxAcceMagnitude;
	private int sTableSize = 100;
	
	public BilateralFilter( int r ) {  
	    this.ds = 1.0f;  
	    this.rs = 1.0f;  
	    radius = r;
	} 
	
	private void buildDistanceWeightTable() {
		int size = 2 * radius + 1;
		cWeightTable = new double[size];
		for(int i = -radius; i <= radius; i++) {
			cWeightTable[i+radius]= Math.exp( i*i / ds * factor);
		}
	}
	
	private void buildSimilarityWeightTable() {
		sWeightTable = new double[sTableSize];
		float delta = (maxAcceMagnitude - minAcceMagnitude) / sTableSize;
		float delta2 = delta*delta;
		for(int i = 0; i < sTableSize; i++) {
			sWeightTable[i]= Math.exp( i*i * delta2 / rs * factor);
		}
	}
	
}

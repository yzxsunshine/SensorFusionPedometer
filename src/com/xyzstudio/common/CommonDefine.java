package com.xyzstudio.common;

public class CommonDefine {
	public static double EPSILON = 0.00000001f;
	public static double RATIO_EPSILON = 0.0001f;
	public static final float NS2S = 1.0f / 1000000000.0f;
	
	public static double Ratio2Degree(double ratio) {
		return ratio*180/Math.PI;
	}
	
	public static double Degree2Ratio(double degree) {
		return degree*Math.PI/180;
	}
}

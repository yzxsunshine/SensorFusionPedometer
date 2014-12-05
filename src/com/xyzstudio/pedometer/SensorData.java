package com.xyzstudio.pedometer;

import com.xyzstudio.math.Vector3;

public class SensorData {
	private float acceMag;
	private Vector3 acceOrient;
	private Vector3 orient;
	private float timeStamp;
	
	private boolean isPeak = false;
	private boolean isValley = false;
	private boolean isStep = false;
	
	private float meanVal = 0.0f;
	private float stdVal = 0.0f;
	
	private int stepCount = 0;
	
	private Vector3 orientMean;
	private Vector3 orientStd;
	
	public SensorData()
	{
		acceMag = 0.0f;
		timeStamp = 0.0f;
		orient = new Vector3();
		acceOrient = new Vector3();
		isPeak = false;
		isValley = false;
		isStep = false;
		meanVal = 0.0f;
		stdVal = 0.0f;
		stepCount = 0;
		orientMean = new Vector3();
		orientStd = new Vector3();
	}
	
	public SensorData(SensorData sd)
	{
		acceMag = sd.acceMag;
		acceOrient = sd.acceOrient;
		orient = sd.orient;
		timeStamp = sd.timeStamp;
		
		meanVal = sd.meanVal;
		stdVal = sd.stdVal;
		
		isPeak = sd.isPeak;
		isValley = sd.isValley;
		isStep = sd.isStep;
		stepCount = sd.stepCount;
		
		orientMean = sd.orientMean;
		orientStd = sd.orientStd;
	}
	
	public SensorData(float am, Vector3 ao, Vector3 o, float ts)
	{
		acceMag = am;
		acceOrient = ao;
		orient = o;
		timeStamp = ts;
		
		isPeak = false;
		isValley = false;
		isStep = false;
		
		stepCount = 0;
	}
	
	public void CopyData(float am, Vector3 ao, Vector3 o, float ts)
	{
		acceMag = am;
		acceOrient = ao;
		orient = o.clone();
		timeStamp = ts;
	}
	
	public SensorData Clone()
	{
		return new SensorData(this);
	}
	
	public float GetAcceMagnitude()
	{
		return acceMag;
	}
	
	public Vector3 GetAcceOrient()
	{
		return acceOrient;
	}
	
	public Vector3 GetOrient()
	{
		return orient;
	}
	
	public void SetPeak(boolean isp)
	{
		isPeak = isp;
	}
	
	public void SetValley(boolean isv)
	{
		isValley = isv;
	}
	
	public void SetStep(boolean iss)
	{
		isStep = iss;
	}
	
	public boolean IsPeak()
	{
		return isPeak;
	}
	
	public boolean IsValley()
	{
		return isValley;
	}
	
	public boolean IsStep()
	{
		return isStep;
	}
	
	public float GetMean()
	{
		return meanVal;
	}
	
	public float GetStd()
	{
		return stdVal;
	}
	
	public void SetMeanStd(float mean, float std)
	{
		meanVal = mean;
		stdVal = std;
	}
	
	public float GetTimeStamp()
	{
		return timeStamp;
	}
	
	public int GetStepCount()
	{
		return stepCount;
	}
	
	public void SetStepCount(int sc)
	{
		stepCount = sc;
	}
	
	public void UpdateStepCount()
	{
		stepCount++;
	}
	
	public Vector3 GetOrientMean() {
		return orientMean;
	}
	
	public Vector3 GetOrientStd() {
		return orientStd;
	}
	
	public void SetOrientMeanStd(Vector3 omean, Vector3 ostd) {
		orientMean = omean;
		orientStd = ostd;
	}
}

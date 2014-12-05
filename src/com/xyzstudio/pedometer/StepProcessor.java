package com.xyzstudio.pedometer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.xyzstudio.pedometer.StepListener;

import android.hardware.SensorManager;
import android.util.Log;

import com.xyzstudio.algorithm.GaussianFilter;
import com.xyzstudio.math.Matrix3x3;
import com.xyzstudio.math.RotationSpace;
import com.xyzstudio.math.Vector3;

// 定义一个新的数据结构，一个float 一个long，用来存peak值时间片段，然后在判断有这个数据结构组成的数组里面那个最合适
/**
 * 2014-04-11. Now we have filtered, std, mean value of 7 dimension( 1 accelerometer magnititude
 * , 3 accelerometer orientation and 3 orientation). The next step is to test and tuning the parameters.
 * We should notice that, with information from orientation and accelerometer orientation, we can tell the
 * difference between a fake step generate by moving hand.
 * 
 * 2014-4-27. We have a coarse version now. Some parameters including mean and standard derivation have been added
 * in. Next step, we could work on integration in order to get the position. And some other factors could be helpful to get better
 * performance:
 * 1. we could use current mean and standard deviation to process previous step when it was not recognized
 * 2. some classifier should be used here to find different type of walking to improve the precision 
 * */

public class StepProcessor {
	private static final String TAG = "com.xyzstudio.pedometer.StepProcessor";
	private static final int FILTER_WINDOW_SIZE = 10;
	private static final int QUEUE_SIZE = 100;
	private static final float MIN_STEP_COST = 400;	// mili second
	private static final float MIN_HALF_STEP = 200;	// mili second
	private static final int MAX_STEP_COST = 3000;	// mili second
	private static final float MAX_STEP_STD_RATE = 0.6f;
	private static final float MIN_PEAK_VALLEY_DIFF = 0.1f;
	
	private long startTimeStamp = 0;
	private int deltaTime = 30;	// millisecond
	private int curTimeStamp = 0;
	private static final long maxTimeStamp = 100000000;
	
	private Queue<Float> acceMagWindow = null;
	private Queue<Float> acceMagQueue = null;
	private Queue<Vector3> orientWindow = null;
	
	private Vector3 avgStepAcce = null;
	private int frameInStep = 0;
	
	private float lastVal = 0.0f;
	private float lastDiff = 0.0f;
	private float peakValue = 0.0f;
	private float valleyValue = 0.0f;
	private int peakNum = 0;
	private int valleyNum = 0;
	
	private int seqLen = 0;
	private float meanVal = 0.0f;
	private float stdVal = 0.0f;
	private float sqrSum = 0.0f;
	
	private GaussianFilter gaussianFilter = null;
	private int stepNum = 0;
	
	private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();
	private SensorDataListener mSensorDataListener = null;

	public StepProcessor(int dt)
	{
		acceMagWindow = new LinkedList<Float>();
		acceMagQueue = new LinkedList<Float>();
		avgStepAcce = new Vector3(0, 0, 0);
		orientWindow = new LinkedList<Vector3>();
		
		gaussianFilter = new GaussianFilter(FILTER_WINDOW_SIZE - 1, 30.0f);
		this.deltaTime = dt;
		frameInStep = 0;
	}
	
	public void Initialize(long startTS)
	{
		startTimeStamp = startTS;
		curTimeStamp = 0;
		peakNum = 0;
		valleyNum = 0;
		if(stdVal == 0)
		{
			meanVal = 0.0f;
		}
		stdVal = 0.0f;
		seqLen = 0;
		sqrSum = 0.0f;
		avgStepAcce = Vector3.zeros();
		frameInStep = 0;
	}
	
	public void InitializeBrief(long startTS)
	{
		startTimeStamp = startTS;
		curTimeStamp = 0;
		peakNum = 0;
		valleyNum = 0;
		avgStepAcce = Vector3.zeros();
		frameInStep = 0;
	}
	
	public void addStepListener(StepListener sl) {
        mStepListeners.add(sl);
    }
	
	public void setSensorDataListener(SensorDataListener sdl) {
        mSensorDataListener = sdl;
    }
	
	
	public int GetStepNumber()
	{
		return stepNum;
	}
	
	public void AddData(Vector3 acce, Vector3 orient) {
		Matrix3x3 rotMat = RotationSpace.Eular2Matrix(orient);
		Vector3 worldAcce = rotMat.mul(acce.clone());	// z is gravity, x and y are directions
		//float mag = worldAcce.magnitude();
		Vector3 worldAcceNorm = worldAcce.clone();
		worldAcceNorm.normalize();
		int wSize = acceMagWindow.size();
		curTimeStamp += deltaTime;
		float val = worldAcceNorm.x() + worldAcceNorm.y();
		boolean isPeak = false;
		boolean isValley = false;
		boolean isStep = false;
		avgStepAcce.addToSelf(worldAcce);
		frameInStep++;
		if(wSize >= FILTER_WINDOW_SIZE)
			acceMagWindow.poll();
		acceMagWindow.offer(val);
		if(wSize >= FILTER_WINDOW_SIZE)
		{
			val = gaussianFilter.DoFilter(acceMagWindow);
		}
		
		wSize = acceMagQueue.size();
		
		if(wSize >= QUEUE_SIZE)
		{
			float prevVal = acceMagQueue.poll();
			acceMagQueue.offer(val);
			meanVal = (meanVal*QUEUE_SIZE - prevVal + val) / QUEUE_SIZE;
			sqrSum = sqrSum - prevVal*prevVal + val*val;
			stdVal = (float)Math.sqrt((sqrSum - QUEUE_SIZE*meanVal*meanVal)/(QUEUE_SIZE-1));
		}
		else
		{
			acceMagQueue.offer(val);
			wSize++;
			meanVal = (meanVal*(wSize-1) + val)/wSize;
			sqrSum = sqrSum + val*val;
			stdVal = (float)Math.sqrt((sqrSum - wSize*meanVal*meanVal) / (wSize - 1));
		}
		
		if(wSize > 1) {
			float diff = val - lastVal;	
			if(wSize > 2) {
				if(diff * lastDiff < 0) {
					if(diff < 0) {	// Peak
						if( peakNum == valleyNum
						  &&curTimeStamp > MIN_STEP_COST
						  &&val > meanVal + stdVal*MAX_STEP_STD_RATE
						  )
						{
							if(peakValue - valleyValue > MIN_PEAK_VALLEY_DIFF) {
								isPeak = true;
								stepNum++;
								peakNum++;
								Log.i(TAG, "A Step");
								if(frameInStep > 0)
								{
									avgStepAcce.divide(frameInStep);
									for (StepListener stepListener : mStepListeners) {
		                                stepListener.onStep(avgStepAcce);
		                            }
								}
								InitializeBrief(curTimeStamp);
								peakValue = val;
							}
						}
						else {
							if(peakValue < val) {
								peakValue = val;
							}
						}
					}
					else {	// Valley
						if( peakNum > valleyNum
						  &&curTimeStamp > MIN_HALF_STEP
						  )
						{
							valleyNum++;
							isValley = true;
							valleyValue = val;
						}
						else {
							if(valleyValue > val) {
								valleyValue = val;
							}
						}
					}
				}
			}
			lastDiff = diff;
		}
		lastVal = val;
		
		
		if(mSensorDataListener != null)
		{
			SensorData sd = new SensorData(val, worldAcce, orient
										 , 0.001f*curTimeStamp);
			sd.SetStepCount(stepNum);
			sd.SetPeak(isPeak);
			sd.SetValley(isValley);
			sd.SetStep(isStep);
			sd.SetMeanStd(meanVal, stdVal);
			mSensorDataListener.passValue(sd);
		}
	}
	
}

package com.xyzstudio.pedometer;

import java.util.ArrayList;

import com.xyzstudio.math.Vector3;

/**
 * Counts steps provided by StepDetector and passes the current
 * step count to the activity.
 */
public class StepMotion implements StepListener{

    private Vector3 direction = new Vector3();
    private int stepNum;
    
    public StepMotion() {
        notifyListener();
    }

    public void setSteps(Vector3 dir, int stepNum) {
    	direction = dir.clone();
    	this.stepNum = stepNum;
        notifyListener();
    }
    public void onStep(Vector3 dir) {
    	direction = dir.clone();
    	stepNum++;
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }
    

	@Override
	public void passValue() {
		// TODO Auto-generated method stub
		
	}    
    
    //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void stepsChanged(Vector3 direction, int stepNum);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged(direction, stepNum);
        }
    }

	@Override
	public void onStep() {
		// TODO Auto-generated method stub
		
	}
    
}

package com.xyzstudio.pedometer;

import java.util.ArrayList;
import java.util.Queue;

import com.xyzstudio.math.Vector3;

public class StepDebuger implements SensorDataListener {

	SensorData mSensorData;
	
	public StepDebuger() {
		
		//notifyListener();
    }

    public void setSteps(SensorData sd) {
    	mSensorData = sd;
        notifyListener();
    }
    
    public void passValue(SensorData sd) {
    	mSensorData = sd;
        notifyListener();
    }
    
    public void reloadSettings() {
        notifyListener();
    }

	
	 //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void stepsChanged(SensorData sd);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged(mSensorData.Clone());	// Do we really need a clone?
        }
    }

}

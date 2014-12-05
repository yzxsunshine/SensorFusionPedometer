package com.xyzstudio.pedometer;

import com.xyzstudio.algorithm.ComplementaryFilterFusion;
import com.xyzstudio.algorithm.FusionInterface;
import com.xyzstudio.badlogic.framework.impl.SensorFusionHandler;
import com.xyzstudio.math.Vector3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class StepService extends Service {
	private static final String TAG = "com.xyzstudio.pedometer.StepService";
	private SensorFusionHandler sensorHandler = null;
	private static final int FILTER_NEIGHBOR_SIZE = 10;
	
	private FusionInterface orientFusion = null;
	private StepProcessor stepProcess = null;
	private static final int deltaTime = 30;
	private StepDebuger stepDebuger = null;
	private StepMotion stepMotion = null;
	private int stepNum;
	private Vector3 stepDir;
	
	SensorData mSensorData;
	@Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();
        stepNum = 0;
        sensorHandler = new SensorFusionHandler(this);
		orientFusion = new ComplementaryFilterFusion();
		stepMotion = new StepMotion();
        stepMotion.setSteps(Vector3.zeros(), 0);
        stepMotion.addListener(stepListener);
		stepDebuger = new StepDebuger();
		stepDebuger.addListener(debugListener);
        stepProcess = new StepProcessor(deltaTime);
		stepProcess.addStepListener(stepMotion);
		stepProcess.setSensorDataListener(stepDebuger);
		stepProcess.Initialize(0);
		stepDir = Vector3.zeros();
		mSensorData = new SensorData();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");        
        super.onDestroy();
        
        // Stop detecting
        sensorHandler.onStop();
        // Tell the user we stopped.
        Toast.makeText(this, "Stop Step Service", Toast.LENGTH_SHORT).show();
    }

	
    public void reloadSettings() {
    
    }
    
	public class StepBinder extends Binder {
	    public StepService getService() {
	        return StepService.this;
	    }
	}

	private final IBinder mBinder = new StepBinder();
	/**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private StepMotion.Listener stepListener = new StepMotion.Listener() {
        public void stepsChanged(Vector3 dir, int num) {
            stepNum = num;
            stepDir = dir;
            passValue();
        }
        public void passValue() {
            if (callback != null) {
                callback.stepsChanged(stepDir, stepNum);
            }
        }
    };
    
    private StepDebuger.Listener debugListener = new StepDebuger.Listener() {
        public void passValue() {
            if (callback != null) {
                callback.stepsChanged(mSensorData);
            }
        }
		@Override
		public void stepsChanged(SensorData sd) {
			// TODO Auto-generated method stub
			mSensorData = sd;
			passValue();
		}
    };
    
    public interface ICallback {
        public void stepsChanged(Vector3 stepDir, int value);
        public void stepsChanged(SensorData sd);
    }
    
    private ICallback callback;

    public void registerCallback(ICallback cb) {
        callback = cb;
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }

    public void StartCount()
	{
		signalHandler.postDelayed(runnable, deltaTime);
	}
	
	public void EndCount()
	{
		signalHandler.removeCallbacks(runnable);
	}
	
	private Handler signalHandler = new Handler(); 
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Vector3 acce = sensorHandler.getAccel();
			Vector3 orient = orientFusion.orientationFusion(sensorHandler.getAccMagOrientation(), sensorHandler.getGyroOrientation());
			sensorHandler.setGyroOrientation(orient);
			//stepProcess.AddData(acceMagnitude, acceOrient, orient);
			stepProcess.AddData(acce, orient);
			signalHandler.postDelayed(runnable, deltaTime);
		}  
		
	};
}

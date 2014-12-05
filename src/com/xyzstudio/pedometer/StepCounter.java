/**
 * 
 */
package com.xyzstudio.pedometer;

import java.util.Queue;

import com.xyzstudio.algorithm.ComplementaryFilterFusion;
import com.xyzstudio.algorithm.FusionInterface;
import com.xyzstudio.badlogic.framework.impl.SensorFusionHandler;
import com.xyzstudio.math.Vector3;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;

/**
 * @author yzx
 * 
 * The class is used for counting steps, there're several steps for the developing plan:
 * 1. Implement a simple step counter only include peak and valley detector
 * , a bilateral filter with a filter window like 10 delta t, and take peaks as steps.
 * Usually, a valley is where we lift up our foot, and a peak is we put down our feet and moving forward.
 * Some delay(less then half a second) is tolerable in a pedometer application.
 * 2. Do bilateral filter for our accelerometer direction as well, and add the step length to the direction.
 * Besides, we should apply it to Kalman Filter. Some map constrains and marked place synchronizing processes
 * should be finished in this period.
 * 3. Add DTW to get better measurement of steps signal. But we have to deal with the similarity rate.
 * There're a lot of variations of a series of step signals, maybe we can calculate a average sequence
 * and a neighbor sequence to deal with the variations.
 * 4. Before this step, we only deal with a phone in our hand. This time, we should deal with several situations
 * like a phone in ones pocket, a phone swinging with masters hand, switches between different modes. Maybe some
 * semi-supervise machine learning like P-N learning could be helpful. 
 * 
 * Constrains of human step:
 * 1. One step may cost 0.3s ~ 3s
 * 2. According to the paper of MSRA[1] the acceleration magnitudes are between 0.2g ~ 2g
 * 3. If a low peak comes after a high peak, there must be a quite deep valley between them, verse visa 
 *
 */
public class StepCounter {
	private SensorFusionHandler sensorHandler = null;
	private static final int FILTER_NEIGHBOR_SIZE = 10;
	
	private FusionInterface orientFusion = null;
	private StepProcessor stepProcess = null;
	private static final int deltaTime = 30;
	public StepCounter(Context context)
	{
		sensorHandler = new SensorFusionHandler(context);
		orientFusion = new ComplementaryFilterFusion();
		stepProcess = new StepProcessor(deltaTime);
		stepProcess.Initialize(0);
	}
	
	public void StartCount()
	{
		signalHandler.postDelayed(runnable, deltaTime);
	}
	
	public void EndCount()
	{
		signalHandler.removeCallbacks(runnable);
	}
	
	public void onStop() {
		sensorHandler.onStop();
	}
	
	public void onPause() {
		sensorHandler.onStop();
	}

	public void onResume() {
		sensorHandler.onResume();
	}
	
	
	private Handler signalHandler = new Handler(); 
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Vector3 acce = sensorHandler.getAccel();
			Vector3 orient = orientFusion.orientationFusion(sensorHandler.getAccMagOrientation(), sensorHandler.getGyroOrientation());
			stepProcess.AddData(acce, orient);
			signalHandler.postDelayed(runnable, deltaTime);
		}  
		
	};
}

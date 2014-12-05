package com.xyzstudio.sensorfusion;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.achartengine.GraphicalView;

import com.xystudio.sensorfusion.R;
import com.xyzstudio.chart.ChartInterface;
import com.xyzstudio.chart.MapChart;
import com.xyzstudio.chart.OrientChart;
import com.xyzstudio.chart.PeakValleyChart;
import com.xyzstudio.chart.SensorChart;
import com.xyzstudio.math.Vector3;
import com.xyzstudio.pedometer.SensorData;
import com.xyzstudio.pedometer.StepService;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PedometerStepCounter extends Activity {
	private static final String TAG = "com.xyzstudio.PedometerStepCounter";
	private TextView mStepValueView;
    private int mStepValue;
    private boolean isServiceRunning;
    private StepService mService;
    private ChartInterface sensorChart;
    private GraphicalView chartView;
    private MapChart glView;
    
    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;
    private static final int MENU_MAP = 1;
    private static final int MENU_SENSOR = 2;
    private static final int MENU_PEAKVALLEY = 3;
    private static final int MENU_ACCEORIENT = 4;
    private static final int MENU_ORIENT = 5;
    private int mChartStatus = 1;
    
    private static String sdState = Environment.getExternalStorageState();
    private static String path = Environment.getExternalStorageDirectory().toString();
	private Vector<SensorData> sdList = new Vector<SensorData>();
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_count);
		Button startBtn = (Button) this.findViewById(R.id.startRecord);
		Button endBtn = (Button) this.findViewById(R.id.endRecord);
		startBtn.setOnClickListener(listener);
		endBtn.setOnClickListener(listener);
		mStepValue = 0;
		isServiceRunning = false;
		
		LinearLayout linearView=(LinearLayout)findViewById(R.id.chart);
		linearView.setVisibility(View.GONE);
		sensorChart = new SensorChart();
		mChartStatus = MENU_MAP;
		//chartView = sensorChart.getDemoChartGraphicalView(this);
	       
        //linearView.addView(chartView,
        //        new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		LinearLayout mapLinearView=(LinearLayout)findViewById(R.id.mapchart);
		mapLinearView.setVisibility(View.VISIBLE);
		glView = new MapChart(this);
		mapLinearView.addView(glView,
						   new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
    
    @Override
	protected void onStop() {
		super.onStop();
	}
 
    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
        //stopStepService();
        //unbindStepService();
    }
	
    @Override
	protected void onPause() {
		super.onPause();
		unbindStepService();
	}

	@Override
	public void onResume() {
        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!isServiceRunning) {
            startStepService();
            bindStepService();
        }
        else {
            bindStepService();
        }
        
        mStepValueView     = (TextView) findViewById(R.id.stepnum);
		super.onResume();
	}
	
	 // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(Vector3 dir, int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0, dir));
        }

		@Override
		public void stepsChanged(SensorData sd) {
			// TODO Auto-generated method stub
			mHandler.sendMessage(mHandler.obtainMessage(DEBUG_MSG, 0, 0, sd));
		}
    };
    
    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, MENU_MAP, 0, "Map");
        menu.add(0, MENU_SENSOR, 0, "Sensor Data");
        menu.add(0, MENU_PEAKVALLEY, 0, "Peak & Valley");
        menu.add(0, MENU_ORIENT, 0, "Orient");
        menu.add(0, MENU_QUIT, 0, "Quit");
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case MENU_MAP:
    		if(mChartStatus != MENU_MAP)
    		{
    			mService.EndCount();
    			sensorChart = null;
    			LinearLayout linearView=(LinearLayout)findViewById(R.id.chart);
    			linearView.setVisibility(View.GONE);
    			linearView.removeView(chartView);
    			//sensorChart = new SensorChart();
    			//chartView = sensorChart.getDemoChartGraphicalView(this);
    			LinearLayout mapLinearView=(LinearLayout)findViewById(R.id.mapchart);
    			mapLinearView.setVisibility(View.VISIBLE);
    			glView = new MapChart(this);
    			mapLinearView.addView(glView,
    	                new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
    			mService.StartCount();
    			mChartStatus = MENU_MAP;
    		}
    		return true;
        	case MENU_SENSOR:
        		if(mChartStatus != MENU_SENSOR)
        		{
        			mService.EndCount();
        			sensorChart = null;
        			LinearLayout mapLinearView=(LinearLayout)findViewById(R.id.mapchart);
        			mapLinearView.setVisibility(View.GONE);
        			
        			LinearLayout linearView=(LinearLayout)findViewById(R.id.chart);
        			linearView.setVisibility(View.VISIBLE);
        			linearView.removeView(chartView);
        			sensorChart = new SensorChart();
        			chartView = sensorChart.getDemoChartGraphicalView(this);
        			linearView.addView(chartView,
        	                new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        			mService.StartCount();
        			mChartStatus = MENU_SENSOR;
        		}
        		return true;
        	case MENU_PEAKVALLEY:
        		if(mChartStatus != MENU_PEAKVALLEY)
        		{
        			mService.EndCount();
        			sensorChart = null;
        			LinearLayout mapLinearView=(LinearLayout)findViewById(R.id.mapchart);
        			mapLinearView.setVisibility(View.GONE);
        			
        			LinearLayout linearView=(LinearLayout)findViewById(R.id.chart);
        			linearView.setVisibility(View.VISIBLE);
        			linearView.removeView(chartView);
        			sensorChart = new PeakValleyChart();
        			chartView = sensorChart.getDemoChartGraphicalView(this);
        			linearView.addView(chartView,
        	                new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        			mService.StartCount();
        			mChartStatus = MENU_PEAKVALLEY;
        		}
        		return true;
        	case MENU_ORIENT:
        		if(mChartStatus != MENU_ORIENT)
        		{
        			mService.EndCount();
        			sensorChart = null;
        			LinearLayout mapLinearView=(LinearLayout)findViewById(R.id.mapchart);
        			mapLinearView.setVisibility(View.GONE);
        			
        			LinearLayout linearView=(LinearLayout)findViewById(R.id.chart);
        			linearView.setVisibility(View.VISIBLE);
        			linearView.removeView(chartView);
        			
        			sensorChart = new OrientChart();
        			chartView = sensorChart.getDemoChartGraphicalView(this);
        			linearView.addView(chartView,
        	                new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        			mService.StartCount();
        			mChartStatus = MENU_ORIENT;
        		}
        		return true;
            case MENU_QUIT:
                unbindStepService();
                stopStepService();
                finish();
                return true;
        }
        return false;
    }

    
    private static final int STEPS_MSG = 1;
    private static final int DEBUG_MSG = 2;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    Vector3 dir = (Vector3)msg.obj;
                    if(mChartStatus == MENU_MAP)
                    {
                    	glView.updateStep(dir);
                    	glView.postInvalidate();
                    }
                    break;
                case DEBUG_MSG:
                    SensorData sd = (SensorData)msg.obj;
                    //sensorChart.updateData(sd.GetTimeStamp(), sd.GetAcceMagnitude());
                    if(mChartStatus != MENU_MAP && sensorChart != null)
                    {
                    	sensorChart.updateData(sd);
                    	chartView.postInvalidate();
                    }
                    else if(mChartStatus == MENU_MAP)
                    {
                    	glView.updateData(sd);
                    	glView.postInvalidate();
                    }
                    //mStepValue = sd.GetStepCount();
                    //mStepValueView.setText("" + mStepValue);
                    sdList.add(sd);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
    private void startStepService() {
        if (! isServiceRunning) {
            Log.i(TAG, "[SERVICE] Start");
            isServiceRunning = true;
            startService(new Intent(PedometerStepCounter.this,
                    StepService.class));
        }
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(PedometerStepCounter.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(PedometerStepCounter.this,
                  StepService.class));
        }
        isServiceRunning = false;
    }
    
	private View.OnClickListener listener = new OnClickListener() {  
		public void onClick(View v) {  
			Button view = (Button) v;  
	        switch (view.getId()) {  
	        	case R.id.startRecord:  
	        		mService.StartCount();
	                break;  
	            case R.id.endRecord:
	            	mService.EndCount(); 
	            	save();
	            	sdList.clear();
	                break;  
	  
	        }
	    }  
	};
	
	@SuppressLint("DefaultLocale")
	private void save() {
		String ts = String.format("%d", System.currentTimeMillis());  
		try
		{
			File file;
			String dirPathFirstOrder = path + "/ShowMeThePath";
			String dirPath = path + "/ShowMeThePath/Data/";
			if(sdState.equals(Environment.MEDIA_MOUNTED)) {
				// root directory of SD card
				file = new File(dirPathFirstOrder);
				boolean mkdirFlag = true;
				if(!file.exists()){
					mkdirFlag = file.mkdirs();
				}
				file = new File(dirPath);
				if(!file.exists()){
					mkdirFlag = file.mkdirs();
				}
				if(!mkdirFlag) {
					Toast.makeText(PedometerStepCounter.this, "Fail to make directory structure.", Toast.LENGTH_LONG).show();
					return;
				}
		    }
			String outFileSensorData = String.format("sd_0427_%s.txt", ts);
			File acceFile = new File(dirPath, outFileSensorData);
			FileOutputStream ostream = new FileOutputStream(acceFile);
			for(int i=0; i<sdList.size(); i++) {
				if(sdList.get(i) == null)
					break;
				
				String tmp = String.format("%f %f %f %f %f %f %f %f %f\r\n"
										 , sdList.get(i).GetAcceMagnitude()
										 , sdList.get(i).GetAcceOrient().x()
										 , sdList.get(i).GetAcceOrient().y()
										 , sdList.get(i).GetAcceOrient().z()
										 , sdList.get(i).GetOrient().x()
										 , sdList.get(i).GetOrient().y()
										 , sdList.get(i).GetOrient().z()
										 , sdList.get(i).GetMean()
										 , sdList.get(i).GetStd()
										 );
				byte[] buffer = tmp.getBytes();
				ostream.write(buffer);
			}
			ostream.flush();
			ostream.close();
			
			Toast.makeText(PedometerStepCounter.this, "Save Succeed", Toast.LENGTH_LONG).show();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}

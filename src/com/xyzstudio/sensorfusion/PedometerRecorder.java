package com.xyzstudio.sensorfusion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xystudio.sensorfusion.R;
import com.xyzstudio.algorithm.ComplementaryFilterFusion;
import com.xyzstudio.algorithm.FusionInterface;
import com.xyzstudio.badlogic.framework.impl.SensorFusionHandler;
import com.xyzstudio.badlogic.framework.impl.SensorFusionInput;
import com.xyzstudio.math.Vector3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PedometerRecorder extends Activity {
	private SensorFusionHandler sensorHandler;
	
	private List<Vector3> acceList = new ArrayList<Vector3>();
	private List<Vector3> orientList = new ArrayList<Vector3>();
	private List<Float> acceTimeStampList = new ArrayList<Float>();
	private List<Float> orientTimeStampList = new ArrayList<Float>();
	
	private static String sdState = Environment.getExternalStorageState();
    private static String path = Environment.getExternalStorageDirectory().toString();
	private FusionInterface orientFusion;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_count);
		sensorHandler = new SensorFusionHandler(this);
		// Initialize Sensor
		orientFusion = new ComplementaryFilterFusion();
		Button startBtn = (Button) this.findViewById(R.id.startRecord);
		Button endBtn = (Button) this.findViewById(R.id.endRecord);
		
		startBtn.setOnClickListener(listener);
		endBtn.setOnClickListener(listener);
	}
    
    @Override
	protected void onStop() {
		super.onStop();
		sensorHandler.onStop();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		sensorHandler.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		sensorHandler.onResume();
	}
	
	private Handler recordHandler = new Handler(); 
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			acceList.add(sensorHandler.getAccel().clone());
			acceTimeStampList.add(sensorHandler.getAccelTimeStamp());
			
			orientList.add(orientFusion.orientationFusion(sensorHandler.getAccMagOrientation(), sensorHandler.getGyroOrientation()));
			orientTimeStampList.add(sensorHandler.getTimeStamp());
			
			recordHandler.postDelayed(runnable, 30);
		}  
		
	};
	
	
	
	@SuppressLint("DefaultLocale")
	private void save() {
		String ts = String.format("%d", System.currentTimeMillis());  
		try
		{
			File file;
			TextView text = (TextView) this.findViewById(R.id.caseName);
			String dirPathFirstOrder = path + "/ShowMeThePath";
			String dirPath = path + "/ShowMeThePath/"+ text.getText().toString() + ts + "/";
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
					Toast.makeText(PedometerRecorder.this, "Fail to make directory structure.", Toast.LENGTH_LONG).show();
					return;
				}
		    }
			String outFileAcce = String.format("acce_%s.txt", ts);
			String outFileorient = String.format("orient_%s.txt", ts);
			File acceFile = new File(dirPath, outFileAcce);
			File orientFile = new File(dirPath, outFileorient);
			FileOutputStream ostream = new FileOutputStream(acceFile);
			for(int i=0; i<acceList.size(); i++) {
				if(acceList.get(i) == null)
					break;
				String tmp = String.format("%f %f %f %f \r\n"
										 , acceList.get(i).x()
										 , acceList.get(i).y()
										 , acceList.get(i).z()
										 , acceTimeStampList.get(i));
				byte[] buffer = tmp.getBytes();
				ostream.write(buffer);
			}
			ostream.flush();
			ostream.close();
			
			FileOutputStream ostreamorient = new FileOutputStream(orientFile);
			for(int i=0; i<orientList.size(); i++) {
				if(orientList.get(i) == null)
					break;
				String tmp = String.format("%f %f %f %f \r\n"
										 , orientList.get(i).x()
										 , orientList.get(i).y()
										 , orientList.get(i).z()
										 , orientTimeStampList.get(i));
				byte[] buffer = tmp.getBytes();
				ostreamorient.write(buffer);
				
			}
			ostreamorient.flush();
			ostreamorient.close();
			
			Toast.makeText(PedometerRecorder.this, "Save Succeed", Toast.LENGTH_LONG).show();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private View.OnClickListener listener = new OnClickListener() {  
		public void onClick(View v) {  
			Button view = (Button) v;  
	        switch (view.getId()) {  
	        	case R.id.startRecord:  
	        		recordHandler.postDelayed(runnable, 30);
	                break;  
	            case R.id.endRecord:
	            	recordHandler.removeCallbacks(runnable);
	                save();  
	                break;  
	  
	        }
	    }  
	};
}

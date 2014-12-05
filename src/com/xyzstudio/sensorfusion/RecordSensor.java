package com.xyzstudio.sensorfusion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xystudio.sensorfusion.R;

import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.Contacts.Data;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

class TriDataType {
	public float[] val = null;
	
	TriDataType() {
		val = new float[3];
	}
};

public class RecordSensor extends Activity
implements SensorEventListener {

	private SensorManager mSensorManager = null;
	private List<TriDataType> acceList = new ArrayList<TriDataType>();
	private List<TriDataType> gyroList = new ArrayList<TriDataType>();
	private List<TriDataType> magnetList = new ArrayList<TriDataType>();
	private List<Long> acceTimeStampList = new ArrayList<Long>();
	private List<Long> gyroTimeStampList = new ArrayList<Long>();
	private List<Long> magnetTimeStampList = new ArrayList<Long>();
	private static String sdState = Environment.getExternalStorageState();
    private static String path = Environment.getExternalStorageDirectory().toString();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_count);
		// Initialize Sensor
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		
		Button startBtn = (Button) this.findViewById(R.id.startRecord);
		Button endBtn = (Button) this.findViewById(R.id.endRecord);
		
		startBtn.setOnClickListener(listener);
		endBtn.setOnClickListener(listener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_sensor, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		TriDataType tdt = new TriDataType();
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER: {
				System.arraycopy(event.values, 0, tdt.val, 0, 3);
				acceList.add(tdt);
				acceTimeStampList.add((Long)event.timestamp);
			}
		case Sensor.TYPE_GYROSCOPE: {
				System.arraycopy(event.values, 0, tdt.val, 0, 3);
				gyroList.add(tdt);
				gyroTimeStampList.add((Long)event.timestamp);
			}
		case Sensor.TYPE_MAGNETIC_FIELD: {
				System.arraycopy(event.values, 0, tdt.val, 0, 3);
				magnetList.add(tdt);
				magnetTimeStampList.add((Long)event.timestamp);
			}
		}
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		initSensorListeners();
	}
	
	public void initSensorListeners() {
		mSensorManager.registerListener(this 
									  , mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
									  , SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this 
				  , mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
				  , SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this 
				  , mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
				  , SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	private View.OnClickListener listener = new OnClickListener() {  
		public void onClick(View v) {  
			Button view = (Button) v;  
	        switch (view.getId()) {  
	        	case R.id.startRecord:  
	        		initSensorListeners();  
	                break;  
	            case R.id.endRecord:
	            	
	                save();  
	                break;  
	  
	        }
	    }  
	};
	
	@SuppressLint("DefaultLocale")
	private void save() {
		mSensorManager.unregisterListener(this);
		String ts = String.format("%d", System.currentTimeMillis());  
		try
		{
			File file;
			String dirPathFirstOrder = path + "/ShowMeThePath";
			String dirPath = path + "/ShowMeThePath/Sensor" + ts + "/";
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
					Toast.makeText(RecordSensor.this, "Fail to make directory structure.", Toast.LENGTH_LONG).show();
					return;
				}
		    }
			String outFileAcce = String.format("sensor_acce_%s.txt", ts);
			String outFileGyro = String.format("sensor_gyro_%s.txt", ts);
			String outFileMagnet = String.format("sensor_magnet_%s.txt", ts);
			File acceFile = new File(dirPath, outFileAcce);
			File gyroFile = new File(dirPath, outFileGyro);
			File magnetFile = new File(dirPath, outFileMagnet);
			FileOutputStream ostream = new FileOutputStream(acceFile);
			for(int i=0; i<acceList.size(); i++) {
				String tmp = String.format("%f %f %f %d \r\n"
										 , acceList.get(i).val[0]
										 , acceList.get(i).val[1]
										 , acceList.get(i).val[2]
										 , acceTimeStampList.get(i));
				byte[] buffer = tmp.getBytes();
				ostream.write(buffer);
			}
			ostream.flush();
			ostream.close();
			
			FileOutputStream ostreamGyro = new FileOutputStream(gyroFile);
			for(int i=0; i<gyroList.size(); i++) {
				String tmp = String.format("%f %f %f %d \r\n"
										 , gyroList.get(i).val[0]
										 , gyroList.get(i).val[1]
										 , gyroList.get(i).val[2]
										 , gyroTimeStampList.get(i));
				byte[] buffer = tmp.getBytes();
				ostreamGyro.write(buffer);
				
			}
			ostreamGyro.flush();
			ostreamGyro.close();
			
			FileOutputStream ostreamMagnet = new FileOutputStream(magnetFile);
			for(int i=0; i<magnetList.size(); i++) {
				String tmp = String.format("%f %f %f %d \r\n"
										 , magnetList.get(i).val[0]
										 , magnetList.get(i).val[1]
										 , magnetList.get(i).val[2]
										 , magnetTimeStampList.get(i));
				byte[] buffer = tmp.getBytes();
				ostreamMagnet.write(buffer);
			}
			ostreamMagnet.flush();
			ostreamMagnet.close();
			
			Toast.makeText(RecordSensor.this, "Save Succeed", Toast.LENGTH_LONG).show();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
}


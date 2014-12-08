package com.xyzstudio.sensorfusion;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.xystudio.sensorfusion.R;
import com.xyzstudio.common.CommonDefine;
import com.xyzstudio.math.Matrix3x3;
import com.xyzstudio.math.Quaternion;
import com.xyzstudio.math.RotationSpace;
import com.xyzstudio.math.Vector3;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SensorFusionMain extends Activity 
implements SensorEventListener, RadioGroup.OnCheckedChangeListener {
	private SensorManager mSensorManager = null;
	private Vector3 accel = new Vector3();
	private Vector3 gyro = new Vector3();
	private Vector3 magnet = new Vector3();
	
	// rotation matrix from gyro data
    private Matrix3x3 gyroMatrix = new Matrix3x3();
 
    // orientation angles from gyro matrix
    private Vector3 gyroOrientation = new Vector3();
 
    // orientation angles from accel and magnet
    private Vector3 accMagOrientation = new Vector3();
 
    // final orientation angles from sensor fusion
    private Vector3 fusedOrientation = new Vector3();
 
    // accelerometer and magnetometer based rotation matrix
    private Matrix3x3 rotationMatrix = new Matrix3x3();
    
	private float timestamp;
	private int initState = 0;
    
	public static final int TIME_CONSTANT = 30;
	public static final float FILTER_COEFFICIENT = 0.98f;
	private Timer fuseTimer = new Timer();
	
	// The following members are only for displaying the sensor output.
	public Handler mHandler;
	private RadioGroup mRadioGroup;
	private TextView mAzimuthView;
	private TextView mPitchView;
	private TextView mRollView;
	private int radioSelection;
	DecimalFormat d = new DecimalFormat("#.##");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surface_fusion_main);
		// Initialize Sensor
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		gyroOrientation = Vector3.zeros();
 
        // initialise gyroMatrix with identity matrix
		gyroMatrix = Matrix3x3.eye();
		// get sensorManager and initialise sensor listeners
        initSensorListeners();
        
        // wait for one second until gyroscope and magnetometer/accelerometer
        // data is initialised then scedule the complementary filter task
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                                      1000, TIME_CONSTANT);
        
        // GUI stuff
        mHandler = new Handler();
        radioSelection = 0;
        d.setRoundingMode(RoundingMode.HALF_UP);
        d.setMaximumFractionDigits(3);
        d.setMinimumFractionDigits(3);
        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup1);
        mAzimuthView = (TextView)findViewById(R.id.textView4);
        mPitchView = (TextView)findViewById(R.id.textView5);
        mRollView = (TextView)findViewById(R.id.textView6);
        mRadioGroup.setOnCheckedChangeListener(this);
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
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER: {
				accel.copyData(event.values);
				calculateAccMagOrientation();
				break;
			}
		case Sensor.TYPE_GYROSCOPE: {
				gyroFunction(event);
				//System.arraycopy(event.values, 0, gyro, 0, 3);
				break;
			}
		case Sensor.TYPE_MAGNETIC_FIELD: {
				magnet.copyData(event.values);
				break;
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

	// calculates orientation angles from accelerometer and magnetometer output
	public void calculateAccMagOrientation() {
	    if(SensorManager.getRotationMatrix(rotationMatrix.getData(), null, accel.getData(), magnet.getData())) {
	        SensorManager.getOrientation(rotationMatrix.getData(), accMagOrientation.getData());
	        if(initState == 0)
	        {
	        	initState = 1;
	        }
	    }
	}
	
    // This function performs the integration of the gyroscope data.
    // It writes the gyroscope based orientation into gyroOrientation.
    public void gyroFunction(SensorEvent event) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (accMagOrientation == null || initState == 0)
            return;
     
        // initialisation of the gyroscope based rotation matrix       
        if(initState == 1) {
            Matrix3x3 initMatrix = new Matrix3x3();
            //initMatrix.copyData( getRotationMatrixFromOrientation(accMagOrientation.getData()) );
            Vector3 accMagVec = new Vector3(accMagOrientation.y(), accMagOrientation.z(), accMagOrientation.x());
            initMatrix = RotationSpace.Eular2Matrix(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix.getData(), test);
            
            gyroMatrix.mulToSelf(initMatrix);
            initState = 2;
        }
     
        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        Quaternion deltaVector = new Quaternion();
        if(timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * CommonDefine.NS2S;
            gyro.copyData(event.values);
            gyro.mul(dT);
            deltaVector = RotationSpace.Rodrigues2Quaternion(gyro);
        }
     
        // measurement done, save current time for next interval
        timestamp = event.timestamp;
     
        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector.data);
        Matrix3x3 deltaMat = new Matrix3x3(deltaMatrix);
        gyroMatrix.mulToSelf(deltaMat);
        // apply the new rotation interval on the gyroscope based rotation matrix
        //gyroMatrix.copyData(matrixMultiplication(gyroMatrix.getData(), deltaMatrix));
     
        // get the gyroscope based orientation from the rotation matrix
        float[] gyroOrientationData = new float[3];
        SensorManager.getOrientation(gyroMatrix.getData(), gyroOrientationData);
        gyroOrientation.copyData(gyroOrientationData);
    }
 
    class calculateFusedOrientationTask extends TimerTask {
    	public void run() {
            float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
            
            /*
             * Fix for 179锟�--> -179锟絫ransition problem:
             * Check whether one of the two orientation angles (gyro or accMag) is negative while the other one is positive.
             * If so, add 360锟�2 * math.PI) to the negative value, perform the sensor fusion, and remove the 360锟絝rom the result
             * if it is greater than 180锟�This stabilizes the output in positive-to-negative-transition cases.
             */
            
            // azimuth
            if (gyroOrientation.x() < -0.5 * Math.PI && accMagOrientation.x() > 0.0) {
            	float val = (float) (FILTER_COEFFICIENT * (gyroOrientation.x() + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation.x());
        		val -= (val > Math.PI) ? 2.0 * Math.PI : 0;
        		fusedOrientation.x(val);
            }
            else if (accMagOrientation.x() < -0.5 * Math.PI && gyroOrientation.x() > 0.0) {
            	float val = (float) (FILTER_COEFFICIENT * gyroOrientation.x() + oneMinusCoeff * (accMagOrientation.x() + 2.0 * Math.PI)); 
            	val -= (val > Math.PI)? 2.0 * Math.PI : 0;
            	fusedOrientation.x(val);
            }
            else {
            	float val = FILTER_COEFFICIENT * gyroOrientation.x() + oneMinusCoeff * accMagOrientation.x();
            	fusedOrientation.x(val);
            }
            
            // pitch
            if (gyroOrientation.y() < -0.5 * Math.PI && accMagOrientation.y() > 0.0) {
            	float val = (float) (FILTER_COEFFICIENT * (gyroOrientation.y() + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation.y());
        		val -= (val > Math.PI) ? 2.0 * Math.PI : 0;
        		fusedOrientation.y(val);
            }
            else if (accMagOrientation.y() < -0.5 * Math.PI && gyroOrientation.y() > 0.0) {
            	float val = (float) (FILTER_COEFFICIENT * gyroOrientation.y() + oneMinusCoeff * (accMagOrientation.y() + 2.0 * Math.PI));
            	val -= (val > Math.PI)? 2.0 * Math.PI : 0;
            	fusedOrientation.y(val);
            }
            else {
            	float val = FILTER_COEFFICIENT * gyroOrientation.y() + oneMinusCoeff * accMagOrientation.y();
            	fusedOrientation.y(val);
            }
            
            // roll
            if (gyroOrientation.z() < -0.5 * Math.PI && accMagOrientation.z() > 0.0) {
            	float val = (float) (FILTER_COEFFICIENT * (gyroOrientation.z() + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation.z());
        		val -= (val > Math.PI) ? 2.0 * Math.PI : 0;
        		fusedOrientation.z(val);
            }
            else if (accMagOrientation.z() < -0.5 * Math.PI && gyroOrientation.z() > 0.0) {
            	float val = (float) (FILTER_COEFFICIENT * gyroOrientation.z() + oneMinusCoeff * (accMagOrientation.z() + 2.0 * Math.PI));
            	val -= (val > Math.PI)? 2.0 * Math.PI : 0;
            	fusedOrientation.z(val);
            }
            else {
            	float val = FILTER_COEFFICIENT * gyroOrientation.z() + oneMinusCoeff * accMagOrientation.z();
            	fusedOrientation.z(val);
            }
     
            // overwrite gyro matrix and orientation with fused orientation
            // to comensate gyro drift
            gyroMatrix = RotationSpace.Eular2Matrix(fusedOrientation);
            gyroOrientation = fusedOrientation;
            // update sensor output in GUI
            mHandler.post(updateOreintationDisplayTask);
        }
    }
    
    
    // **************************** GUI FUNCTIONS *********************************
    
    @Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
		case R.id.radio0:
			radioSelection = 0;
			break;
		case R.id.radio1:
			radioSelection = 1;
			break;
		case R.id.radio2:
			radioSelection = 2;
			break;
		}
	}
    
	public void updateOreintationDisplay() {
		switch(radioSelection) {
		case 0:
			mAzimuthView.setText(d.format(accMagOrientation.x() * 180/Math.PI) + "");
	        mPitchView.setText(d.format(accMagOrientation.y() * 180/Math.PI) + "");
	        mRollView.setText(d.format(accMagOrientation.z() * 180/Math.PI) + "");
			break;
		case 1:
			mAzimuthView.setText(d.format(gyroOrientation.x() * 180/Math.PI) + "");
	        mPitchView.setText(d.format(gyroOrientation.y() * 180/Math.PI) + "");
	        mRollView.setText(d.format(gyroOrientation.z() * 180/Math.PI) + "");
			break;
		case 2:
			mAzimuthView.setText(d.format(fusedOrientation.x() * 180/Math.PI) + "");
	        mPitchView.setText(d.format(fusedOrientation.y() * 180/Math.PI) + "");
	        mRollView.setText(d.format(fusedOrientation.z() * 180/Math.PI) + "");
			break;
		}
	}
	    
	private Runnable updateOreintationDisplayTask = new Runnable() {
		public void run() {
			updateOreintationDisplay();
		}
	};
}

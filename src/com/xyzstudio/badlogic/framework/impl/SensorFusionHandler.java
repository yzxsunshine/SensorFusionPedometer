package com.xyzstudio.badlogic.framework.impl;

import com.xyzstudio.common.CommonDefine;
import com.xyzstudio.math.Matrix3x3;
import com.xyzstudio.math.Quaternion;
import com.xyzstudio.math.RotationSpace;
import com.xyzstudio.math.Vector3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorFusionHandler implements SensorEventListener {
	private Vector3 accel = new Vector3();
	private Vector3 gyro = new Vector3();
	private Vector3 magnet = new Vector3();
	private SensorManager mSensorManager = null;
	
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
    
    private float accelTimeStamp;
    private float timestamp;
	private int initState = 0;
	
    public SensorFusionHandler(Context context)
    {
    	mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		gyroOrientation = Vector3.zeros();
 
        // initialise gyroMatrix with identity matrix
		gyroMatrix = Matrix3x3.eye();
		// get sensorManager and initialise sensor listeners
        initSensorListeners();
    }
    
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	// calculates orientation angles from accelerometer and magnetometer output
	// getOrientation will return a eular angle as z, x, y;
	public void calculateAccMagOrientation() {
	    if(SensorManager.getRotationMatrix(rotationMatrix.getData(), null, accel.getData(), magnet.getData())) {
	        SensorManager.getOrientation(rotationMatrix.getData(), accMagOrientation.getData());
	        if(initState == 0) {
	        	initState = 1;
	        }
	        //Vector3 accMagVec = new Vector3(accMagOrientation.y(), accMagOrientation.z(), accMagOrientation.x());
	        //accMagOrientation = accMagVec;
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
            gyro.mulToSelf(dT);
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
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER: {
				accelTimeStamp = event.timestamp;
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
	
	public void onStop() {
		mSensorManager.unregisterListener(this);
	}
	
	public void onPause() {
		mSensorManager.unregisterListener(this);
	}

	public void onResume() {
		initSensorListeners();
	}

	/*
	 * get and set
	 * */
	public Vector3 getAccel() {
		return accel;
	}
	
	public float getAccelTimeStamp() {
		return accelTimeStamp;
	}
	
	public float getTimeStamp() {
		return timestamp;
	}
	
	public Vector3 getGyro() {
		return gyro;
	}
	
	public Vector3 getMagnet() {
		return magnet;
	}
	
	public Vector3 getAccMagOrientation() {
		return accMagOrientation;
	}
	
	public Vector3 getGyroOrientation() {
		return gyroOrientation;
	}
	
	public void setGyroOrientation(Vector3 fusedOrientation) {
		gyroOrientation = fusedOrientation;
		gyroMatrix = RotationSpace.Eular2Matrix(fusedOrientation);
	}
	
}

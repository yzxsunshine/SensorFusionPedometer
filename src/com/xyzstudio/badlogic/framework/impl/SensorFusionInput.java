package com.xyzstudio.badlogic.framework.impl;

import java.util.List;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

import com.xyzstudio.badlogic.framework.Input;
import com.xyzstudio.badlogic.framework.impl.KeyboardHandler;
import com.xyzstudio.badlogic.framework.impl.MultiTouchHandler;
import com.xyzstudio.badlogic.framework.impl.SingleTouchHandler;
import com.xyzstudio.badlogic.framework.impl.TouchHandler;
import com.xyzstudio.math.Vector3;

public class SensorFusionInput implements Input {
	SensorFusionHandler sensorHandler;
	KeyboardHandler keyHandler;
    TouchHandler touchHandler;
	
	public SensorFusionInput(Context context, View view, float scaleX, float scaleY) {
		sensorHandler = new SensorFusionHandler(context);
        keyHandler = new KeyboardHandler(view);               
        if(Integer.parseInt(VERSION.SDK) < 5) 
            touchHandler = new SingleTouchHandler(view, scaleX, scaleY);
        else
            touchHandler = new MultiTouchHandler(view, scaleX, scaleY);        
    }

	@Override
	public float getAccelX() {
		// TODO Auto-generated method stub
		return sensorHandler.getAccel().x();
	}

	@Override
	public float getAccelY() {
		// TODO Auto-generated method stub
		return sensorHandler.getAccel().y();
	}

	@Override
	public float getAccelZ() {
		// TODO Auto-generated method stub
		return sensorHandler.getAccel().z();
	}
	
	public Vector3 getAccel() {
		return sensorHandler.getAccel();
	}
	
	public float getAccelTimeStamp() {
		return sensorHandler.getAccelTimeStamp();
	}
	
	public float getOrientationTimeStamp() {
		return sensorHandler.getTimeStamp();
	}
	
	public Vector3 getGyro() {
		return sensorHandler.getGyro();
	}
	
	public Vector3 getMagnet() {
		return sensorHandler.getMagnet();
	}
	
	public Vector3 getGyroOrientation() {
		return sensorHandler.getGyroOrientation();
	}

	public Vector3 getAccMagOrientation() {
		return sensorHandler.getAccMagOrientation();
	}
	
	// Important, please remember to update gyro after fused
	public void setGyroOrientation(Vector3 fusedOrientation) {
		sensorHandler.setGyroOrientation(fusedOrientation);
	}
	
	@Override
    public List<TouchEvent> getTouchEvents() {
        return touchHandler.getTouchEvents();
    }
    
    @Override
    public List<KeyEvent> getKeyEvents() {
        return keyHandler.getKeyEvents();
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return keyHandler.isKeyPressed(keyCode);
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }

    @Override
    public int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }

    @Override
    public int getTouchY(int pointer) {
        return touchHandler.getTouchY(pointer);
    }
    
	public void onStop() {
		sensorHandler.onStop();
	}
	
	public void onPause() {
		sensorHandler.onPause();
	}

	public void onResume() {
		sensorHandler.onResume();
	}
}

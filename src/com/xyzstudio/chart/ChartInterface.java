package com.xyzstudio.chart;

import org.achartengine.GraphicalView;

import android.content.Context;

import com.xyzstudio.pedometer.SensorData;

public interface ChartInterface {
	public void updateData(SensorData sd);
	
	public GraphicalView getDemoChartGraphicalView(Context context);
}

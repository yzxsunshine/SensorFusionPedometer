package com.xyzstudio.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.xyzstudio.pedometer.SensorData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.hardware.SensorManager;

public class PeakValleyChart implements ChartInterface {
	private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    //private XYSeries series = new XYSeries("Sensor Data");
    private XYSeries filteredSeries = new XYSeries("Filtered Sensor Data");
    private XYSeries peakSeries = new XYSeries("Is Peak");
    private XYSeries valleySeries = new XYSeries("Is Valley");
    private XYSeries stepSeries = new XYSeries("Is Step");
    private static final int INIT_SIZE = 10;
    public PeakValleyChart(){
        super();
        dataset=new XYMultipleSeriesDataset();
        renderer=new XYMultipleSeriesRenderer();
        
    }

    public GraphicalView getChartGraphicalView(Context context){
        return ChartFactory.getTimeChartView(context, dataset, renderer,null);
    }
    
    public XYMultipleSeriesDataset bulidBasicDataset(){
        return dataset;
    }
    
    public XYMultipleSeriesRenderer buildRenderer(){
        return renderer;
    }

    public void setRandererBasicProperty(
            String title,String xTitle,String yTitle,int axeColor,int labelColor){    
       
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
//        renderer.setRange(new double[]{0,100,0,10});
       
        renderer.setAxesColor(axeColor);
        renderer.setLabelsColor(labelColor);
       
        renderer.setXLabels(10);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.RIGHT);
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setYAxisMin(-1.0);
        renderer.setYAxisMax(1.0);
        
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
       
        renderer.setMargins(new int[]{20, 30, 15, 20});
        renderer.setShowGrid(true);
        renderer.setZoomEnabled(false, false);
    }
    
    public void addNewPair(XYSeries Series,XYSeriesRenderer xyRenderer){
        if(dataset==null||renderer==null){
            return ;
        }else{
            dataset.addSeries(Series);
            renderer.addSeriesRenderer(xyRenderer);
        }
    }
    
    public XYMultipleSeriesDataset getLastestDateset(){
        return dataset;
    }
    
    public XYMultipleSeriesRenderer getLastestRenderer(){
        return renderer;
    }
    
    public GraphicalView getDemoChartGraphicalView(Context context){
        setRandererBasicProperty("SensorChart", "X", "Y",Color.WHITE,Color.GRAY);
        
        // accelerometer data
      /*  XYSeriesRenderer xyRenderer=new XYSeriesRenderer();
        
        xyRenderer.setColor(Color.BLUE);
        xyRenderer.setPointStyle(PointStyle.CIRCLE);
        
        for(int i=0; i<20; i++)
        {
        	series.add(0.03*i, SensorManager.GRAVITY_EARTH);
        }
        
        addNewPair(series, xyRenderer);
        */
        // filtered data
        
        XYSeriesRenderer filteredRenderer=new XYSeriesRenderer();
        
        filteredRenderer.setColor(Color.GREEN);
        filteredRenderer.setPointStyle(PointStyle.CIRCLE);
        
        for(int i=0; i<INIT_SIZE; i++)
        {
        	filteredSeries.add(0.03*i, 0.0);
        }
        
        addNewPair(filteredSeries, filteredRenderer);
        
     // filtered data
        
        XYSeriesRenderer peakRenderer=new XYSeriesRenderer();
        peakRenderer.setColor(Color.RED);
        peakRenderer.setLineWidth(6);
        peakRenderer.setPointStyle(PointStyle.CIRCLE);
        for(int i=0; i<INIT_SIZE; i++)
        {
        	peakSeries.add(0.03*i, 0.0f);
        }
        
        addNewPair(peakSeries, peakRenderer);
        
        XYSeriesRenderer valleyRenderer=new XYSeriesRenderer();
        
        valleyRenderer.setColor(Color.MAGENTA);
        valleyRenderer.setLineWidth(6);
        valleyRenderer.setPointStyle(PointStyle.CIRCLE);
        for(int i=0; i<INIT_SIZE; i++)
        {
        	valleySeries.add(0.03*i, 0.0f);
        }
        
        addNewPair(valleySeries, valleyRenderer);
        
        XYSeriesRenderer stepRenderer=new XYSeriesRenderer();
        
		stepRenderer.setColor(Color.BLACK);
        stepRenderer.setLineWidth(6);
        stepRenderer.setPointStyle(PointStyle.CIRCLE);
        for(int i=0; i<INIT_SIZE; i++)
        {
        	stepSeries.add(0.03*i, 0.0f);
        }
        
        addNewPair(stepSeries, stepRenderer);
        return getChartGraphicalView(context);
    }

    public void updateData(double timestamp,double rate){
        //series.remove(0);
        //series.add(timestamp, rate);
    }

    public void updateData(SensorData sd){
        //series.remove(0);
        //series.add(sd.GetTimeStamp(), sd.GetAcceMagnitude());
    	if(sd == null)
    		return;
    	if(filteredSeries.getItemCount() > 0)
    	{
    		filteredSeries.remove(0);
        	filteredSeries.add(sd.GetTimeStamp(), sd.GetAcceMagnitude());
    	}
    	
    	if(peakSeries.getItemCount() > 0)
    	{
    		peakSeries.remove(0);
    		double val = 0.8;
    		if(!sd.IsPeak())
    			val = 0.0;
    		peakSeries.add(sd.GetTimeStamp(), val);
    	}
    	
    	if(valleySeries.getItemCount() > 0){
    		valleySeries.remove(0);
    		double val = 0.7;
    		if(!sd.IsValley())
    			val = 0.0;
        	valleySeries.add(sd.GetTimeStamp(), val);
    	}
    	
    	if(stepSeries.getItemCount() > 0){
    		stepSeries.remove(0);
    		double val = 0.6;
    		if(!sd.IsStep())
    			val = 0.0;
        	stepSeries.add(sd.GetTimeStamp(), val);
    	}
        //stepSeries.remove(0);
        //stepSeries.add(sd.GetTimeStamp(), sd.IsStep()? 12 : 0);
    }
}

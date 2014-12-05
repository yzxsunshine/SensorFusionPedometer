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

public class SensorChart implements ChartInterface {
	private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    //private XYSeries series = new XYSeries("Sensor Data");
    private XYSeries xSeries = new XYSeries("x");
    //private XYSeries peakSeries = new XYSeries("Is Peak");
    //private XYSeries valleySeries = new XYSeries("Is Valley");
    //private XYSeries stepSeries = new XYSeries("Is Step");
    private XYSeries ySeries = new XYSeries("y");
    private XYSeries zSeries = new XYSeries("z");
    private static final int INIT_SIZE = 10;
    public SensorChart(){
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
       
        renderer.setXLabels(INIT_SIZE);
        renderer.setYLabels(INIT_SIZE);
        renderer.setXLabelsAlign(Align.RIGHT);
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setYAxisMin(-2.0);
        renderer.setYAxisMax(2.0);
        
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
        
        XYSeriesRenderer xRenderer=new XYSeriesRenderer();
        
        xRenderer.setColor(Color.GREEN);
        xRenderer.setLineWidth(6);
        xRenderer.setPointStyle(PointStyle.CIRCLE);
        
        for(int i=0; i<INIT_SIZE; i++)
        {
        	xSeries.add(0.03*i, SensorManager.GRAVITY_EARTH);
        }
        
        addNewPair(xSeries, xRenderer);
        
        XYSeriesRenderer yRenderer=new XYSeriesRenderer();
        
        yRenderer.setColor(Color.CYAN);
        yRenderer.setPointStyle(PointStyle.CIRCLE);
        yRenderer.setLineWidth(6);
        
        for(int i=0; i<INIT_SIZE; i++)
        {
        	ySeries.add(0.03*i, 8);
        }
        
        addNewPair(ySeries, yRenderer);
        
        XYSeriesRenderer zRenderer=new XYSeriesRenderer();
        
        zRenderer.setColor(Color.RED);
        zRenderer.setPointStyle(PointStyle.CIRCLE);
        zRenderer.setLineWidth(6);
        for(int i=0; i<INIT_SIZE; i++)
        {
        	zSeries.add(0.03*i, 8);
        }
        
        addNewPair(zSeries, zRenderer);
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
    	if(xSeries.getItemCount() > 0)
    	{
    		xSeries.remove(0);
        	xSeries.add(sd.GetTimeStamp(), sd.GetAcceOrient().x());
    	}
    	
    	if(ySeries.getItemCount() > 0)
    	{
    		ySeries.remove(0);
        	ySeries.add(sd.GetTimeStamp(), sd.GetAcceOrient().y());
    	}
    	if(zSeries.getItemCount() > 0)
    	{
    		zSeries.remove(0);
        	zSeries.add(sd.GetTimeStamp(), sd.GetAcceOrient().z());
    	}//peakSeries.remove(0);
        //peakSeries.add(sd.GetTimeStamp(), sd.IsPeak()? 12.0f : 0.0f);
        
        //valleySeries.remove(0);
        //valleySeries.add(sd.GetTimeStamp(), sd.IsValley()? 12 : 0);
        
        //stepSeries.remove(0);
        //stepSeries.add(sd.GetTimeStamp(), sd.IsStep()? 12 : 0);
    }
}

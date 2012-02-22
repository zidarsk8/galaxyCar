package org.psywerx.car;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

public class Graph implements DataListener {

	private XYMultipleSeriesDataset datasetAll;
	private XYSeries mGx;
	private XYSeries mGy;
	private XYSeries mGz;
	private XYSeries mRevs;
	private XYSeries mTurn;

	private final int MAX_POINTS = 100;
	private XYMultipleSeriesRenderer renderer;
	private int ticks = 0;
	private XYMultipleSeriesDataset datasetTurn;
	private XYMultipleSeriesDataset datasetRevs;
	private XYMultipleSeriesDataset datasetG;
	private XYMultipleSeriesRenderer rendererTurn;
	private XYMultipleSeriesRenderer rendererRevs;
	private XYMultipleSeriesRenderer rendererG;

	public Graph(){
		mTurn = new XYSeries("Turn");
		mRevs = new XYSeries("Revs");
		mGx = new XYSeries("Gx");
		mGy = new XYSeries("Gy");
		mGz = new XYSeries("Gz");
	}
	
	public XYMultipleSeriesRenderer getRendererAll() {
		renderer = new XYMultipleSeriesRenderer();
		renderer.setInScroll(false);
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		
		XYSeriesRenderer r2 = new XYSeriesRenderer();
		r2.setColor(Color.RED);
		r2.setFillPoints(true);
		renderer.addSeriesRenderer(r2);
		
		XYSeriesRenderer r3 = new XYSeriesRenderer();
		r3.setColor(Color.YELLOW);
		r3.setFillPoints(true);
		renderer.addSeriesRenderer(r3);
		
		XYSeriesRenderer r4 = new XYSeriesRenderer();
		r4.setColor(Color.GREEN);
		r4.setFillPoints(true);
		renderer.addSeriesRenderer(r4);
		
		XYSeriesRenderer r5 = new XYSeriesRenderer();
		r5.setColor(Color.MAGENTA);
		r5.setFillPoints(true);
		renderer.addSeriesRenderer(r5);
		
		
		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.GREEN);
		r.setFillPoints(true);
		return renderer;
	}
	public XYMultipleSeriesRenderer getRendererG() {
		rendererG = new XYMultipleSeriesRenderer();
		rendererG.setInScroll(false);
		rendererG.setAxisTitleTextSize(16);
		rendererG.setChartTitleTextSize(20);
		rendererG.setLabelsTextSize(15);
		rendererG.setLegendTextSize(15);
		rendererG.setPointSize(5f);
		rendererG.setMargins(new int[] { 20, 30, 15, 0 });
		
		XYSeriesRenderer r3 = new XYSeriesRenderer();
		r3.setColor(Color.YELLOW);
		r3.setFillPoints(true);
		rendererG.addSeriesRenderer(r3);
		
		XYSeriesRenderer r4 = new XYSeriesRenderer();
		r4.setColor(Color.GREEN);
		r4.setFillPoints(true);
		rendererG.addSeriesRenderer(r4);
		
		XYSeriesRenderer r5 = new XYSeriesRenderer();
		r5.setColor(Color.MAGENTA);
		r5.setFillPoints(true);
		rendererG.addSeriesRenderer(r5);

		return rendererG;
	}
	public XYMultipleSeriesRenderer getRendererTurn() {
		rendererTurn = new XYMultipleSeriesRenderer();
		rendererTurn.setInScroll(false);
		rendererTurn.setAxisTitleTextSize(16);
		rendererTurn.setChartTitleTextSize(20);
		rendererTurn.setLabelsTextSize(15);
		rendererTurn.setLegendTextSize(15);
		rendererTurn.setPointSize(5f);
		rendererTurn.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.BLUE);
		r.setFillPoints(true);
		rendererTurn.addSeriesRenderer(r);
		
		return rendererTurn;
	}
	public XYMultipleSeriesRenderer getRendererRevs() {
		rendererRevs = new XYMultipleSeriesRenderer();
		rendererRevs.setInScroll(false);
		rendererRevs.setAxisTitleTextSize(16);
		rendererRevs.setChartTitleTextSize(20);
		rendererRevs.setLabelsTextSize(15);
		rendererRevs.setLegendTextSize(15);
		rendererRevs.setPointSize(5f);
		rendererRevs.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.RED);
		r.setFillPoints(true);
		rendererRevs.addSeriesRenderer(r);
		
		return rendererRevs;
	}

	public XYMultipleSeriesDataset getDatasetAll() {
		datasetAll = new XYMultipleSeriesDataset();
		datasetAll.addSeries(mTurn);
		datasetAll.addSeries(mRevs);
		datasetAll.addSeries(mGx);
		datasetAll.addSeries(mGy);
		datasetAll.addSeries(mGz);
		return datasetAll;
	}
	public XYMultipleSeriesDataset getDatasetTurn() {
		datasetTurn = new XYMultipleSeriesDataset();
		datasetTurn.addSeries(mTurn);
		return datasetTurn;
	}
	public XYMultipleSeriesDataset getDatasetRevs() {
		datasetRevs = new XYMultipleSeriesDataset();
		datasetRevs.addSeries(mRevs);
		return datasetRevs;
	}
	public XYMultipleSeriesDataset getDatasetG() {
		datasetG = new XYMultipleSeriesDataset();
		datasetG.addSeries(mGx);
		datasetG.addSeries(mGy);
		datasetG.addSeries(mGz);
		return datasetG;
	}
	
	public synchronized void updateData(float[] data) {
		
		// warning ugly looking code ahead:
		if (mTurn.getItemCount() > MAX_POINTS)
			mTurn.remove(0);
		if (mGx.getItemCount() > MAX_POINTS)
			mGx.remove(0);
		if (mGy.getItemCount() > MAX_POINTS)
			mGy.remove(0);
		if (mGz.getItemCount() > MAX_POINTS)
			mGz.remove(0);
		if (mRevs.getItemCount() > MAX_POINTS)
			mRevs.remove(0);

		mTurn.add(ticks, data[4]);
		mGx.add(ticks, data[0]);
		mGy.add(ticks, data[1]);
		mGz.add(ticks, data[2]);
		mRevs.add(ticks, data[3]);
		
		renderer.setXAxisMax(ticks);
		renderer.setXAxisMin(ticks - MAX_POINTS);
		
		rendererG.setXAxisMax(ticks);
		rendererG.setXAxisMin(ticks - MAX_POINTS);
		
		rendererTurn.setXAxisMax(ticks);
		rendererTurn.setXAxisMin(ticks - MAX_POINTS);
		
		rendererRevs.setXAxisMax(ticks);
		rendererRevs.setXAxisMin(ticks - MAX_POINTS);
		ticks++;
	}

	public void setAlpha(float alpha) {

	}

}

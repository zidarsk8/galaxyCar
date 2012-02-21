package org.psywerx.car;

import java.util.Random;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

public class Graph implements DataListener {

	private XYMultipleSeriesDataset dataset;
	private XYSeries mGx;
	private XYSeries mGy;
	private XYSeries mGz;
	private XYSeries mRevs;
	private XYSeries mTurn;
	private GraphicalView mChartView;
	private Thread randomDataGenerator;
	private Thread repaintGraph;

	private final int MAX_POINTS = 100;
	private XYMultipleSeriesRenderer renderer;
	private int ticks = 0;

	public XYMultipleSeriesRenderer getDemoRenderer() {
		renderer = new XYMultipleSeriesRenderer();
		renderer.setInScroll(false);
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		// r.setColor(Color.BLUE);
		// r.setPointStyle(PointStyle.SQUARE);
		// r.setFillBelowLine(true);
		// r.setFillBelowLineColor(Color.WHITE);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		
		XYSeriesRenderer r2 = new XYSeriesRenderer();
		r2.setColor(Color.RED);
		// r.setPointStyle(PointStyle.SQUARE);
		// r.setFillBelowLine(true);
		// r.setFillBelowLineColor(Color.WHITE);
		r2.setFillPoints(true);
		renderer.addSeriesRenderer(r2);
		
		XYSeriesRenderer r3 = new XYSeriesRenderer();
		r3.setColor(Color.YELLOW);
		// r.setPointStyle(PointStyle.SQUARE);
		// r.setFillBelowLine(true);
		// r.setFillBelowLineColor(Color.WHITE);
		r3.setFillPoints(true);
		renderer.addSeriesRenderer(r3);
		
		XYSeriesRenderer r4 = new XYSeriesRenderer();
		r4.setColor(Color.GREEN);
		// r.setPointStyle(PointStyle.SQUARE);
		// r.setFillBelowLine(true);
		// r.setFillBelowLineColor(Color.WHITE);
		r4.setFillPoints(true);
		renderer.addSeriesRenderer(r4);
		
		XYSeriesRenderer r5 = new XYSeriesRenderer();
		r5.setColor(Color.MAGENTA);
		// r.setPointStyle(PointStyle.SQUARE);
		// r.setFillBelowLine(true);
		// r.setFillBelowLineColor(Color.WHITE);
		r5.setFillPoints(true);
		renderer.addSeriesRenderer(r5);
		
		
		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.GREEN);
		r.setFillPoints(true);
		return renderer;
	}

	private static final int SERIES_NR = 1;

	public XYMultipleSeriesDataset getDemoDataset() {
		dataset = new XYMultipleSeriesDataset();
		mTurn = new XYSeries("Turn");
		mRevs = new XYSeries("Revs");
		mGx = new XYSeries("Gx");
		mGy = new XYSeries("Gy");
		mGz = new XYSeries("Gz");
		dataset.addSeries(mTurn);
		dataset.addSeries(mRevs);
		dataset.addSeries(mGx);
		dataset.addSeries(mGy);
		dataset.addSeries(mGz);
		return dataset;
	}

	public void start(GraphicalView m) {

		mChartView = m;

		randomDataGenerator = new Thread() {
			private Random random = new Random();

			public void run() {
				while (true) {
					try {
						Thread.sleep(50L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateData(new float[]{random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()});
				}
			}
		};
		//randomDataGenerator.start();
		repaintGraph = new Thread() {
			public static final long THREAD_REFRESH_PERIOD = 50;
			private Random random = new Random();

			public void run() {
				while (true) {
					try {
						Thread.sleep(THREAD_REFRESH_PERIOD);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					((GraphicalView) mChartView).repaint();
				}
			}
		};
		repaintGraph.start();
	}
	public synchronized void updateGraph() {
		
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
		ticks++;
	}

	public void setAlpha(float alpha) {

	}

}

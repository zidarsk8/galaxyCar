package org.psywerx.car;

import java.util.Random;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

public class Graph {

	private XYMultipleSeriesDataset dataset;
	private XYSeries series;
	private GraphicalView mChartView;
	private Thread mThread;

	private final int MAX_POINTS = 100;
	private XYMultipleSeriesRenderer renderer;

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
		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.GREEN);
		r.setFillPoints(true);
		return renderer;
	}

	private static final int SERIES_NR = 1;

	public XYMultipleSeriesDataset getDemoDataset() {
		dataset = new XYMultipleSeriesDataset();
		series = new XYSeries("Graf 1");
		dataset.addSeries(series);
		return dataset;
	}

	public void start(GraphicalView m) {

		mChartView = m;

		mThread = new Thread() {
			private Random random = new Random();

			public void run() {
				while (true) {
					try {
						Thread.sleep(500L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					series.add(series.getItemCount(),
							20 + random.nextInt() % 100);
					renderer.setXAxisMax(series.getMaxX());
					renderer.setXAxisMin(series.getItemCount() - MAX_POINTS);
					((GraphicalView) mChartView).repaint();
				}
			}
		};
		mThread.start();
	}

}

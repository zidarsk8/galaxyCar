package org.psywerx.car;

import java.util.Arrays;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

public class Graph implements DataListener {

	/**
	 * The maximum number of points drawn on graphs
	 */
	private final int MAX_POINTS = 100;

	/**
	 * Fields that contain graph data
	 */
	private XYSeries mGx;
	private XYSeries mGy;
	private XYSeries mGz;
	private XYSeries mRevs;
	private XYSeries mTurn;

	/**
	 *  Graph styles and renderers
	 */
	private XYMultipleSeriesDataset datasetAll;
	private XYMultipleSeriesDataset datasetTurn;
	private XYMultipleSeriesDataset datasetRevs;
	private XYMultipleSeriesDataset datasetG;
	private XYMultipleSeriesRenderer renderer;
	private XYMultipleSeriesRenderer rendererTurn;
	private XYMultipleSeriesRenderer rendererRevs;
	private XYMultipleSeriesRenderer rendererG;

	private int ticks = 0;

	public Graph() {

		// Init the series
		mTurn = new XYSeries("Turn");
		mRevs = new XYSeries("Revs");
		mGx = new XYSeries("Gx");
		mGy = new XYSeries("Gy");
		mGz = new XYSeries("Gz");

	}

	public XYMultipleSeriesRenderer getRendererAll() {
		// Init the renderer
		renderer = new XYMultipleSeriesRenderer();
		renderer.setInScroll(false);
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });

		XYSeriesRenderer r1 = new XYSeriesRenderer();
		r1.setFillPoints(true);
		renderer.addSeriesRenderer(r1);

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

		// Init the renderer
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
		try {

			// We need to remove data points or else the rendering becomes slow
			while(mTurn.getItemCount() >= MAX_POINTS)
				mTurn.remove(0);
			while(mGx.getItemCount() >= MAX_POINTS)
				mGx.remove(0);
			while(mGy.getItemCount() >= MAX_POINTS)
				mGy.remove(0);
			while(mGz.getItemCount() >= MAX_POINTS)
				mGz.remove(0);
			while(mRevs.getItemCount() >= MAX_POINTS)
				mRevs.remove(0);

//			mGx.add(ticks, data[0]);
//			mGy.add(ticks, data[1]);
//			mGz.add(ticks, data[2]);
			mRevs.add(ticks, data[3]);
			mTurn.add(ticks, data[4]);
//

//			// Set the X axis to always point to the beginning and end of the graph
//			renderer.setXAxisMax(ticks);
//			renderer.setXAxisMin(ticks - MAX_POINTS);
//
//			rendererG.setXAxisMax(ticks);
//			rendererG.setXAxisMin(ticks - MAX_POINTS);
//
//			rendererTurn.setXAxisMax(ticks);
//			rendererTurn.setXAxisMin(ticks - MAX_POINTS);
//
//			rendererRevs.setXAxisMax(ticks);
//			rendererRevs.setXAxisMin(ticks - MAX_POINTS);

			ticks++;
		} catch (Exception e) {
			D.dbge(e.toString(), e);
		}
	}

	public void insertWholeHistory(float[][] history){
		XYSeries[] sets = {mGx, mGy, mGz, mRevs, mTurn};

		for (int i = 0; i < sets.length; i++) {
			sets[i].clear();
		}

		XYMultipleSeriesRenderer[] renderers = {rendererG, rendererRevs, rendererTurn};

		for(int i=0; i < history.length; i++){
			for(int j=0; j < history[i].length; j++){
				sets[j].add(i, history[i][j]);
			}
		}
		for (int i=0; i < renderers.length && history.length > 0; i++){
			renderers[i].setXAxisMin(history.length-250);
			renderers[i].setXAxisMax(Math.max(history.length, 250));
		}
	}
}

package ydp.weightrecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;


public class ShowChart {
	private DatabaseHelper db = null;
	private Cursor myCursor = null;
	private String StartDate = null;
	private String EndDate = null;
	private float maxWeight = 0f;
	private float minWeight = 0f;
	private Context mContext = null;
	private Intent mIntent = null;
	private static int onedayTime = 86400000;
	/** Called when the activity is first created. */
	private ArrayList<Map<String, String>> maps = new ArrayList<Map<String, String>>();

	public ShowChart(Context context) {
		mContext = context;
		db = new DatabaseHelper(context);
		myCursor = db.select();
		myCursor.moveToFirst();
		EndDate = myCursor.getString(myCursor.getColumnIndex("date"));
		myCursor.moveToPosition(myCursor.getCount() - 1);
		StartDate = myCursor.getString(myCursor.getColumnIndex("date"));
		maxWeight = db.getMaxWeight();
		minWeight = db.getMinWeight();			

		showLinearChar();	
	}

	private void showLinearChar() {
		String[] titles = new String[] { "体重的变化" };

		int[] colors = new int[] { Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setChartSettings(renderer, "体重走势图", "时间", "体重(kg)",
				new Date(StartDate).getTime() - onedayTime,
				new Date(EndDate).getTime() + onedayTime, minWeight - 10,
				maxWeight + 10, Color.GRAY, Color.LTGRAY);
		renderer.setXLabels(5);
		renderer.setYLabels(10);
		SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(0);
		seriesRenderer.setDisplayChartValues(true);
		mIntent = ChartFactory.getTimeChartIntent(mContext,
				buildDateDataset(titles), renderer, "yyyy/MM/dd");
	}
	public Intent getIntent(){
		return mIntent;
	}
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {
		renderer.setAxisTitleTextSize(22);
		renderer.setChartTitleTextSize(30);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(20);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 30, 40, 30, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, float xMin, float xMax,
			float yMin, float yMax, int axesColor, int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	protected XYMultipleSeriesDataset buildDateDataset(String[] titles) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		TimeSeries series = new TimeSeries(titles[0]);
		Date tempDate;
		double tempD;
		myCursor.moveToPosition(-1);
		while (myCursor.moveToNext()) {
			tempDate = new Date(myCursor.getString(myCursor
					.getColumnIndex("date")));
			tempD = Double.parseDouble(myCursor.getString(myCursor
					.getColumnIndex("weight")));

			series.add(tempDate, tempD);
		}
		dataset.addSeries(series);
		return dataset;
	}
}

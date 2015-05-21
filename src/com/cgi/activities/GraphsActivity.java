package com.cgi.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cgi.gaswarnings.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import data.Entry;
import data.EntryDAO;

public class GraphsActivity extends Activity {

	private GraphView graph;
	private TextView title, secondYAxisTitle;
	private Button update_button;
	private Typeface face;
	
	private Viewport viewport;
    private GridLabelRenderer grid;
    private LegendRenderer legend;
	
	Handler handler = new Handler();
	Runnable runnable;
	
	boolean update_state = false;
	
	EntryDAO dao;
	Vector<Entry> entries = new Vector<Entry>();
	LineGraphSeries<DataPoint> series1, series2;
	int values_count;
	DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		
		/* fetching elements */
		graph = (GraphView) findViewById(R.id.activity_graphs_graph);
		title = (TextView) findViewById(R.id.activity_graphs_title);
		update_button = (Button) findViewById(R.id.activity_graph_button_update);
		secondYAxisTitle = (TextView) findViewById(R.id.activity_graphs_secondYaxis_title);
		 
		/* setting typeface */
		face = Typeface.createFromAsset(getAssets(), "fonts/LinLibertine_R.ttf");
		title.setTypeface(face, Typeface.BOLD);
		update_button.setTypeface(face);
		
		face = Typeface.createFromAsset(getAssets(), "fonts/Calibri.ttf");
		secondYAxisTitle.setTypeface(face);
		secondYAxisTitle.setTextColor(Color.BLUE);
		
		/* update button listener */
		update_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(update_state){
					update_button.setText(R.string.activity_graphs_update_OFF);
					update_button.setBackgroundResource(R.drawable.red_button);
					update_state = false;
					stopUpdate();
				}
				else{
					update_button.setText(R.string.activity_graphs_update_ON);
					update_button.setBackgroundResource(R.drawable.green_button);
					update_state = true;
					startUpdate();
				}
			}
		});
		
		/* drawing graph */
		drawGraph();
        
        /* start updating service if the warnings service is on */
        Intent intent = this.getIntent();
        boolean service_state = intent.getBooleanExtra("service_state", false);
        
	}
	
	public void drawGraph(){
		
		/* fecthing data */
		dao = new EntryDAO(this);
		refreshData();
		
		/* SERIES */
		
		refreshUI(false);
		
		series1.setDrawDataPoints(true);
		series2.setDrawDataPoints(true);
		series1.setColor(Color.RED);
		series2.setColor(Color.BLUE);
		series1.setThickness(3);
		series2.setThickness(3);
		series1.setDataPointsRadius(5);
		series2.setDataPointsRadius(5);
		series1.setTitle("temperature");
		series2.setTitle("gas");
		
		OnDataPointTapListener pointTapListener = new OnDataPointTapListener() {
			@Override
			public void onTap(Series arg0, DataPointInterface point) {
				int indice = (int) point.getX();
				Date date = entries.get(indice).getDate();
				Toast.makeText(GraphsActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
			}
		};
		
		series1.setOnDataPointTapListener(pointTapListener);
		series2.setOnDataPointTapListener(pointTapListener);
		
		viewport = graph.getViewport();
        grid = graph.getGridLabelRenderer();
        legend = graph.getLegendRenderer();
		
        /* GRID */
        grid.setPadding(25);
        grid.setLabelsSpace(5);
        grid.setHorizontalAxisTitle("Time");
        grid.setVerticalAxisTitle("Temperature");
        grid.setHorizontalAxisTitleTextSize(25);
        grid.setVerticalAxisTitleTextSize(25);
        grid.setHorizontalAxisTitleColor(Color.BLACK);
        grid.setVerticalAxisTitleColor(Color.RED);
        grid.setNumHorizontalLabels(2);
        grid.setNumVerticalLabels(5);
        grid.setVerticalLabelsColor(Color.RED);
    	grid.setVerticalLabelsSecondScaleColor(Color.BLUE);
        
        /* VIEWPORT */
        viewport.setScrollable(true);
        viewport.setScalable(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        if(values_count == 0){
        	viewport.setMaxX(5);
        }
        else{
        	viewport.setMaxX(values_count);
        }
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(10);
        viewport.setMaxY(40);
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(100);
        
        /* LEGEND */
        
        legend.setVisible(true);
        legend.setAlign(LegendRenderer.LegendAlign.TOP);
        legend.setTextSize(15);
        legend.setMargin(15);
        legend.setSpacing(10);
        legend.setPadding(15);
        legend.setBackgroundColor(Color.WHITE);
        
        graph.addSeries(series1);
        graph.getSecondScale().addSeries(series2);
        
	}
	
	public void refreshData(){
		entries.clear();
		dao.open();
		entries = dao.selectAll();
		dao.close();
	}
	
	public void refreshUI(boolean refresh){
		if(entries != null){
			values_count = entries.size();
		}
		else{
			values_count = 0;
		}
		DataPoint [] data1 = new DataPoint[values_count];
        DataPoint [] data2 = new DataPoint[values_count];
		int i = 0;
        if(entries != null){
	        for(Entry entry : entries){
	            data1[i] = new DataPoint(i,entry.getTemperature());
	            data2[i] = new DataPoint(i,entry.getGas());
	            i++;
	        }
        }
		
		if(refresh){
			series1.resetData(data1);
			series2.resetData(data2);
			if(values_count == 0){
	        	viewport.setMaxX(5);
	        }
	        else{
	        	viewport.setMaxX(values_count);
	        }
		}
		else{
			series1 = new LineGraphSeries<DataPoint>(data1);			
			series2 = new LineGraphSeries<DataPoint>(data2);			
		}
		
	}

	public void startUpdate(){
		
		refreshUI(true);
		
		runnable = new Runnable() {
			@Override
			public void run() {
				
				Vector<Entry> old_entries = new Vector<Entry>();
				for(Entry e : entries){
					old_entries.add(e);
				}
				refreshData();
				Vector<Entry> diff = new Vector<Entry>();
				for(Entry e : entries){
					diff.add(e);
				}
				diff.removeAll(old_entries);
				for(Entry e : diff){
					series1.appendData(new DataPoint(values_count, e.getTemperature()), false, 500);
					series2.appendData(new DataPoint(values_count, e.getGas()), true, 500);
					values_count++;
					viewport.setMaxX(values_count);
				}
				handler.postDelayed(this, 3000);
			}
		};
		handler.postDelayed(runnable, 1000);
	}
	
	public void stopUpdate(){
		handler.removeCallbacks(runnable);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.graphs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

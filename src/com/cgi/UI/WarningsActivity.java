/**
 * Activity : Warnings history
 * Displays the warnings list, a clear button, and refreshes every 'refreshInterval' ms
 */

package com.cgi.UI;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cgi.gaswarnings.R;

import data.Warning;
import data.WarningsDAO;

public class WarningsActivity extends Activity {

	/* Views */
	private ListView listView;
	private TextView title_tv;
	private Button button_clear;
	private List<Warning> list;
	private Typeface face;

	/* Data */
	private WarningsDAO warningsDAO = new WarningsDAO(this);
	private WarningsAdapter adapter;
	
	/* Threads */
	private int refreshInterval = 2000;
	private Handler handler = new Handler();
	private Runnable periodicRequests = new Runnable() {
		@Override
		public void run() {
			refreshData();
			handler.postDelayed(periodicRequests, refreshInterval);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warnings);	
		
		/* fetching views */
		listView = (ListView) findViewById(R.id.activity_warnings_listview);
		title_tv = (TextView) findViewById(R.id.activity_warnings_title);
		button_clear = (Button) findViewById(R.id.activity_warnings_button_clear);

		/* fetting typeface */
		face = Typeface.createFromAsset(getAssets(), "fonts/LinLibertine_R.ttf");
		title_tv.setTypeface(face);
		button_clear.setTypeface(face);
		
		/* button listener */
		button_clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				warningsDAO.drop();
				warningsDAO.create();
				refreshData();
			}
		});
	}
	
	/* Refreshes the Warnings list */
	public void refreshData(){
		Vector<Warning> vect = warningsDAO.selectAll();
		list = vect.subList(0, vect.size());
		adapter = new WarningsAdapter(this, list);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.warnings, menu);
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
	
	/* Stops the refreshing service */
	@Override
	public void onPause(){
		warningsDAO.close();
		handler.removeCallbacks(periodicRequests);
		super.onPause();
	}
	
	/* Resumes the refreshing service */
	@Override
	public void onResume(){
		warningsDAO.open();
		refreshData();
		handler.post(periodicRequests);
		super.onResume();
	}
	
}

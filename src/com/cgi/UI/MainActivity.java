package com.cgi.UI;

import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cgi.gaswarnings.R;
import com.cgi.services.WarningsService;

import data.Entry;
import data.EntryDAO;
import data.Warning;
import data.WarningsDAO;

public class MainActivity extends Activity {

	/* Views */
	private TextView tv_welcome;
	private Button button_service, button_list, button_graphs;
	private Typeface face;
	private boolean service_state;

	/* Data */
	private EntryDAO entryDAO = new EntryDAO(this);
	private WarningsDAO warningsDAO = new WarningsDAO(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* fetching elements */
		tv_welcome = (TextView) findViewById(R.id.activity_main_tv_welcome);
		button_service = (Button) findViewById(R.id.activity_main_button_service);
		button_list = (Button) findViewById(R.id.activity_main_button_list);
		button_graphs = (Button) findViewById(R.id.activity_main_button_data);

		/* setting typeface */
		face = Typeface.createFromAsset(getAssets(), "fonts/LinLibertine_R.ttf");
		tv_welcome.setTypeface(face, Typeface.BOLD);
		button_service.setTypeface(face);
		button_list.setTypeface(face);
		button_graphs.setTypeface(face);
		
		/* setting service button depending on service state */
		service_state = ServiceRunning(WarningsService.class);
		if(service_state){
			button_service.setText(R.string.activity_main_service_turnOff);
			button_service.setBackgroundResource(R.drawable.red_button);
		}
		else{
			button_service.setText(R.string.activity_main_service_turnOn);
			button_service.setBackgroundResource(R.drawable.green_button);
		}
		
		/* setting button listeners */
		button_service.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent warnings_service = new Intent(MainActivity.this, WarningsService.class);
				// if service is on we stop it
				if(service_state){
					button_service.setText(R.string.activity_main_service_turnOn);
					button_service.setBackgroundResource(R.drawable.green_button);
					service_state = false;
					stopService(warnings_service);
				}
				// else we start it
				else{
					button_service.setText(R.string.activity_main_service_turnOff);
					button_service.setBackgroundResource(R.drawable.red_button);
					service_state = true;
					startService(warnings_service);
				}
				
			}
		});
		
		/* link to warnings history activity */
		button_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startWarningsActivity = new Intent(MainActivity.this, WarningsActivity.class);
				startActivity(startWarningsActivity);
			}
		});
			
		/* link to graphs activity */
		button_graphs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startGraphsActivity = new Intent(MainActivity.this, GraphsActivity.class);
				startGraphsActivity.putExtra("service_state", service_state);
				startActivity(startGraphsActivity);
			}
		});
		
	}
	
	/* @return true if warnings service is running */
	private boolean ServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		case R.id.activity_main_menu_random:
			Entry e = Entry.randomEntry();
			entryDAO.add(e);
			break;
		case R.id.activity_main_menu_create:
			entryDAO.create();
			warningsDAO.create();
			break;
		case R.id.activity_main_menu_drop:
			entryDAO.drop();
			warningsDAO.drop();
			break;
		case R.id.activity_main_menu_randomWarning:
			warningsDAO.add(new Warning(new Date(), null));
			break;
		default:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume(){
		entryDAO.open();
		warningsDAO.open();
		super.onResume();
	}
	
	@Override
	public void onPause(){
		entryDAO.close();
		warningsDAO.close();
		super.onPause();
	}
	
}

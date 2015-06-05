/**
 * Service : data requesting
 * Periodically sends http requests to the Arduino server and stores them in the local database
 */

package com.cgi.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cgi.UI.UIHelper;

import data.Entry;
import data.EntryDAO;
import data.Warning;
import data.WarningsDAO;

public class WarningsService extends Service {
	
	/* Net */
	String ip_arduino = "http://172.20.10.5";
	Object lock = new Object();
	
	/* Binder */
	private final IBinder binder = new LocalBinder();
	
	/* Data */
	private Boolean DAO_closed;
	private EntryDAO entryDAO = new EntryDAO(this);
	private WarningsDAO warningsDAO = new WarningsDAO(this);
	
	/* Threads */
	private int requestsInterval = 1000;
	private Handler handler = new Handler();
	
	/* Periodic http requests */
	private Runnable periodicRequests = new Runnable() {
		@Override
		public void run() {
			Thread thread = new Thread(new Runnable(){
			    @Override
			    public void run() {
			        HttpURLConnection httpconn = null;
			    	BufferedReader br = null;
			    	try {
				    	URL url = new URL(ip_arduino);
				    	URLConnection conn = url.openConnection();
				    	httpconn = (HttpURLConnection) conn;
//				    	httpconn.setConnectTimeout(3000);
//				    	httpconn.setReadTimeout(3000);
				    	httpconn.setRequestProperty("connection","close");
				    	synchronized(lock){
				    		if(!DAO_closed){
				    			if(httpconn.getResponseCode() == HttpURLConnection.HTTP_OK){
				    				// reading stream
				    				InputStream stream = httpconn.getInputStream();
				    				br = new BufferedReader(new InputStreamReader(stream));
				    				String line;
				    				String response = "";
				    				while((line = br.readLine()) != null){
				    					response += line+'\n';
				    				}
				    				// storing data
				    				parseAndStore(response);
				    			}
				    		}
				    	}
			    	}
			    	catch(java.net.ConnectException ce){
			    		Log.e("SERVICE", "connection failed");
			    		ce.printStackTrace();
			    	}
			    	catch(java.net.SocketException se){
			    		Log.e("SERVICE","connect timeout");
			    	}
			    	catch (Exception e) {
			    		e.printStackTrace();
			    	}
			    	finally{
			    		try{br.close();}catch(Exception e){}
			    		try{httpconn.disconnect();}catch(Exception e){}
			    	}
			    }
			});
			thread.start();
			handler.postDelayed(periodicRequests, requestsInterval);
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return Service.START_NOT_STICKY;
	}
	
	/* Starting http requests on create, opening DAOs */
	@Override
	public void onCreate(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		ip_arduino = preferences.getString("arduino_ip", "null");
		Log.d("SERVICE","ip : "+ip_arduino);
		entryDAO.open();
		warningsDAO.open();
		DAO_closed = false;
		handler.post(periodicRequests);
		Log.d("SERVICE","STARTED");
	}
	
	/* Stopping http requests on destroy, closing DAOs */
	@Override
	public void onDestroy(){
		entryDAO.close();
		warningsDAO.close();
		DAO_closed = true;
		handler.removeCallbacks(periodicRequests);
		Log.d("SERVICE","DESTROYED");
	}

	
	public class LocalBinder extends Binder {
        public WarningsService getService() {
            return WarningsService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	/* Parsing http response and storing values in database */
	private void parseAndStore(String response){
		if(DAO_closed){
			Log.d("parseAndStore","fail");
			return;
		}
		float temperature = 0;
		float gas = 0;
		int id_temp = -1;
		int id_gas = -1;
		String split [] = response.split("\n");
		for(String line : split){
			if(line.startsWith("Temperature : ")){
				line = line.substring(14);
				String [] split2 = line.split("%");
				temperature = Float.parseFloat(split2[0]);
				id_temp = Integer.parseInt(split2[1]);
			}
			if(line.startsWith("Gas : ")){
				line = line.substring(6);
				String [] split2 = line.split("%");
				gas = Float.parseFloat(split2[0]);
				id_gas = Integer.parseInt(split2[1]);
			}
		}
		Log.d("SERVICE","RECEIVED temp = "+temperature+", gas = "+gas+", IDs = ("+id_temp+","+id_gas+")");
		
		float last_gas = entryDAO.selectLastGas(id_gas);
		Date now = new Date();
		
		/* If last gas measured was over limit and it's now below, the warning is over */
		if (last_gas >= 20 && gas <= 20){
			Date warning_start = warningsDAO.selectWarningStart(id_gas);
			if(warning_start != null){
				// alert duration in ms
				long diff = now.getTime() - warning_start.getTime();
				// in seconds
				diff /= 1000;
				// rounded
				diff = Math.round(diff);
				String duration = diff+" seconds";
				UIHelper.sendNotification(this, "Gas level returned to normal (Sensor ID : "+id_gas+")", "End of warning", "Alert duration : "+duration, 1);
				warningsDAO.endWarning(id_gas, now);
			}
		}
		/* If last gas measured was below limit and it's now over, it's the start of a new warning */
		else if (last_gas <= 20 && gas >= 20){
			UIHelper.sendNotification(this, "Warning : gas limit reached (Sensor ID : "+id_gas+")", "Warning !", "Gas level : "+gas, 0);
			warningsDAO.add(new Warning(id_gas, now, null));
		}
		Entry e = new Entry(now, id_temp, temperature, id_gas, gas);
		entryDAO.add(e);
	}
	
}

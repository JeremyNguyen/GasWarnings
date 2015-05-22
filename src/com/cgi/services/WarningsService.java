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
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import data.Entry;
import data.EntryDAO;
import data.Warning;
import data.WarningsDAO;

public class WarningsService extends Service {
	
	/* Binder */
	private final IBinder binder = new LocalBinder();
	
	/* Data */
	private boolean warningOn = false;
	private EntryDAO entryDAO = new EntryDAO(this);
	private WarningsDAO warningsDAO = new WarningsDAO(this);
	
	/* Threads */
	private int requestsInterval = 3000;
	private Handler handler = new Handler();
	private Runnable periodicRequests = new Runnable() {
		@Override
		public void run() {
			Thread thread = new Thread(new Runnable(){
			    @Override
			    public void run() {
			        HttpURLConnection httpconn = null;
			    	BufferedReader br = null;
			    	try {
			    		URL url = new URL("http://172.20.10.5");
			    		URLConnection conn = url.openConnection();
			    		httpconn = (HttpURLConnection) conn;
			    		if(httpconn.getResponseCode() == HttpURLConnection.HTTP_OK){
			    			InputStream stream = httpconn.getInputStream();
			    			br = new BufferedReader(new InputStreamReader(stream));
			    			String line;
			    			String response = "";
			    			while((line = br.readLine()) != null){
			    				response += line+'\n';
			    			}
			    			parseAndStore(response);
			    		}	
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
	
	public WarningsService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onCreate(){
		entryDAO.open();
		warningsDAO.open();
		handler.post(periodicRequests);
		Log.d("SERVICE","STARTED");
	}
	
	@Override
	public void onDestroy(){
		entryDAO.close();
		warningsDAO.close();
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
	
	private void parseAndStore(String response){
		float temperature = 0;
		float gas = 0;
		String split [] = response.split("\n");
		for(String line : split){
			if(line.startsWith("Temperature : ")){
				line = line.replace("<br />", "");
				temperature = Float.parseFloat(line.substring(14));
			}
			if(line.startsWith("Gas : ")){
				line = line.replace("<br />", "");
				gas = Float.parseFloat(line.substring(6));
			}
		}
		Log.d("SERVICE","RECEIVED "+temperature+"/"+gas);
		Entry e = new Entry(temperature, gas);
		entryDAO.add(e);
		if(gas > 20 && ! warningOn){
			warningOn = true;
			warningsDAO.add(new Warning(new Date(), null));
		}
		if(gas < 20 && warningOn){
			warningOn = false;
			warningsDAO.update(new Date());
		}
	}
	
}

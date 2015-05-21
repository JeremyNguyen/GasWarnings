package com.cgi.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import data.Entry;
import data.EntryDAO;

public class WarningsService extends Service {
	
	private final IBinder binder = new LocalBinder();
	
	EntryDAO entryDAO = new EntryDAO(this);
	
	public WarningsService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onCreate(){
		// starting handler
		handler.post(periodicRequests);
		Log.d("SERVICE","STARTED");
	}
	
	@Override
	public void onDestroy(){
		// stopping handler
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
	
	Handler handler = new Handler();
	
	Runnable periodicRequests = new Runnable() {
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
			handler.postDelayed(periodicRequests, 5000);
		}
	};
	
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
		Entry e = new Entry(temperature, gas);
		// TODO : try not to open and close
		entryDAO.open();
		entryDAO.add(e);
		entryDAO.close();
	}
	
}

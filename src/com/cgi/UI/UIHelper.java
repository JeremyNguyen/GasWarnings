package com.cgi.UI;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cgi.gaswarnings.R;

public class UIHelper {
	 public static void sendNotification(Context context, String message, String ticker, float gas, int id) {
		 final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		 Intent intent_hist = new Intent(context, WarningsActivity.class);
		 final PendingIntent pendingIntent = PendingIntent.getActivity(context,
						0, intent_hist,
						PendingIntent.FLAG_ONE_SHOT);
		 
		Notification.Builder builder = new Notification.Builder(context)
				.setWhen(System.currentTimeMillis())
				.setPriority( Notification.PRIORITY_HIGH )
				.setVibrate(new long[] { 500, 500, 500 })
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(message)
				.setContentText(gas+"%")
				.setTicker(ticker)
				.setContentIntent(pendingIntent);
		
		Notification notif = builder.build();
		notif.flags = Notification.FLAG_AUTO_CANCEL;
	
		manager.notify(id, notif);
		
	 }
}

/**
 * User Interface Helper 
 */

package com.cgi.UI;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cgi.gaswarnings.R;

public class UIHelper {
	
	/* Notification sending tool */
	public static void sendNotification(Context context, String title, String ticker, String content, int id) {
		final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent_hist = new Intent(context, WarningsActivity.class);
		intent_hist.setAction(Long.toString(System.currentTimeMillis()));
		final PendingIntent pendingIntent = PendingIntent.getActivity(context,
				0, intent_hist,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder builder = new Notification.Builder(context)
			.setWhen(System.currentTimeMillis())
			.setPriority( Notification.PRIORITY_HIGH )
			.setVibrate(new long[] { 500, 500, 500 })
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(title)
			.setContentText(content)
			.setTicker(ticker)
			.setContentIntent(pendingIntent);
		Notification notif = builder.build();
		notif.flags = Notification.FLAG_AUTO_CANCEL;
		manager.notify(id, notif);
	}
	 
}

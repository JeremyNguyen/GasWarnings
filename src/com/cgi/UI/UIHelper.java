/**
 * User Interface Helper 
 */

package com.cgi.UI;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
	
	public static void ipConfigDialog(final Context context){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("Setting Arduino URL");
		alertDialog.setMessage("Enter Arduino's URL (192.168.240.1/arduino/measures)");
		final EditText input = new EditText(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialog.setView(input);
		alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String ip = input.getText().toString().trim();
				ip = "http://" + ip;
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("arduino_ip", ip);
				editor.commit();
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}

}

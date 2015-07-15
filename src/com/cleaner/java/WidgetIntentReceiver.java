package com.cleaner.java;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cleaner.java.preferences.AppPreferences;
import com.cleaner.java.process.KillProcess;

public class WidgetIntentReceiver extends BroadcastReceiver {
	private static int clickCount = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				"com.cleaner.java.intent.action.CLEAN_MEMORY")) {
			Log.i(AppPreferences.LOGTAG, "Limpiando memoria");
			clean_memory(context);
			Log.i(AppPreferences.LOGTAG, "Memoria limpiada");
		}
	}

	private void updateWidgetPictureAndButtonListener(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_cleaner);
		remoteViews.setImageViewResource(R.id.widget_image, getImageToSet());
		remoteViews.setOnClickPendingIntent(R.id.widget_image,
				WidgetProvider.buildButtonPendingIntent(context));

		WidgetProvider.pushWidgetUpdate(context.getApplicationContext(),
				remoteViews);
	}

	private int getImageToSet() {
		clickCount++;
		return clickCount % 2 == 0 ? R.drawable.trash_black
				: R.drawable.trash_white;
	}

	public void clean_memory(final Context context) {
		// show toast
		Toast.makeText(context, context.getString(R.string.before_clean),
				Toast.LENGTH_LONG).show();
		// begin clean memory
		ActivityManager activityManger = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = activityManger
				.getRunningAppProcesses();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				ActivityManager.RunningAppProcessInfo apinfo = list.get(i);

				String[] pkgList = apinfo.pkgList;
				if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					for (int j = 0; j < pkgList.length; j++) {
						String packageName = pkgList[j];
						KillProcess process = new KillProcess(context,
								apinfo.pid, packageName);
						process.execute();
					}
				}
			}
		}
		// end clean memory
		// change icon
		updateWidgetPictureAndButtonListener(context);
		// show toast
		Toast.makeText(context, context.getString(R.string.after_clean),
				Toast.LENGTH_LONG).show();
	}
}

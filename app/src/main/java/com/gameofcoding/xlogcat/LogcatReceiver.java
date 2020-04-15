package com.gameofcoding.xlogcat;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;

public class LogcatReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(AppConstants.ACTION_APP_LOG)) {
			String fromPackge = intent.getStringExtra(AppConstants.KEY_APP_PACKAGE_NAME);
			String logLine = intent.getStringExtra(AppConstants.KEY_APP_LOG_LINE);
			context.sendBroadcast(new Intent().setAction(AppConstants.ACTION_FORWARD_APP_LOG)
								  .putExtra(AppConstants.KEY_APP_PACKAGE_NAME, fromPackge)
								  .putExtra(AppConstants.KEY_APP_LOG_LINE, logLine));
		}
	}

}

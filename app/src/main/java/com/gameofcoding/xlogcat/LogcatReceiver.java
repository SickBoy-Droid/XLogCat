package com.gameofcoding.xlogcat;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogcatReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(AppConstants.ACTION_APP_LOG)) {
			String fromPackge = intent.getStringExtra(AppConstants.KEY_APP_PACKAGE_NAME);
			String logLine = intent.getStringExtra(AppConstants.KEY_APP_LOG_LINE);
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter(new File(context.getExternalCacheDir(), AppConstants.LOGCAT_FILE_NAME), true));
				br.write(logLine + "\n");
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

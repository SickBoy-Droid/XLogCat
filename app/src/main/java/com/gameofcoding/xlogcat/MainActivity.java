package com.gameofcoding.xlogcat;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.gameofcoding.xlogcat.LogManager.Log;
import com.gameofcoding.xlogcat.LogManager.LogManager;
import java.io.File;
import android.widget.ScrollView;
import android.view.View;
import java.io.IOException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;

public class MainActivity extends Activity {
	public class ForwLogcatRecei extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(AppConstants.ACTION_FORWARD_APP_LOG)) {
				String fromPackge = intent.getStringExtra(AppConstants.KEY_APP_PACKAGE_NAME);
				String logLine = intent.getStringExtra(AppConstants.KEY_APP_LOG_LINE);
				tvLogMsg.setText(tvLogMsg.getText() + "\n" +logLine);
			}
		}
	}
	String TAG = "MainActivity";
	boolean isActivityInForeground = false;
	String prevoiusLogs;
	private final File CACHE_DIR = new File("/storage/emulated/0/Android/data/com.gameofcoding." + "automater" + "/cache");
	TextView tvLogTime;
	TextView tvLogPriority;
	TextView tvLogTag;
	TextView tvLogMsg;
	ScrollView mVerticalScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.init(CACHE_DIR);
//		final Thread.UncaughtExceptionHandler defHandler = Thread.getDefaultUncaughtExceptionHandler();
//		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//				@Override
//				public void uncaughtException(Thread t, Throwable ex) {
//					try {
//						Log.e(TAG, "Exception last: ", ex);
//					} 
//					finally {
//						defHandler.uncaughtException(t, ex);
//					}
//				}
//			});
		setContentView(R.layout.main);
		tvLogTime = findViewById(R.id.logTime);
		tvLogPriority = findViewById(R.id.logPriority);
		tvLogTag = findViewById(R.id.logTag);
		tvLogMsg = findViewById(R.id.logMsg);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AppConstants.ACTION_FORWARD_APP_LOG);
		registerReceiver(new ForwLogcatRecei(), intentFilter);
		mVerticalScrollView = findViewById(R.id.verticalScrollView);
		Typeface googleSansMedium = Typeface.createFromAsset(getAssets(),
															 "googlesans_medium.ttf");
		Typeface googleSansBold = Typeface.createFromAsset(getAssets(),
														   "googlesans_bold.ttf");
		tvLogTime.setTypeface(googleSansMedium);
		tvLogPriority.setTypeface(googleSansBold);
		tvLogTag.setTypeface(googleSansMedium);
		tvLogMsg.setTypeface(googleSansMedium);
		//startThread();
		//reloadLogs();
	}

	private void startThread() {
		new Thread(new Runnable() {
				@Override
				public void run() {
					int i =0;
					while (isActivityInForeground) {
						try {
							synchronized (this) {
								wait(600);
								reloadLogs();
							}
						} catch (InterruptedException e) {
							Log.e(TAG, "Exception ocurred.", e);
						}
					}
				}
			}).start();
	}

	public void reloadLogs() {
		final LogManager logManager = new LogManager(CACHE_DIR);
		logManager.spanLogs();
		if (!logManager.getSpannedLogTime().toString().equals(prevoiusLogs))
			prevoiusLogs = logManager.getSpannedLogTime().toString();
		else
			return;
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvLogTime.setText(logManager.getSpannedLogTime());
					tvLogPriority.setText(logManager.getSpannedLogPriority());
					tvLogTag.setText(logManager.getSpannedLogTag());
					tvLogMsg.setText(logManager.getSpannedLogMsg());
					mVerticalScrollView.post(new Runnable() {
							@Override
							public void run() {
								mVerticalScrollView.fullScroll(View.FOCUS_DOWN);
								mVerticalScrollView.fullScroll(View.FOCUS_LEFT);
							}
						});
				}
			});
	}

	@Override
	protected void onResume() {
		super.onResume();
		isActivityInForeground = true;
		startThread();
	}

	@Override
	protected void onPause() {
		super.onPause();
		isActivityInForeground = false;
	}
}

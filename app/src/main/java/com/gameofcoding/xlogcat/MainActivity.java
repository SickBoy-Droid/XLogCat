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

public class MainActivity extends Activity {
	String TAG = "MainActivity";
	boolean isActivityInForeground = false;
	String prevoiusLogs;
	private final File CACHE_DIR = new File("/storage/emulated/0/Android/data/com.gameofcoding." + "xlogcat" + "/cache");
	TextView tvLogTime;
	TextView tvLogPriority;
	TextView tvLogTag;
	TextView tvLogMsg;
	ScrollView mVerticalScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//				@Override
//				public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
//					//Catch your exception
//					// Without System.exit() this will not work.
//
//					System.exit(2);
//					Thread.setDefaultUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
//				}
//			});
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tvLogTime = findViewById(R.id.logTime);
		tvLogPriority = findViewById(R.id.logPriority);
		tvLogTag = findViewById(R.id.logTag);
		tvLogMsg = findViewById(R.id.logMsg);
		mVerticalScrollView = findViewById(R.id.verticalScrollView);
		Typeface googleSansMedium = Typeface.createFromAsset(getAssets(),
															 "googlesans_medium.ttf");
		Typeface googleSansBold = Typeface.createFromAsset(getAssets(),
														   "googlesans_bold.ttf");
		Log.init(CACHE_DIR);
		tvLogTime.setTypeface(googleSansMedium);
		tvLogPriority.setTypeface(googleSansBold);
		tvLogTag.setTypeface(googleSansMedium);
		tvLogMsg.setTypeface(googleSansMedium);
		startThread();
		reloadLogs();
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

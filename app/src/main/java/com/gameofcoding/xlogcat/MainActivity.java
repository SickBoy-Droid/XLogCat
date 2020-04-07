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
	private final File CACHE_DIR = new File("/storage/emulated/0/Android/data/com.gameofcoding." + "clicker" + "/cache");
	TextView tvLog;
    TextView tvLogInfo;
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
		tvLog = findViewById(R.id.log);
		tvLogInfo = findViewById(R.id.logInfo);
		mVerticalScrollView = findViewById(R.id.verticalScrollView);
		Typeface googleSansMedium = Typeface.createFromAsset(getAssets(),
															 "googlesans_medium.ttf");
		Typeface googleSansBold = Typeface.createFromAsset(getAssets(),
														   "googlesans_bold.ttf");
		Log.init(CACHE_DIR);
		tvLog.setTypeface(googleSansMedium);
		tvLogInfo.setTypeface(googleSansBold);
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
								wait(800);
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
		if (!logManager.getSpannedLogInfo().toString().equals(prevoiusLogs))
			prevoiusLogs = logManager.getSpannedLogInfo().toString();
		else
			return;
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvLogInfo.setText(logManager.getSpannedLogInfo());
					tvLog.setText(logManager.getSpannedLog());
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

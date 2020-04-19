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
import android.webkit.WebView;
import org.apache.http.util.EncodingUtils;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;

public class MainActivity extends Activity {
    private static String TAG = "MainActivity";
    final Pattern PATTERN_PRIORITY_VERBOSE = Pattern.compile("..-..\\s..:..:......\\s.....\\s.....\\sV\\s.*");
    final Pattern PATTERN_PRIORITY_DEBUG = Pattern.compile("..-..\\s..:..:......\\s.....\\s.....\\sD\\s.*");
    final Pattern PATTERN_PRIORITY_INFO = Pattern.compile("..-..\\s..:..:......\\s.....\\s.....\\sI\\s.*");
    final Pattern PATTERN_PRIORITY_WARN = Pattern.compile("..-..\\s..:..:......\\s.....\\s.....\\sW\\s.*");
    final Pattern PATTERN_PRIORITY_ERROR = Pattern.compile("..-..\\s..:..:......\\s.....\\s.....\\sE\\s.*");
    private boolean mShouldReadLogs;
    private StringBuilder mLogs = new StringBuilder();
    private BufferedReader mLogsReader;
    private WebView mWebView;

    private Thread mLogsReader = new Thread(new Runnable() {
	    @Override
	    public void run() {	
		try {
		    if (mLogsReader == null) {
			FileReader fileReader = new FileReader(new File(getExternalCacheDir(), AppConstants.LOGCAT_FILE_NAME));
			mLogsReader = new BufferedReader(fileReader);
		    }
		    while (mShouldReadLogs) {
			String logLine = mLogsReader.readLine();
			if (logLine != null) {
			    mLogs.append(toHtml(logLine));
			    updateLogs();
			}
		    }
		} catch (Throwable e) {
		    showToast(e.toString());
		}
	    }
	});
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mWebView = findViewById(R.id.webView);
	Typeface googleSansMedium = Typeface.createFromAsset(getAssets(),
							     "googlesans_medium.ttf");
	Typeface googleSansBold = Typeface.createFromAsset(getAssets(),
							   "googlesans_bold.ttf");
    }

    private void updateLogs() {
	runOnUiThread(new Runnable() {
		@Override
		public void run() {
		    mWebView.loadData(mLogs.toString(), "text/html", "UTF-8");
		}
	    });
    }

    public void startReadingLogs() {
	mShouldReadLogs = true;
	if(!mLogsReader.isAlive()) {
	    mLogsReader.start();
	}
    }

    private String toHtml(String logLine) {
	String color = "black";
	if (PATTERN_PRIORITY_VERBOSE.matcher(logLine).matches()) {
	    color = "#FF1744";
	} else if (PATTERN_PRIORITY_DEBUG.matcher(logLine).matches()) {
	    color = "#FF1744";
	} else if (PATTERN_PRIORITY_INFO.matcher(logLine).matches()) {
	    color = "#FF1744";
	} else if (PATTERN_PRIORITY_WARN.matcher(logLine).matches()) {
	    color = "#FF1744";
	} else if (PATTERN_PRIORITY_ERROR.matcher(logLine).matches()) {
	    color = "#FF1744";
	}
	logLine = "<p style=\"color:blue\">" + logLine + "</p>";
	return logLine;
    }

    public void showToast(final String msg) {
	runOnUiThread(new Runnable() {
		@Override
		public void run() {
		    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
	    });
    }
    @Override
    protected void onPause() {
	super.onPause();
	stopReadingLogs();
    }

    @Override
    protected void onResume() {
	super.onResume();
	startReadingLogs();
    }

    public void stopReadingLogs() {
	mShouldReadLogs = false;
    }

    @Override
    protected void onDestroy() {
	try {
	    if (mLogsReader != null)
		mLogsReader.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	super.onDestroy();
    }
}

package com.gameofcoding.xlogcat.LogManager;

import java.io.File;
import android.app.Application;

public class Log {
	private static File mExternalCacheDir;
	private static LogManager mLogManager;
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
	public static final int ERROR = 6;
    public static void init(File externalCacheDir) {
		mExternalCacheDir = externalCacheDir;
		mLogManager = new LogManager(externalCacheDir);
	}

	public static boolean deleteLogs() {
		return new File(mExternalCacheDir, LogManager.JSON_LOG_FILE_NAME).delete();
	}

	public static void v(String tag, String msg) {printLog(VERBOSE, tag, msg);}

	public static void v(String tag, String msg, Throwable tr) {printLog(VERBOSE, tag, msg, tr);}

	public static void d(String tag, String msg) {printLog(DEBUG, tag, msg);}

	public static void d(String tag, String msg, Throwable tr) {printLog(DEBUG, tag, msg, tr);}

	public static void i(String tag, String msg) {printLog(INFO, tag, msg);}

	public static void i(String tag, String msg, Throwable tr) {printLog(INFO, tag, msg, tr);}

	public static void w(String tag, String msg) {printLog(WARN, tag, msg);}

	public static void w(String tag, String msg, Throwable tr) {printLog(WARN, tag, msg, tr);}

	public static void e(String tag, String msg) {printLog(ERROR, tag, msg);}

	public static void e(String tag, String msg, Throwable tr) {printLog(ERROR, tag, msg, tr);}

	public static void printLog(int priority, String tag, String msg) {printLog(priority, tag, msg, null);}

	public static void printLog(int priority, String tag, String msg, Throwable tr) {
		if (mLogManager == null || mExternalCacheDir == null) {
			mExternalCacheDir = new File("/sdcard/Android/data/" + Log.class.getPackage().getName() + "/cache");
			if (!mExternalCacheDir.mkdirs())
				return;
		}
		if (tag == null) tag = "null";
		if (msg == null) msg = "Unknown message";
		if (tr != null)
			mLogManager.addLog(priority, tag, msg, tr);
		else
			mLogManager.addLog(priority, tag, msg);
	}
}

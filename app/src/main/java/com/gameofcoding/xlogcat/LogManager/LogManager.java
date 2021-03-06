package com.gameofcoding.xlogcat.LogManager;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

public class LogManager {
	private static File JSONLogFile;
	public static final String JSON_LOG_FILE_NAME = "appLog.json";
	public static final String NAME_DATE = "date";
	public static final String NAME_PRIORITY= "priority";
	public static final String NAME_TAG = "tag";
	public static final String NAME_MSG = "msg";
	public static final String NAME_EXCEP = "exception";
	public static final String NAME_EXCEP_REASON = "exception_reason";
	public static final String NAME_EXCEP_STACK_TRACE = "exception_stack_trace";

	private SpannableStringBuilder mSpannedLogTime = new SpannableStringBuilder();
	private SpannableStringBuilder mSpannedLogPriority = new SpannableStringBuilder();
	private SpannableStringBuilder mSpannedLogTag = new SpannableStringBuilder();
	private SpannableStringBuilder mSpannedLogMsg = new SpannableStringBuilder();
	public LogManager(File cacheDir) {
		this.JSONLogFile = new File(cacheDir, JSON_LOG_FILE_NAME);
	}

	public void addLog(int priority, String tag, String msg) {
		addLog(priority, tag, msg, null);
	}

	public void addLog(int priority, String tag, String msg, Throwable tr) {
		try {
			// Parse log date and time
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM");
			String date = dateFormat.format(new Date());

			// Parse exception, if has
			JSONObject JSONExcepObject = null;
			if (tr != null) {
				JSONExcepObject = new JSONObject();
				JSONExcepObject.put(NAME_EXCEP_REASON, tr.getClass().getName() + ", " + tr.getMessage());
				JSONArray JSONExcepStackTraceArray = new JSONArray();
				StackTraceElement[] stackTraceElements = tr.getStackTrace();
				for (StackTraceElement stackTraceElement : stackTraceElements)
					JSONExcepStackTraceArray.put("at " + stackTraceElement.toString());
				JSONExcepObject.put(NAME_EXCEP_STACK_TRACE, JSONExcepStackTraceArray);
			}

			// Create json log object
			JSONObject JSONLogObject = new JSONObject();
			JSONLogObject.put(NAME_DATE, date);
			JSONLogObject.put(NAME_PRIORITY, priority);
			JSONLogObject.put(NAME_TAG, tag);
			JSONLogObject.put(NAME_MSG, msg);
			if (JSONExcepObject != null)
				JSONLogObject.put(NAME_EXCEP, JSONExcepObject);

			// Add above log to previously stored logs
			JSONArray JSONLogsArray = readJSONLogFile();
			JSONLogsArray.put(JSONLogObject);
			FileWriter file = new FileWriter(JSONLogFile);
            file.write(JSONLogsArray.toString());
            file.flush();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray readJSONLogFile() {
		try {
			if (!JSONLogFile.exists())
				return new JSONArray();
			// Read log file and convert it to JSON
			FileReader fr = new FileReader(JSONLogFile);
			BufferedReader br = new BufferedReader(fr);
			String log = "";
			String line = "";
			while ((line = br.readLine()) != null)
				log += line;
			br.close();
			fr.close();
			return new JSONArray(log);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Spanned getSpannedLogTime() {
		return mSpannedLogTime;
	}
	public Spanned getSpannedLogPriority() {
		return mSpannedLogPriority;
	}
	public Spanned getSpannedLogTag() {
		return mSpannedLogTag;
	}
	public Spanned getSpannedLogMsg() {
		return mSpannedLogMsg;
	}

	public void addSpannedLogTime(Spanned spannedLogTime) {
		if (mSpannedLogTime != null && spannedLogTime != null) {
			mSpannedLogTime.append(spannedLogTime);
			mSpannedLogTime.append("\n");
		}
	}

	public void addSpannedLogPriority(Spanned spannedLogPriority) {
		if (mSpannedLogPriority != null && spannedLogPriority != null) {
			mSpannedLogPriority.append(spannedLogPriority);
			mSpannedLogPriority.append("\n");
		}
	}

	public void addSpannedLogTag(Spanned spannedLogTag) {
		if (mSpannedLogTag != null && spannedLogTag != null) {
			mSpannedLogTag.append(spannedLogTag);
			mSpannedLogTag.append("\n");
		}
	}
	public void addSpannedLogMsg(Spanned spannedLogMsg) {
		if (mSpannedLogMsg != null && spannedLogMsg != null) {
			mSpannedLogMsg.append(spannedLogMsg);
			mSpannedLogMsg.append("\n");
		}
	}

	public void spanLogs() {
		try {
			// Get date from JSON log file
			JSONArray JSONLogsArray = readJSONLogFile();
			for (int i = 0; i < JSONLogsArray.length(); i++) {
				JSONObject JSONLogObject = JSONLogsArray.getJSONObject(i);
				String date = JSONLogObject.getString(NAME_DATE);
				int priority = JSONLogObject.getInt(NAME_PRIORITY);
				String tag = JSONLogObject.getString(NAME_TAG);
				String msg = JSONLogObject.getString(NAME_MSG);

				// Check whether the exception was added or not to log
				if (JSONLogObject.has(NAME_EXCEP)) {
					JSONObject JSONExcepObject = JSONLogObject.getJSONObject(NAME_EXCEP);
					String excepReason = JSONExcepObject.getString(NAME_EXCEP_REASON);
					JSONArray JSONExcepStackTraceArray = JSONExcepObject.getJSONArray(NAME_EXCEP_STACK_TRACE);

					// Get stack trace of exception
					String excepStackTrace = "";
					for (int j = 0; j < JSONExcepStackTraceArray.length(); j++)
						excepStackTrace += JSONExcepStackTraceArray.getString(j) + "\n";

					// Span and store log
					Spanned[] spannedStrings = spanLogLine(date, priority, tag, msg, excepReason, excepStackTrace.trim());
					addSpannedLogTime(spannedStrings[0]);
					addSpannedLogPriority(spannedStrings[1]);
					addSpannedLogTag(spannedStrings[2]);
					addSpannedLogMsg(spannedStrings[3]);
				} else {
					Spanned[] spannedStrings = spanLogLine(date, priority, tag, msg);
					addSpannedLogTime(spannedStrings[0]);
					addSpannedLogPriority(spannedStrings[1]);
					addSpannedLogTag(spannedStrings[2]);
					addSpannedLogMsg(spannedStrings[3]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Spanned[] spanLogLine(String date, int priority, String tag, String msg) {
		return spanLogLine(date, priority, tag, msg, null, null);
	}

	public Spanned[] spanLogLine(String date, int priority, String tag, String msg, String excepReason, String excepStackTrace) {
		SpannableStringBuilder spannedlogTime = new SpannableStringBuilder();
		SpannableStringBuilder spannedlogPriority = new SpannableStringBuilder();
		SpannableStringBuilder spannedlogTag = new SpannableStringBuilder();
		SpannableStringBuilder spannedlogMsg = new SpannableStringBuilder();
		final String doubleTab = "        ";

		// Span date
		spannedlogTime.append(date);

		// Span priority
		int priorityColor = 0;
		String prioritySymbol = "";
		switch (priority) {
			case Log.VERBOSE:
				priorityColor = Color.rgb(176, 190, 197);
				prioritySymbol = "V";
				break;
			case Log.ERROR:
				priorityColor = Color.rgb(244, 67, 54);
				prioritySymbol = "E";
				break;
			case Log.INFO:
				priorityColor = Color.rgb(105, 240, 174);
				prioritySymbol = "I";
				break;
			case Log.WARN:
				priorityColor = Color.rgb(255, 109, 0);
				prioritySymbol = "W";
				break;
			case Log.DEBUG:
				priorityColor = Color.rgb(33, 150, 243);
				prioritySymbol = "D";
				break;
		}
		spannedlogPriority.append(setTextAppearanceSpan(prioritySymbol, ColorStateList.valueOf(priorityColor)));

		// Span tag
		spannedlogTag.append(setTextAppearanceSpan(tag, ColorStateList.valueOf(priorityColor)));

		// Span msg
		spannedlogMsg.append(msg);

		// Span stack trace of exception
		if (excepReason != null && excepStackTrace != null) {
			spannedlogTime.append("\n");
			spannedlogPriority.append("\n");
			spannedlogTag.append("\n");
			spannedlogMsg.append("\n");
			spannedlogMsg.append(setTextAppearanceSpan("FATAL EXCEPTION:   ", ColorStateList.valueOf(Color.rgb(239, 83, 80)), 
													   Typeface.BOLD_ITALIC));
			spannedlogMsg.append(excepReason);
			spannedlogTime.append("\n");
			spannedlogPriority.append("\n");
			spannedlogTag.append("\n");
			spannedlogMsg.append("\n");
			spannedlogMsg.append(excepStackTrace);
			int numLines = excepStackTrace.split("\n").length;
			while (--numLines > 0) {
				spannedlogTime.append("\n");
				spannedlogPriority.append("\n");
				spannedlogTag.append("\n");
			}
		}
		return new Spanned[]{spannedlogTime,spannedlogPriority, spannedlogTag, spannedlogMsg};
	}

	/*
	 * Sets {@link #android.text.TextAppearanceSpan} to given String.
	 * @see #setTextAppearance(String, int, int, int, ColorStateList, ColorStateList, int, String)
	 */
	public static Spanned setTextAppearanceSpan(String source,
												ColorStateList color) {
		return setTextAppearanceSpan(source, -1, color, null,
									 Typeface.NORMAL, null);
	}

	/*
	 * Sets {@link #android.text.TextAppearanceSpan} to given String.
	 * @see #setTextAppearance(String, int, int, int, ColorStateList, ColorStateList, int, String)
	 */
	public static Spanned setTextAppearanceSpan(String source,
												ColorStateList color, int style) {
		return setTextAppearanceSpan(source, -1, color, null, style, null);
	}

	/*
	 * Sets {@link #android.text.TextAppearanceSpan} to given String.
	 * 
	 * @param source Text for which you want to set span.
	 * @param size Size for text.
	 * @param color Color for text.
	 * @param linkColor Link color for text.
	 * @param style Style for text. (Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC,
	 *                               Typeface.BOLD_ITALIC)
	 * @param fontFamily Font family for text.
	 *
	 * @return Spanned text.
	 */
	public static Spanned setTextAppearanceSpan(String source,
												int size, ColorStateList color, ColorStateList linkColor,
												int style, String fontFamily) {
		SpannableString spannableString = new SpannableString(source);
		spannableString.setSpan(new TextAppearanceSpan(fontFamily, style , size, color,
													   linkColor), 0, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}
}

package com.itheima.safeguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsTool {

	public interface OnBackupRestoreListener {
		
		void onBackupRestoreCount(int count);
		
		void onBackupRestoreProgress(int progress);
		
		void onBackupRestoreSuccess();
		
		void onBackupRestoreFailed(String errorMsg);
		
		void onBackupRestoreFinish();
	}
	
	public static void backup(Context context, OnBackupRestoreListener listener) {
		 try {
			File backupFile = new File(Environment.getExternalStorageDirectory(), "sms_bak.xml");
			FileOutputStream fos = new FileOutputStream(backupFile);
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(fos, "utf-8");
			serializer.startDocument("utf-8", true);
			serializer.startTag(null, "smss");
			Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "type", "date", "body"}, null, null, null);
			listener.onBackupRestoreCount(cursor.getCount());
			serializer.attribute(null, "count", String.valueOf(cursor.getCount()));
			int progress = 0;
			while(cursor.moveToNext()) {
				serializer.startTag(null, "sms");
				serializer.startTag(null, "address");
				serializer.text(cursor.isNull(0) ? "" : cursor.getString(0));
				serializer.endTag(null, "address");
				serializer.startTag(null, "type");
				serializer.text(cursor.isNull(1) ? "" :cursor.getString(1));
				serializer.endTag(null, "type");
				serializer.startTag(null, "date");
				serializer.text(cursor.isNull(2) ? "" :cursor.getString(2));
				serializer.endTag(null, "date");
				serializer.startTag(null, "body");
				serializer.text(cursor.isNull(3) ? "" :cursor.getString(3));
				serializer.endTag(null, "body");
				serializer.endTag(null, "sms");
				listener.onBackupRestoreProgress(++ progress); 
			}
			serializer.endTag(null, "smss");
			serializer.endDocument();
			serializer.flush();
			cursor.close();
			fos.close();
			listener.onBackupRestoreSuccess();
		 } catch(Throwable ex) {
			 listener.onBackupRestoreFailed(ex.getMessage());
			 ex.printStackTrace();
		 } finally {
			 listener.onBackupRestoreFinish();
		 }
	}
	
	public static void restore(Context context, OnBackupRestoreListener listener) {
		 try {
			File backupFile = new File(Environment.getExternalStorageDirectory(), "sms_bak.xml");
			FileInputStream fis = new FileInputStream(backupFile);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(fis, "utf-8"); 
			ContentValues cv = new ContentValues();
			int progress = 0;
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					if("smss".equals(parser.getName())) {
						int count = Integer.valueOf(parser.getAttributeValue(null, "count"));
						listener.onBackupRestoreCount(count);
					} else if("address".equals(parser.getName())) {
						cv.put("address", parser.nextText());
					} else if("type".equals(parser.getName())) {
						cv.put("type", parser.nextText());
					} else if("date".equals(parser.getName())) {
						cv.put("date", parser.nextText());
					} else if("body".equals(parser.getName())) {
						cv.put("body", parser.nextText());
					} 
				} else if(eventType == XmlPullParser.END_TAG) {
					if("sms".equals(parser.getName())) {
						context.getContentResolver().insert(Uri.parse("content://sms/"), cv);
						listener.onBackupRestoreProgress(++ progress);
					}
				}
				eventType = parser.next();
			}
			fis.close();
			listener.onBackupRestoreSuccess();
		 } catch(Exception ex) {
			 listener.onBackupRestoreFailed(ex.getMessage());
			 ex.printStackTrace();
		 } finally {
			 listener.onBackupRestoreFinish();
		 }
	}
}

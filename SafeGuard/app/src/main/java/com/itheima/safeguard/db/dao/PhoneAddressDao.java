package com.itheima.safeguard.db.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import com.itheima.safeguard.db.MySQLiteOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class PhoneAddressDao {
	
	public static String findAddress(Context context, String phoneNumber) {
		String phoneAddress = "»ðÐÇµØÇø";
		if(phoneNumber != null) {
			if(Pattern.matches("^1[3578]\\d{9}", phoneNumber)) {
				MySQLiteOpenHelper.copyDatabaseFile(context, "address.db");
				SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath("address.db").getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
				Cursor cursor = db.rawQuery("select location from data2 where id =( select outkey from data1 where id=?)", new String[]{phoneNumber.substring(0, 7)});
				if(cursor.moveToNext()) {
					phoneAddress = cursor.getString(0);
				}
			} else if(Pattern.matches("^1[12][09]", phoneNumber)) {
				phoneAddress = "½ô¼±ºÅÂë";
			} else if(Pattern.matches("^[19][02]\\d{3}", phoneNumber)) {
				phoneAddress = "·þÎñºÅÂë";
			}
		}
		return phoneAddress;
	}
}

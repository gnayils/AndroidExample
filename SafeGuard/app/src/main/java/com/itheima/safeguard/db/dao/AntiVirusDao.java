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

public class AntiVirusDao {
	
	private SQLiteDatabase db;
	
	public AntiVirusDao(Context context) {
		MySQLiteOpenHelper.copyDatabaseFile(context, "antivirus.db");
		db = SQLiteDatabase.openDatabase(context.getDatabasePath("antivirus.db").getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
	}
	
	public boolean isVirus(String md5) {
		boolean result = false;
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		result = cursor.moveToNext();
		cursor.close();
		return result;
	}
	
	public void close() {
		if(db != null) {
			db.close();
		}
	}
}

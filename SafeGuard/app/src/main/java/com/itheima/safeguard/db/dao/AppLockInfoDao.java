package com.itheima.safeguard.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.itheima.safeguard.db.MySQLiteOpenHelper;
import com.itheima.safeguard.entity.BlockNumber;
import com.itheima.safeguard.service.WatchDogService;
import com.itheima.safeguard.utils.SystemTool;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockInfoDao {

	private MySQLiteOpenHelper h;
	private Context context;
	private static final Intent INTENT_RECORD_CHANGED = new Intent(WatchDogService.ACTION_APP_LOCK_RECORD_CHANGED);

	public AppLockInfoDao(Context context) {
		h = new MySQLiteOpenHelper(context);
		this.context = context;
	}

	public void add(String name) {
		SQLiteDatabase db = h.getWritableDatabase();
		ContentValues v = new ContentValues();
		v.put("name", name);
		db.insert("app_lock_info", null, v);
		db.close();
		context.sendBroadcast(INTENT_RECORD_CHANGED);
		
	}

	public void delete(String name) {
		SQLiteDatabase db = h.getWritableDatabase();
		db.delete("app_lock_info", "name=?", new String[] { name });
		db.close();
		context.sendBroadcast(INTENT_RECORD_CHANGED);
	}
	

	public boolean isExists(String name) {
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select name from app_lock_info where name=?",
				new String[] { name });
		boolean isExists = cursor.moveToNext();
		cursor.close();
		db.close();
		return isExists;
	}

	public List<String> getAll() {
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = db.rawQuery("select name from app_lock_info",
				null);
		List<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			list.add(name);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	public int getRecordCount() {
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(_id) from app_lock_info",
				null);
		int recordCount = 0;
		if (cursor.moveToNext()) {
			recordCount = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return recordCount;
	}
}

package com.itheima.safeguard.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "safe_guard";
	
	public MySQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table block_number (_id integer primary key autoincrement, number varchar(20), mode integer)");
		db.execSQL("create table app_lock_info (_id integer primary key autoincrement, name varchar(200))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public static void copyDatabaseFile(Context context, String databaseFileName) {
		try {
			if(context.getDatabasePath(databaseFileName).exists()) {
				return ;
			}
			context.getDatabasePath(databaseFileName).getParentFile().mkdirs();
			InputStream is = null;
			try {
				is = context.getAssets().open(databaseFileName);
			} catch (IOException e) {
				Toast.makeText(context, "请将数据库文件" + databaseFileName + "放到项目的assets目录下", Toast.LENGTH_LONG).show();
				throw e;
			}
			FileOutputStream fos = new FileOutputStream(context.getDatabasePath(databaseFileName));
			if(is != null) {
				byte[] buffer = new byte[1024];
				int length = -1;
				while((length = is.read(buffer)) != -1){
					fos.write(buffer, 0 ,length);
				}
				fos.flush();
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

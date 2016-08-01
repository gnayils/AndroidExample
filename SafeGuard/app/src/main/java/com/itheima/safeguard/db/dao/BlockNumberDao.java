package com.itheima.safeguard.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.itheima.safeguard.db.MySQLiteOpenHelper;
import com.itheima.safeguard.entity.BlockNumber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlockNumberDao {

	private MySQLiteOpenHelper h;

	public BlockNumberDao(Context context) {
		h = new MySQLiteOpenHelper(context);
	}

	public void add(String number, int mode) {
		SQLiteDatabase db = h.getWritableDatabase();
		ContentValues v = new ContentValues();
		v.put("number", number);
		v.put("mode", mode);
		db.insert("block_number", null, v);
		db.close();
	}

	public void delete(String number) {
		SQLiteDatabase db = h.getWritableDatabase();
		db.delete("block_number", "number=?", new String[] { number });
		db.close();
	}

	public void update(String number, int mode) {
		SQLiteDatabase db = h.getWritableDatabase();
		ContentValues v = new ContentValues();
		v.put("mode", mode);
		db.update("block_number", v, "number=?", new String[] { number });
		db.close();
	}
	
	public List<BlockNumber> getPart(int offset, int limit) {
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number, mode from block_number order by _id desc limit ? offset ?",
				new String[]{String.valueOf(limit), String.valueOf(offset)});
		List<BlockNumber> list = new ArrayList<BlockNumber>();
		while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			int mode = cursor.getInt(1);
			list.add(new BlockNumber(number, mode));
		}
		cursor.close();
		db.close();
		return list;
	}

	public List<BlockNumber> getAll() {
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number, mode from block_number order by _id desc",
				null);
		List<BlockNumber> list = new ArrayList<BlockNumber>();
		while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			int mode = cursor.getInt(1);
			list.add(new BlockNumber(number, mode));
		}
		cursor.close();
		db.close();
		return list;
	}

	public BlockNumber get(String number) {
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from block_number where number=?",
				new String[] { number });
		BlockNumber bn = null;
		if(cursor.moveToNext()) {
			String n = cursor.getString(0);
			int m = cursor.getInt(1);
			bn = new BlockNumber(n, m);
		}
		cursor.close();
		db.close();
		return bn;
	}
}

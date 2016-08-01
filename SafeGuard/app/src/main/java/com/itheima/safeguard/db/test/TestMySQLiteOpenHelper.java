package com.itheima.safeguard.db.test;

import com.itheima.safeguard.db.MySQLiteOpenHelper;

import android.test.AndroidTestCase;

public class TestMySQLiteOpenHelper extends AndroidTestCase {

	public void testOnCreate() {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this.getContext());
		helper.getWritableDatabase();
	}
}

package com.itheima.safeguard.db.dao.test;

import java.util.List;
import java.util.Random;

import com.itheima.safeguard.db.dao.AppLockInfoDao;
import com.itheima.safeguard.db.dao.BlockNumberDao;
import com.itheima.safeguard.entity.BlockNumber;

import android.test.AndroidTestCase;

public class TestAppLockInfoDao extends AndroidTestCase {

	public void testAdd() {
		AppLockInfoDao dao = new AppLockInfoDao(this.getContext());
		for (int i = 0; i < 100; i++) {
			dao.add("com.app." + i);
		}
	}
	
	public void testDelete() {
		AppLockInfoDao dao = new AppLockInfoDao(this.getContext());
		dao.delete("com.app.50");
	}
	
	public void testIsExists() {
		AppLockInfoDao dao = new AppLockInfoDao(this.getContext());
		assertTrue(dao.isExists("com.app.80"));
		assertFalse(dao.isExists("com.app.50"));
	}
	
	public void testGetAll() {
		AppLockInfoDao dao = new AppLockInfoDao(this.getContext());
		List<String> list = dao.getAll();
		for(String str : list) {
			System.out.println(str);
		}
	}
}

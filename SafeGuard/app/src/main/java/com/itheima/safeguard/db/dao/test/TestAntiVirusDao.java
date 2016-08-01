package com.itheima.safeguard.db.dao.test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.itheima.safeguard.db.dao.AntiVirusDao;
import com.itheima.safeguard.db.dao.BlockNumberDao;
import com.itheima.safeguard.entity.BlockNumber;

import android.test.AndroidTestCase;

public class TestAntiVirusDao extends AndroidTestCase {
	
	public void testScan() throws IOException {
		AntiVirusDao dao = new AntiVirusDao(this.getContext());
		assertTrue(dao.isVirus("00006abdcd81e5a258051af63dcd11b3"));
	}
}

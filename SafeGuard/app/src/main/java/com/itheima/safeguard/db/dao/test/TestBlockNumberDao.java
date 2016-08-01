package com.itheima.safeguard.db.dao.test;

import java.util.List;
import java.util.Random;

import com.itheima.safeguard.db.dao.BlockNumberDao;
import com.itheima.safeguard.entity.BlockNumber;

import android.test.AndroidTestCase;

public class TestBlockNumberDao extends AndroidTestCase {

	public void testAdd() {
		BlockNumberDao dao = new BlockNumberDao(this.getContext());
		long number = 13000000000L;
		Random r = new Random();
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(number + i), r.nextInt(3));
		}
	}
	
	public void testDelete() {
		BlockNumberDao dao = new BlockNumberDao(this.getContext());
		dao.delete("15210770787");
	}
	
	public void testUpdate() {
		BlockNumberDao dao = new BlockNumberDao(this.getContext());
		dao.update("15210770788", BlockNumber.BLOCK_ALL);
	}
	
	public void testGetPart() {
		BlockNumberDao dao = new BlockNumberDao(this.getContext());
		List<BlockNumber> list = dao.getPart(0, 10);
		for(BlockNumber bn : list) {
			System.out.println(bn.toString());
		}
	}
	
	public void testGetAll() {
		BlockNumberDao dao = new BlockNumberDao(this.getContext());
		List<BlockNumber> list = dao.getAll();
		for(BlockNumber bn : list) {
			System.out.println(bn.toString());
		}
	}
}

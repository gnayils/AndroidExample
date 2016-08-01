package com.itheima.safeguard.entity;

import java.util.HashMap;
import java.util.Map;

public class BlockNumber {

	public static final int BLOCK_PHONE = 0;
	public static final int BLOCK_SMS = 1;
	public static final int BLOCK_ALL = 2;
	
	public static final Map<Integer, String> BLOCK_OPTION = new HashMap<Integer, String>();
	static {
		BLOCK_OPTION.put(BLOCK_PHONE, "拦截电话");
		BLOCK_OPTION.put(BLOCK_SMS, "拦截短信");
		BLOCK_OPTION.put(BLOCK_ALL, "拦截电话和短信");
	}
	private String number;
	private int mode;

	public BlockNumber(String number, int mode) {
		this.number = number;
		this.mode = mode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "BlockNumber [number=" + number + ", mode=" + mode + "]";
	}
}

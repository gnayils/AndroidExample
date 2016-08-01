package com.itheima.safeguard.entity;

import java.util.HashMap;
import java.util.Map;

public class BlockNumber {

	public static final int BLOCK_PHONE = 0;
	public static final int BLOCK_SMS = 1;
	public static final int BLOCK_ALL = 2;
	
	public static final Map<Integer, String> BLOCK_OPTION = new HashMap<Integer, String>();
	static {
		BLOCK_OPTION.put(BLOCK_PHONE, "���ص绰");
		BLOCK_OPTION.put(BLOCK_SMS, "���ض���");
		BLOCK_OPTION.put(BLOCK_ALL, "���ص绰�Ͷ���");
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

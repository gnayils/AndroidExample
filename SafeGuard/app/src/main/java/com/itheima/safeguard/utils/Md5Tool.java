package com.itheima.safeguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Tool {

	public static String encrypt(String text) {
		StringBuffer buffer = new StringBuffer();
		try {
			if (text != null) {
				MessageDigest digest = MessageDigest.getInstance("md5");
				byte[] result = digest.digest(text.getBytes());
				for (byte b : result) {
					String str = Integer.toHexString(b & 0xff);
					if (str.length() < 2) {
						buffer.append("0");
					}
					buffer.append(str);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	public static String calcFileMd5(String path) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(new File(path));
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				String str = Integer.toHexString(b & 0xff);
				if (str.length() < 2) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

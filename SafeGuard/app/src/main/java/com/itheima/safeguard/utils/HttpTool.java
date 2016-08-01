package com.itheima.safeguard.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.itheima.safeguard.SplashActivity;

public class HttpTool {

	
	public static InputStream get(URL url, int timeout) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(timeout);
		int returnCode = conn.getResponseCode();
		if(returnCode == 200) {
			return conn.getInputStream();
		}
		return null;
	}
}

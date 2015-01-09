package com.bjtu.nourriture.user;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class ConnectToServer {
	
	final static String ipaddress="http://123.57.38.31:3000/";
	@SuppressLint("NewApi")
	public String testURLConn1(String urlAdd) throws Exception{
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork()
		.penaltyLog().build());
	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		.penaltyLog().penaltyDeath().build());
	   
	    URL url=new URL(ipaddress+urlAdd);
		System.out.println("url-----------"+url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");	
		System.out.println("zhuangtaima----"+conn.getResponseCode());
		if (conn.getResponseCode() != 200)
			throw new RuntimeException("失败");
		InputStream is = conn.getInputStream();
		String result=readData(is, "UTF-8");
		
		conn.disconnect();
		return result;
		
	}
	
public String testURLConn2(String urlAdd,byte[] bytes) throws Exception{
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork()
		.penaltyLog().build());
	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		.penaltyLog().penaltyDeath().build());
	   
	    URL url=new URL(ipaddress+urlAdd);
		System.out.println("url-----------"+url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("POST");	
		conn.getOutputStream().write(bytes);// 输入参数
		int abc = conn.getResponseCode() ;
		System. out.println("ResponseCode------"+abc);
		if (conn.getResponseCode() != 200)
			throw new RuntimeException("失败");
		InputStream is = conn.getInputStream();
		String result=readData(is, "UTF-8");
		
		conn.disconnect();
		return result;
		
	}
	
	
	public static String readData(InputStream inSream, String charsetName)
			throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inSream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inSream.close();
		return new String(data, charsetName);
	}

}

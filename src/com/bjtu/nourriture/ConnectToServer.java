package com.bjtu.nourriture;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class ConnectToServer {
	
	//final static String ipaddress="http://10.0.2.2:8080/Letu-Travel/";
	final static String ipaddress="http://123.57.38.31:3000/";
	@SuppressLint("NewApi")
	public String testURLConn(String urlAdd,String method) throws Exception{
		System.out.println("yes1---------");
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
	   
	    System.out.println("yes---------");
	    URL url=new URL(ipaddress+urlAdd);		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);		
		conn.setRequestMethod(method);
		if (conn.getResponseCode() != 200)
			throw new RuntimeException("失败");
		InputStream is = conn.getInputStream();
		String result=readData(is, "UTF-8");
		System.out.println(result);
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
	
	public static void main(String[] args) {
		ConnectToServer connectToServer = new ConnectToServer();

		String url = "service/recipe/listAll?pageNo=1&pageSize=3";
		
		try {
			String result = connectToServer.testURLConn(url,"GET");
			
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	
}

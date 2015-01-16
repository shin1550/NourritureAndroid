package com.bjtu.nourriture.user;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.common.Session;

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
		conn.setInstanceFollowRedirects(false);
		Session session = Session.getSession();
		String sessionid = (String) session.get("sessionId");
		if(sessionid!=null&&sessionid.length()>0){
			conn.setRequestProperty("Cookie", "JSPSESSID.732cdf6d=" + sessionid+";"+Constants.POST_SESSIONID);
		}		
		System.out.println("Status:"+conn.getResponseCode());
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
		conn.setInstanceFollowRedirects(false);
		Session session = Session.getSession();
		String sessionid = (String) session.get("sessionId");
		if(sessionid!=null&&sessionid.length()>0){
			conn.setRequestProperty("Cookie", "JSPSESSID.732cdf6d=" + sessionid+";"+Constants.POST_SESSIONID);
		}
		
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("POST");	
		conn.getOutputStream().write(bytes);// 输入参数
		

		int abc = conn.getResponseCode() ;
		System. out.println("ResponseCode------"+abc);
		if (conn.getResponseCode() != 200)
			throw new RuntimeException("失败");
		InputStream is = conn.getInputStream();
		String result=readData(is, "UTF-8");
		System.out.println("result------"+result);
		conn.disconnect();
		return result;

	}

	public String testURLConn3(String urlAdd,byte[] bytes) throws Exception{

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
		String cookieval = conn.getHeaderField("set-cookie"); 
		if(cookieval != null) { 
			Constants.POST_SESSIONID = cookieval.substring(0, cookieval.indexOf(";")); 
		}
		System. out.println("ResponseCode------"+abc);
		if (conn.getResponseCode() != 200)
			throw new RuntimeException("失败");
		InputStream is = conn.getInputStream();
		String result=readData(is, "UTF-8");
		System.out.println("result------"+result);
		conn.disconnect();
		return result;

	}
	public String testURLConn4(String urlAdd) throws Exception{
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork()
		.penaltyLog().build());

		String result = null;
		BufferedReader reader = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost();
			request.setURI(new URI("http://123.57.38.31:3000/" + urlAdd));

			Session session = Session.getSession();
			String sessionid = (String) session.get("sessionId");
			if(sessionid != null) { 
				request.setHeader("Cookie", "JSPSESSID.732cdf6d=" + sessionid+";"+Constants.POST_SESSIONID); 
			}

			//UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					//postParameters);
			//request.setEntity(formEntity);

			HttpResponse response = client.execute(request);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			StringBuffer strBuffer = new StringBuffer("");
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuffer.append(line);
			}
			result = strBuffer.toString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

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

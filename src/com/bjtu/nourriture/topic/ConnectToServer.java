package com.bjtu.nourriture.topic;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.common.Session;

import android.annotation.SuppressLint;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class ConnectToServer {
	
	//final static String ipaddress="http://10.0.2.2:8080/Letu-Travel/";
	final static String ipaddress="http://123.57.38.31:3000/";
	@SuppressLint("NewApi")
	public String testURLConn(String urlAdd,String method) throws Exception{
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
	   
	
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
	
	public String topicPost(String url,List<NameValuePair> postParameters) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		String result = null;
		BufferedReader reader = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost();
			request.setURI(new URI("http://123.57.38.31:3000/" + url));
			
			Session session = Session.getSession();
			String sessionid = (String) session.get("sessionId");
			if(sessionid != null) { 
				request.setHeader("Cookie", "JSPSESSID.732cdf6d=" + sessionid+";"+Constants.POST_SESSIONID); 
			}

			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters);
			request.setEntity(formEntity);

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
	
}

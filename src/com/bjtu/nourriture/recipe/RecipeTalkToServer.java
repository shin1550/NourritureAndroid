package com.bjtu.nourriture.recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.StrictMode;

public class RecipeTalkToServer {

	public static String recipeGet(String url){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		
		String PHPSESSID = null;
		String ret = "none";
		String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(
                    "http://123.57.38.31:3000/service/"+url));
            
            if(null != PHPSESSID){
            	request.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
            } 
            /*CookieStore mCookieStore = ((AbstractHttpClient) client).getCookieStore();
            List<Cookie> cookies = mCookieStore.getCookies();
            System.out.println("---+++---");
            System.out.println(cookies.toString());*/
            
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
 
            StringBuffer strBuffer = new StringBuffer("");
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
 
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                ret = EntityUtils.toString(entity);
                CookieStore mCookieStore = ((AbstractHttpClient) client).getCookieStore();
                List<Cookie> cookies = mCookieStore.getCookies();
                System.out.println("---+++---");
                System.out.println(cookies.toString());
                for (int i = 0; i < cookies.size(); i++) {
                     //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
                     if ("PHPSESSID".equals(cookies.get(i).getName())) {
                        PHPSESSID = cookies.get(i).getValue();
                        break;
                    }

                }
            }
            
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
	
	public static String recipePost(String url,List<NameValuePair> postParameters){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		
		String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            request.setURI(new URI("http://123.57.38.31:3000/service/"+url));
            
            CookieStore mCookieStore = ((AbstractHttpClient) client).getCookieStore();
            List<Cookie> cookies = mCookieStore.getCookies();
            System.out.println("---+++---");
            System.out.println(cookies.toString());
            
            /*Header[] cookieval = request.getHeaders("set-cookie");
            //String cookieval = con.getHeaderField("set-cookie"); 
            String sessionid; 
            if(cookieval != null) { 
            	sessionid = cookieval.substring(0, cookieval.indexOf(";")); 
            }*/
            
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

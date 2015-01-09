package com.bjtu.nourriture;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GetImage {
	
	public static Bitmap getBitmapFromServer(String imagePath)
	{  		
		String path="http://123.57.38.31:3000/"+imagePath;
		HttpGet get = new HttpGet(path); 
		HttpClient client = new DefaultHttpClient(); 
		SoftReference<Bitmap> pic = null; 
		Bitmap bit = null;
		try {
			HttpResponse response = client.execute(get); 
			HttpEntity entity = response.getEntity(); 
			InputStream is = entity.getContent();
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = 4;
			bit = BitmapFactory.decodeStream(is,null,option); // 关键是这句代码  
			pic = new SoftReference<Bitmap>(bit);
			
		} 
		catch (ClientProtocolException e){ 
			e.printStackTrace(); 
		} 
		catch (IOException e){ 
			e.printStackTrace(); 
		} 
		bit = null;
		System.gc();
		
		return pic.get(); 
		
	}
	

}

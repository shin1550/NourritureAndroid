package com.bjtu.nourriture.topic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bjtu.nourriture.ConnectToServer;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class TopicUploadActivity extends Activity {
	
	DisplayImageOptions options;
	ConnectToServer connect=new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	String topicListResult;
	String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_upload);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();

		Intent intent = getIntent();
		path = intent
				.getStringExtra(Constants.INTENT_EXTRA_TOPIC_UPLAOD_PATH);
		System.out.println("path-------"+path);
		
		ImageView mypicture = (ImageView) findViewById(R.id.my_production);
		Bitmap bitmap = getLoacalBitmap(path);
		mypicture .setImageBitmap(bitmap);
		
		LinearLayout linearLayout =(LinearLayout)findViewById(R.id.upload_picture);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			// TODO Auto-generated method stub
				
				EditText upload_name= (EditText) v.findViewById(R.id.upload_name);
				EditText story= (EditText) v.findViewById(R.id.story);
				
				String uploadNameString = upload_name.getText().toString().trim();
				String storyString = story.getText().toString().trim();
				System.out.println("-----------------"+uploadNameString+"----"+storyString+"----");
				
				final File file = new File(path);
                if (file != null) {
                
                	String urlString="http://123.57.38.31:3000/topic/upload";                
                	String result = UploadUtil.uploadFile(file, urlString);
                	
                	
                	
                	
                	
     
                 
                }
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.topic_upload, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static Bitmap getLoacalBitmap(String url) {
	     try {
	          FileInputStream fis = new FileInputStream(url);
	          return BitmapFactory.decodeStream(fis);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
}

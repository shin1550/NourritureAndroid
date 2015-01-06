package com.bjtu.nourriture.recipe;

import org.json.JSONException;
import org.json.JSONObject;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class SingleRecipeActivity extends Activity{

	DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_single);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		Intent intent = getIntent();
		String singleData = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE);
		JSONObject sinngleObject = null;
		try {
			sinngleObject = new JSONObject(singleData);
			System.out.println("-----it's ok-------------");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ImageView recipePhoto = (ImageView) findViewById(R.id.singleRecipePhoto);
		try {
			ImageLoader.getInstance()
			.displayImage("http://123.57.38.31:3000/"+sinngleObject.getString("photo").trim(), recipePhoto, options, new SimpleImageLoadingListener() {
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//recipePhoto.setImageResource("http://123.57.38.31:3000/"+sinngleObject.getString("photo").trim());
	}
}

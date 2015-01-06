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
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
		JSONObject singleObject = null;
		try {
			singleObject = new JSONObject(singleData);
			System.out.println("-----it's ok-------------");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ImageView recipePhoto = (ImageView) findViewById(R.id.singleRecipePhoto);
		ImageView recipeAuthorHead = (ImageView) findViewById(R.id.singleRecipeAuthorHead);
		
		try {
			ImageLoader.getInstance()
			.displayImage("http://123.57.38.31:3000/"+singleObject.getString("photo").trim(), recipePhoto, options, new SimpleImageLoadingListener() {
			});

			ImageLoader.getInstance()
			.displayImage("http://123.57.38.31:3000"+singleObject.getJSONObject("author").getString("head").trim(), recipeAuthorHead, options, new SimpleImageLoadingListener() {
			});
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView recipeNameTextView = (TextView) findViewById(R.id.singleRecipeName);
		TextView singleRecipeAuthor = (TextView) findViewById(R.id.singleRecipeAuthor);
		TextView singleRecipeCollectNum = (TextView) findViewById(R.id.singleRecipeCollectNum);
		TextView singleRecipeCommentNum = (TextView) findViewById(R.id.singleRecipeCommentNum);
		try {
			recipeNameTextView.setText(singleObject.getString("recipeName"));
			singleRecipeAuthor.setText(singleObject.getJSONObject("author").getString("account"));
			singleRecipeCollectNum.setText(singleObject.getString("collectNum"));
			singleRecipeCommentNum.setText(singleObject.getString("commentNum"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

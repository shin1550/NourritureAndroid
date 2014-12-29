package com.bjtu.nourriture.recipe;

import org.json.JSONException;
import org.json.JSONObject;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SingleRecipeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_single);
		Intent intent = getIntent();
		String singleData = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE);
		try {
			JSONObject sinngleObject = new JSONObject(singleData);
			System.out.println("-----it's ok-------------");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

package com.bjtu.edu.nourriture.attention;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SingleRecipeActivity extends Activity{

	DisplayImageOptions options;
	ArrayList<JSONObject> commentList = null;
	JSONObject singleObject = null;
	String singleRecipeId = null;
	String recipeName;
	String authorAccount;
	String authorId;
	String authorHead;
	String collectNum;
	String commentNum;
	String difficult;
	String cookTime;
	String description;
	JSONArray materialArray = null;
	JSONArray stepArray = null;
	String singleData;
	String recipePhoto;
	
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
		singleData = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE);
		singleRecipeId = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID);
		
		SingleRecipeTask task = new SingleRecipeTask();
		task.activity = this;
		task.execute();
	}
	
	public String getCommentList(){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		
		String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(
                    "http://123.57.38.31:3000/service/recipe/listComment?pageNo=1&pageSize=5&recipeId="+singleRecipeId));
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
	
	class SingleRecipeTask extends AsyncTask<Object, Object, Object>{
		SingleRecipeActivity activity;
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		
		@Override
		protected Object doInBackground(Object... arg0) {
			//deal with singleData;
			try {
				singleObject = new JSONObject(singleData);
				System.out.println("-----it's ok-------------");
				recipePhoto = "http://123.57.38.31:3000/"+singleObject.getString("photo").trim();
				authorHead = "http://123.57.38.31:3000"+singleObject.getJSONObject("author").getString("head").trim();
				recipeName = singleObject.getString("recipeName");
				authorAccount = singleObject.getJSONObject("author").getString("account");
				collectNum = singleObject.getString("collectNum");
				commentNum = singleObject.getString("commentNum");
				description = singleObject.getString("description");
				difficult = singleObject.getString("difficult");
				cookTime = singleObject.getString("cookTime");
				materialArray = singleObject.getJSONArray("material");
				stepArray = singleObject.getJSONArray("step");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			//评论信息
			String commentRecult = getCommentList();
			try {
				JSONObject jsonObject = new JSONObject(commentRecult);
				JSONArray jsonArray = jsonObject.getJSONArray("root");
				for(int i=0;i<jsonArray.length();i++){   
	                JSONObject jo = (JSONObject)jsonArray.opt(i);
	                list.add(jo);
	            }
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public void onPostExecute(Object result){
			commentList = list;
			showRecipe();
			showComment();
		}
	}
	
	public void showRecipe(){
		ImageView recipePhotoImageView = (ImageView) findViewById(R.id.singleRecipePhoto);
		ImageView recipeAuthorHeadImageView = (ImageView) findViewById(R.id.singleRecipeAuthorHead);
		TextView recipeNameTextView = (TextView) findViewById(R.id.singleRecipeName);
		TextView singleRecipeAuthor = (TextView) findViewById(R.id.singleRecipeAuthor);
		TextView singleRecipeCollectNum = (TextView) findViewById(R.id.singleRecipeCollectNum);
		TextView singleRecipeCommentNum = (TextView) findViewById(R.id.singleRecipeCommentNum);
		TextView singleRecipeDescription = (TextView) findViewById(R.id.singleRecipeDescription);
		TextView singleRecipeDifficultity = (TextView) findViewById(R.id.singleRecipeDifficultity);
		TextView singleRecipeTime = (TextView) findViewById(R.id.singleRecipeTime);
		TextView singleRecipeComment = (TextView) findViewById(R.id.singleRecipeComment);
		
		ImageLoader.getInstance()
		.displayImage(recipePhoto, recipePhotoImageView, options, new SimpleImageLoadingListener() {
		});

		ImageLoader.getInstance()
		.displayImage(authorHead, recipeAuthorHeadImageView, options, new SimpleImageLoadingListener() {
		});
		
		recipeNameTextView.setText(recipeName);
		singleRecipeAuthor.setText(authorAccount);
		singleRecipeCollectNum.setText(collectNum);
		singleRecipeCommentNum.setText(commentNum);
		singleRecipeDescription.setText(description);
		singleRecipeDifficultity.append("  "+difficult);
		singleRecipeTime.append("  "+cookTime);
		singleRecipeComment.append("("+commentNum+"):");
		
		TableLayout materialTable = (TableLayout) findViewById(R.id.singleMaterialTable);
		materialTable.setStretchAllColumns(true);
		for (int i = 0; i < materialArray.length(); i++) {
			TableRow tablerow = new TableRow(SingleRecipeActivity.this);
			
			TextView materialNameView = new TextView(SingleRecipeActivity.this);
			TextView materialAmountView = new TextView(SingleRecipeActivity.this);
			
			try {
				materialNameView.setText(materialArray.getJSONObject(i).getString("materialName"));
				materialAmountView.setText(materialArray.getJSONObject(i).getString("amount"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			tablerow.addView(materialNameView);
			tablerow.addView(materialAmountView);
			
			materialTable.addView(tablerow);
		}
		
		TableLayout stepTable = (TableLayout) findViewById(R.id.singleStepTable);
		stepTable.setStretchAllColumns(true);
		for (int i = 0; i < stepArray.length(); i++) {
			TableRow tablerow = new TableRow(SingleRecipeActivity.this);
			tablerow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			
			TextView stepNumView = new TextView(SingleRecipeActivity.this);
			TextView stepExplainView = new TextView(SingleRecipeActivity.this);
			ImageView stepPhotoImageView = new ImageView(SingleRecipeActivity.this);
			stepPhotoImageView.setScaleType(ScaleType.CENTER);
			try {
				stepNumView.setText(stepArray.getJSONObject(i).getString("stepNum"));
				stepExplainView.setText(stepArray.getJSONObject(i).getString("stepExplain"));
				ImageLoader.getInstance()
				.displayImage("http://123.57.38.31:3000/"+stepArray.getJSONObject(i).getString("stepPhoto").trim(), stepPhotoImageView, options, new SimpleImageLoadingListener() {
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
			stepNumView.setPadding(5, 0, 0, 0);
			tablerow.addView(stepNumView,10,LayoutParams.WRAP_CONTENT);
			tablerow.addView(stepExplainView,150,LayoutParams.WRAP_CONTENT);
			tablerow.addView(stepPhotoImageView,200,200);
			tablerow.setPadding(0, 0, 0, 5);
			
			stepTable.addView(tablerow);
		}
	}
	
	public void showComment(){
		TableLayout commentTable = (TableLayout) findViewById(R.id.singleCommentTable);
		commentTable.setStretchAllColumns(true);
		if(commentList != null){
			for (int i = 0; i < commentList.size(); i++) {
				TableRow tablerow = new TableRow(SingleRecipeActivity.this);
				tablerow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				
				LinearLayout commentUserLayout = new LinearLayout(SingleRecipeActivity.this);
				LinearLayout commentInfoLayout = new LinearLayout(SingleRecipeActivity.this);
				commentUserLayout.setOrientation(LinearLayout. VERTICAL);
				commentInfoLayout.setOrientation(LinearLayout. VERTICAL);
				
				ImageView commentHeadImageView = new ImageView(SingleRecipeActivity.this);
				TextView commentUserAccount = new TextView(SingleRecipeActivity.this);
				TextView commentTime = new TextView(SingleRecipeActivity.this);
				TextView commentContent = new TextView(SingleRecipeActivity.this);
				
				commentHeadImageView.setScaleType(ScaleType.CENTER);
				try {
					commentUserAccount.setText(commentList.get(i).getJSONObject("author").getString("account"));
					commentTime.setText(commentList.get(i).getString("logTime"));
					commentContent.setText(commentList.get(i).getString("content"));
					ImageLoader.getInstance()
					.displayImage("http://123.57.38.31:3000"+commentList.get(i).getJSONObject("author").getString("head").trim(), commentHeadImageView, options, new SimpleImageLoadingListener() {
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
				commentHeadImageView.setPadding(10, 0, 0, 0);
				commentUserAccount.setPadding(10, 0, 0, 0);
				commentUserAccount.setTextSize(12);
				commentUserLayout.addView(commentHeadImageView,70,60);
				commentUserLayout.addView(commentUserAccount);
				tablerow.addView(commentUserLayout,70,88);
				commentTime.setTextSize(12);
				commentInfoLayout.addView(commentTime);
				commentInfoLayout.addView(commentContent);
				tablerow.addView(commentInfoLayout);
				tablerow.setPadding(0, 0, 0, 5);
				
				commentTable.addView(tablerow);
			}
		}
	}
	
	public void commentIt(View view){
		EditText newCommentEditText = (EditText) findViewById(R.id.singleNewComment);
		String commentString = newCommentEditText.getText().toString();
		if(commentString == null || commentString.trim().equals("")){
			Toast.makeText(getApplicationContext(), "Empty comment",
				     Toast.LENGTH_SHORT).show();
		}
 	}
	
}

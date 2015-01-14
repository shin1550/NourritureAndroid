package com.bjtu.nourriture.recipe;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.bjtu.nourriture.common.Session;
import com.bjtu.nourriture.user.LoginActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SingleRecipeActivity extends Activity{

	DisplayImageOptions options;
	DisplayImageOptions optionRound;
	Session session = Session.getSession();
	Intent intentLogIn = new Intent();
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
		.displayer(new RoundedBitmapDisplayer(0))
		.build();
		optionRound = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(90))
		.build();
		intentLogIn.setClass(SingleRecipeActivity.this, LoginActivity.class);
		
		Intent intent = getIntent();
		singleData = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE);
		singleRecipeId = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID);
		
		SingleRecipeTask task = new SingleRecipeTask();
		task.activity = this;
		task.execute();
	}
	
	public String getCommentList(){
		return RecipeTalkToServer.recipeGet("recipe/listComment?pageNo=1&pageSize=3&recipeId="+singleRecipeId);
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
			showCollect();
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
		.displayImage(authorHead, recipeAuthorHeadImageView, optionRound, new SimpleImageLoadingListener() {
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
		commentTable.removeAllViews();
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
					.displayImage("http://123.57.38.31:3000"+commentList.get(i).getJSONObject("author").getString("head").trim(), commentHeadImageView, optionRound, new SimpleImageLoadingListener() {
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
				commentHeadImageView.setPadding(10, 0, 0, 0);
				commentUserAccount.setPadding(10, 0, 0, 0);
				commentUserAccount.setTextSize(12);
				commentUserLayout.addView(commentHeadImageView,70,60);
				commentUserLayout.addView(commentUserAccount);
				tablerow.addView(commentUserLayout,70,LayoutParams.WRAP_CONTENT);
				commentTime.setTextSize(12);
				commentInfoLayout.addView(commentTime);
				commentInfoLayout.addView(commentContent);
				tablerow.addView(commentInfoLayout);
				tablerow.setPadding(0, 0, 0, 5);
				
				commentTable.addView(tablerow);
			}
		}
	}
	
	public void showCollect(){
		ImageView singleRecipeCollect = (ImageView) findViewById(R.id.singleRecipeCollect);
		String resultString = RecipeTalkToServer.recipeGet("recipe/checkCollect?recipeId="+singleRecipeId+"&"+Constants.POST_RECIPE_USER_ID+"="+(String) session.get("user_id"));
		if(resultString.equals("false")){
			singleRecipeCollect.setImageDrawable(getResources().getDrawable(R.drawable.collect));
			singleRecipeCollect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(session.get("username") == null || session.get("username").equals("")){
						Toast.makeText(getApplicationContext(), "Sign in please",
							     Toast.LENGTH_SHORT).show();
						//intentLogIn = new Intent();
						//intentLogIn.setClass(SingleRecipeActivity.this, LoginActivity.class);
						//intentLogIn = new Intent(getApplicationContext(), LoginActivity.class);
						startActivity(intentLogIn);
					}else{
						List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_COLLECT_RECIPEID, singleRecipeId));
			            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ID, (String) session.get("user_id")));
			            String resultString = RecipeTalkToServer.recipePost("recipe/collect",postParameters);
			            if(resultString.equals("collect success！")){
			            	Toast.makeText(getApplicationContext(), "collect success !",
								     Toast.LENGTH_SHORT).show();
			        		collectNum = String.valueOf(Integer.parseInt(collectNum)+1);
			        		TextView singleRecipeCollectNum = (TextView) findViewById(R.id.singleRecipeCollectNum);
			        		singleRecipeCollectNum.setText(collectNum);
			            }
						showCollect();
					}
				}
	        });
		}else{
			singleRecipeCollect.setImageDrawable(getResources().getDrawable(R.drawable.already_collect));
			singleRecipeCollect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
	            	Toast.makeText(getApplicationContext(), "Already Collected !",
						     Toast.LENGTH_SHORT).show();
				}
	        });
		}
	}
	
	public void commentIt(View view){
		EditText newCommentEditText = (EditText) findViewById(R.id.singleNewComment);
		String commentString = newCommentEditText.getText().toString();
		if(commentString == null || commentString.trim().equals("")){
			Toast.makeText(getApplicationContext(), "Empty comment",
				     Toast.LENGTH_SHORT).show();
		}else{
			
			if(session.get("username") == null || session.get("username").equals("")){
				Toast.makeText(getApplicationContext(), "Sign in please",
					     Toast.LENGTH_SHORT).show();
				//intentLogIn = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intentLogIn);
			}else{
				List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_COMMENT_CONENT, commentString));
	            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_COMMENT_REPLYID, singleRecipeId));
	            
	            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ID, (String) session.get("user_id")));
	            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ACCOUNT, (String) session.get("username")));
	            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_HEAD, (String) session.get("head")));
	            String resultString = RecipeTalkToServer.recipePost("recipe/comment",postParameters);
	            
	            if(resultString.equals("comment success！")){
	            	Toast.makeText(getApplicationContext(), "Comment success !",
						     Toast.LENGTH_SHORT).show();
	            	newCommentEditText.setText("");
	            	RefreshCommentTask task = new RefreshCommentTask();
	        		task.activity = this;
	        		task.execute();
	        		commentNum = String.valueOf(Integer.parseInt(commentNum)+1);
	        		TextView singleRecipeCommentNum = (TextView) findViewById(R.id.singleRecipeCommentNum);
	        		TextView singleRecipeComment = (TextView) findViewById(R.id.singleRecipeComment);
	        		singleRecipeCommentNum.setText(commentNum);
	        		singleRecipeComment.setText("People say("+commentNum+"):");
	            }
			}
		}
 	}
	
	class RefreshCommentTask extends AsyncTask<Object, Object, Object>{
		SingleRecipeActivity activity;
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		
		@Override
		protected Object doInBackground(Object... arg0) {
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
			commentList.clear();
			commentList = list;
			showComment();
		}
		
	}
	
	public void moreComment(View view){
		Intent intent = new Intent(this, SingleRecipeCommentActivity.class);
		intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_NAME, recipeName);
		intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID, singleRecipeId);
		startActivity(intent);
	}
	
	public void collectIt(View view){
		Session session = Session.getSession();
		if(session.get("username") == null || session.get("username").equals("")){
			Toast.makeText(getApplicationContext(), "Sign in please",
				     Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}else{
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_COLLECT_RECIPEID, singleRecipeId));
            
            postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ID, (String) session.get("user_id")));
            String resultString = RecipeTalkToServer.recipePost("recipe/collect",postParameters);
            
            if(resultString.equals("collect success！")){
            	Toast.makeText(getApplicationContext(), "collect success !",
					     Toast.LENGTH_SHORT).show();
        		collectNum = String.valueOf(Integer.parseInt(collectNum)+1);
        		TextView singleRecipeCollectNum = (TextView) findViewById(R.id.singleRecipeCollectNum);
        		singleRecipeCollectNum.setText(collectNum);
            }
		}
 	}
}

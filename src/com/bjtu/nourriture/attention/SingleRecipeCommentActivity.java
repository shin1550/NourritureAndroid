package com.bjtu.nourriture.attention;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.attention.ListAttentionActivity.ListRecipeTask;
import com.bjtu.nourriture.recipe.RecipeTalkToServer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SingleRecipeCommentActivity extends Activity{

	DisplayImageOptions options;
	String recipeName;
	String recipeId;
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	ListCommentAdapter adapter;
	ListView listView;
	int totalNumber;
	int number;
	int pageNo = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_comment_all);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(90))
		.build();
		
		Intent intent = getIntent();
		recipeName = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_NAME);
		recipeId = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID);
		
		//TextView recipeNameTextView = (TextView) findViewById(R.id.singleRecipeCommentName);
		listView = (ListView) findViewById(R.id.singleRecipeCommentList);
		//recipeNameTextView.append(recipeName);
		
		adapter = new ListCommentAdapter(this,list);
		//listView.requestLayout();
		listView.setAdapter(adapter);
		
		/*View loadMoreView;
		loadMoreView = getLayoutInflater().inflate(R.layout.activity_recipe_comment_loadmore, null);  
    	listView.addFooterView(loadMoreView);*/
    	
		SingleCommnetTask task = new SingleCommnetTask();
		task.activity = this;
		task.execute();
	}
	
	class SingleCommnetTask extends AsyncTask<Object, Object, Object>{
		SingleRecipeCommentActivity activity;
		
		@Override
		protected Object doInBackground(Object... arg0) {
			//评论信息
			String commentRecult = getCommentList();
			try {
				JSONObject jsonObject = new JSONObject(commentRecult);
				JSONArray jsonArray = jsonObject.getJSONArray("root");
				String totalString = jsonObject.getString("total");
				totalNumber = Integer.parseInt(totalString);
				number += jsonArray.length();
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
			listView.requestLayout();
			adapter.notifyDataSetChanged();
		}
	}
	
	public String getCommentList(){
		return RecipeTalkToServer.recipeGet("recipe/listComment?pageNo="+pageNo+"&pageSize=10&recipeId="+recipeId);
	}
	
	class ListCommentAdapter extends BaseAdapter{
		ArrayList<JSONObject> list;
		Context context;
		private Activity activity;  
	    private LayoutInflater inflater=null;  
	    public ImageLoader imageLoader;
	    
	    public ListCommentAdapter(Activity a, ArrayList<JSONObject> d){
	    	activity = a;  
	        list=d;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }
	    
		public void setContext(Context context){
			this.context = context;
		}

		public void setArrayList(ArrayList<JSONObject> list){
			this.list = list;
		}
		
		@Override
		public int getCount(){
			return list.size();
		}
		
		@Override
		public JSONObject getItem(int positon){
			JSONObject item = list.get(positon);
			return item;
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View vi=convertView;
			
			JSONObject oneComment = list.get(position);
	        if(convertView==null){
	        	vi = inflater.inflate(R.layout.activity_recipe_comment_all_inner, null);
	        }
	        
	        TextView singleCommentAccount = (TextView)vi.findViewById(R.id.singleCommentAccount); 
	        TextView singleCommentTime = (TextView)vi.findViewById(R.id.singleCommentTime); 
	        TextView singleCommentContent = (TextView)vi.findViewById(R.id.singleCommentContent);
	        ImageView head_image=(ImageView)vi.findViewById(R.id.singleCommentHead); // 缩略图  
	          
	        // 设置ListView的相关值  
	        try {
	        	singleCommentAccount.setText(oneComment.getJSONObject("author").getString("account"));
	        	singleCommentTime.setText(oneComment.getString("logTime"));
	        	singleCommentContent.setText(oneComment.getString("content"));
	        	ImageLoader.getInstance()
				.displayImage("http://123.57.38.31:3000"+list.get(position).getJSONObject("author").getString("head").trim(), head_image, options, new SimpleImageLoadingListener() {
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        return vi;
		}
	}
	
	public void backToRecipe(View view){
		SingleRecipeCommentActivity.this.finish();
	}
	
	public void loadMoreComment(View view){
		if(totalNumber <= number){
			Toast.makeText(getApplicationContext(), "No more",
				     Toast.LENGTH_SHORT).show();
		}else{
			pageNo += 1;
			SingleCommnetTask task = new SingleCommnetTask();
			task.activity = this;
			task.execute();
		}
	}
}

package com.bjtu.nourriture.recipe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
	PullToRefreshListView mPullRefreshListView;
	int pageNo = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		recipeName = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_NAME);
		recipeId = intent.getStringExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID);
		
		setTitle(recipeName);
		
		setContentView(R.layout.activity_recipe_comment_all);
		final ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayUseLogoEnabled(false);
	    actionBar.setDisplayShowHomeEnabled(false);
	    
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(90))
		.build();
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.singleRecipeCommentList);
		
		mPullRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
		mPullRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel(
				"pull to load");
		mPullRefreshListView.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("loading");
		mPullRefreshListView.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("release to load");
		
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(
						getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				pageNo += 1;
				SingleCommnetTask task = new SingleCommnetTask();
				task.execute();
			}
		});
		
		adapter = new ListCommentAdapter(this,list);
		mPullRefreshListView.setVisibility(View.GONE);
		mPullRefreshListView.requestLayout();
		mPullRefreshListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mPullRefreshListView.setVisibility(View.VISIBLE);
		
		SingleCommnetTask task = new SingleCommnetTask();
		task.activity = this;
		task.execute();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	SingleRecipeCommentActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	class SingleCommnetTask extends AsyncTask<Object, Object, String>{
		ArrayList<JSONObject> tempList = new ArrayList<JSONObject>();
		SingleRecipeCommentActivity activity;
		
		@Override
		protected String doInBackground(Object... arg0) {
			//评论信息
			String commentRecult = getCommentList();
			try {
				JSONObject jsonObject = new JSONObject(commentRecult);
				JSONArray jsonArray = jsonObject.getJSONArray("root");
				if(jsonArray.length() == 0){
					return "No";
				}else{
					for(int i=0;i<jsonArray.length();i++){   
		                JSONObject jo = (JSONObject)jsonArray.opt(i);
		                tempList.add(jo);
		            }
					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public void onPostExecute(String result){
			if(result != null && result.equals("No")){
				Toast.makeText(getApplicationContext(), "No more",
					     Toast.LENGTH_SHORT).show();
				mPullRefreshListView.requestLayout();
				adapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				super.onPostExecute(null);
			}else{
				list.addAll(tempList);
				mPullRefreshListView.setVisibility(View.GONE);
				mPullRefreshListView.requestLayout();
				adapter.notifyDataSetChanged();
				mPullRefreshListView.setVisibility(View.VISIBLE);
				
				adapter.notifyDataSetInvalidated();
				mPullRefreshListView.onRefreshComplete();
				super.onPostExecute(null);
			}
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
	
}

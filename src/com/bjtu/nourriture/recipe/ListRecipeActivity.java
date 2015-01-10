package com.bjtu.nourriture.recipe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ListRecipeActivity extends Activity implements AdapterView.OnItemClickListener{

	DisplayImageOptions options;
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	ListRecipeAdapter adapter;
	ListView listview;
	int pageNo = 1;
	int totalNumber;
	int number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())  
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()  
        .discCacheFileNameGenerator(new Md5FileNameGenerator())  
        .tasksProcessingOrder(QueueProcessingType.LIFO)  
        .build();  
    	ImageLoader.getInstance().init(config);
    	
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		setContentView(R.layout.activity_recipe_list_all);
		listview=(ListView) findViewById(R.id.listView1);
    	
    	adapter = new ListRecipeAdapter(this,list);
    	listview.requestLayout();
    	listview.setAdapter(adapter);
    	//adapter.notifyDataSetChanged();
    	
    	View loadMoreView;
		loadMoreView = getLayoutInflater().inflate(R.layout.activity_recipe_comment_loadmore, null);  
    	listview.addFooterView(loadMoreView);
    	
    	ListRecipeTask task = new ListRecipeTask();
		task.listView = listview;
		task.activity = this;
		task.execute();
		
		EditText searchEditText = (EditText) findViewById(R.id.listRecipeSearch);
		searchEditText.setOnKeyListener(new OnKeyListener() {  
            @Override  
            public boolean onKey(View v, int keyCode, KeyEvent event) {  
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {  
                    searchRecipe1();
                    return true;  
                }  
                return false;  
  
            }  
        });
		listview.setOnItemClickListener(this);
	}
	
	public String getRecipeList(){
		return RecipeTalkToServer.recipeGet("recipe/listAll?pageNo="+pageNo+"&pageSize=10");
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id){
		System.out.println("----------click--------------");
		System.out.println(list.get(pos).toString());
		
		list.get(pos);
		//ImageLoaderConfiguration.;
		Intent intent = new Intent(this, SingleRecipeActivity.class);
		
		try {
			intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID, list.get(pos).getString("_id"));
			intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE, list.get(pos).toString());
			startActivity(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class ListRecipeAdapter extends BaseAdapter{
		ArrayList<JSONObject> list;
		Context context;
		private Activity activity;  
	    private LayoutInflater inflater=null;  
	    public ImageLoader imageLoader;
	    
	    public ListRecipeAdapter(Activity a, ArrayList<JSONObject> d){
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
			
			JSONObject oneRecipe = list.get(position);
	        if(convertView==null){
	        	vi = inflater.inflate(R.layout.activity_recipe_list_all_inner, null);
	        	
	        }
	  
	        TextView name = (TextView)vi.findViewById(R.id.recipe_name); 
	        TextView material = (TextView)vi.findViewById(R.id.recipe_material); 
	        TextView hot = (TextView)vi.findViewById(R.id.recipe_hot);
	        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // 缩略图  
	          
	        // 设置ListView的相关值  
	        name.setText("recipe name here");
	        try {
	        	name.setText(oneRecipe.getString("recipeName"));
	        	String mString = "";
	        	JSONArray mArray = oneRecipe.getJSONArray("material");
	        	for(int i=0;i<mArray.length();i++){
	                JSONObject jo = (JSONObject)mArray.opt(i);
	                mString +=jo.getString("materialName")+"  ";
	            }
				material.setText(mString);
				hot.setText("Collect Num : " +oneRecipe.getString("collectNum")+"    Comment Num : "+list.get(position).getString("commentNum"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        
	        try {
				ImageLoader.getInstance()
				.displayImage("http://123.57.38.31:3000/"+list.get(position).getString("photo").trim(), thumb_image, options, new SimpleImageLoadingListener() {
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return vi;
		}
	}
	
	class ListRecipeTask extends AsyncTask<Object, Object, Object>{
		ListView listView;
		ListRecipeActivity activity;
		String search;
		String recipeRecult;
		
		@Override
		protected Object doInBackground(Object... arg0) {
			if(search == null){
				recipeRecult = getRecipeList();
			}else{
				recipeRecult = getSearchRecipeList(search);
			}
			
			try {
				JSONObject jsonObject = new JSONObject(recipeRecult);
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
			//adapter.notifyDataSetChanged();
			return null;
		}
		
		@Override
		public void onPostExecute(Object result){
			listview.requestLayout();
			adapter.notifyDataSetChanged();
		}
	}
	
	public void loadMore(View view){
		if(totalNumber <= number){
			Toast.makeText(getApplicationContext(), "No more",
				     Toast.LENGTH_SHORT).show();
		}else{
			pageNo += 1;
			ListView listview=(ListView) findViewById(R.id.listView1);
			ListRecipeTask task = new ListRecipeTask();
			task.listView = listview;
			task.activity = this;
			task.execute();
		}
	}
	
	public void searchRecipe(View view){
		searchRecipe1();
	}
	
	public void searchRecipe1(){
		EditText searchEditText = (EditText) findViewById(R.id.listRecipeSearch);
		String searchString = searchEditText.getText().toString();
		 
		if(searchString == null || searchString.trim().equals("")){
			/*Toast.makeText(getApplicationContext(), "Please enter something",
				     Toast.LENGTH_SHORT).show();*/
			pageNo = 1;
			list.clear();
			ListView listview=(ListView) findViewById(R.id.listView1);
			ListRecipeTask task = new ListRecipeTask();
			task.listView = listview;
			task.activity = this;
			task.search = null;
			task.execute();
		}else{
			pageNo = 1;
			list.clear();
			ListView listview=(ListView) findViewById(R.id.listView1);
			ListRecipeTask task = new ListRecipeTask();
			task.listView = listview;
			task.activity = this;
			task.search = searchString;
			task.execute();
		}
	}
	
	public String getSearchRecipeList(String search){
		return RecipeTalkToServer.recipeGet("recipe/search?pageNo="+pageNo+"&pageSize=10&queryStr="+search);
		
	}
	
}

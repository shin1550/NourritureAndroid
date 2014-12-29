package com.bjtu.nourriture.recipe;

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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
		ListRecipeTask task = new ListRecipeTask();
		ListView listview=(ListView) findViewById(R.id.listView1);
		
		task.listView = listview;
		task.activity = this;
		task.list = list;
		task.execute();
		
		listview.setOnItemClickListener(this);
	}
	
	public String getRecipeList(){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		
		String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(
                    "http://123.57.38.31:3000/service/recipe/listAll?pageNo=1&pageSize=10"));
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
	
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id){
		System.out.println("----------click--------------");
		System.out.println(list.get(pos).toString());
		
		list.get(pos);
		Intent intent = new Intent(this, SingleRecipeActivity.class);
		
		intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE, list.get(pos).toString());
		startActivity(intent);
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
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.activity_recipe_list_all_inner, null);
	  
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
		ArrayList<JSONObject> list;
		
		@Override
		protected Object doInBackground(Object... arg0) {
			String recipeRecult = getRecipeList();
			try {
				JSONObject jsonObject = new JSONObject(recipeRecult);
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
		
		public void onPostExecute(Object result){
			ListRecipeAdapter adapter = new ListRecipeAdapter(activity,list);
	        listView.setAdapter(adapter);
		}
	}
	
}

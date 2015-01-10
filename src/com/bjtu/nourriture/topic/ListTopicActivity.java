package com.bjtu.nourriture.topic;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class ListTopicActivity extends Activity{
	
	DisplayImageOptions options;
	ConnectToServer connect=new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	String topicListResult;
	
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
		
		setContentView(R.layout.activity_topic_list_all);

		ListView listview=(ListView) findViewById(R.id.topic_list_view);
		
		try {
			String url="topic/showTopicList?pageNo=1&pageSize=6";
			String method ="GET";
			topicListResult = connect.testURLConn(url,method);
			
			JSONObject jsonObject = new JSONObject(topicListResult);
			JSONArray jsonArray = jsonObject.getJSONArray("topics");
			for(int i=0;i<jsonArray.length();i++){   
                JSONObject jo = (JSONObject)jsonArray.opt(i);
                list.add(jo);
            }
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TopicListViewAdapter adapter = new TopicListViewAdapter(this,list);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListenerImpl());
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	private class OnItemClickListenerImpl implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?>parent, View view, int position,
				long id) {
			
			System.out.println("----------click--------------");
			System.out.println(list.get(position).toString());
			
			list.get(position);
			Intent intent = new Intent();    
            intent.setClass(ListTopicActivity.this, TopicDetailActivity.class); 		
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL,list.get(position).toString() );
			startActivity(intent);
		
			
			
		}
		
	}
	
	class TopicListViewAdapter extends BaseAdapter {
		
		ArrayList<JSONObject> list;
		DisplayImageOptions options;
		Context context;
		private Activity activity;  
	    //private ArrayList<HashMap<String, String>> data;  
	    private LayoutInflater inflater=null;  
	    public ImageLoader imageLoader;
	   
	    
	    public TopicListViewAdapter(Activity a, ArrayList<JSONObject> d){
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
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.activity_topic_list_all_inner, null);
	  
	        ImageView imageView =(ImageView)vi.findViewById(R.id.list_image);
	        
	        TextView name = (TextView)vi.findViewById(R.id.topic_name); 
	        TextView short_content = (TextView)vi.findViewById(R.id.topic_short_content); 
	        TextView author_and_hot = (TextView)vi.findViewById(R.id.topic_upload);
	        // 缩略图  
	          
	        //HashMap<String, String> song = new HashMap<String, String>();  
	        //item = list.get(position);
	          
	        // 设置ListView的相关值   
	        try {
	        	name.setText("#"+list.get(position).getString("topicName")+"#");
	        	String topic_content = list.get(position).getString("content");
	        	if(topic_content.length()>105){//如果长度大于100则截取
	        		topic_content = topic_content.substring(0, 35);
	        	}
	        	 try {
	 				ImageLoader.getInstance()
	 				.displayImage("http://123.57.38.31:3000/"+list.get(position).getString("picture").trim(), imageView, options, new SimpleImageLoadingListener() {
	 				});
	 			} catch (JSONException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 			}
	        	
	        	short_content.setText(topic_content+"...");
	        	author_and_hot.setText("Upload Count : "+list.get(position).getString("upload_count"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        //hot.setText(song.get(CustomizedListView.KEY_DURATION));
	        
	      
	        return vi;
		}
	}
	

}

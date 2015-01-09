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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.bjtu.nourriture.ConnectToServer;
import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class TopicDetailActivity extends Activity {
	
	DisplayImageOptions options;
	ConnectToServer connect=new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	String uploadListResult;
	GridView gridView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_detail);
		
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
		
		Intent intent = getIntent();
		String singleData = intent.getStringExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL);
		JSONObject singleObject = null;
		
		try {
			singleObject = new JSONObject(singleData);
			System.out.println("-----it's ok-------------");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		try {
			TextView name = (TextView)findViewById(R.id.name);
			name.setText("#"+singleObject.getString("topicName")+"#");
			TextView content = (TextView)findViewById(R.id.content);
			content.setText(singleObject.getString("content"));
			TextView upload = (TextView)findViewById(R.id.uploadcount);
			upload.setText("Upload count number : " +singleObject.getString("upload_count"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			String url="topic/getUploadToATopic?pageNo=1&pageSize=10&topic_id="+singleObject.getString("_id");
			String method ="GET";
			uploadListResult = connect.testURLConn(url,method);
			
			JSONObject jsonObject = new JSONObject(uploadListResult);
			JSONArray jsonArray = jsonObject.getJSONArray("uploads");
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
		
		
		TopicDetailListViewAdapter adapter = new TopicDetailListViewAdapter(this,list);
		gridView = (GridView)findViewById(R.id.gridView1);
		gridView.setAdapter(adapter);
		
		gridView.setOnItemClickListener(new OnItemClickListenerImpl());
		
	}
	
	public void addLike(View view){
		
	}
	
	private class OnItemClickListenerImpl implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?>parent, View view, int position,
				long id) {
			
			System.out.println("----------click--------------");

			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.topic_detail, menu);
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
	
	class TopicDetailListViewAdapter extends BaseAdapter{
		
		ArrayList<JSONObject> list;
		DisplayImageOptions options;
		Context context;
		private Activity activity;  
	    //private ArrayList<HashMap<String, String>> data;  
	    private LayoutInflater inflater=null;  
	    public ImageLoader imageLoader;
	   
	    
	    public TopicDetailListViewAdapter(Activity a, ArrayList<JSONObject> d){
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
	            vi = inflater.inflate(R.layout.activity_topic_detail_inner, null);
	  
	        ImageView picture =(ImageView)vi.findViewById(R.id.picture);
	        
	        TextView name = (TextView)vi.findViewById(R.id.upload_name); 
	        TextView author = (TextView)vi.findViewById(R.id.author); 
	        TextView like_count = (TextView)vi.findViewById(R.id.likeCount); 
	       
	          
	        // 设置ListView的相关值   
	        try {
	        	ImageLoader.getInstance()
		 		.displayImage("http://123.57.38.31:3000/"+list.get(position).getString("picture").trim(), picture, options, new SimpleImageLoadingListener() {
		 		});
		 	
	        	 
	        	name.setText(list.get(position).getString("title"));
	        	String authorname="";
	        	if(list.get(position).getJSONObject("author").getString("account")==null){
	        		authorname="travel";
	        	}else{
	        		authorname = list.get(position).getJSONObject("author").getString("account");
	        	}

	        	author.setText("by "+authorname);
	        	like_count.setText(list.get(position).getString("like_count"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  

	        
	      
	        return vi;
		}
		
	}
}

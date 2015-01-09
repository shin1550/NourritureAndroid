package com.bjtu.nourriture.topic;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjtu.nourriture.GetImage;
import com.bjtu.nourriture.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class TopicListViewAdapter1 extends BaseAdapter {
	
	ArrayList<JSONObject> list;
	DisplayImageOptions options;
	Context context;
	private Activity activity;  
    //private ArrayList<HashMap<String, String>> data;  
    private LayoutInflater inflater=null;  
    public ImageLoader imageLoader;
   
    
    public TopicListViewAdapter1(Activity a, ArrayList<JSONObject> d){
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
        	System.out.println(list.get(position).getString("picture"));
        	setViewImage(imageView, list.get(position).getString("picture"));
        	author_and_hot.setText("Upload Count : "+list.get(position).getString("upload_count"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        //hot.setText(song.get(CustomizedListView.KEY_DURATION));
        
      
        return vi;
	}
	
public void setViewImage(ImageView v, String value) {
		
		System.out.println("value---"+value);
		if(value.equals("null")){
			  v.setImageResource(R.drawable.ic_launcher);
			
		}else{
			 Bitmap bitmap = GetImage.getBitmapFromServer(value);
			  v.setImageBitmap(bitmap);
		}
	 
	 }

}

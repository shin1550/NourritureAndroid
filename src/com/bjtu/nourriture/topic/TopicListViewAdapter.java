package com.bjtu.nourriture.topic;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bjtu.nourriture.R;


public class TopicListViewAdapter extends BaseAdapter {
	
	ArrayList<JSONObject> list;
	Context context;
	private Activity activity;  
    //private ArrayList<HashMap<String, String>> data;  
    private LayoutInflater inflater=null;  
   
    
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
  
        TextView name = (TextView)vi.findViewById(R.id.topic_name); 
        TextView short_content = (TextView)vi.findViewById(R.id.topic_short_content); 
        TextView author_and_hot = (TextView)vi.findViewById(R.id.topic_upload);
        // 缩略图  
          
        //HashMap<String, String> song = new HashMap<String, String>();  
        //item = list.get(position);
          
        // 设置ListView的相关值  
        name.setText("recipe name here");
        try {
        	name.setText("#"+list.get(position).getString("topicName")+"#");
        	String topic_content = list.get(position).getString("content");
        	if(topic_content.length()>105){//如果长度大于100则截取
        		topic_content = topic_content.substring(0, 100);
        	}
        	
        	short_content.setText(topic_content+"...");
        	author_and_hot.setText(" Upload Count : "+list.get(position).getString("upload_count"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        //hot.setText(song.get(CustomizedListView.KEY_DURATION));
        
      
        return vi;
	}
	
/*public void setViewImage(ImageView v, String value) {
		
		System.out.println("value---"+value);
		if(value.equals("null")){
			  v.setImageResource(R.drawable.ic_launcher);
			
		}else{
			 Bitmap bitmap = GetImage.getBitmapFromServer(value);
			  v.setImageBitmap(bitmap);
		}
	 
	 }
	*/

}

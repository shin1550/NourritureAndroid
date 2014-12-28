package com.bjtu.nourriture.topic;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
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
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class TopicListViewAdapter extends BaseAdapter {
	
	ArrayList<JSONObject> list;
	Context context;
	private Activity activity;  
    //private ArrayList<HashMap<String, String>> data;  
    private LayoutInflater inflater=null;  
    public ImageLoader imageLoader;
    
    public TopicListViewAdapter(Activity a, ArrayList<JSONObject> d){
    	activity = a;  
        list=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader();
        //imageLoader=new ImageLoader(activity.getApplicationContext());
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
  
        TextView name = (TextView)vi.findViewById(R.id.recipe_name); 
        TextView material = (TextView)vi.findViewById(R.id.recipe_material); 
        TextView hot = (TextView)vi.findViewById(R.id.recipe_hot);
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // 缩略图  
          
        //HashMap<String, String> song = new HashMap<String, String>();  
        //item = list.get(position);
          
        // 设置ListView的相关值  
        name.setText("recipe name here");
        try {
        	name.setText(list.get(position).getString("recipeName"));
        	String mString = "";
        	JSONArray mArray = list.get(position).getJSONArray("material");
        	for(int i=0;i<mArray.length();i++){
                JSONObject jo = (JSONObject)mArray.opt(i);
                mString +=jo.getString("materialName")+"  ";
            }
			material.setText(mString);
			hot.setText("Collect Num : " + list.get(position).getString("collectNum")+"    Comment Num : "+list.get(position).getString("commentNum"));
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

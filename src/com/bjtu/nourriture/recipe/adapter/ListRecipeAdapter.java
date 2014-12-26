package com.bjtu.nourriture.recipe.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.utils.ImageLoader;

public class ListRecipeAdapter extends BaseAdapter{

	ArrayList<String> list;
	Context context;
	private Activity activity;  
    //private ArrayList<HashMap<String, String>> data;  
    private static LayoutInflater inflater=null;  
    public ImageLoader imageLoader;
    
    public ListRecipeAdapter(Activity a, ArrayList<String> d){
    	activity = a;  
        list=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        //imageLoader=new ImageLoader(activity.getApplicationContext());
    }
    
	public void setContext(Context context){
		this.context = context;
	}

	public void setArrayList(ArrayList<String> list){
		this.list = list;
	}
	
	public int getCount(){
		return list.size();
	}
	
	public String getItem(int positon){
		String item = list.get(positon);
		return item;
	}
	
	public long getItemId(int position){
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		/*TextView textView = new TextView(context);
		textView.setTextSize(1,30);
		String string = getItem(position);
		textView.setText(string);
		return textView;*/
		
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_recipe_list_all_inner, null);
        	//vi = R.layout.activity_main;
  
        TextView name = (TextView)vi.findViewById(R.id.recipe_name); 
        TextView material = (TextView)vi.findViewById(R.id.recipe_material); 
        TextView hot = (TextView)vi.findViewById(R.id.recipe_hot);
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // 缩略图  
          
        //HashMap<String, String> song = new HashMap<String, String>();  
        //item = list.get(position);
          
        // 设置ListView的相关值  
        name.setText("recipe name here");  
        material.setText(list.get(position));  
        /*hot.setText(song.get(CustomizedListView.KEY_DURATION));
        imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);  */
        return vi;
	}
}

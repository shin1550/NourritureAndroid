package com.bjtu.nourriture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieAdapter extends BaseAdapter {

	private List<Map<String,Object>> list;
	private Context context;
	public MovieAdapter(Context context){
		this.context=context;
		this.list=new ArrayList<Map<String,Object>>();
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Map<String,Object> getItem(int location) {
		return list.get(location);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void addObject(Map<String,Object> map){
		list.add(map);
		notifyDataSetChanged();
	}
	@Override
	public View getView(int location, View arg1, ViewGroup arg2) {
		View view = LayoutInflater.from(context).inflate(R.layout.movie,null);
		ImageView image=(ImageView)view.findViewById(R.id.movie_image);
		TextView text=(TextView)view.findViewById(R.id.movie_text);
		Map<String,Object> map=getItem(location);
		image.setBackgroundDrawable((Drawable)map.get("image"));
		text.setText(map.get("text").toString());
		return view;
	}

}

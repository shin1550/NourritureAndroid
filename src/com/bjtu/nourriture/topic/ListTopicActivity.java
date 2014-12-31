package com.bjtu.nourriture.topic;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import com.bjtu.nourriture.ConnectToServer;
import com.bjtu.nourriture.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ListTopicActivity extends Activity{
	
	DisplayImageOptions options;
	ConnectToServer connect=new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	String topicListResult;
	TopicListViewAdapter topicListViewAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
//		ListTopicTask task = new ListTopicTask();
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
		
//		task.listView = listview;
//		task.activity = this;
//		task.list = list;
//		task.execute();
		
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

			
			
		}
		
	}
	

}

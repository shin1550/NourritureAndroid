package com.bjtu.nourriture.topic;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.R.id;
import com.bjtu.nourriture.R.layout;
import com.bjtu.nourriture.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PublishTopicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pulish_topic);
		
		EditText titleEditText = (EditText) findViewById(R.id.title);
		EditText contentEditText = (EditText) findViewById(R.id.topic_content);		
		ImageView picImageView = (ImageView) findViewById(R.id.my_topic_picture);
		
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pulish_picture);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_topic, menu);
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
}

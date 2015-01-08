package com.bjtu.nourriture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bjtu.nourriture.attention.ListAttentionActivity;
import com.bjtu.nourriture.recipe.ListRecipeActivity;
import com.bjtu.nourriture.topic.ListTopicActivity;
//hihihi
//da ping guo
public class MainActivity extends Activity {

	private SlidingMenu mMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
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
	
	public void toListRecipe(View view){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,ListRecipeActivity.class);
		startActivity(intent);
	}
	public void toListTopic(View view){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,ListTopicActivity.class);
		startActivity(intent);
	}
	public void toListAttention(View view){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, ListAttentionActivity.class);
		startActivity(intent);
	}
	
	public void toggleMenu(View view)
	{
		mMenu.toggle();
	}
	
}

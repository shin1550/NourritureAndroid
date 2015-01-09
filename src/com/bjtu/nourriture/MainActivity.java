package com.bjtu.nourriture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bjtu.nourriture.common.Session;
import com.bjtu.nourriture.recipe.ListRecipeActivity;
import com.bjtu.nourriture.topic.ListTopicActivity;
import com.bjtu.nourriture.user.LoginActivity;

public class MainActivity extends Activity {

	private SlidingMenu mMenu;
	private View layout_1,layout_1_1;
	private TextView menu_1_1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout_1 = this.findViewById(R.id.layout_1);
		layout_1_1 = this.findViewById(R.id.layout_1_1);
		menu_1_1 = (TextView)this.findViewById(R.id.menu_1_1);
		//逻辑还存在问题，待修改
		Session session = Session.getSession();
		Boolean islogin=(Boolean)session.get("islogin");
		System.out.println("islogin----"+islogin);
		if(islogin!=null){
			if(islogin){
				layout_1.setVisibility(View.GONE);
				layout_1_1.setVisibility(View.VISIBLE);
				String username = (String) session.get("username");
				String head = (String) session.get("head");
				menu_1_1.setText(username);
			}
		}
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
	
	public void toMenu1(View view){
		System.out.println("1");
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,LoginActivity.class);
		startActivity(intent);
	}
	
	public void toggleMenu(View view)
	{
		mMenu.toggle();
	}
	
}

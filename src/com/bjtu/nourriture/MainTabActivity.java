package com.bjtu.nourriture;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;

import com.baidu.mobstat.StatService;
import com.bjtu.nourriture.common.Session;
import com.bjtu.nourriture.recipe.ListRecipeActivity;
import com.bjtu.nourriture.topic.ListTopicActivity;
import com.bjtu.nourriture.user.LoginActivity;
import com.bjtu.nourriture.user.SettingActivity;

public class MainTabActivity extends TabActivity implements OnCheckedChangeListener{

	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private Intent mDIntent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maintabs);
        
        this.mAIntent = new Intent(this,MainActivity.class);
        this.mBIntent = new Intent(this,ListRecipeActivity.class);
        this.mCIntent = new Intent(this,ListTopicActivity.class);
        this.mDIntent = new Intent(this,LoginActivity.class);
        
        Session session = Session.getSession();
		Boolean islogin=(Boolean)session.get("islogin");
		System.out.println("IsLogin:"+islogin);
		if(islogin!=null){
			if(islogin){
				this.mDIntent = new Intent(this,SettingActivity.class);
			}
		}
		((RadioButton) findViewById(R.id.radio_button0))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button1))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button2))
		.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button3))
		.setOnCheckedChangeListener(this);
        
        setupIntent();
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				this.mTabHost.setCurrentTabByTag("A_TAB");
				break;
			case R.id.radio_button1:
				this.mTabHost.setCurrentTabByTag("B_TAB");
				break;
			case R.id.radio_button2:
				this.mTabHost.setCurrentTabByTag("C_TAB");
				break;
			case R.id.radio_button3:
				this.mTabHost.setCurrentTabByTag("D_TAB");
				break;
			}
		}
		
	}
	
	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;
		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_home,
				R.drawable.icon_1_n, this.mAIntent));
		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_recipe,
				R.drawable.icon_2_n, this.mBIntent));
		localTabHost.addTab(buildTabSpec("C_TAB",R.string.main_topic, 
				R.drawable.icon_3_n,this.mCIntent));
		localTabHost.addTab(buildTabSpec("D_TAB",R.string.main_publish, 
				R.drawable.icon_4_n,this.mDIntent));
	}
	private void setupIntent2() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;
		this.mDIntent=  new Intent(this,SettingActivity.class);
		localTabHost.addTab(buildTabSpec("D_TAB",R.string.main_publish, 
				R.drawable.icon_4_n,this.mDIntent));
	}
	
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}
	public void onResume() {
		super.onResume();
		System.out.println("MainActivityTab");
		Session session = Session.getSession();
		Boolean islogin=(Boolean)session.get("islogin");
		System.out.println("IsLogin:"+islogin);
		if(islogin!=null){
			if(islogin){
				setupIntent2();
			}
		}
		
		StatService.onResume(this);
	}

}

package com.bjtu.nourriture.user;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.CheckHttpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MoreActivity extends Activity{

	Button backButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		CheckHttpUtil.initIntener(this);
		backButton = (Button)(Button) findViewById(R.id.more_back_btn);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
	}
}

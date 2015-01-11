package com.bjtu.nourriture.user;

import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.CheckHttpUtil;
import com.bjtu.nourriture.common.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MoreActivity extends Activity{

	Button backButton;
	TextView exit,personal_center;
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
		exit = (TextView)this.findViewById(R.id.exits);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialog dialog = new AlertDialog.Builder(MoreActivity.this)
				.setTitle("Logout")
				.setMessage("Verify to log out?")
				.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Session session = Session.getSession();
						session.cleanUpSession();
						Toast.makeText(MoreActivity.this,"Logout Successful!", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(MoreActivity.this,MainActivity.class);
						 MoreActivity.this.startActivity(intent);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
			dialog.show();
			}
		});
		personal_center = (TextView)this.findViewById(R.id.personal_center);
		personal_center.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(MoreActivity.this,PersonalCenterActivity.class);
				 MoreActivity.this.startActivity(intent);
			}
		});
	}
}

package com.bjtu.nourriture.user;

import org.apache.commons.lang.BitField;

import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.MainTabActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity{
	String usernameString,headString,headString2;
	Handler handler;
	ImageView headImage;
	TextView usernameTextView;
	Bitmap bitmap;
	RelativeLayout logoutLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		Session session =Session.getSession();
		usernameString=(String)session.get("username");
		headString=(String)session.get("head");
		handler=new Handler();
		
		usernameTextView = (TextView)this.findViewById(R.id.setting_username_textview);
		usernameTextView.setText(usernameString);
		headImage = (ImageView)this.findViewById(R.id.setting_head_view);
		headString2 = "http://123.57.38.31:3000"+(String) session.get("head");
		System.out.println("headString2:"+headString2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								bitmap = ImageUtil.getHttpBitmap(headString2);
								headImage .setImageBitmap(bitmap);	//设置Bitmap
							}
						});
					}
				});
			}
		}).start();
		
		logoutLayout = (RelativeLayout)this.findViewById(R.id.setting_logout_layout);
		logoutLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialog dialog = new AlertDialog.Builder(SettingActivity.this)
				.setTitle("Logout")
				.setMessage("Verify to log out?")
				.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Session session = Session.getSession();
						session.cleanUpSession(); 
						Toast.makeText(SettingActivity.this,"Logout Successful!", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(SettingActivity.this,MainTabActivity.class);
						 SettingActivity.this.startActivity(intent);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
			dialog.show();
			}
		});
	}
}

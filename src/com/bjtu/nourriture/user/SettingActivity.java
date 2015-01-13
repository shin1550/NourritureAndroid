package com.bjtu.nourriture.user;

import org.apache.commons.lang.BitField;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Session;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends Activity{
	String usernameString,headString,headString2;
	Handler handler;
	ImageView headImage;
	TextView usernameTextView;
	Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		Session session =Session.getSession();
		usernameString=(String)session.get("username");
		headString=(String)session.get("head");
		handler=new Handler();

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


		usernameTextView = (TextView)this.findViewById(R.id.setting_username_textview);
		usernameTextView.setText(usernameString);
		
	}
}

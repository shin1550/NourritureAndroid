package com.bjtu.nourriture.user;

import org.json.JSONObject;

import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Session;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	String url;
	String result;
	EditText account;
	EditText email;
	EditText password;
	Button registerButton;
	Button backButton;
	CheckBox aggre;
	ConnectToServer connect;
	ProgressDialog proDia;
	Handler handler;
	String accounts ,emailString,pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		connect = new ConnectToServer();
		account = (EditText) this.findViewById(R.id.register_user_edit);
		email = (EditText) this.findViewById(R.id.register_nickname_edit);
		password = (EditText) this.findViewById(R.id.register_passwd_edit);
		aggre = (CheckBox) this.findViewById(R.id.aggre);
		backButton = (Button) this.findViewById(R.id.login_reback_btn);
		handler = new Handler();
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		registerButton = (Button) this.findViewById(R.id.login_login_btn);
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				accounts = account.getText().toString();
				emailString = email.getText().toString();
				pass = password.getText().toString();
				System.out.println("account：" + accounts+"-------email：" + emailString+"-------password:" + pass);
				// 非空验证
				if ("".equals(accounts.trim()) || accounts.trim() == null) {
					Toast.makeText(RegisterActivity.this, "账号不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				if ("".equals(emailString.trim()) || emailString.toString().trim() == null) {
					Toast.makeText(RegisterActivity.this, "邮箱不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				if ("".equals(pass.trim()) || pass.trim() == null) {
					Toast.makeText(RegisterActivity.this, "密码不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (aggre.isChecked() == false) {
					Toast.makeText(RegisterActivity.this, "请选中协议",
							Toast.LENGTH_LONG).show();
					return;
				}

				proDia = ProgressDialog.show(RegisterActivity.this, "注册","正在注册，请耐心等待");
				proDia.show();
				new Thread() {
					@Override
					public void run() {
						try {
							registerConnection(accounts, emailString, pass);
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									proDia.dismiss();
									Toast.makeText(RegisterActivity.this, "注册成功",Toast.LENGTH_SHORT).show();
								}
							});
							
						} catch (Exception e) {
						} finally {
							Intent intent = new Intent();
							intent.setClass(RegisterActivity.this,MainActivity.class);
							startActivity(intent);
						}

					}
				}.start();
			}
	});
	}
	
	public void registerConnection(String username,String email,String password) throws Exception{
		url = "service/userinfo/register";
		StringBuffer paramsBuffer = new StringBuffer();
		paramsBuffer.append("username").append("=").append(username).append("&")
								.append("password").append("=").append(password).append("&")
								.append("email").append("=").append(email);
		byte[] bytes = paramsBuffer.toString().getBytes();
		result = connect.testURLConn2(url,bytes);
		JSONObject jsonObj = new JSONObject(result);
		String message = jsonObj.getString("message");
		System.out.println("RegisterMessage---" + message);
		
		
		Session session=Session.getSession();
		session.put("username", username);
		session.put("islogin", true);
		
	}
}
	


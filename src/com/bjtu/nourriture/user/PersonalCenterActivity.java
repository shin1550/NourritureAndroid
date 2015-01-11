package com.bjtu.nourriture.user;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Session;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalCenterActivity extends Activity {

	ImageView headImage;
	TextView username;
	ImageView sex;
	TextView province;
	TextView birthdate;
	String infoResult;
	private ListView dataList;
	ArrayList<JSONObject> recipes = new ArrayList<JSONObject>();
	ConnectToServer connect;
	String url;
	String serverResult;
	ImageButton leavemessage;
	Button reback;
	String usernameString,emailString,headString;
	String head;
	Bitmap bitmap;
	Handler handler;
	String connectionMessage,recipesString;
	JSONObject messageJsonObject;

	private List<Map<String,String>> list=new ArrayList<Map<String,String>>();

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_center);

		connect=new ConnectToServer();
		Session session =Session.getSession();
		usernameString=(String)session.get("username");
		emailString=(String)session.get("emal");
		headString=(String)session.get("head");
		handler=new Handler();

		headImage = (ImageView)this.findViewById(R.id.peronalcenter_head);
		head = "http://123.57.38.31:3000/"+(String) session.get("head");
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								bitmap = getHttpBitmap(head);
								headImage .setImageBitmap(bitmap);	//设置Bitmap
							}
						});
					}
				});
			}
		}).start();
		

		username = (TextView)this.findViewById(R.id.personalCenterName);
		username.setText(usernameString);

		province = (TextView)this.findViewById(R.id.personalCenterCity);
		province.setText(emailString);

		dataList=(ListView)findViewById(R.id.datalist);
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							getRecipesConnection();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}).start();
		
	

		JSONArray postList;
//		try {
//			postList = messageJsonObject.getJSONArray("root");
//			System.out.println("postList:"+postList);
//			String newContent;
//			for(int i=0;i<postList.length();i++){
//				JSONObject info = (JSONObject)postList.get(i);   //单个recipe
//				System.out.println("recipe("+i+"):"+info);
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  //取出recipes串

		//			Map<String,String> map=new HashMap<String,String>();
		//			map.put("img", String.valueOf(info.getString("photo"));
		//			System.out.println("username===="+info.getJSONObject("sender").getString("userName"));
		//
		//			int index=info.getString("time").indexOf("T");
		//			String pubTime=info.getString("time").substring(0, index)+" "+info.getString("time").substring(index+1);
		//
		//			map.put("userInfo", info.getJSONObject("sender").getString("userName")+"\t"+pubTime);
		//			map.put("postContent", info.getString("content"));
		//
		//			Information newInfo=new Information();
		//			newInfo.setInfoId(Integer.parseInt(info.getString("infoId")));
		//			Userinfo sender=new Userinfo();
		//			sender.setAccount(info.getJSONObject("sender").getString("account"));
		//			sender.setUserName(info.getJSONObject("sender").getString("userName"));
		//			sender.setUserid(Integer.parseInt(info.getJSONObject("sender").getString("userid")));
		//			newInfo.setSender(sender);
		//			newInfo.setContent(info.getString("content"));
		//
		//
		//			newInfo.setTime(pubTime);
		//			information.add(newInfo);
		//
		//			this.list.add(map);
//	}
	reback = (Button)this.findViewById(R.id.personalcenter_reback_btn);
	reback.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	});

}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	return true;
}
//
//	int userid;
//	View myView ;
//	private class OnItemClickListenerImpl implements OnItemClickListener{
//
//		@Override
//		public void onItemClick(AdapterView<?>parent, View view, int position,
//				long id) {
//			// TODO Auto-generated method stub
//			System.out.println("position=============="+position);
//			userid=information.get(position).getSender().getUserid();
//			System.out.println("postsid============="+userid);
//
//
//			LayoutInflater factory = LayoutInflater.from(PersonalCenterActivity.this);
//			myView = factory.inflate(R.layout.message, null);
//			Dialog dialog = new AlertDialog.Builder(PersonalCenterActivity.this)
//			.setTitle("回复消息")
//			.setView(myView)
//			.setPositiveButton("发送", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					EditText messages =(EditText)myView.findViewById(R.id.messages);
//					String content = messages.getText().toString();
//					System.out.println("content---"+content);
//					try {
//						url = "androidTalkAction.action?userid1="+host.getUserid()+"&userid2="+userid+
//								"&content="+URLEncoder.encode(content, "UTF-8");
//
//						connect.testURLConn(url);
//						Toast.makeText(PersonalCenterActivity.this, "消息发送成功", Toast.LENGTH_LONG).show();
//
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//
//				}
//			}).create();
//
//
//			dialog.show();
//		}
//	}
public static Bitmap getHttpBitmap(String url) {
	URL myFileUrl = null;
	Bitmap bitmap = null;
	try {
		myFileUrl = new URL(url);
	} catch (MalformedURLException e) {
		e.printStackTrace();
	}
	try {
		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
		conn.setConnectTimeout(0);
		conn.setDoInput(true);
		conn.connect();
		InputStream is = conn.getInputStream();
		bitmap = BitmapFactory.decodeStream(is);
		is.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return bitmap;
}

public void getRecipesConnection() throws Exception{
	url = "service/userinfo/getUserRecipes";
	connectionMessage = connect.testURLConn4(url);
	System.out.println("connectionMessage:" + connectionMessage);
	messageJsonObject = new JSONObject(connectionMessage);
	recipesString = messageJsonObject.getString("root");
}
}

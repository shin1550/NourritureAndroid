package com.bjtu.nourriture.topic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.recipe.RecipeTalkToServer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class TopicUploadActivity extends Activity {

	DisplayImageOptions options;
	ConnectToServer connect = new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	String topicListResult;
	String path;
	EditText upload_name;
	EditText story;
	String topicId;
	String singleData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_upload);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();

		Intent intent = getIntent();
		path = intent.getStringExtra(Constants.INTENT_EXTRA_TOPIC_UPLAOD_PATH);

		ImageView mypicture = (ImageView) findViewById(R.id.my_production);
		Bitmap bitmap = getLoacalBitmap(path);
		mypicture.setImageBitmap(bitmap);

		topicId = intent.getStringExtra(Constants.INTENT_EXTRA_TOPIC_TOPIC_ID);
		singleData = intent.getStringExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL);

		upload_name = (EditText) findViewById(R.id.upload_title);
		story = (EditText) findViewById(R.id.story);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.upload_picture);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String uploadNameString = upload_name.getText().toString();
				String storyString = story.getText().toString();
				if (uploadNameString == null || uploadNameString.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Empty product name", Toast.LENGTH_SHORT).show();
				} else if (storyString == null || storyString.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Empty product story", Toast.LENGTH_SHORT).show();
				} else {
					final File file = new File(path);
					if (file != null) {

						String urlString = "http://123.57.38.31:3000/topic/upload";
						String result = UploadUtil.uploadFile(file, urlString);

						String url = "topic/uploadProduct";

						List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
						postParameters.add(new BasicNameValuePair(
								Constants.POST_UPDLOAD_TOPIC_ID, topicId));
						postParameters.add(new BasicNameValuePair(
								Constants.POST_UPLOAD_TOPIC_TITLE,
								uploadNameString));
						postParameters
								.add(new BasicNameValuePair(
										Constants.POST_UPLOAD_TOPIC_STORY,
										storyString));
						postParameters.add(new BasicNameValuePair(
								Constants.POST_UPLOAD_TOPIC_PICTURE_PATH,
								result));
						String resultString = connect.topicPost(url,
								postParameters);

						JSONObject singleObject = null;

						try {
							singleObject = new JSONObject(resultString);

						} catch (JSONException e) {
							e.printStackTrace();
						}

						String status;
						try {
							status = singleObject.getString("status");
							if (status.equals("true")) {
								Intent intent = new Intent();
								intent.setClass(TopicUploadActivity.this,
										TopicDetailActivity.class);
								url = "topic/showTopicDetail/" + topicId;
								String method = "GET";

								try {
									result = connect.testURLConn(url, method);

									JSONObject jsonObject2 = new JSONObject(
											result);
									singleData = jsonObject2.getJSONObject(
											"topic").toString();

									intent.putExtra(
											Constants.INTENT_EXTRA_TOPIC_DETAIL,
											singleData);
									startActivity(intent);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {

								Toast.makeText(getApplicationContext(),
										"upload fail,try again",
										Toast.LENGTH_SHORT).show();

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.topic_upload, menu);
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

	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}

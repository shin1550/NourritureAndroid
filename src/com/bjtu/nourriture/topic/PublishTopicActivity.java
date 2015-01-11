package com.bjtu.nourriture.topic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class PublishTopicActivity extends Activity {
	
	EditText titleEditText;
	EditText contentEditText; 
	ImageView picImageView ;
	DisplayImageOptions options;
	ConnectToServer connect = new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	String topicListResult;
	String picPath = null;
	
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final String TAG = "ImageUtils";
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pulish_topic);
		
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
		
		
		titleEditText = (EditText) findViewById(R.id.title);
		contentEditText = (EditText) findViewById(R.id.topic_content);		
		picImageView = (ImageView) findViewById(R.id.my_topic_picture);
		
		picImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog();
				
			}
		});
		
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pulish_picture);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				System.out.println("in this place.........");
				
				String title = titleEditText.getText().toString();
				String content = contentEditText.getText().toString();
				if (title == null || title.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Empty topic title", Toast.LENGTH_SHORT).show();
				} else if (content == null || content.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Empty topic content", Toast.LENGTH_SHORT).show();
				}
				
				String url = "topic/publishTopic";
				
				List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair(
						Constants.POST_PUBLISH_TOPIC_TITLE, title));
				postParameters.add(new BasicNameValuePair(
						Constants.POST_PUBLISH_TOPIC_CONTENT,
						content));
				
				
				
				if(picPath!=null){
					postParameters
					.add(new BasicNameValuePair(
							Constants.POST_PUBLISH_TOPIC_PICTURE_PATH,
							picPath));
				}
				String resultString = connect.topicPost(url,
						postParameters);
				
				JSONObject singleObject = null;

				try {
					singleObject = new JSONObject(resultString);
					String messageString = singleObject.getString("message");
					
					Toast.makeText(getApplicationContext(),
							messageString, Toast.LENGTH_SHORT).show();
					
					if(messageString.equals("publish successful")){
						
						Intent intent = new Intent();
						intent.setClass(PublishTopicActivity.this,ListTopicActivity.class);
						startActivity(intent);
						
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_topic, menu);
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
	
	
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("upload picture")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				//startActivityForResult(data, 2);
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									+ IMAGE_FILE_NAME);
					System.out.println("carame-------uri"+Uri.fromFile(tempFile));
					startPhotoZoom(Uri.fromFile(tempFile));
				//	startActivityForResult(data, 2);
				} else {
					Toast.makeText(PublishTopicActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					
					System.out.println("date----"+data);
					System.out.println("uri222222222----"+data.getData());
					getImageToView(data);
					
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
      
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {	
			
			Bitmap photo = extras.getParcelable("data");		
			String path=saveImage(photo,80);
			
			final File file = new File(path);
			if (file != null) {

				String urlString = "http://123.57.38.31:3000/topic/upload";
				String result = UploadUtil.uploadFile(file, urlString);
				picPath=result;
				System.out.println("file path --------"+picPath);
			}
			Bitmap bitmap = null;
			try {
				bitmap = getLoacalBitmap(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			picImageView.setImageBitmap(bitmap);
			
			
			
		}
	}
	
	public static String saveImage(Bitmap bitmap, int quality) {
		
		System.out.println("bitmap-----"+bitmap);

		String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
		String filePath = Environment.getExternalStorageDirectory()+"/"+ fileName;
		System.out.println("filepath-----"+filePath);
		String path = null;
		try {
			File file = new File(filePath);			
			FileOutputStream out = new FileOutputStream(file);
			
			BufferedOutputStream bos = new BufferedOutputStream(out);  
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)) {
				Log.d(TAG, "saveImage seccess: fileName= " + filePath + ", quality = " + quality);
				bos.flush();
				out.close();
				path = filePath;
			} else {
				Log.d(TAG, "saveImage fail: fileName= " + filePath);
			}
		} catch (Exception e) {
			Log.d(TAG, "saveImage Exception: " + e);
			e.printStackTrace();
		}
		
		System.out.println(path);
		return path;
	}
	
	public static Bitmap getLoacalBitmap(String url) throws IOException {
		FileInputStream fis = null ;
		try {
			fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}finally{
			fis.close();
		
	}
}
}

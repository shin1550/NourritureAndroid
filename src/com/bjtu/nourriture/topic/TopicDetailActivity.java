package com.bjtu.nourriture.topic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class TopicDetailActivity extends Activity {

	DisplayImageOptions options;
	ConnectToServer connect = new ConnectToServer();
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	String uploadListResult;
	GridView gridView;
	String idString;
	String singleData;
    TopicDetailListViewAdapter adapter;
	
	
	private String picPath = null;
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
		setContentView(R.layout.activity_topic_detail);

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
		singleData = intent
				.getStringExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL);
		JSONObject singleObject = null;

		try {
			singleObject = new JSONObject(singleData);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			TextView topic_id = (TextView) findViewById(R.id.topic_id);
			topic_id.setText(singleObject.getString("_id"));
			idString = singleObject.getString("_id");
			TextView name = (TextView) findViewById(R.id.name);
			name.setText("#" + singleObject.getString("topicName") + "#");
			TextView content = (TextView) findViewById(R.id.content);
			content.setText(singleObject.getString("content"));
			TextView upload = (TextView) findViewById(R.id.uploadcount);
			upload.setText("Upload count number : "
					+ singleObject.getString("upload_count"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			String url = "topic/getUploadToATopic?pageNo=1&pageSize=10&topic_id="
					+ singleObject.getString("_id");
			String method = "GET";
			uploadListResult = connect.testURLConn(url, method);

			JSONObject jsonObject = new JSONObject(uploadListResult);
			JSONArray jsonArray = jsonObject.getJSONArray("uploads");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jo = (JSONObject) jsonArray.opt(i);
				list.add(jo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LinearLayout uploadLinearLayout =(LinearLayout) findViewById(R.id.upload_picture);
		
		uploadLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showDialog();
				

	    
	     
				
			}
		});   

		adapter = new TopicDetailListViewAdapter(
				this, list);
		
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(adapter);


		gridView.setOnItemClickListener(new OnItemClickListenerImpl());

	}

	private class OnItemClickListenerImpl implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			System.out.println("----------click--------------");

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.topic_detail, menu);
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

	class TopicDetailListViewAdapter extends BaseAdapter {

		ArrayList<JSONObject> list;
		DisplayImageOptions options;
		Context context;
		TextView like_count;
		ImageView like_view;
		private Activity activity;
		// private ArrayList<HashMap<String, String>> data;
		private LayoutInflater inflater = null;
		public ImageLoader imageLoader;

		public TopicDetailListViewAdapter(Activity a, ArrayList<JSONObject> d) {
			activity = a;
			list = d;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public void setContext(Context context) {
			this.context = context;
		}

		public void setArrayList(ArrayList<JSONObject> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public JSONObject getItem(int positon) {
			JSONObject item = list.get(positon);
			return item;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View vi = convertView;
			if (convertView == null)
				vi = inflater.inflate(R.layout.activity_topic_detail_inner,
						null);

			ImageView picture = (ImageView) vi.findViewById(R.id.picture);

			TextView upload_id = (TextView) vi.findViewById(R.id.upload_id);
			TextView name = (TextView) vi.findViewById(R.id.upload_name);
			TextView author = (TextView) vi.findViewById(R.id.author);
			like_count = (TextView) vi.findViewById(R.id.likeCount);
			
			
			

			// 设置ListView的相关值
			try {
				ImageLoader.getInstance().displayImage(
						"http://123.57.38.31:3000/"
								+ list.get(position).getString("picture")
										.trim(), picture, options,
						new SimpleImageLoadingListener() {
						});

				upload_id.setText(list.get(position).getString("_id"));
			
				name.setText(list.get(position).getString("title"));
				
				String authorString =list.get(position).optString("author");
				String authorname = "travel";
				if(authorString==null||authorString.equals("")){
					
				}
				else{
					authorname = list.get(position).getJSONObject("author")
							.getString("account");
					if (authorname == null||authorname.equals("")) {
						authorname = "travel";
					}
				}

				author.setText("by " + authorname);
				like_count.setText(list.get(position).getString("like_count"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LinearLayout linearLayout =(LinearLayout)vi.findViewById(R.id.likelayout);
			linearLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				// TODO Auto-generated method stub
					try {
						
						
						String pictureId = list.get(position).getString("_id");
						String url = "topic/likeTopicUpload/"
								+ pictureId;
						String method = "GET";		
						uploadListResult = connect.testURLConn(url, method);
						JSONObject jsonObject = new JSONObject(uploadListResult);
						String message = jsonObject.getString("message");
						System.out.println("------message-------"+message);
						ArrayList<JSONObject> list2 = new ArrayList<JSONObject>();
						if(message.equals("like successful")){
							System.out.println("idString---------"+idString);
							
							String url2 = "topic/getUploadToATopic?pageNo=1&pageSize=10&topic_id="
									+ idString;

							String uploadListResult2 = connect.testURLConn(url2, method);

							JSONObject jsonObject2 = new JSONObject(uploadListResult2);
							JSONArray jsonArray = jsonObject2.getJSONArray("uploads");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jo = (JSONObject) jsonArray.opt(i);
								list2.add(jo);
							}
							System.out.println(list2);
							TopicDetailListViewAdapter adapter2 = new TopicDetailListViewAdapter(TopicDetailActivity.this, list2);
							gridView.setAdapter(adapter2);
			
						}
						
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			});

			return vi;
		}

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
					Toast.makeText(TopicDetailActivity.this, "未找到存储卡，无法存储照片！",
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
			picPath=path;
			
			
			Intent intent = new Intent();
			intent.setClass(TopicDetailActivity.this,TopicUploadActivity.class);
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_UPLAOD_PATH, path);
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_TOPIC_ID, idString);
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL, singleData);
			startActivity(intent);

			
		}
	}
	
	public static String saveImage(Bitmap bitmap, int quality) {
		
		

		String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
		String filePath = Environment.getExternalStorageDirectory()+"/"+ fileName;
		
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

	
}

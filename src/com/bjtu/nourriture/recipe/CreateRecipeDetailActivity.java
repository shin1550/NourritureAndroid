package com.bjtu.nourriture.recipe;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjtu.nourriture.MainActivity;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.common.Session;
import com.bjtu.nourriture.topic.Tools;
import com.bjtu.nourriture.topic.UploadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CreateRecipeDetailActivity extends Activity{

	Session session = Session.getSession();
	DisplayImageOptions options;
	private String recipeName = "";
	private String difficultity = "";
	private String cookTime = "";
	private String material = "";
	private String amount = "";
	private String description = "";
	private String recipePhotoPath;
	private String recipePhoto;
	private String recipeStepPhotoPath;
	private String recipeStepPhoto;
	private String explain;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final String TAG = "ImageUtils";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_create_step);
		
		Intent intent = getIntent();
		recipePhotoPath = intent.getStringExtra(Constants.INTENT_EXTRA_RECIPE_PHOTO_PATH);
		recipeName = intent.getStringExtra(Constants.INTENT_EXTRA_RECIPE_NAME);
		description = intent.getStringExtra(Constants.INTENT_EXTRA_RECIPE_DESCRIPTION);
		material = intent.getStringExtra(Constants.INTENT_EXTRA_RECIPE_MATERIAL);
		amount = intent.getStringExtra(Constants.INTENT_EXTRA_RECIPE_AMOUNT);
		difficultity = intent.getStringExtra(Constants.INTENT_EXTRA_RECIPE_DIFFICULTITY);
		cookTime = intent.getStringExtra(Constants.INTENT_EXTRA_RECIPE_COOK_TIME);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(0))
		.build();
		
		ImageView recipePhotoImageView = (ImageView) findViewById(R.id.createRecipePhoto);
		ImageView authorHeadImageView = (ImageView) findViewById(R.id.createRecipeHead);
		Bitmap bitmap = getLoacalBitmap(recipePhotoPath);
		ImageLoader.getInstance()
		.displayImage("http://123.57.38.31:3000"+(String) session.get("head"), authorHeadImageView, options, new SimpleImageLoadingListener() {
		});
		recipePhotoImageView .setImageBitmap(bitmap);
		
		TextView authorAccountTextView = (TextView) findViewById(R.id.createRecipeAccount);
		TextView recipeNameTextView = (TextView) findViewById(R.id.createRecipeName);
		authorAccountTextView.setText(session.get("username").toString());
		recipeNameTextView.setText(recipeName);
		
		UploadTask task = new UploadTask();
		task.type = Constants.RECIPE_PHOTO_PATH;
		task.execute();
	}
	
	class UploadTask extends AsyncTask<Object, Object, Object>{
		String type;
		String result;
		String local; 
		@Override
		protected Object doInBackground(Object... arg0) {
			if(type.equals(Constants.RECIPE_PHOTO_PATH)){
				local = recipePhotoPath;
			}else{
				local = recipeStepPhotoPath;
			}
			final File file = new File(local);
            if (file != null) {
            	String urlString="http://123.57.38.31:3000/service/recipe/upload";                
            	String result = UploadUtil.uploadFile(file, urlString);
            	System.out.println(result);
            	this.result = result;
			}
			return null;
		}
		
		@Override
		public void onPostExecute(Object result){
			if(type.equals(Constants.RECIPE_PHOTO_PATH)){
				recipePhoto = this.result;
			}else{
				recipeStepPhoto = this.result;
			}
		}
	}
	
	public void createRecipe(View view){
		EditText explainEditText = (EditText) findViewById(R.id.createRecipeStepExplain);
		explain = explainEditText.getText().toString();
		
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_RECIPE_NAME, recipeName));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_DIFFICULT, difficultity));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_COOK_TIME, cookTime));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_PHOTO, recipePhoto));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_DESCRIPTION, description));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_MATERIAL_NAME, material));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_MATERIAL_AMOUNT, amount));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_STEP_PHOTO, recipeStepPhoto));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_M_NUM, "1"));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_S_NUM, "1"));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_STEP_EXPLAIN, explain));
		
        postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ID, (String) session.get("user_id")));
        postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ACCOUNT, (String) session.get("username")));
        postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_HEAD, (String) session.get("head")));
        
        String resultString = RecipeTalkToServer.recipePost("recipe/create",postParameters);
        if(resultString.equals("create recipe success！")){
        	Toast.makeText(getApplicationContext(), "create success !",
				     Toast.LENGTH_SHORT).show();
    		Intent intent = new Intent(this,MainActivity.class);
    		startActivity(intent);
        }
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

	public void addStep(View view){
		new AlertDialog.Builder(this)
		.setTitle("Upload Photo")
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery,IMAGE_REQUEST_CODE);
					break;
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (Tools.hasSdcard()) {
						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME)));
					}
					startActivityForResult(intentFromCapture,CAMERA_REQUEST_CODE);
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
				startActivityForResult(data, 2);
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(Environment.getExternalStorageDirectory()+IMAGE_FILE_NAME);
					System.out.println("carame-------uri"+Uri.fromFile(tempFile));
					startPhotoZoom(Uri.fromFile(tempFile));
					startActivityForResult(data, 2);
				} else {
					System.out.println("-------");
					/*Toast.makeText(TopicDetailActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();*/
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
	
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {	
			
			Bitmap photo = extras.getParcelable("data");		
			String path=saveImage(photo,80);
			recipeStepPhotoPath=path;
			
			showAndUpload();
		}
	}
	
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1.5);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 480);
		intent.putExtra("return-data", true);
      
		startActivityForResult(intent, 2);
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
	
	public void showAndUpload(){
		ImageView stepImageView = (ImageView) findViewById(R.id.createRecipeStep);
		Bitmap bitmap = getLoacalBitmap(recipeStepPhotoPath);
		stepImageView.setImageBitmap(bitmap);
		
		UploadTask task = new UploadTask();
		task.type = Constants.RECIPE_STEP_PHOTO_PATH;
		task.execute();
	}
	
}

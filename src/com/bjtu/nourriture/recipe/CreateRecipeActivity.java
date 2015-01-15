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

import android.app.ActionBar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.common.Session;
import com.bjtu.nourriture.topic.Tools;
import com.bjtu.nourriture.topic.UploadUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CreateRecipeActivity extends Activity{
	
	DisplayImageOptions options;
	Session session = Session.getSession();
	private String[] items = new String[] { "Choose local photo", "Take a new photo" };
	private static final String IMAGE_FILE_NAME = "tempImage.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private String localPicPath;
	private String recipePath = null;
	private String stepPath = null;
	private String picType = "";
	private String RECIPE_PHOTO = "recipePhoto";
	private String STEP_PHOTO = "stepPhoto";
	private static final String TAG = "ImageUtils";
	private String[] difficultity = new String[]{"Junior Level","Middle Level","Senior Level"};
	private String[] time = new String[] {"About 10 minutes","10 - 30 minutes","30 - 60 minutes","More than 60 minutes"};
	private File tempFile = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
	
	private String recipeName = "";
	private String difficultityChoose = "";
	private String cookTime = "";
	private String material = "";
	private String amount = "";
	private String description = "";
	private String stepExplain = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_create_basic);
		setTitle("Create Recipe");
		final ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayUseLogoEnabled(false);
	    actionBar.setDisplayShowHomeEnabled(false);
	    
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
				.displayer(new RoundedBitmapDisplayer(10)).build();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.recipe_create, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	CreateRecipeActivity.this.finish();
                return true;
            case R.id.menu_publish:
            	createRecipe();
            	CreateRecipeActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void chooseDifficultity(View view){   
        new AlertDialog.Builder(this)
        .setTitle("Difficultity")
        .setItems(difficultity,new DialogInterface.OnClickListener(){  
            public void onClick(DialogInterface dialog, int which){
            	TextView textView = (TextView) findViewById(R.id.createRecipeDifficultity);
            	textView.setText("Difficultity:\n"+difficultity[which]);
            	difficultityChoose = difficultity[which];
            	dialog.dismiss();  
            }  
         }).show(); 
	}
	
	public void chooseTime(View view){
		new AlertDialog.Builder(this)
        .setTitle("Cook Time")
        .setItems(time,new DialogInterface.OnClickListener(){  
            public void onClick(DialogInterface dialog, int which){
            	TextView textView = (TextView) findViewById(R.id.createRecipeTime);
            	textView.setText("Cook Time:\n"+time[which]);
            	cookTime = time[which];
            	dialog.dismiss();  
            }  
         }).show();
	}
	
	/*public void addMoreMaterial(View view){
		TableLayout tableLayout = (TableLayout) findViewById(R.id.createRecipeMaterial);
		TableRow tablerow = new TableRow(CreateRecipeActivity.this);
		tablerow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		
		EditText MaterialView = new EditText(CreateRecipeActivity.this);
		EditText AmountView = new EditText(CreateRecipeActivity.this);
		View blankView = new View(CreateRecipeActivity.this);
		blankView.setBackgroundColor(Color.GRAY);
		MaterialView.setHint("Material");
		AmountView.setHint("Amount");
		
		tablerow.addView(MaterialView,10,LayoutParams.WRAP_CONTENT);
		tablerow.addView(blankView, 1, LayoutParams.MATCH_PARENT);
		tablerow.addView(AmountView,10,LayoutParams.WRAP_CONTENT);
		
		tableLayout.addView(tablerow);
	}*/

	public void uploadRecipe(View view){
		picType = RECIPE_PHOTO;
		showDialog();
	}
	
	public void uploadStep(View view){
		picType = STEP_PHOTO;
		showDialog();
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
								Uri.fromFile(tempFile));
			
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
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(CreateRecipeActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		//ImageView showImageView1 = (ImageView) findViewById(R.id.createRecipePhoto);
		//ImageView showImageView2 = (ImageView) findViewById(R.id.createRecipeStep);
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			String path = saveImage(photo,80);
			localPicPath = path;
		}
		
		CreateRecipeTask task = new CreateRecipeTask();
		task.execute();
		
		/*if (extras != null) {
			Bitmap photo = extras.getParcelable("data");		
			String path = saveImage(photo,80);
			localPicPath = path;
			if(picType.equals(RECIPE_PHOTO)){
				Bitmap bitmap = getLoacalBitmap(path);
				showImageView1.setImageBitmap(bitmap);
			}else{
				Bitmap bitmap = getLoacalBitmap(path);
				showImageView2.setImageBitmap(bitmap);
			}
			
			CreateRecipeTask task = new CreateRecipeTask();
			task.execute();*/
			
//			Intent intent = new Intent();
//			intent.setClass(CreateRecipeActivity.this,TopicUploadActivity.class);
//			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_UPLAOD_PATH, path);
//			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_TOPIC_ID, idString);
//			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL, singleData);
//			startActivity(intent);

			
		//}
	}
	
	public void startPhotoZoom(Uri uri) {
		System.out.println("ssssss----"+uri);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");  
		//intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 2);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
      
		startActivityForResult(intent, 2);
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
	
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	class CreateRecipeTask extends AsyncTask<Object, Object, Object>{
		String recipeRecult;
		String local;
		String result;
		String url;
		ImageView showImageView1 = (ImageView) findViewById(R.id.createRecipePhoto);
		ImageView showImageView2 = (ImageView) findViewById(R.id.createRecipeStep);
		
		@Override
		protected Object doInBackground(Object... arg0) {
			local = localPicPath;
			
			final File file = new File(local);
            if (file != null) {
            	String urlString="http://123.57.38.31:3000/service/recipe/upload";                
            	String result = UploadUtil.uploadFile(file, urlString);
            	System.out.println("---path-resule---"+result);
            	this.result = result;
			}
			return null;
		}
		
		@Override
		public void onPostExecute(Object result){
			if(picType.equals(RECIPE_PHOTO)){
				recipePath = this.result;
				url = "http://123.57.38.31:3000/"+this.result.trim();
				ImageLoader.getInstance()
				.displayImage(url, showImageView1, options, new SimpleImageLoadingListener() {
				});
			}else{
				stepPath = this.result;
				url = "http://123.57.38.31:3000/"+this.result.trim();
				ImageLoader.getInstance()
				.displayImage(url, showImageView2, options, new SimpleImageLoadingListener() {
				});
			}
		}
	}
	
	/*public void toStepActivity(){
		
		
		Intent intent = new Intent();
		intent.setClass(CreateRecipeActivity.this,CreateRecipeDetailActivity.class);
		
		intent.putExtra(Constants.INTENT_EXTRA_RECIPE_PHOTO_PATH, picPath);
		intent.putExtra(Constants.INTENT_EXTRA_RECIPE_NAME, recipeName);
		intent.putExtra(Constants.INTENT_EXTRA_RECIPE_DESCRIPTION, description);
		intent.putExtra(Constants.INTENT_EXTRA_RECIPE_MATERIAL, material);
		intent.putExtra(Constants.INTENT_EXTRA_RECIPE_AMOUNT, amount);
		intent.putExtra(Constants.INTENT_EXTRA_RECIPE_DIFFICULTITY, difficultityChoose);
		intent.putExtra(Constants.INTENT_EXTRA_RECIPE_COOK_TIME, cookTime);
		
		startActivity(intent);
	}*/

	public void createRecipe(){
		EditText recipeNameEditText = (EditText) findViewById(R.id.createRecipeName);
		EditText materialEditText = (EditText) findViewById(R.id.createMaterial);
		EditText amountEditText = (EditText) findViewById(R.id.createAmount);
		EditText descriptioneEditText = (EditText) findViewById(R.id.createRecipeDescription);
		EditText explainEditText = (EditText) findViewById(R.id.createRecipeStepExplain);
		
		recipeName = recipeNameEditText.getText().toString();
		material = materialEditText.getText().toString();
		amount = amountEditText.getText().toString();
		description = descriptioneEditText.getText().toString();
		stepExplain = explainEditText.getText().toString();
		
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_RECIPE_NAME, recipeName));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_DIFFICULT, difficultityChoose));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_COOK_TIME, cookTime));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_PHOTO, recipePath));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_DESCRIPTION, description));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_MATERIAL_NAME, material));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_MATERIAL_AMOUNT, amount));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_STEP_PHOTO, stepPath));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_M_NUM, "1"));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_S_NUM, "1"));
		postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_CREATE_STEP_EXPLAIN, stepExplain));
		
        postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ID, (String) session.get("user_id")));
        postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_ACCOUNT, (String) session.get("username")));
        postParameters.add(new BasicNameValuePair(Constants.POST_RECIPE_USER_HEAD, (String) session.get("head")));
        
        String resultString = RecipeTalkToServer.recipePost("recipe/create",postParameters);
        if(resultString.equals("create recipe success！")){
        	Toast.makeText(getApplicationContext(), "create success !",
				     Toast.LENGTH_SHORT).show();
        	CreateRecipeActivity.this.finish();
    		Intent intent = new Intent(this,ListRecipeActivity.class);
    		startActivity(intent);
        }
	}
}

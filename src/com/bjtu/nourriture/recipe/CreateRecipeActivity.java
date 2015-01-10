package com.bjtu.nourriture.recipe;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.topic.Tools;

public class CreateRecipeActivity extends Activity{

	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private String picPath = null;
	private static final String TAG = "ImageUtils";
	private String[] difficultity = new String[]{"Junior Level","Middle Level","Senior Level"};
	private String[] time = new String[] {"About 10 minutes","10 - 30 minutes","30 - 60 minutes","More than 60 minutes"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_create_basic);
	}
	
	public void backTo(View view){
		CreateRecipeActivity.this.finish();
	}
	
	public void forwardTo(View view){
		//CreateRecipeActivity.this.finish();
	}
	
	public void chooseDifficultity(View view){   
        new AlertDialog.Builder(this)
        .setTitle("Difficultity")
        .setItems(difficultity,new DialogInterface.OnClickListener(){  
            public void onClick(DialogInterface dialog, int which){
            	TextView textView = (TextView) findViewById(R.id.createRecipeDifficultity);
            	textView.append("Difficultity:\n"+difficultity[which]);
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
            	textView.append("Cook Time:\n"+time[which]);
            	dialog.dismiss();  
            }  
         }).show();
	}
	
	public void addMoreMaterial(View view){
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
	}

	public void showDialog(View view){
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
				//startPhotoZoom(data.getData());
				startActivityForResult(data, 2);
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									+ IMAGE_FILE_NAME);
					System.out.println("carame-------uri"+Uri.fromFile(tempFile));
					//startPhotoZoom(Uri.fromFile(tempFile));
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
			picPath=path;
			
			System.out.println("====----------"+picPath);
			/*Intent intent = new Intent();
			intent.setClass(TopicDetailActivity.this,TopicUploadActivity.class);
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_UPLAOD_PATH, path);
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_TOPIC_ID, idString);
			intent.putExtra(Constants.INTENT_EXTRA_TOPIC_DETAIL, singleData);
			startActivity(intent);*/

			
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
}

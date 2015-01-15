package com.bjtu.nourriture.user;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract.Root;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bjtu.nourriture.R;
import com.bjtu.nourriture.common.Constants;
import com.bjtu.nourriture.recipe.RecipeTalkToServer;
import com.bjtu.nourriture.recipe.SingleRecipeActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PersonalCenterActivity extends Activity implements
		AdapterView.OnItemClickListener {

	ImageView headImage;
	TextView username, province;
	ImageView sex;
	String infoResult;
	private ListView dataList;
	ArrayList<JSONObject> recipes = new ArrayList<JSONObject>();
	ConnectToServer connect;
	String url;
	String serverResult;
	ImageButton leavemessage;
	Button backButton;
	String usernameString, emailString, headString;
	String head;
	Bitmap bitmap;
	Handler handler;
	String connectionMessage, recipesString;
	JSONObject messageJsonObject;

	DisplayImageOptions options;
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	PullToRefreshListView mPullRefreshListView;
	ArrayAdapter<String> recipeNameAdapter;
	List<String> recipeNames = new ArrayList<String>();
	int pageNo = 1;
	ListRecipeAdapter adapter;

	// String searchString;
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_center);

		connect = new ConnectToServer();
		Session session = Session.getSession();
		usernameString = (String) session.get("username");
		emailString = (String) session.get("emal");
		headString = (String) session.get("head");
		handler = new Handler();

		headImage = (ImageView) this.findViewById(R.id.peronalcenter_head);
		head = "http://123.57.38.31:3000/" + (String) session.get("head");
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
								bitmap = ImageUtil.getHttpBitmap(head);
								headImage.setImageBitmap(bitmap); // 设置Bitmap
							}
						});
					}
				});
			}
		}).start();

		username = (TextView) this.findViewById(R.id.personalCenterName);
		username.setText(usernameString);

		province = (TextView) this.findViewById(R.id.personalCenterCity);
		province.setText(emailString);

		backButton = (Button) this.findViewById(R.id.personalcenter_reback_btn);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

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

		// AutoCompleteTextView searchEditText = (AutoCompleteTextView)
		// findViewById(R.id.personalcenter_recipe_listview);
		// recipeNameAdapter = new
		// ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,
		// recipeNames);
		// searchEditText.setAdapter(recipeNameAdapter);
		// searchEditText.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() ==
		// KeyEvent.ACTION_DOWN) {
		// searchRecipe1();
		// return true;
		// }
		// return false;
		//
		// }
		// });

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.personalcenter_recipe_listview);
		mPullRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
		mPullRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel("pull to load");
		mPullRefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("loading");
		mPullRefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel("release to load");

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						String label = DateUtils.formatDateTime(getApplicationContext(),System.currentTimeMillis(),
										DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						pageNo += 1;
						ListRecipeTask task = new ListRecipeTask();
						task.execute();
					}
				});

		adapter = new ListRecipeAdapter(this, list);
		mPullRefreshListView.requestLayout();
		mPullRefreshListView.setAdapter(adapter);
		mPullRefreshListView.setOnItemClickListener(this);

		ListRecipeTask task = new ListRecipeTask();
		task.execute();
	}

	public void getRecipesConnection() throws Exception {
		// url = "service/userinfo/getUserRecipes";
		url = "service/recipe/listAll";
		connectionMessage = connect.testURLConn1(url);

		System.out.println("connectionMessage:" + connectionMessage);
		messageJsonObject = new JSONObject(connectionMessage);
		recipesString = messageJsonObject.getString("root");
		System.out.println("recipesString:" + recipesString);
	}

	class ListRecipeAdapter extends BaseAdapter {
		ArrayList<JSONObject> list;
		Context context;
		private Activity activity;
		private LayoutInflater inflater = null;
		public ImageLoader imageLoader;

		public ListRecipeAdapter(Activity a, ArrayList<JSONObject> d) {
			activity = a;
			list = d;
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			JSONObject oneRecipe = list.get(position);
			if (convertView == null) {
				vi = inflater.inflate(R.layout.activity_recipe_list_all_inner,null);
			}

			TextView name = (TextView) vi.findViewById(R.id.recipe_name);
			TextView material = (TextView) vi	.findViewById(R.id.recipe_material);
			TextView hot = (TextView) vi.findViewById(R.id.recipe_hot);
			ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // 缩略图

			// 设置ListView的相关值
			name.setText("recipe name here");
			try {
				name.setText(oneRecipe.getString("recipeName"));
				String mString = "";
				JSONArray mArray = oneRecipe.getJSONArray("material");
				for (int i = 0; i < mArray.length(); i++) {
					JSONObject jo = (JSONObject) mArray.opt(i);
					mString += jo.getString("materialName") + "  ";
				}
				material.setText(mString);
				hot.setText("Collect Num : "
						+ oneRecipe.getString("collectNum")
						+ "    Comment Num : "
						+ list.get(position).getString("commentNum"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				ImageLoader.getInstance().displayImage(
						"http://123.57.38.31:3000/"
								+ list.get(position).getString("photo").trim(),
						thumb_image, options, new SimpleImageLoadingListener() {
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return vi;
		}
	}

	// public void searchRecipe(View view){
	// searchRecipe1();
	// }
	//
	// public void searchRecipe1(){
	// if(searchString == null || searchString.trim().equals("")){
	// pageNo = 1;
	// list.clear();
	// recipeNames.clear();
	// ListRecipeTask task = new ListRecipeTask();
	// task.execute();
	// }else{
	// pageNo = 1;
	// list.clear();
	// recipeNames.clear();
	// ListRecipeTask task = new ListRecipeTask();
	// task.execute();
	// }
	// }

	class ListRecipeTask extends AsyncTask<Object, Object, String> {
		// String search;
		String recipeRecult;

		@Override
		protected String doInBackground(Object... arg0) {
			// AutoCompleteTextView searchEditText = (AutoCompleteTextView)
			// findViewById(R.id.listRecipeSearch);
			// searchString = searchEditText.getText().toString();
			//
			// if(searchString == null || searchString.trim().equals("")){
			try {
				recipeRecult = getRecipeList();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("recipeRecult:"+recipeRecult);
			// }else{
			// searchString = searchString.replaceAll(" ", "%20");
			// recipeRecult = getSearchRecipeList(searchString);
			// }
			//
			try {
				JSONObject jsonObject = new JSONObject(recipeRecult);
				JSONArray jsonArray = jsonObject.getJSONArray("root");
				System.out.println("root:"+jsonArray);
				if (jsonArray.length() == 0) {
					return "No";
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jo = (JSONObject) jsonArray.opt(i);
					list.add(jo);
					recipeNames.add(jo.getString("recipeName"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void onPostExecute(String result) {
			if (result != null && result.equals("No")) {
				Toast.makeText(getApplicationContext(), "No more",
						Toast.LENGTH_SHORT).show();
				mPullRefreshListView.onRefreshComplete();
				super.onPostExecute(null);
			} else {
				// mPullRefreshListView.setVisibility(View.GONE);
				mPullRefreshListView.requestLayout();
				//notifyDataSetChanged()可以在修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新ListView
				adapter.notifyDataSetChanged();
				// mPullRefreshListView.setVisibility(View.VISIBLE);

				// recipeNameAdapter.notifyDataSetInvalidated();

				//更新
				mPullRefreshListView.onRefreshComplete();
				super.onPostExecute(result);
			}
		}
	}

	public String getRecipeList() throws Exception {
//		return RecipeTalkToServer.recipeGet("recipe/listAll?pageNo=" + pageNo
//				+ "&pageSize=10");//service/userinfo/getUserRecipes
		ConnectToServer connectToServer = new ConnectToServer();
		return connectToServer.testURLConn1("service/userinfo/getUserRecipes?pageNo=" + pageNo+ "&pageSize=10");
	}

	public String getSearchRecipeList(String search) {
		return RecipeTalkToServer.recipeGet("recipe/search?pageNo=" + pageNo
				+ "&pageSize=10&queryStr=" + search);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
		System.out.println("-----pos----" + pos);
		Intent intent = new Intent(this, SingleRecipeActivity.class);

		try {
			intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE_ID,
					list.get(pos - 1).getString("_id"));
			intent.putExtra(Constants.INTENT_EXTRA_SINGLE_RECIPE,
					list.get(pos - 1).toString());
			startActivity(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

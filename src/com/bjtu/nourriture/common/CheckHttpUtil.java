package com.bjtu.nourriture.common;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckHttpUtil {

	/**
	 * 对网络连接状态进行判断
	 * 
	 * @return true, 可用； false， 不可用
	 */
	private static boolean isOpenNetwork(Activity activity) {
		Context context = activity.getApplicationContext();
		ConnectivityManager connManager = (ConnectivityManager)context. getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}


	/**
	 *网络可用就调用下一步需要进行的方法， 网络不可用则需设置
	 */
	public static void initIntener(final Activity activity) {


		// 判断网络是否可用
		if (isOpenNetwork(activity) == true) {
			// 网络可用，则开始加载。
			//initPross();//这里是我个人程序要进行网络加载的方法，根据自己的程序而定，灵活运用。
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("没有可用的网络").setMessage("是否对网络进行设置?");


			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = null;


					try {
						intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						activity.startActivity(intent);
					} catch (Exception e) {
						// Log.w(TAG,
						// "open network settings failed, please check...");
						e.printStackTrace();
					}
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					//finish();//因为网络不可用的状态，也是不让自己的程序结束运行， 这是根据个人需要设置。
					Toast.makeText(activity, "网络异常，加载失败！", Toast.LENGTH_SHORT).show();
					//initAll();//这里是没有网络的时候，又不需要手动设置，则显示出来的一个静态页面，根据个人需要。
				}
			}).show();


		}
	}


}
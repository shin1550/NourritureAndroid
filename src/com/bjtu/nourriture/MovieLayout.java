package com.bjtu.nourriture;


import java.util.Map;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MovieLayout extends LinearLayout {
	private MovieAdapter adapter;
	private Context context;
	public MovieLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}
	
	public void setAdapter(MovieAdapter adapter) {
		this.adapter = adapter;
		for(int i=0;i<adapter.getCount();i++){
			final Map<String,Object> map=adapter.getItem(i);
			View view=adapter.getView(i, null, null);
			view.setPadding(10, 0, 10, 0);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "To be expected"+map.get("text"), Toast.LENGTH_SHORT).show();
				}
			});
			this.setOrientation(HORIZONTAL);
			this.addView(view,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}
	}
}


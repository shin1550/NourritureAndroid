package com.bjtu.nourriture.recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;
import android.widget.TextView;

import com.bjtu.nourriture.R;
import com.bjtu.nourriture.recipe.adapter.ListRecipeAdapter;

public class ListRecipeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_list_all);
		TextView testView = (TextView)findViewById(R.id.textView1);
		String recipeRecult = getRecipeList();
		try {
			JSONObject jsonObject = new JSONObject(recipeRecult);
			System.out.println("==============");
			JSONArray jsonArray = jsonObject.getJSONArray("root");
			//System.out.println(jsonArray);
			//testView.append(jsonArray.toString());
			ArrayList<String> list = new ArrayList<String>();
			for(int i=0;i<jsonArray.length();i++){   
                JSONObject jo = (JSONObject)jsonArray.opt(i);
                list.add(jo.toString());
                //ss[i]=jo.getInt("id")+"  "+jo.getString("name")+"  "+jo.getString("地址");  
            }
			
			 //(ArrayList)JSONArray.toList(jsonArray, String.class);
			ListView listview=(ListView) findViewById(R.id.listView1);   
	        ListRecipeAdapter adapter = new ListRecipeAdapter(this,list);
	        //adapter.setArrayList(list);
			listview.setAdapter(adapter);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		
	}
	
	public String getRecipeList(){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		
		String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(
                    "http://123.57.38.31:3000/service/recipe/listAll?pageNo=1&pageSize=3"));
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
 
            StringBuffer strBuffer = new StringBuffer("");
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return result;
	}
	
	/*public String[] analyticJson(String resultJson){
		try {  
            String[] ss=new String[5]; 
            JSONObject jsonObject = new JSONObject(resultJson).getJSONObject("root");  
            JSONArray jsonArray=jsonObject.getJSONArray("info");  
            for(int i=0;i<jsonArray.length();i++){   
                JSONObject jo = (JSONObject)jsonArray.opt(i);  
                ss[i]=jo.getInt("id")+"  "+jo.getString("name")+"  "+jo.getString("地址");  
            }
            return ss;  
        } catch (JSONException e) {  
            e.printStackTrace();  
        }  
        return null;  
	}*/
}

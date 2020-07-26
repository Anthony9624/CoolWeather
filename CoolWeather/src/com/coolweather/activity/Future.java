package com.coolweather.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.R;


















import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebIconDatabase.IconListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Future extends Activity{
		
private ListView lvFur;
private static  List<Furweather> list= new ArrayList<Furweather>();
private static TextView tvTest;
	@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.furture);
	list.clear();
	lvFur = (ListView) findViewById(R.id.listView1);
	tvTest = (TextView) findViewById(R.id.text_test);
	WeatherActivity wa = new WeatherActivity();
	handleWeatherResponse(wa.result);
	//tvTest.setText(wa.result);
	lvFur.setAdapter(adapter);
	
}
//适配器
public  BaseAdapter adapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = LayoutInflater.from(Future.this);
			if(convertView==null){
				convertView = inflater.inflate(R.layout.item, null);
				TextView tv_data = (TextView) convertView.findViewById(R.id.textView_data);
				TextView tv_wendu = (TextView) convertView.findViewById(R.id.textView_wendu);
				tv_data.setText(list.get(position).getCityname()+list.get(position).getDate());
				tv_wendu.setText(list.get(position).getWeather()+list.get(position).getTmper());
				ViewHolder vh= new ViewHolder(tv_data,tv_wendu);
				convertView.setTag(vh);
			}else{
				ViewHolder vh = (ViewHolder) convertView.getTag();

				vh.tv_data.setText(list.get(position).getCityname()+" "+list.get(position).getDate());
				vh.tv_wendu.setText(list.get(position).getWeather()+" "+list.get(position).getTmper());
			}
			adapter.notifyDataSetChanged();
			return convertView;
		}
		
		public int getCount() { 
			// TODO Auto-generatedmethod stub 
			return list.size();
			} 
		@Override 
		public Object getItem(int position) { 
			// TODO Auto-generated method stub 
			return list.get(position); 
			}
		@Override 
		public long getItemId(int position){ 
			// TODO Auto-generated method stub 
			return position; 
			}
	};
	
	public class ViewHolder{
		
		private TextView tv_data;
		private TextView tv_wendu;
		//private ImageView iv_head;
		public TextView getTv_data() {
			return tv_data;
		}
		public void setTv_data(TextView tv_data) {
			this.tv_data = tv_data;
		}
		public TextView getTv_wendu() {
			return tv_wendu;
		}
		public void setTv_wendu(TextView tv_wendu) {
			this.tv_wendu = tv_wendu;
		}
//		public ImageView getIv_head() {
//			return iv_head;
//		}
//		public void setIv_head(ImageView iv_head) {
//			this.iv_head = iv_head;
//		}   
		public ViewHolder(TextView tv_data, TextView tv_wendu) {
			super();
			this.tv_data = tv_data;
			this.tv_wendu = tv_wendu;
			//this.iv_head = iv_head;
		}
		
	}
	//解析joson数据
	public static void handleWeatherResponse(String response) {
		try {
			
			JSONObject root = new JSONObject(response);
			JSONArray result = root.getJSONArray("result");
			int i= 0;
			for(i=0;i < result.length();i++){
				
				JSONObject item = result.getJSONObject(i);
				String date = item.getString("days");
				String week = item.getString("week");
				String weather = item.getString("weather");
				String cityname = item.getString("citynm");
				String tmp = item.getString("temperature");
				list.add(new Furweather(date+week,weather,cityname, tmp));
			}
			System.out.println("//////////////////////////////////////////////////////////////////////");
			System.out.println(i);
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
//定义未来天气类
class Furweather{
	private String date;
	private String weather;
	private String cityname;
	private String tmper;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
	}
	public String getTmper() {
		return tmper;
	}
	public void setTmper(String tmper) {
		this.tmper = tmper;
	}
	public Furweather(String date, String weather, String cityname,
			String tmper) {
		super();
		this.date = date;
		this.weather = weather;
		this.cityname = cityname;
		this.tmper = tmper;
	}
	
		
}

package com.coolweather.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener {
	//跑马灯内容
	
	private TextView runhouse;
	private LinearLayout weatherInfoLayout;
	public  static String result;
	private static String weatherCode;
	
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	// 切换城市
	private Button switchCity;
	// 更新天气
	private Button refreshWeather;
	private TextView currentDateText;
	//枚举天气类型
	private enum WeatherKind {
		cloudy, fog, hailstone, light_rain, moderte_rain, overcast, rain_snow, sand_strom, rainstorm, shower_rain, snow, sunny, thundershower;
	}
	//添加一个静态的map对象来存储String天气类型和枚举天气类型的对应关系
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// 初始化各控件
		weatherCode = null;
		result = null;
		runhouse=(TextView) findViewById(R.id.runhouse);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		getFurture();
	
		String countyCode = getIntent().getStringExtra("county_code");
		//System.out.println( countyCode);
		
		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// 没有县级代号时就直接显示本地天气
			queryWeatherCode("070101");
//			showWeather();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			//Log.v("MainActivity", "choose");
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			
			
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		
		default:
			break;
		}

	}
	public void furtureClick(View v){
			getFurture();
			Intent i = new Intent();
			i.setClass(this, Future.class);
			startActivity(i);
			
		}			

	/*
	 * 查询县级天气
	 */

	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 查询天气代号所对应的天气。
	 */
	private  void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
		
	}
	//获取未来几天天气     
	private void getFurture(){
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
			 weatherCode = prefs.getString("weather_code", "");
		new Thread(){
			public void run() {  
			StringBuffer strBuf;
			strBuf = new StringBuffer();  		
	        try { 	
	            URL url = new URL("http://api.k780.com:88/?app=weather.future&weaid="+weatherCode+"&&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json请求方式");  
	            URLConnection conn = url.openConnection();  
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));// 转码。  
	            String line = null;  
				Message msg = new Message();	
	            while ((line = reader.readLine()) != null)  
	                strBuf.append(line + " ");  
	            msg.obj = strBuf.toString();
				handler.sendMessage(msg);
	            reader.close();  
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}.start();
	}	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			result = (String) msg.obj;
			System.out.println(result);
		};
	};
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	/*
	 * 
	 * 各天气以键值对形式保存
	 * */
	private static Map<String, WeatherKind> weatherkind = new HashMap<String, WeatherKind>();
	static {
		weatherkind.put("多云", WeatherKind.cloudy);
		weatherkind.put("雾", WeatherKind.fog);
		weatherkind.put("冰雹", WeatherKind.hailstone);
		weatherkind.put("小雨", WeatherKind.light_rain);
		weatherkind.put("中雨", WeatherKind.moderte_rain);
		weatherkind.put("阴", WeatherKind.overcast);
		weatherkind.put("雨加雪", WeatherKind.rain_snow);
		weatherkind.put("沙尘暴", WeatherKind.sand_strom);
		weatherkind.put("暴雨", WeatherKind.rainstorm);
		weatherkind.put("阵雨", WeatherKind.shower_rain);
		weatherkind.put("小雪", WeatherKind.snow);
		weatherkind.put("晴", WeatherKind.sunny);
		weatherkind.put("雷阵雨", WeatherKind.thundershower);
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		String weatherDesp=prefs.getString("weather_desp", "");
		weatherDespText.setText(weatherDesp);
		WeatherKind myWeather = weatherkind.get(weatherDesp);  
        if (myWeather != null) {  
            changeBackground(myWeather);  
        }  
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		Log.v("MainActivity", "ServiceStart");
	}
	private void changeBackground(WeatherKind weather) {
		View view = findViewById(R.id.weather_background);
		ImageView imageview=(ImageView) findViewById(R.id.weather_icon);
		
		switch (weather) {
		case cloudy:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.duoyun));
			imageview.setImageResource(R.drawable.lcloudy);
			runhouse.setText("   今天天气为多云，天气较为爽朗，注意天气变化，有下雨的可能     ");
			break;
		case fog:
			view.setBackground(this.getResources().getDrawable(R.drawable.wumai));
			imageview.setImageResource(R.drawable.lfog);
			runhouse.setText("   今天天气为霾，空气质量较差，不建议出行，尽量减少户外运动     ");
			break;
		case hailstone:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.hailstone));
			imageview.setImageResource(R.drawable.lhailstone);
			runhouse.setText("   今天天气为沙尘，空气质量较差，不建议出行，尽量减少户外运动     ");
			break;
		case light_rain:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.fengyeyu));
			imageview.setImageResource(R.drawable.llight_rain);
			runhouse.setText("   今天天气为小雨 ，空气湿润，出门记得带好雨具     ");
			break;
		case moderte_rain:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.moderte_rain));
			imageview.setImageResource(R.drawable.lmoderte_rain);
			runhouse.setText("   今天天气为中雨 ，空气湿润，出门记得带好雨具     ");
			break;
		case overcast:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.overcast));
			imageview.setImageResource(R.drawable.lovercast);
			break;
		case rain_snow:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.xiaxue));
			imageview.setImageResource(R.drawable.lsnow);
			break;
		case rainstorm:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.rainstorm));
			imageview.setImageResource(R.drawable.lrainstorm);
			break;
		case sand_strom:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.sand_storm));
			imageview.setImageResource(R.drawable.lsand_strom);
			break;
		case shower_rain:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.shower_rain));
			imageview.setImageResource(R.drawable.lshower_rain);
			break;
		case snow:
			view.setBackground(this.getResources().getDrawable(R.drawable.snow));
			imageview.setImageResource(R.drawable.lsnow);
			break;
		case sunny:
			view.setBackground(this.getResources()
					.getDrawable(R.drawable.sunnyd));
			runhouse.setText("   今天天气为晴天 ，天空晴朗，适合出行          ");
			imageview.setImageResource(R.drawable.lsunny1);
			break;
		
		case thundershower:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.black_stom));
			imageview.setImageResource(R.drawable.lthundershower);
			runhouse.setText("   今天天气为雷雨 ，天气恶劣，不建议出行，注意用电安全          ");
			break;
		default:
			break;
		}
		
		
		
		
	}
}

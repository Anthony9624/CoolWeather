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
	//���������
	
	private TextView runhouse;
	private LinearLayout weatherInfoLayout;
	public  static String result;
	private static String weatherCode;
	
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	/**
	 * ������ʾ��ǰ����
	 */
	// �л�����
	private Button switchCity;
	// ��������
	private Button refreshWeather;
	private TextView currentDateText;
	//ö����������
	private enum WeatherKind {
		cloudy, fog, hailstone, light_rain, moderte_rain, overcast, rain_snow, sand_strom, rainstorm, shower_rain, snow, sunny, thundershower;
	}
	//���һ����̬��map�������洢String�������ͺ�ö���������͵Ķ�Ӧ��ϵ
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// ��ʼ�����ؼ�
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
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
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
			publishText.setText("ͬ����...");
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
	 * ��ѯ�ؼ�����
	 */

	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * ��ѯ������������Ӧ��������
	 */
	private  void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
		
	}
	//��ȡδ����������     
	private void getFurture(){
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
			 weatherCode = prefs.getString("weather_code", "");
		new Thread(){
			public void run() {  
			StringBuffer strBuf;
			strBuf = new StringBuffer();  		
	        try { 	
	            URL url = new URL("http://api.k780.com:88/?app=weather.future&weaid="+weatherCode+"&&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json����ʽ");  
	            URLConnection conn = url.openConnection();  
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));// ת�롣  
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
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ��
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// ������������ص�������Ϣ
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
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	/*
	 * 
	 * �������Լ�ֵ����ʽ����
	 * */
	private static Map<String, WeatherKind> weatherkind = new HashMap<String, WeatherKind>();
	static {
		weatherkind.put("����", WeatherKind.cloudy);
		weatherkind.put("��", WeatherKind.fog);
		weatherkind.put("����", WeatherKind.hailstone);
		weatherkind.put("С��", WeatherKind.light_rain);
		weatherkind.put("����", WeatherKind.moderte_rain);
		weatherkind.put("��", WeatherKind.overcast);
		weatherkind.put("���ѩ", WeatherKind.rain_snow);
		weatherkind.put("ɳ����", WeatherKind.sand_strom);
		weatherkind.put("����", WeatherKind.rainstorm);
		weatherkind.put("����", WeatherKind.shower_rain);
		weatherkind.put("Сѩ", WeatherKind.snow);
		weatherkind.put("��", WeatherKind.sunny);
		weatherkind.put("������", WeatherKind.thundershower);
	}

	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
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
		publishText.setText("����" + prefs.getString("publish_time", "") + "����");
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
			runhouse.setText("   ��������Ϊ���ƣ�������Ϊˬ�ʣ�ע�������仯��������Ŀ���     ");
			break;
		case fog:
			view.setBackground(this.getResources().getDrawable(R.drawable.wumai));
			imageview.setImageResource(R.drawable.lfog);
			runhouse.setText("   ��������Ϊ�������������ϲ��������У��������ٻ����˶�     ");
			break;
		case hailstone:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.hailstone));
			imageview.setImageResource(R.drawable.lhailstone);
			runhouse.setText("   ��������Ϊɳ�������������ϲ��������У��������ٻ����˶�     ");
			break;
		case light_rain:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.fengyeyu));
			imageview.setImageResource(R.drawable.llight_rain);
			runhouse.setText("   ��������ΪС�� ������ʪ�󣬳��żǵô������     ");
			break;
		case moderte_rain:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.moderte_rain));
			imageview.setImageResource(R.drawable.lmoderte_rain);
			runhouse.setText("   ��������Ϊ���� ������ʪ�󣬳��żǵô������     ");
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
			runhouse.setText("   ��������Ϊ���� ��������ʣ��ʺϳ���          ");
			imageview.setImageResource(R.drawable.lsunny1);
			break;
		
		case thundershower:
			view.setBackground(this.getResources().getDrawable(
					R.drawable.black_stom));
			imageview.setImageResource(R.drawable.lthundershower);
			runhouse.setText("   ��������Ϊ���� ���������ӣ���������У�ע���õ簲ȫ          ");
			break;
		default:
			break;
		}
		
		
		
		
	}
}

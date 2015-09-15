package com.zhukun.coolweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhukun.coolweather.R;
import com.zhukun.coolweather.util.HttpCallBackListener;
import com.zhukun.coolweather.util.HttpUtil;
import com.zhukun.coolweather.util.KeyGenerate;
import com.zhukun.coolweather.util.Utility;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2015/9/11.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    public static final int TODAY = 0;
    public static final int TOMORROW = 1;
    public static final int DAY_AFTER_TOMORROW = 2;
    private TextView CityText;
    private TextView publishText;
    private TextView currentText;
    private TextView weatherType;
    private TextView midTempText;
    private LinearLayout infoLayout;
    private TextView tmp1Text;
    private TextView tmp2Text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        TimeZone time = TimeZone.getTimeZone("GMT+8");  //设置时区
        TimeZone.setDefault(time);
        CityText = (TextView) findViewById(R.id.City_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        currentText = (TextView) findViewById(R.id.current_date);
        infoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weatherType = (TextView) findViewById(R.id.Weather_Type);
        tmp1Text = (TextView) findViewById(R.id.temp1);
        tmp2Text = (TextView) findViewById(R.id.temp2);
        midTempText = (TextView) findViewById(R.id.midTempText);
        String  countyName = getIntent().getStringExtra("countyName");
        String areaId = getIntent().getStringExtra("areId");
        int countyId = getIntent().getIntExtra("countyId", 0);
        CityText.setText(countyName);
        if(!TextUtils.isEmpty(areaId)){
            publishText.setText("同步中..");
            Log.d("Weather",getHourAndMinute("201509152055"));
            infoLayout.setVisibility(View.INVISIBLE);
            CityText.setVisibility(View.INVISIBLE);
            queryWeatherFromServer(areaId, TODAY);
        }

    }

    private void queryWeatherFromServer(String areaId, final int dayId) {
        String address = getAddress(areaId);
        Log.d("address",address);
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeather(WeatherActivity.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather(dayId);
                    }
                });
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

    private void showWeather(int dayId) {
        SharedPreferences pref = getSharedPreferences("data" + dayId, 0);
        String tmp1 = pref.getString("tmp1", "0");
        if(tmp1.equals("")){
            tmp1Text.setText("夜间");
            midTempText.setText("温度:");
        }
        else {
            tmp1Text.setText(tmp1 + "oC");
            midTempText.setText("~");
        }
        tmp2Text.setText(pref.getString("tmp2", "0") + "oC");
        String publishTime = getHourAndMinute(pref.getString("publishTime", ""));
        publishText.setText("今天" + publishTime + "发布");
        currentText.setText(pref.getString("currentTime","")+"天气:");
        weatherType.setText(Utility.matchWeather(pref.getString("type2","")));
        infoLayout.setVisibility(View.VISIBLE);
        CityText.setVisibility(View.VISIBLE);
    }

    private String getHourAndMinute(String publishTime) {
        String tmp1 = publishTime.substring(8,10);
        String tmp2 = publishTime.substring(10,12);
        return tmp1 +":"+tmp2;
    }


    private String getAddress(String areaId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String current_date = sdf.format(new Date());
        String apiKey = KeyGenerate.Genarate(areaId, current_date);
        String address=  "http://open.weather.com.cn/data/?areaid=" + areaId + "&type=forecast_v&date=" +
               current_date + "&appid=b0e98c&key=" + apiKey;
        return address;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }

    }
}

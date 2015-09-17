package com.zhukun.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhukun.coolweather.R;
import com.zhukun.coolweather.service.AutoUpdateService;
import com.zhukun.coolweather.util.HttpCallBackListener;
import com.zhukun.coolweather.util.HttpUtil;
import com.zhukun.coolweather.util.KeyGenerate;
import com.zhukun.coolweather.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 2015/9/11.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    public static final int TODAY = 0;
    public static final int TOMORROW = 1;
    public static final int DAY_AFTER_TOMORROW = 2;
    public static final int DAY = 3;
    public static final int NIGHT = 4;
    private int dayNow;
    private TextView CityText;
    private TextView publishText;
    private TextView currentText;
    private TextView weatherType;
    private TextView midTempText;
    private LinearLayout infoLayout;
    private TextView tmp1Text;
    private TextView tmp2Text;
    private ViewPager pager = null;
    private List<View> viewContainer = new ArrayList<>();
    private View view1;
    private View view2;
    private View view3;
    private String countyName;
    private ImageView imageView;
    private ImageView[] imageArry;
    private ViewGroup dotlist;
    private Button switchButton;
    private Button refreshButton;
    private ImageView weatherImage;
    private String areaId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewpager);
        TimeZone time = TimeZone.getTimeZone("GMT+8");  //设置时区
        TimeZone.setDefault(time);
        CityText = (TextView) findViewById(R.id.City_name);
        pager = (ViewPager) findViewById(R.id.viewpager);
        dotlist = (ViewGroup) findViewById(R.id.dotlist);
        view1 = LayoutInflater.from(this).inflate(R.layout.weather_layout, null);
        view2 = LayoutInflater.from(this).inflate(R.layout.weather_layout, null);
        view3 = LayoutInflater.from(this).inflate(R.layout.weather_layout, null);
        viewContainer.add(view1);
        viewContainer.add(view2);
        viewContainer.add(view3);
        imageArry = new ImageView[viewContainer.size()];
        //生成每个点
        for(int i=0;i<viewContainer.size();i++){
            imageView = new ImageView(WeatherActivity.this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            lp.leftMargin = 10;
            lp.rightMargin = 10;
            imageArry[i] = imageView;
            if(i==0){
                imageView.setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            dotlist.addView(imageView,lp);
        }

        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewContainer.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewContainer.get(position));
                return viewContainer.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewContainer.get(position));
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        bindViews(view1);
                        showWeather(TODAY);
                        dayNow = TODAY;
                        reFreshdot(i);
                        break;
                    case 1:
                        bindViews(view2);
                        showWeather(TOMORROW);
                        dayNow = TOMORROW;
                        reFreshdot(i);
                        break;
                    case 2:
                        bindViews(view3);
                        showWeather(DAY_AFTER_TOMORROW);
                        dayNow = DAY_AFTER_TOMORROW;
                        reFreshdot(i);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bindViews(view1);
        String countyName = getIntent().getStringExtra("countyName");
        areaId = getIntent().getStringExtra("areId");
        int countyId = getIntent().getIntExtra("countyId", 0);
        SharedPreferences.Editor editor = getSharedPreferences("CityInfo", 0).edit();
        editor.putString("areId", areaId);
        editor.putInt("countyId", countyId);
        editor.putString("countyName", countyName);
        editor.putBoolean("city_selected", true);
        editor.commit();

        if(!TextUtils.isEmpty(areaId)){
            publishText.setText("同步中..");
            infoLayout.setVisibility(View.INVISIBLE);
            CityText.setVisibility(View.INVISIBLE);
            CityText.setText(countyName);
            queryWeatherFromServer(areaId, TODAY);
            dayNow = TODAY;
        }
        switchButton = (Button) findViewById(R.id.switch_city);
        refreshButton = (Button) findViewById(R.id.refresh);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, SelectedCityActivity.class);
                //intent.putExtra("from_weather_activity", true);
                startActivity(intent);
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishText.setText("同步中..");
                switch (dayNow){
                    case 0:queryWeatherFromServer(areaId, TODAY);
                            break;
                    case 1:queryWeatherFromServer(areaId, TOMORROW);
                            break;
                    case 2:queryWeatherFromServer(areaId, DAY_AFTER_TOMORROW);
                            break;
                }

            }
        });

    }

    private void reFreshdot(int i) {
        for(int j=0;j<imageArry.length;j++){
            if(j==i){
                imageArry[j].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                imageArry[j].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    private void bindViews(View view1) {
        publishText = (TextView) view1.findViewById(R.id.publish_text);
        currentText = (TextView) view1.findViewById(R.id.current_date);
        infoLayout = (LinearLayout) view1.findViewById(R.id.weather_info_layout);
        weatherType = (TextView) view1.findViewById(R.id.Weather_Type);
        tmp1Text = (TextView) view1.findViewById(R.id.temp1);
        tmp2Text = (TextView) view1.findViewById(R.id.temp2);
        midTempText = (TextView) view1.findViewById(R.id.midTempText);
        weatherImage = (ImageView) view1.findViewById(R.id.weather_image);
    }


    private void queryWeatherFromServer(final String areaId, final int dayId) {
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
        SharedPreferences.Editor editor =getSharedPreferences(areaId, 0).edit();
        String tmp1 = pref.getString("tmp1", "0");
        String tmp2 = pref.getString("tmp2", "0");
        String type1 = pref.getString("type1","");
        String type2 = pref.getString("type2","");
        if(tmp1.equals("")){
            tmp1Text.setText("夜间");
            midTempText.setText("温度:");
            weatherType.setText(Utility.matchWeather(type2));
            weatherImage.setImageResource(Utility.getImageId(type2, NIGHT));
        }
        else {
            tmp1Text.setText(tmp1 + "℃");
            midTempText.setText("~");
            weatherType.setText(Utility.matchWeather(type1));
            weatherImage.setImageResource(Utility.getImageId(type1, DAY));
        }
        tmp2Text.setText(tmp2 + "℃");
        String publishTime = getHourAndMinute(pref.getString("publishTime", ""));
        publishText.setText("今天" + publishTime + "发布");
        currentText.setText(pref.getString("currentTime","")+ ":");

        infoLayout.setVisibility(View.VISIBLE);
        CityText.setVisibility(View.VISIBLE);
        if(dayId == TODAY){
            if(publishTime.equals("18:00")) {   //如果18：00发布，则保存晚上的温度
                editor.putString("tmp", tmp2);
            }else{
                editor.putString("tmp", tmp1);
            }
            editor.putString("date", publishTime+"发布");
            editor.commit();
        }
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
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

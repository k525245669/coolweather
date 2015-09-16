package com.zhukun.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhukun.coolweather.R;
import com.zhukun.coolweather.db.RestoreData;
import com.zhukun.coolweather.model.City;
import com.zhukun.coolweather.model.CoolWeatherDB;
import com.zhukun.coolweather.model.County;
import com.zhukun.coolweather.model.Province;
import com.zhukun.coolweather.util.HttpCallBackListener;
import com.zhukun.coolweather.util.HttpUtil;
import com.zhukun.coolweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/8.
 */
public class ChooseAreaActivity extends Activity {
    public static final int PROVINCE_LEVEL = 1;
    public static final int CITY_LEVEL = 2;
    public static final int COUNTY_LEVEL = 3;
    private int currentLevel;
    private int Runtime = 0;
    private TextView titleText;
    private ListView listView ;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private ArrayAdapter<String> adapter ;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private CoolWeatherDB db;
    private ProgressDialog progressDialog;
    private boolean isFromWeatherActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("CityInfo" , 0);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        if(pref.getBoolean("city_selected",false) && !isFromWeatherActivity){
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("areId", pref.getString("areId", ""));
            intent.putExtra("countyId", pref.getInt("countyId", 0));
            intent.putExtra("countyName", pref.getString("countyName", ""));
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        RestoreData restoreData = new RestoreData();
        try {
            restoreData.createDatabase(ChooseAreaActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.choose_are);


        db = CoolWeatherDB.getInstance(this);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == PROVINCE_LEVEL) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == CITY_LEVEL) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == COUNTY_LEVEL){
                    //跳转到具体的天气页面；
                    String countyName = countyList.get(i).getCountyName();
                    String areId = countyList.get(i).getCountyCode();
                    int countyId = countyList.get(i).getId();
                    Log.d("ChooseArea","areId:" + areId);
                    Log.d("ChooseArea", "countyId:" + countyId);
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("areId", areId);
                    intent.putExtra("countyId", countyId);
                    intent.putExtra("countyName", countyName);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince() {
        provinceList = db.loadProvince();
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = PROVINCE_LEVEL;
        }else{
            queryServer("Province_Type");
        }
    }

    private void queryCities() {
        cityList = db.loadCity(selectedProvince.getId());
        if(cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = CITY_LEVEL;
        }
        else{
            queryServer("City_Type");
        }
    }

    private void queryCounties() {
        countyList = db.loadCounty(selectedCity.getId());
        if(countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = COUNTY_LEVEL;
        }else{
            queryServer("County_Type");
        }
    }

    private void queryServer(final String Type) {
        String address = "http://120.26.214.228/xlpc/CityMgr";
        Log.d("Tag", "queryServer" + Type);
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("Province_Type".equals(Type)){
                    result = Utility.handleProvince(db, response);
                }
                else if("City_Type".equals(Type)){
                    result = Utility.handleCity(db, response);
                }
                else if ("County_Type".equals(Type)){
                    result = Utility.handleCounty(db, response);
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("Province_Type".equals(Type)) queryProvince();
                            else if("City_Type".equals(Type)) queryCities();
                            else if("County_Type".equals(Type)) queryCounties();
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("首次加载，请稍等");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    @Override
    public void onBackPressed() {
        if(currentLevel == COUNTY_LEVEL){
            queryCities();
        }
        else if(currentLevel == CITY_LEVEL){
            queryProvince();
        }
        else {
            if(isFromWeatherActivity){
                SharedPreferences pref = getSharedPreferences("CityInfo" , 0);
                Intent intent = new Intent(this, WeatherActivity.class);
                intent.putExtra("areId", pref.getString("areId", ""));
                intent.putExtra("countyId", pref.getInt("countyId", 0));
                intent.putExtra("countyName", pref.getString("countyName", ""));
                startActivity(intent);
            }
            super.onBackPressed();
        }

    }
}

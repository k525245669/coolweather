package com.zhukun.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhukun.coolweather.R;
import com.zhukun.coolweather.activity.WeatherActivity;
import com.zhukun.coolweather.model.City;
import com.zhukun.coolweather.model.CoolWeatherDB;
import com.zhukun.coolweather.model.County;
import com.zhukun.coolweather.model.District;
import com.zhukun.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2015/9/8.
 */
public class Utility {

    public synchronized static boolean handleProvince(CoolWeatherDB db, String response) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            List<District> disList = gson.fromJson(response, new TypeToken<List<District>>() {
            }.getType());
            String foreProvince = "";
            for (District district : disList) {
                if (!foreProvince.equals(district.getProvcn())) {
                    Province province = new Province();
                    province.setProvinceName(district.getProvcn());
                    db.savaProvince(province);
                    foreProvince = district.getProvcn();
                }
            }
            return true;
        }
        return false;
    }

    public static boolean handleCity(CoolWeatherDB db, String response) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            List<District> disList = gson.fromJson(response, new TypeToken<List<District>>() {
            }.getType());
            String foreCity = "";
            List<Province> proList = db.loadProvince();
            int index = -1;
            String proName = "";
            int proId = 0;
            for (District district : disList) {
                String curCityName = district.getDistrictcn();
                String curProName = district.getProvcn();
                if (foreCity.equals(curCityName)) continue; //上个处理保存过的市名与这条数据相等，跳过
                if (!curProName.equals(proName)) {
                    index++;
                    proName = proList.get(index).getProvinceName();
                    proId = proList.get(index).getId();
                }
                City city = new City();
                city.setCityName(curCityName);
                city.setProvinceId(proId);
                db.saveCity(city);
                foreCity = curCityName;

            }
            return true;
        }
        return false;
    }

    public static boolean handleCounty(CoolWeatherDB db, String response) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            List<District> disList = gson.fromJson(response, new TypeToken<List<District>>() {
            }.getType());
            List<City> cityList = db.loadAllCity(); //get from db
            String dbCityName  = "";  //db data init
            int dbCityId = 0;
            int index = -1;
            for(District district : disList){
                if(!dbCityName.equals(district.getDistrictcn())){
                    index++;
                    dbCityName = cityList.get(index).getCityName();
                    dbCityId = cityList.get(index).getId();
                }
                County county = new County();
                county.setCountyCode(district.getAreaid());
                county.setCountyName(district.getNamecn());
                county.setCityId(dbCityId);
                db.saveCounty(county);
            }

        }
        return true;
    }
    public static void handleWeather(Context context, String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject basicInfo =  jsonObject.getJSONObject("f");
            String publishTime = basicInfo.getString("f0");
            JSONArray weatherArray = basicInfo.getJSONArray("f1");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);

            for(int i=0; i<weatherArray.length(); i++){
                JSONObject weatherObject = weatherArray.getJSONObject(i);
                String type1 = weatherObject.getString("fa");
                String type2 = weatherObject.getString("fb");
                String tmp1 = weatherObject.getString("fc");
                String tmp2 = weatherObject.getString("fd");
                SharedPreferences.Editor editor = context.getSharedPreferences("data" + i, 0).edit();
                editor.putBoolean("city_selected", true);
                editor.putString("type1", type1);
                editor.putString("type2", type2);
                editor.putString("tmp1", tmp1);
                editor.putString("tmp2", tmp2);
                editor.putString("publishTime", publishTime);
                editor.putString("currentTime", sdf.format(new Date().getTime()+i*24*60*60*1000));
                editor.commit();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String matchWeather(String weatherId){
        HashMap weatherMap = weatherMapInit();
        return (String) weatherMap.get(weatherId);
    }

    private static HashMap weatherMapInit() {
        HashMap weatherMap = new HashMap();
        weatherMap.put("00","晴");
        weatherMap.put("01","多云");
        weatherMap.put("02","阴");
        weatherMap.put("03","阵雨");
        weatherMap.put("04","雷阵雨");
        weatherMap.put("05","雷阵雨伴有冰雹");
        weatherMap.put("06","雨夹雪");
        weatherMap.put("07","小雨");
        weatherMap.put("08","中雨");
        weatherMap.put("09","大雨");
        weatherMap.put("10","暴雨");
        return weatherMap;
    }
    public static int getImageId(String type, int day) {
        if(day == WeatherActivity.DAY){
            switch(type){
                case "00":
                    return R.drawable.day00;
                case "01":
                    return R.drawable.day01;
                case "02":
                    return R.drawable.day02;
                case "03":
                    return R.drawable.day03;
                case "04":
                    return R.drawable.day04;
                case "05":
                    return R.drawable.day05;
                case "06":
                    return R.drawable.day06;
                case "07":
                    return R.drawable.day07;
                case "08":
                    return R.drawable.day08;
                case "09":
                    return R.drawable.day09;
                case "10":
                    return R.drawable.day10;
            }

        }
        else if(day == WeatherActivity.NIGHT){
            switch (type){
                case "00":
                    return R.drawable.night00;
                case "01":
                    return R.drawable.night01;
                case "02":
                    return R.drawable.night02;
                case "03":
                    return R.drawable.night03;
                case "04":
                    return R.drawable.night04;
                case "05":
                    return R.drawable.night05;
                case "06":
                    return R.drawable.night06;
                case "07":
                    return R.drawable.night07;
                case "08":
                    return R.drawable.night08;
                case "09":
                    return R.drawable.night09;
            }
        }
        return 0;
    }
}


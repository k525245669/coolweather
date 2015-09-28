package com.zhukun.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
import java.util.Calendar;
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
            SimpleDateFormat sdf = new SimpleDateFormat("M月d日",Locale.CHINA);

            for(int i=0; i<weatherArray.length(); i++){
                JSONObject weatherObject = weatherArray.getJSONObject(i);
                String type1 = weatherObject.getString("fa");
                String type2 = weatherObject.getString("fb");
                String tmp1 = weatherObject.getString("fc");
                String tmp2 = weatherObject.getString("fd");
                String weekTime = getWeekTime(i);
                SharedPreferences.Editor editor = context.getSharedPreferences("data" + i, 0).edit();
                editor.putBoolean("city_selected", true);
                editor.putString("type1", type1);
                editor.putString("type2", type2);
                editor.putString("tmp1", tmp1);
                editor.putString("tmp2", tmp2);
                editor.putString("publishTime", publishTime);
                editor.putString("weekTime", weekTime);
                editor.putString("currentTime", sdf.format(new Date().getTime()+i*24*60*60*1000));
                editor.commit();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getWeekTime(int i) {

        Calendar cal=Calendar.getInstance();
        int dayId = (cal.get(Calendar.DAY_OF_WEEK) + i) % 7;
        switch (dayId){
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
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
        weatherMap.put("11","大暴雨");
        weatherMap.put("12","特暴雨");
        weatherMap.put("13","降雪");
        weatherMap.put("14","小雪");
        weatherMap.put("15","中雪");
        weatherMap.put("16","大雪");
        weatherMap.put("17","暴雪");
        weatherMap.put("18","雾");
        weatherMap.put("19","冻雨");
        weatherMap.put("20","沙城暴");
        weatherMap.put("21","小到中雨");
        weatherMap.put("22","中到大雨");
        weatherMap.put("23","大到暴雨");
        weatherMap.put("24","暴雨到大暴雨");
        weatherMap.put("25","大暴雨到特大暴雨");
        weatherMap.put("26","小到中雪");
        weatherMap.put("27","中到大雪");
        weatherMap.put("28","大到暴雪");
        weatherMap.put("29","浮尘");
        weatherMap.put("30","扬沙");
        weatherMap.put("31","强沙尘暴");
        weatherMap.put("53","霾");





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
                case "11":
                    return R.drawable.day11 ;
                case "12":
                    return R.drawable.day12;
                case "13":
                    return R.drawable.day13;
                case "14":
                    return R.drawable.day14;
                case "15":
                    return R.drawable.day15;
                case "16":
                    return R.drawable.day16;
                case "17":
                    return R.drawable.day17;
                case "18":
                    return R.drawable.day18;
                case "19":
                    return R.drawable.day19;
                case "20":
                    return R.drawable.day20;
                case "21":
                    return R.drawable.day21;
                case "22":
                    return R.drawable.day22;
                case "23":
                    return R.drawable.day23;
                case "24":
                    return R.drawable.day24;
                case "25":
                    return R.drawable.day25;
                case "26":
                    return R.drawable.day26;
                case "27":
                    return R.drawable.day27;
                case "28":
                    return R.drawable.day28;
                case "29":
                    return R.drawable.day29;
                case "30":
                    return R.drawable.day30;
                case "31":
                    return R.drawable.day31;
                case "53":
                    return R.drawable.day53;













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
                case "10":
                    return R.drawable.night10;
                case "11":
                    return R.drawable.night11 ;
                case "12":
                    return R.drawable.night12;
                case "13":
                    return R.drawable.night13;
                case "14":
                    return R.drawable.night14;
                case "15":
                    return R.drawable.night15;
                case "16":
                    return R.drawable.night16;
                case "17":
                    return R.drawable.night17;
                case "18":
                    return R.drawable.night18;
                case "19":
                    return R.drawable.night19;
                case "20":
                    return R.drawable.night20;
                case "21":
                    return R.drawable.night21;
                case "22":
                    return R.drawable.night22;
                case "23":
                    return R.drawable.night23;
                case "24":
                    return R.drawable.night24;
                case "25":
                    return R.drawable.night25;
                case "26":
                    return R.drawable.night26;
                case "27":
                    return R.drawable.night27;
                case "28":
                    return R.drawable.night28;
                case "29":
                    return R.drawable.night29;
                case "30":
                    return R.drawable.night30;
                case "31":
                    return R.drawable.night31;
                case "53":
                    return R.drawable.night53;
            }
        }
        return 0;
    }
}


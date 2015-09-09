package com.zhukun.coolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhukun.coolweather.model.City;
import com.zhukun.coolweather.model.CoolWeatherDB;
import com.zhukun.coolweather.model.County;
import com.zhukun.coolweather.model.District;
import com.zhukun.coolweather.model.Province;

import java.util.List;

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
}


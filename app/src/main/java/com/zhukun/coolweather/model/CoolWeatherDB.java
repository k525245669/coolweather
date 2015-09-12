package com.zhukun.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhukun.coolweather.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/7.
 */
public class CoolWeatherDB {
    /*数据库名*/
    public static final String DB_NAME = "cool_weather";
    /*数据库版本*/
    public static final int VERSION = 1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;
    private List<District> districts  = new ArrayList<>();

    /*构造方法私有化*/
    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /*获取CoolWeatherDB实例*/
    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /*将Province实例存储到数据库*/
    public void savaProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            db.insert("Province", null, values);
        }
    }

    /*从数据库中读取全国所有省份信息*/
    public List<Province> loadProvince() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /*将City实例存储到数据库*/
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }
    private void GetCity(List<City> list, Cursor cursor){
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }
    }
    /*从数据库中读取对应省份的城市*/
    public List<City> loadCity(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        GetCity(list, cursor);
        return list;
    }
    public List<City> loadAllCity(){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, null , null, null, null, null);
        GetCity(list ,cursor);
        return list;
    }

    /*将County实例存储到数据库*/
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /*从数据库读取某城市下的所有县信息*/
    public List<County> loadCounty(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            }while(cursor.moveToNext());
        }
        return list;}
}

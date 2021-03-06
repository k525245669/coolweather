package com.zhukun.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/9/7.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    /*Province 建表语句*/
    public static final String  CREATE_PROVINCE = "create table Province ("
        + "id integer primary key autoincrement, "
        + "province_name text)";
    /*City 建表语句*/
    public static final String  CREATE_CITI = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "province_id integer)";
    /*County 建表语句*/
    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITI);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

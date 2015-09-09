package com.zhukun.coolweather.model;

/**
 * Created by Administrator on 2015/9/7.
 */
public class City {
    private  int id;
    private  String cityName;
    private int provinceId;

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

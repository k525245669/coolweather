package com.zhukun.coolweather.model;

/**
 * Created by Administrator on 2015/9/8.
 */
public class District {
    private String areaid;
    private String namecn;
    private String districtcn;
    private String provcn;

    public String getAreaid() {
        return areaid;
    }

    public String getDistrictcn() {
        return districtcn;
    }

    public String getNamecn() {
        return namecn;
    }

    public String getProvcn() {
        return provcn;
    }

    public void setProvcn(String provcn) {
        this.provcn = provcn;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public void setDistrictcn(String districtcn) {
        this.districtcn = districtcn;
    }

    public void setNamecn(String namecn) {
        this.namecn = namecn;
    }
}

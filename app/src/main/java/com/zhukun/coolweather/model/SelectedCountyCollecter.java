package com.zhukun.coolweather.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/17.
 */
public class SelectedCountyCollecter {
    public static List<County> selectedCounties = new ArrayList<County>();
    public static void addSelectedCounty(County county){
        for(County cy:selectedCounties) {
            if(cy.getCountyName().equals(county.getCountyName()))
                return;
        }
        selectedCounties.add(county);
    }
    public static void removeSelectedCounty(County county){
        selectedCounties.remove(county);
    }
}

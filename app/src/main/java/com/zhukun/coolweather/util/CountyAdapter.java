package com.zhukun.coolweather.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zhukun.coolweather.R;
import com.zhukun.coolweather.model.County;
import com.zhukun.coolweather.model.CountyWeather;

import java.util.List;

/**
 * Created by Administrator on 2015/9/17.
 */
public class CountyAdapter extends ArrayAdapter<CountyWeather> {
    private int resourceId;

    public CountyAdapter(Context context, int resource, List<CountyWeather> objects) {
        super(context, resource, objects);
        resourceId  = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CountyWeather county = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView dateText = (TextView) view.findViewById(R.id.city_selected_show_time);
        TextView cityText = (TextView) view.findViewById(R.id.city_selected_city_name);
        TextView tmpText = (TextView) view.findViewById(R.id.city_selected_tmp);
        dateText.setText(county.getDate());
        cityText.setText(county.getCountyName());
        tmpText.setText(county.getTmp());
        return view;
    }
}

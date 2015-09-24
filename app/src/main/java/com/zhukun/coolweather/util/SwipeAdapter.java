package com.zhukun.coolweather.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.zhukun.coolweather.R;
import com.zhukun.coolweather.model.CountyWeather;

import java.util.List;

/**
 * Created by Administrator on 2015/9/23.
 */
public class SwipeAdapter extends ArrayAdapter<CountyWeather> {
    private List<CountyWeather> countyWeathers ;
    private SwipeListView mSwipeListView ;
    private int resourceId;

    public SwipeAdapter(Context context, int resource, List<CountyWeather> objects, SwipeListView mSwipeListView) {
        super(context, resource, objects);
        resourceId = resource;
        this.mSwipeListView = mSwipeListView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CountyWeather county = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView dateText = (TextView) view.findViewById(R.id.city_selected_show_time);
        TextView cityText = (TextView) view.findViewById(R.id.city_selected_city_name);
        TextView tmpText = (TextView) view.findViewById(R.id.city_selected_tmp);
        Button deleteButton = (Button) view.findViewById(R.id.swipe_item_action_2);
        ImageView imageView = (ImageView) view.findViewById(R.id.city_selected_image);
        dateText.setText(county.getDate());
        cityText.setText(county.getCountyName());
        tmpText.setText(county.getTmp());
        imageView.setBackgroundResource(county.getImageId());
        Log.d("Adapter", county.getImageId() + "");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭动画
                mSwipeListView.closeAnimate(position);
                //调用dismiss方法删除该项，该方法在Activity中
                mSwipeListView.dismiss(position);
            }
        });
        return view;
    }
}

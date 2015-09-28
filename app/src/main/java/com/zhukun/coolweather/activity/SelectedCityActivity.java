package com.zhukun.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
//import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.zhukun.coolweather.R;
import com.zhukun.coolweather.model.County;
import com.zhukun.coolweather.model.CountyWeather;
import com.zhukun.coolweather.model.SelectedCountyCollecter;
import com.zhukun.coolweather.util.CountyAdapter;
import com.zhukun.coolweather.util.SwipeAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/17.
 */
public class SelectedCityActivity extends Activity {
    private CountyAdapter adapter;
    private ListView listView;
    private List<CountyWeather> countyWeathers = new ArrayList<>();
    private Button addButton;
    private SwipeListView mSwipeListView;
    private SwipeAdapter mAdapter;
    public static int deviceWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.city_selected_layout);
        mSwipeListView = (SwipeListView) findViewById(R.id.swipe_list);
        countyWeathers = DataSupport.findAll(CountyWeather.class);
        deviceWidth = getDeviceWidth();
        mAdapter = new SwipeAdapter(SelectedCityActivity.this, R.layout.swipe_item, countyWeathers, mSwipeListView);
        mSwipeListView.setAdapter(mAdapter);
        mSwipeListView.setSwipeListViewListener(new MyListener());
        addButton = (Button) findViewById(R.id.add_more_city);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedCityActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_selected_activity", true);
                startActivity(intent);
            }
        });
        reload();
    }
    private void reload() {
        //滑动时向左偏移量，根据设备的大小来决定偏移量的大小
        mSwipeListView.setOffsetLeft(deviceWidth * 5/ 6);
        mSwipeListView.setOffsetRight(deviceWidth * 5 / 6);
        //设置动画时间
        mSwipeListView.setAnimationTime(30);
        mSwipeListView.setSwipeOpenOnLongPress(false);
    }
    private int getDeviceWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    class MyListener extends BaseSwipeListViewListener {
        public void onClickFrontView(int position) {
            Intent intent = new Intent(SelectedCityActivity.this, WeatherActivity.class);
            String selectedCountyName = countyWeathers.get(position).getCountyName();
            for (CountyWeather county : countyWeathers) {
                if (county.getCountyName().equals(selectedCountyName)) {
                    intent.putExtra("areId", county.getAreaId());
                    intent.putExtra("countyId", county.getCountyId());
                    intent.putExtra("countyName", county.getCountyName());
                    startActivity(intent);
                    finish();
                }
            }
        }

        public void onDismiss(int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                CountyWeather DeleteCounty = countyWeathers.get(position);
                DeleteCounty.delete();
                countyWeathers.remove(position);
            }
            mAdapter.notifyDataSetChanged();
        }
            ;


    }


}

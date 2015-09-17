package com.zhukun.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.zhukun.coolweather.R;
import com.zhukun.coolweather.model.County;
import com.zhukun.coolweather.model.CountyWeather;
import com.zhukun.coolweather.model.SelectedCountyCollecter;
import com.zhukun.coolweather.util.CountyAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.city_selected_layout);
        listView = (ListView) findViewById(R.id.city_selected_list);
        CountyWeatherInit();
        adapter = new CountyAdapter(SelectedCityActivity.this, R.layout.city_selected_item, countyWeathers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到具体显示页面。
                Intent intent = new Intent(SelectedCityActivity.this, WeatherActivity.class);
                String selectedCountyName = countyWeathers.get(position).getCountyName();
                for (County county : (ArrayList<County>) SelectedCountyCollecter.selectedCounties) {
                    if (county.getCountyName().equals(selectedCountyName)) {
                        intent.putExtra("areId", county.getCountyCode());
                        intent.putExtra("countyId", county.getId());
                        intent.putExtra("countyName", county.getCountyName());
                        startActivity(intent);
                    }
                }
                //ListView 逻辑实现后，添加侧滑删除效果。
            }
        });
        addButton = (Button) findViewById(R.id.add_more_city);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedCityActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_selected_activity", true);
                startActivity(intent);
            }
        });

    }

    private void CountyWeatherInit() {
        List<County> countyList = (ArrayList<County>) SelectedCountyCollecter.selectedCounties;
        for(County county:countyList){
            String areId = county.getCountyCode();
            SharedPreferences spf = getSharedPreferences(areId, 0);
            CountyWeather countyWeather = new CountyWeather();
            countyWeather.setCountyName(county.getCountyName());
            countyWeather.setTmp(spf.getString("tmp", "") + "℃");
            countyWeather.setDate(spf.getString("date", ""));
            countyWeathers.add(countyWeather);
        }
    }
}

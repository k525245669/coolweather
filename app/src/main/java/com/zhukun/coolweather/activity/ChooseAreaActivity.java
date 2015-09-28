package com.zhukun.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhukun.coolweather.R;
import com.zhukun.coolweather.db.RestoreData;
import com.zhukun.coolweather.model.City;
import com.zhukun.coolweather.model.CoolWeatherDB;
import com.zhukun.coolweather.model.County;
import com.zhukun.coolweather.model.Province;
import com.zhukun.coolweather.model.SelectedCountyCollecter;
import com.zhukun.coolweather.util.HttpCallBackListener;
import com.zhukun.coolweather.util.HttpUtil;
import com.zhukun.coolweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/8.
 */
public class ChooseAreaActivity extends Activity {
    public static final int PROVINCE_LEVEL = 1;
    public static final int CITY_LEVEL = 2;
    public static final int COUNTY_LEVEL = 3;
    private int currentLevel;
    private int Runtime = 0;
    private TextView titleText;
    private ListView listView ;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private ArrayAdapter<String> adapter ;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private CoolWeatherDB db;
    private ProgressDialog progressDialog;
    private boolean isFromSelectedActivity;
    private EditText searchText;
    private Button rtnButton;
    private LinearLayout lout;
    private boolean fromResearch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("CityInfo" , 0);
        isFromSelectedActivity = getIntent().getBooleanExtra("from_selected_activity", false);
        if(pref.getBoolean("city_selected",false) && !isFromSelectedActivity){
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("areId", pref.getString("areId", ""));
            intent.putExtra("countyId", pref.getInt("countyId", 0));
            intent.putExtra("countyName", pref.getString("countyName", ""));
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            RestoreData.createDatabase(ChooseAreaActivity.this);
        } catch (IOException e){
            e.printStackTrace();
        }
        setContentView(R.layout.choose_are);
        db = CoolWeatherDB.getInstance(this);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        searchText = (EditText) findViewById(R.id.search_city);
        lout = (LinearLayout) findViewById(R.id.used_for_focus); //隐藏的控件，用于控制焦点
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(fromResearch){
                    lostFocus(searchText);
                }
                if (currentLevel == PROVINCE_LEVEL) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                    searchText.setHint("请输入查询城市");
                    //lostFocus(searchText);
                } else if (currentLevel == CITY_LEVEL) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                    searchText.setHint("请输入查询区县");
                    //lostFocus(searchText);
                } else if (currentLevel == COUNTY_LEVEL) {
                    //跳转到具体的天气页面；
                    String countyName = countyList.get(i).getCountyName();
                    String areId = countyList.get(i).getCountyCode();
                    int countyId = countyList.get(i).getId();
                    SelectedCountyCollecter.addSelectedCounty(countyList.get(i)); //用于在已选城市页面显示。
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("areId", areId);
                    intent.putExtra("countyId", countyId);
                    intent.putExtra("countyName", countyName);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String contains = searchText.getText().toString();
                boolean haveFind = false;
                if (count!=0 ) {
                    Province pro = db.searchProvince(contains);
                    if (pro != null) {
                        provinceList.clear();
                        provinceList.add(pro);
                        refreshList(pro.getProvinceName(), PROVINCE_LEVEL);
                        searchText.setHint("请输入查询城市");
                        fromResearch =true;
                    } else {
                        City city = db.searchCity(contains);
                        if (city != null) {
                            if(cityList == null){
                                cityList = new ArrayList<City>();
                            }
                            cityList.clear();
                            cityList.add(city);
                            refreshList(city.getCityName(), CITY_LEVEL);
                            searchText.setHint("请输入查询区县");
                            fromResearch =true;
                        } else {
                            County county = db.searchCounty(contains);
                            if (county != null) {
                                if(countyList == null){
                                    countyList = new ArrayList<County>();
                                }
                                countyList.clear();
                                countyList.add(county);
                                refreshList(county.getCountyName(), COUNTY_LEVEL);
                            } else {
                                refreshList("无法查询：" + contains, 0);
                            }
                        }

                    }
                }

            }


        });
        rtnButton = (Button) findViewById(R.id.search_return);
        rtnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
                queryProvince();
                lout.requestFocus(); //让隐藏的控件获取焦点，让Edit失去焦点
                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //隐藏软键盘

            }
        });

    }

    private void lostFocus(EditText searchText) {
        searchText.setText("");
        lout.requestFocus(); //让隐藏的控件获取焦点，让Edit失去焦点
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //隐藏软键盘
    }

    private void refreshList(String cityName, int level) {
        dataList.clear();
        dataList.add(cityName);
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        titleText.setText("模糊搜索");
        currentLevel = level;
    }

    private void queryProvince() {
        provinceList = db.loadProvince();
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = PROVINCE_LEVEL;
        }else{
            queryServer("Province_Type");
        }
    }

    private void queryCities() {
        cityList = db.loadCity(selectedProvince.getId());
        if(cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = CITY_LEVEL;
        }
        else{
            queryServer("City_Type");
        }
    }

    private void queryCounties() {
        countyList = db.loadCounty(selectedCity.getId());
        if(countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = COUNTY_LEVEL;
        }else{
            queryServer("County_Type");
        }
    }

    private void queryServer(final String Type) {
        String address = "http://120.26.214.228/xlpc/CityMgr";
        Log.d("Tag", "queryServer" + Type);
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("Province_Type".equals(Type)){
                    result = Utility.handleProvince(db, response);
                }
                else if("City_Type".equals(Type)){
                    result = Utility.handleCity(db, response);
                }
                else if ("County_Type".equals(Type)){
                    result = Utility.handleCounty(db, response);
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("Province_Type".equals(Type)) queryProvince();
                            else if("City_Type".equals(Type)) queryCities();
                            else if("County_Type".equals(Type)) queryCounties();
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("首次加载，请稍等");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    @Override
    public void onBackPressed() {
        if(currentLevel == COUNTY_LEVEL){
            queryCities();
        }
        else if(currentLevel == CITY_LEVEL){
            queryProvince();
        }
        else {
            if(isFromSelectedActivity){
               /* SharedPreferences pref = getSharedPreferences("CityInfo" , 0);
                Intent intent = new Intent(this, WeatherActivity.class);
                intent.putExtra("areId", pref.getString("areId", ""));
                intent.putExtra("countyId", pref.getInt("countyId", 0));
                intent.putExtra("countyName", pref.getString("countyName", ""));*/
                Intent intent = new Intent(this, SelectedCityActivity.class);
                startActivity(intent);
            }
            super.onBackPressed();
        }

    }
}

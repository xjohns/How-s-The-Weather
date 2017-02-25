package com.xiong.howstheweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiong.howstheweather.R;
import com.xiong.howstheweather.util.HttpCallbackListener;
import com.xiong.howstheweather.util.HttpUtil;
import com.xiong.howstheweather.util.Utility;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Created by xiong on 2017/2/23.
 */

public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于显示更新时间
     */
    private TextView updatetimeText;
    /**
     * 用于显示天气描述信息
     */
    private TextView typeText;
    /**
     * 用于显示温度
     */
    private TextView wenduText;
    /**
     * 用于显示湿度
     */
    private TextView shiduText;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;
    /**
     * 切换城市按钮
     */
    private Button changeCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info);
        cityNameText = (TextView) findViewById(R.id.city_name);
        updatetimeText = (TextView) findViewById(R.id.tV_updatetime);
        typeText = (TextView) findViewById(R.id.weather_type);
        wenduText = (TextView) findViewById(R.id.wendu);
        shiduText = (TextView) findViewById(R.id.shidu);
        currentDateText = (TextView) findViewById(R.id.current_date);
        changeCity = (Button) findViewById(R.id.change_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        changeCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            // 有县级代号时就去查询天气
            updatetimeText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            try {
                queryWeatherCode(countyCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
           // 没有县级代号时就直接显示本地天气
            showWeather();
        }
    }

    /**
     * 处理点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_city:{
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.refresh_weather:{
                updatetimeText.setText(" 同步中...");
                SharedPreferences prefs = getSharedPreferences("weather_info", MODE_PRIVATE);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryFromServer(weatherCode);
                }
                break;
            }
        }
    }

    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode){
        SAXReader saxReader = new SAXReader();
        org.dom4j.Document doc = null;
        try {
            InputStream is = getAssets().open("code.xml");
            doc = saxReader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String xpath = "province[@id=\'" + provinceCode + "\']/city[@id=\'" + cityCode + "\']/county[@id=\'" + countyCode + "\']";
        String xpath = "//county[@id=\'" + countyCode + "\']";
        Node county = doc.selectSingleNode(xpath);
        String weatherCode = county.valueOf("@weatherCode");//输出该节点属性名为weatherCode的属性值
        queryFromServer(weatherCode);
    }

    /**
     * 查询天气代号所对应的天气。
     */
    private void queryFromServer(final String weatherCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + weatherCode;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream is = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    is = connection.getInputStream();
                     //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, is, weatherCode);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather() {
        SharedPreferences prefs = getSharedPreferences("weather_info", MODE_PRIVATE);
        cityNameText.setText(prefs.getString("city_name", ""));
        wenduText.setText(prefs.getString("wendu", ""));
        shiduText.setText(prefs.getString("shidu", ""));
        typeText.setText(prefs.getString("type", ""));
        updatetimeText.setText("今天" + prefs.getString("updatetime", "") + "发布");
        currentDateText.setText(prefs.getString("date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}

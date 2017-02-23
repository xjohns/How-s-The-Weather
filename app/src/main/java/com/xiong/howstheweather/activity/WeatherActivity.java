package com.xiong.howstheweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiong.howstheweather.R;
import com.xiong.howstheweather.util.HttpCallbackListener;
import com.xiong.howstheweather.util.HttpUtil;
import com.xiong.howstheweather.util.Utility;

import static android.R.attr.type;

/**
 * Created by xiong on 2017/2/23.
 */

public class WeatherActivity extends Activity {
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
     * 用于显示天气建议
     */
    private TextView suggestText;
//    /**
//     * 切换城市按钮
//     */
//    private Button switchCity;
//    /**
//     * 更新天气按钮
//     */
//    private Button refreshWeather;

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
        suggestText = (TextView) findViewById(R.id.weather_suggest);
//        switchCity = (Button) findViewById(R.id.switch_city);
//        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            // 有县级代号时就去查询天气
            updatetimeText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
           // 没有县级代号时就直接显示本地天气
            showWeather();
        }
    }

    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + weatherCode;
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, String countyCode) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, address);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatetimeText.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

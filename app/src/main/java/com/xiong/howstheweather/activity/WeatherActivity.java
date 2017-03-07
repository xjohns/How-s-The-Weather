package com.xiong.howstheweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiong.howstheweather.R;
import com.xiong.howstheweather.util.Utility;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by xiong on 2017/2/23.
 */

public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    /**
     * 各类要显示的天气信息
     */
    private TextView cityNameText;
    private TextView updatetimeText;
    private TextView typeText;
    private TextView wenduText;
    private TextView shiduText;
    private TextView currentDateText;
    private TextView fengxiangText;
    private TextView fengliText;
    private TextView sunriseText;
    private TextView sunsetText;
    private TextView todayWendu;
    private TextView todayDay;
    private TextView todayNight;
    private TextView tomorrowWendu;
    private TextView tomorrowDay;
    private TextView tomorrowNight;
    private TextView thirdWendu;
    private TextView thirdDay;
    private TextView thirdNight;
    private TextView fourthWendu;
    private TextView fourthDay;
    private TextView fourthNight;
    private TextView fifthWendu;
    private TextView fifthDay;
    private TextView fifthNight;
    private TextView alarmInfo;
    private TextView environmentInfo;
    /**
     * 切换城市按钮
     */
    private Button changeCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;
    /**
     * 用于动画播放的天气图片
     */
    private ImageView weatherPic;
    private ImageView weatherPicSpecial;
    private ImageView weatherPicSpecial2;
    /**
     * 创建三个布局对象，通过在代码中改变其背景颜色，达到根据天气类型动态改变背景的功能
     */
    private RelativeLayout topFunction;
    private RelativeLayout mainView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_info_layout);
        init();
        //计算屏幕大小，并将mainView的布局高度铺满当前屏幕
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float mScreenHeightPx = dm.heightPixels;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mainView.getLayoutParams();
        layoutParams.height = (int) (mScreenHeightPx - 60 * dm.density);//layoutParams.height单位是px
        mainView.setLayoutParams(layoutParams);
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

    //专门初始化各控件
    private void init() {
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info);
        cityNameText = (TextView) findViewById(R.id.city_name);
        updatetimeText = (TextView) findViewById(R.id.tV_updatetime);
        typeText = (TextView) findViewById(R.id.weather_type);
        wenduText = (TextView) findViewById(R.id.wendu);
        shiduText = (TextView) findViewById(R.id.shidu);
        currentDateText = (TextView) findViewById(R.id.current_date);
        fengxiangText = (TextView) findViewById(R.id.fengxiang);
        fengliText = (TextView) findViewById(R.id.fengli);
        sunriseText = (TextView) findViewById(R.id.sunrise_1);
        sunsetText = (TextView) findViewById(R.id.sunset_1);
        alarmInfo = (TextView) findViewById(R.id.alarm_info);
        environmentInfo = (TextView) findViewById(R.id.environment_info);

        todayWendu = (TextView) findViewById(R.id.today_wendu);
        todayDay = (TextView) findViewById(R.id.today_day);
        todayNight = (TextView) findViewById(R.id.today_night);

        tomorrowWendu = (TextView) findViewById(R.id.tomorrow_wendu);
        tomorrowDay = (TextView) findViewById(R.id.tomorrow_day);
        tomorrowNight = (TextView) findViewById(R.id.tomorrow_night);

        thirdWendu = (TextView) findViewById(R.id.third_wendu);
        thirdDay = (TextView) findViewById(R.id.third_day);
        thirdNight = (TextView) findViewById(R.id.third_night);

        fourthWendu = (TextView) findViewById(R.id.fourth_wendu);
        fourthDay = (TextView) findViewById(R.id.fourth_day);
        fourthNight = (TextView) findViewById(R.id.fourth_night);

        fifthWendu = (TextView) findViewById(R.id.fifth_wendu);
        fifthDay = (TextView) findViewById(R.id.fifth_day);
        fifthNight = (TextView) findViewById(R.id.fifth_night);

        weatherPic = (ImageView) findViewById(R.id.weather_pic);
        weatherPicSpecial = (ImageView) findViewById(R.id.weather_pic_special);
        weatherPicSpecial2 = (ImageView) findViewById(R.id.weather_pic_special_2);
        changeCity = (Button) findViewById(R.id.change_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        changeCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        topFunction = (RelativeLayout) findViewById(R.id.top_function);
        mainView = (RelativeLayout) findViewById(R.id.main_view);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
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
        //打开所有获取并保存的天气信息
        SharedPreferences prefs = getSharedPreferences("weather_info", MODE_PRIVATE);
        SharedPreferences prefs1 = getSharedPreferences("weather1", MODE_PRIVATE);
        SharedPreferences prefs2 = getSharedPreferences("weather2", MODE_PRIVATE);
        SharedPreferences prefs3 = getSharedPreferences("weather3", MODE_PRIVATE);
        SharedPreferences prefs4 = getSharedPreferences("weather4", MODE_PRIVATE);
        SharedPreferences prefs5 = getSharedPreferences("weather5", MODE_PRIVATE);
        cityNameText.setText(prefs.getString("city_name", ""));
        wenduText.setText(prefs.getString("wendu", ""));
        shiduText.setText(prefs.getString("shidu", ""));
        fengxiangText.setText(prefs.getString("fengxiang", "") + " ");
        fengliText.setText(prefs.getString("fengli", ""));
        sunriseText.setText("日出：" + prefs.getString("sunrise", ""));
        sunsetText.setText("日落：" + prefs.getString("sunset", ""));
        updatetimeText.setText("今天" + prefs.getString("updatetime", "") + "发布");
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        typeText.setText(prefs1.getString("dayType", ""));
        currentDateText.setText(prefs1.getString("date", ""));
        if (prefs.getBoolean("haveAlarm", false)){
            alarmInfo.setText(prefs.getString("alarmText", "") + "\n" + prefs.getString("alarm_details", ""));
            alarmInfo.setVisibility(View.VISIBLE);
        } else {
            alarmInfo.setVisibility(View.GONE);
        }
        if (prefs.getBoolean("haveEnvironment", false)){
            environmentInfo.setText("更新时间:今天" + prefs.getString("time", "") + "\n" +
                    "aqi:" + prefs.getString("aqi", "无") + " |pm25:" + prefs.getString("pm25", "无") + " |o3:" + prefs.getString("o3", "无")+ " |co:" + prefs.getString("co", "无") +
                    " |pm10:" + prefs.getString("pm10", "无") + " |so2:" + prefs.getString("so2", "无") + " |no2:" + prefs.getString("no2", "无") + " |空气质量:" + prefs.getString("quality", "暂无") +
                    "|主要污染物:" + prefs.getString("MajorPollutants", "暂无") + "\n" +
                    "建议:" + prefs.getString("suggest", "暂无"));
            environmentInfo.setVisibility(View.VISIBLE);
        } else {
            environmentInfo.setVisibility(View.GONE);
        }

        todayWendu.setText("今天" + "\n\n" + prefs1.getString("high", "") + "\n" + prefs1.getString("low", ""));
        todayDay.setText("白天" + "\n\n" + prefs1.getString("dayType", "") + "\n" + prefs1.getString("dayFengXiang", "") + "\n" + prefs1.getString("dayFengLi", ""));
        todayNight.setText("晚上" + "\n\n" + prefs1.getString("nightType", "") + "\n" + prefs1.getString("nightFengXiang", "") + "\n" + prefs1.getString("nightFengLi", ""));

        tomorrowWendu.setText("明天" + "\n\n" + prefs2.getString("high", "") + "\n" + prefs2.getString("low", ""));
        tomorrowDay.setText("白天" + "\n\n" + prefs2.getString("dayType", "") + "\n" + prefs2.getString("dayFengXiang", "") + "\n" + prefs2.getString("dayFengLi", ""));
        tomorrowNight.setText("晚上" + "\n\n" + prefs2.getString("nightType", "") + "\n" + prefs2.getString("nightFengXiang", "") + "\n" + prefs2.getString("nightFengLi", ""));

        thirdWendu.setText(prefs3.getString("date", "") + "\n\n" + prefs3.getString("high", "") + "\n" + prefs3.getString("low", ""));
        thirdDay.setText("白天" + "\n\n" + prefs3.getString("dayType", "") + "\n" + prefs3.getString("dayFengXiang", "") + "\n" + prefs3.getString("dayFengLi", ""));
        thirdNight.setText("晚上" + "\n\n" + prefs3.getString("nightType", "") + "\n" + prefs3.getString("nightFengXiang", "") + "\n" + prefs3.getString("nightFengLi", ""));

        fourthWendu.setText(prefs4.getString("date", "") + "\n\n" + prefs4.getString("high", "") + "\n" + prefs4.getString("low", ""));
        fourthDay.setText("白天" + "\n\n" + prefs4.getString("dayType", "") + "\n" + prefs4.getString("dayFengXiang", "") + "\n" + prefs4.getString("dayFengLi", ""));
        fourthNight.setText("晚上" + "\n\n" + prefs4.getString("nightType", "") + "\n" + prefs4.getString("nightFengXiang", "") + "\n" + prefs4.getString("nightFengLi", ""));

        fifthWendu.setText(prefs5.getString("date", "") + "\n\n" + prefs5.getString("high", "") + "\n" + prefs5.getString("low", ""));
        fifthDay.setText("白天" + "\n\n" + prefs5.getString("dayType", "") + "\n" + prefs5.getString("dayFengXiang", "") + "\n" + prefs5.getString("dayFengLi", ""));
        fifthNight.setText("晚上" + "\n\n" + prefs5.getString("nightType", "") + "\n" + prefs5.getString("nightFengXiang", "") + "\n" + prefs5.getString("nightFengLi", ""));
        //由于天气种类较复杂，采用关键字筛选可以匹配所有天气
        if (typeText.getText().toString().contains("雪")){
            topFunction.setBackgroundColor(Color.parseColor("#a1ade7"));
            scrollView.setBackgroundResource(R.drawable.shape_blue);
            weatherPic.setImageResource(R.drawable.xue);
            Animation rotate = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.xue);
            weatherPic.startAnimation(rotate);
        } else if (typeText.getText().toString().contains("雨")){
            topFunction.setBackgroundColor(Color.parseColor("#282829"));
            scrollView.setBackgroundResource(R.drawable.shape_gray);
            weatherPicSpecial.setImageResource(R.drawable.xiaoyu);
            weatherPicSpecial2.setImageResource(R.drawable.xiaoyu);
            Animation fall = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.fall);
            Animation fall2 = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.fall2);
            weatherPicSpecial.startAnimation(fall);
            weatherPicSpecial2.startAnimation(fall2);
        } else if (typeText.getText().toString().contains("阴")){
            topFunction.setBackgroundColor(Color.parseColor("#a1ade7"));
            scrollView.setBackgroundResource(R.drawable.shape_blue);
            weatherPic.setImageResource(R.drawable.ying);
            Animation ying = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.ying);
            weatherPic.startAnimation(ying);
        } else if (typeText.getText().toString().contains("多云")){
            topFunction.setBackgroundColor(Color.parseColor("#a1ade7"));
            scrollView.setBackgroundResource(R.drawable.shape_blue);
            weatherPic.setImageResource(R.drawable.duoyun);
            Animation duoyun = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.duoyun);
            weatherPic.startAnimation(duoyun);
        } else {
            topFunction.setBackgroundColor(Color.parseColor("#e35711"));
            scrollView.setBackgroundResource(R.drawable.shape_yellow);
            weatherPic.setImageResource(R.drawable.qing);
            Animation qing = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.qing);
            weatherPic.startAnimation(qing);
        }
    }
}

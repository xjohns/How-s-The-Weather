package com.xiong.howstheweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.xiong.howstheweather.db.WeatherDB;
import com.xiong.howstheweather.model.City;
import com.xiong.howstheweather.model.County;
import com.xiong.howstheweather.model.Province;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;


/**
 * Created by xiong on 2017/2/22.
 * 工具类
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(WeatherDB weatherDB, String response){
        if (!TextUtils.isEmpty(response)){//判断response字符串是否不为null，且长度不为0.
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0 ){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析到的数据存储到Province表
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB, String response, int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0 ){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析到的数据存储到City表
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountiesResponse(WeatherDB weatherDB, String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0 ){
                for (String c : allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析到的数据存储到County表
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的xml数据，并将解析出的数据存储到本地
     */
    public static void handleWeatherResponse(Context context, InputStream responce, String weatherCode){
        SAXReader reader = new SAXReader();
        org.dom4j.Document doc = null;
        try {
            doc = reader.read(responce);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element resp = doc.getRootElement();
        String city = resp.element("city").getTextTrim();
        String updatetime = resp.element("updatetime").getTextTrim();
        String wendu = resp.element("wendu").getTextTrim();
        String shidu = resp.element("shidu").getTextTrim();
        String fengxiang = resp.element("fengxiang").getTextTrim();
        String fengli = resp.element("fengli").getTextTrim();
        String sunrise = resp.element("sunrise_1").getTextTrim();
        String sunset = resp.element("sunset_1").getTextTrim();
        String date;
        String high;
        String low;
        String dayType;
        String dayFengXiang;
        String dayFengLi;
        String nightType;
        String nightFengXiang;
        String nightFengLi;
        //循环赋值，解析并保存天气信息，i代表第i天
        for (int i = 1; i < 6; i ++){
            Element weather = (Element) doc.selectSingleNode("//forecast/weather[" + i +"]");
            date = weather.element("date").getTextTrim();
            high = weather.element("high").getTextTrim();
            low = weather.element("low").getTextTrim();
            dayType = weather.element("day").element("type").getTextTrim();
            dayFengXiang = weather.element("day").element("fengxiang").getTextTrim();
            dayFengLi = weather.element("day").element("fengli").getTextTrim();
            nightType = weather.element("night").element("type").getTextTrim();
            nightFengXiang = weather.element("night").element("fengxiang").getTextTrim();
            nightFengLi = weather.element("night").element("fengli").getTextTrim();
            SharedPreferences.Editor editor = context.getSharedPreferences("weather" + i, Context.MODE_PRIVATE).edit();
            editor.putString("date", date);
            editor.putString("high", high);
            editor.putString("low", low);
            editor.putString("dayType", dayType);
            editor.putString("dayFengXiang", dayFengXiang);
            editor.putString("dayFengLi", dayFengLi);
            editor.putString("nightType", nightType);
            editor.putString("nightFengXiang", nightFengXiang);
            editor.putString("nightFengLi", nightFengLi);
            editor.commit();
        }
        SharedPreferences.Editor editor = context.getSharedPreferences("weather_info", Context.MODE_PRIVATE).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", city);
        editor.putString("weather_code", weatherCode);
        editor.putString("updatetime", updatetime);
        editor.putString("wendu", wendu);
        editor.putString("shidu", shidu);
        editor.putString("fengxiang", fengxiang);
        editor.putString("fengli", fengli);
        editor.putString("sunrise", sunrise);
        editor.putString("sunset", sunset);
        if (resp.element("alarm") != null){
            String alarmText = resp.element("alarm").element("alarmText").getTextTrim();
            String alarm_details = resp.element("alarm").element("alarm_details").getTextTrim();
            editor.putBoolean("haveAlarm", true);
            editor.putString("alarmText", alarmText);
            editor.putString("alarm_details", alarm_details);
        } else {
            editor.putBoolean("haveAlarm", false);
        }
        if (resp.element("environment") != null){
            String aqi = resp.element("environment").element("aqi").getTextTrim();
            String pm25 = resp.element("environment").element("pm25").getTextTrim();
            String suggest = resp.element("environment").element("suggest").getTextTrim();
            String quality = resp.element("environment").element("quality").getTextTrim();
            String MajorPollutants = resp.element("environment").element("MajorPollutants").getTextTrim();
            String o3 = resp.element("environment").element("o3").getTextTrim();
            String co = resp.element("environment").element("co").getTextTrim();
            String pm10 = resp.element("environment").element("pm10").getTextTrim();
            String so2 = resp.element("environment").element("so2").getTextTrim();
            String no2 = resp.element("environment").element("no2").getTextTrim();
            String time = resp.element("environment").element("time").getTextTrim();
            editor.putBoolean("haveEnvironment", true);
            editor.putString("aqi", aqi);
            editor.putString("pm25", pm25);
            editor.putString("suggest", suggest);
            editor.putString("quality", quality);
            if (MajorPollutants != null){
                editor.putString("MajorPollutants", MajorPollutants);
            } else {
                editor.putString("MajorPollutants", "暂无");
            }
            editor.putString("o3", o3);
            editor.putString("co", co);
            editor.putString("pm10", pm10);
            editor.putString("so2", so2);
            editor.putString("no2", no2);
            editor.putString("time", time);
        } else {
            editor.putBoolean("haveEnvironment", false);
        }
        editor.commit();
    }
}


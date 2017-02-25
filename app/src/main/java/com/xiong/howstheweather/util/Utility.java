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
        Element weatherElement = (Element) doc.selectSingleNode("//forecast/weather[1]");
        String city = resp.element("city").getTextTrim();
        String updatetime = resp.element("updatetime").getTextTrim();
        String wendu = resp.element("wendu").getTextTrim();
        String shidu = resp.element("shidu").getTextTrim();
        String date = weatherElement.element("date").getTextTrim();
        String type = weatherElement.element("day").element("type").getTextTrim();
        saveWeatherInfo(context, city, weatherCode, updatetime, wendu, shidu, date, type);
        Log.d("xys", city + wendu);
    }

    /**
     *  将服务器返回的所有天气信息存储到SharedPreferences 文件中。
     */
    private static void saveWeatherInfo(Context context, String city, String weatherCode, String updatetime, String wendu, String shidu, String date, String type) {
        SharedPreferences.Editor editor = context.getSharedPreferences("weather_info", Context.MODE_PRIVATE).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", city);
        editor.putString("weather_code", weatherCode);
        editor.putString("updatetime", updatetime);
        editor.putString("wendu", wendu);
        editor.putString("shidu", shidu);
        editor.putString("date", date);
        editor.putString("type", type);
        Log.d("xys", city);
        Log.d("xys", weatherCode);
        editor.commit();
    }

}


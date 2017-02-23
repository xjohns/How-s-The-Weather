package com.xiong.howstheweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.xiong.howstheweather.db.WeatherDB;
import com.xiong.howstheweather.model.City;
import com.xiong.howstheweather.model.County;
import com.xiong.howstheweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
     * 解析服务器返回的xml数据和另一个的json数据，并将解析出的数据存储到本地
     */
    public static void handleWeatherResponse(Context context, String response){
        //创建DocumentBuilderFactory对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String weatherCode = weatherInfo.getString("cityid");
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder的parse方法加载xml文件，并创建org.w3c.dom下的Document对象接收
            Document doc = db.parse(response);
            //获取所需的节点值
            String city = doc.getElementsByTagName("city").item(0).getNodeValue();
            String updatetime = doc.getElementsByTagName("updatetime").item(0).getNodeValue();
            String wendu = doc.getElementsByTagName("wendu").item(0).getNodeValue();
            String shidu = doc.getElementsByTagName("shidu").item(0).getNodeValue();
            String suggest = doc.getChildNodes().item(10).getChildNodes().item(2).getNodeValue();
            String date = doc.getChildNodes().item(12).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
            String type = doc.getChildNodes().item(12).getChildNodes().item(0).getChildNodes().item(3).getChildNodes().item(0).getNodeValue();
            saveWeatherInfo(context, city, weatherCode, updatetime, wendu, shidu, suggest, date, type);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  将服务器返回的所有天气信息存储到SharedPreferences 文件中。
     */
    private static void saveWeatherInfo(Context context, String city, String weatherCode, String updatetime, String wendu, String shidu, String suggest, String date, String type) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", city);
        editor.putString("weather_code", weatherCode);
        editor.putString("updatetime", updatetime);
        editor.putString("wendu", wendu);
        editor.putString("shidu", shidu);
        editor.putString("suggest", suggest);
        editor.putString("date", date);
        editor.putString("type", type);
        editor.commit();
    }

}


package com.xiong.howstheweather.util;

import android.text.TextUtils;

import com.xiong.howstheweather.db.WeatherDB;
import com.xiong.howstheweather.model.City;
import com.xiong.howstheweather.model.County;
import com.xiong.howstheweather.model.Province;

/**
 * Created by xiong on 2017/2/22.
 * 解析和处理"代号|城市,代号|城市"这类数据
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(WeatherDB weatherDB, String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0 ){
                for (String p : allProvinces){
                    String[] array = response.split("\\|");
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
                    String[] array = response.split("\\|");
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
                    String[] array = response.split("\\|");
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
}

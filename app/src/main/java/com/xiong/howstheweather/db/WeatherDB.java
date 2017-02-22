package com.xiong.howstheweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiong.howstheweather.model.City;
import com.xiong.howstheweather.model.County;
import com.xiong.howstheweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiong on 2017/2/22.
 */

public class WeatherDB {

    /**
     * 数据库名
     */
    private static final String DB_NAME = "weather";

    /**
     * 数据库版本
     */
    private static final int VERSION = 1;

    private static WeatherDB weatherDB;

    private SQLiteDatabase sqLiteDatabase;

    /**
     * 将构造方法私有化
     */
    private WeatherDB(Context context){
        WeatherOpenHelper weatherOpenHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
        sqLiteDatabase = weatherOpenHelper.getWritableDatabase();
    }

    /**
     * 获取WeatherDB的实例
     */
    public synchronized static WeatherDB getInstance(Context context){
        if (weatherDB == null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            sqLiteDatabase.insert("province", null, values);
        }
    }

    /**
     * 从数据库读取全国的省份信息
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = sqLiteDatabase.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            sqLiteDatabase.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = sqLiteDatabase.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将County实例存储到数据库
     */
    public void saveCounty(County county){
        if (county != null){
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            sqLiteDatabase.insert("County", null, values);
        }
    }

    /**
     * 从数据库中读取某城市下所有的县信息
     */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = sqLiteDatabase.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }
}

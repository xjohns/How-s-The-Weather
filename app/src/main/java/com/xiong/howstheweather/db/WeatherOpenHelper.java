package com.xiong.howstheweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiong on 2017/2/22.
 * 数据库建表。
 */

public class WeatherOpenHelper extends SQLiteOpenHelper {
    /**
     * Province表的建表语句
     */
    private static final String CREATE_PROVINCE = "create table province(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    /**
     * City表的建表语句
     */
    private static final String CREATE_CITY = "create table city(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";

    /**
     * County表的建表语句
     */
    private static final String CREATE_COUNTY = "create table county(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer)";

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);//创建表Province
        db.execSQL(CREATE_CITY);//创建表City
        db.execSQL(CREATE_COUNTY);//创建表County
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

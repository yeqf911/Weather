package com.xhliyuxiao.weather;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xhliyuxiao.weather.bean.City;
import com.xhliyuxiao.weather.utils.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;


public class WeatherApplication extends Application {
    private static final String TAG = "WeatherApplication";
    private static Application application;
    private SQLiteHelper sqLiteHelper;
    private List<City> cityList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
        application = this;
        sqLiteHelper = new SQLiteHelper(this, SQLiteHelper.DB_NAME, null, SQLiteHelper.VERSION);
        cityList = new ArrayList<>();

        // 初始化城市的数据，就是添加10个城市
        initCityList();
    }

    private void initCityList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                update();
            }
        }).start();
    }

    // 往 SQLite 数据库中插入10条数据
    private void insertCities() {
        SQLiteDatabase database = sqLiteHelper.getWritableDatabase();
        List<City> initCities = new ArrayList<>();

        initCities.add(new City("北京", "beijing", "101010100", "北京"));
        initCities.add(new City("杭州", "hangzhou", "101210101", "浙江"));
        initCities.add(new City("合肥", "hefei", "101220102", "安徽"));
        initCities.add(new City("深圳", "shenzhen", "101280603", "广东"));
        initCities.add(new City("上海", "shanghai", "101020100", "上海"));
        initCities.add(new City("广州", "guangzhou", "101280101", "广东"));
        initCities.add(new City("洛阳", "luoyang", "101180901", "河南"));
        initCities.add(new City("南京", "nanjing", "101190101", "江苏"));
        initCities.add(new City("成都", "chengdu", "101270101", "四川"));
        initCities.add(new City("重庆", "chongqin", "101040100", "重庆"));


        for (City city : initCities) {
            Log.d(TAG, "---->>  insertCities: " + city.getName());
            ContentValues values = new ContentValues();
            values.put("name", city.getName());
            values.put("tag", city.getTag());
            values.put("code", city.getCode());
            values.put("province", city.getProvince());
            database.insert(SQLiteHelper.TABLE_CITY, null, values);
        }

        this.cityList.clear();
        this.cityList = initCities;
        database.close();
    }

    private void update() {
        Cursor cursor = sqLiteHelper.getReadableDatabase().query(SQLiteHelper.TABLE_CITY,
                new String[]{"id", "name", "tag", "code", "province"}, null, null, null, null, null);

        // 先清空 cityList
        this.cityList.clear();

        // 如果数据库有数据了，就不需要初始化10条数据
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String tag = cursor.getString(2);
                String code = cursor.getString(3);
                String province = cursor.getString(4);
                City city = new City(name, tag, code, province);
                Log.d(TAG, "========>>>> " + id + " " + name);
                this.cityList.add(city);
                cursor.moveToNext();
            }
        } else {
            insertCities();
        }

        for (City city :
                this.cityList) {
            Log.d(TAG, "-------------> " + city.getName());
        }

        cursor.close();
    }

    public static Application getInstance() {
        return application;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void addCity(City city) {
        if (exist(city.getName())) {
            return;
        }

        SQLiteDatabase database = sqLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", city.getName());
        values.put("tag", city.getTag());
        values.put("code", city.getCode());
        values.put("province", city.getProvince());
        database.insert(SQLiteHelper.TABLE_CITY, null, values);
        database.close();
        update();
    }

    public boolean exist(String name) {
        for (City city : cityList) {
            if (city.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}

package com.xhliyuxiao.weather.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DB_NAME = "weather.db";
    public static final String TABLE_CITY = "cities";
    private Context context;

    private static final String CREATE_CITY_TABLE = "create table cities(id integer primary key autoincrement," +
            "name varchar(64)," +
            "tag varchar(64)," +
            "code varchar(64)," +
            "province varchar(64))";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

//    public List<City> getCityList() {
//        List<City> cities = new ArrayList<>();
//        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CITY, null);
//        while (cursor.moveToNext()) {
//            String name = cursor.getString(cursor.getColumnIndex("name"));
//            String code = cursor.getString(cursor.getColumnIndex("code"));
//            String province = cursor.getString(cursor.getColumnIndex("province"));
//            City city = new City(name, code, province);
//            cities.add(city);
//        }
//        return cities;
//    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

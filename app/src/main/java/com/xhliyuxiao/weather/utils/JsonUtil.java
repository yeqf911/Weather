package com.xhliyuxiao.weather.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

// 解析json的帮助类
public class JsonUtil {

    // 使用Gson这个第三方库解析json数据
    private static Gson gson;

    public static <T> T parseJson(String json, Type type) {
        if (gson == null) {
            gson = new Gson();
        }

        if (json == null || json.length() == 0) {
            return null;
        }

        return gson.fromJson(json, type);
    }
}

package com.xhliyuxiao.weather.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtil {

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

package com.xhliyuxiao.weather.bean;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse<T> {
    @SerializedName("success")
    private String success;

    @SerializedName("result")
    private T result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

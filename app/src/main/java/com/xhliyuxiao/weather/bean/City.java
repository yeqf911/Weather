package com.xhliyuxiao.weather.bean;

public class City {
    private String name;
    private String tag;
    private String code;
    private String province;

    public City(String name, String tag, String code, String province) {
        this.name = name;
        this.tag = tag;
        this.code = code;
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}

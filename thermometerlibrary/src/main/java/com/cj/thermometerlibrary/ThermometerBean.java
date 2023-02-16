package com.cj.thermometerlibrary;

public class ThermometerBean {
    private int type;//测量部位  01，表示耳温；02，表示额温；03表示物温
    private int temperature_unit;//温度单位 1，表示为华氏度，0，表示为摄氏度
    private String time;//时间  年/月/日/时/分
    private String timestamp;//时间戳
    private String temperature;//测量温度
    private int precision;//精度 (0表示为高精度，1表示为低精度,其他表示没有获取到精度)

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTemperature_unit() {
        return temperature_unit;
    }

    public void setTemperature_unit(int temperature_unit) {
        this.temperature_unit = temperature_unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}

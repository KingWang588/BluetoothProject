package com.cj.ovulationlibrary;

public class OvulationBean {
    private int precision;//精度 (0表示为高精度，1表示为低精度,其他表示没有获取到精度)
    private String time;//时间  年/月/日/时/分
    private String timestamp;//时间戳
    private String temperature;//测量温度
    private int temperature_unit;//温度单位 1，表示为华氏度，0，表示为摄氏度

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getTemperature_unit() {
        return temperature_unit;
    }

    public void setTemperature_unit(int temperature_unit) {
        this.temperature_unit = temperature_unit;
    }
}

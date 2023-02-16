package com.hzkj.bw.bloodoxygenslibrary;

import java.io.Serializable;

public class BORealTimeBean implements Serializable {
    private int blood_oxygen;//血氧
    private int pulse_rate;   //脉率
    private float PI;        //PI
    private int histogram; //柱状图
    private int waveform; //波形
    private String time;//时间

    public int getBlood_oxygen() {
        return blood_oxygen;
    }

    public void setBlood_oxygen(int blood_oxygen) {
        this.blood_oxygen = blood_oxygen;
    }

    public int getPulse_rate() {
        return pulse_rate;
    }

    public void setPulse_rate(int pulse_rate) {
        this.pulse_rate = pulse_rate;
    }

    public float getPI() {
        return PI;
    }

    public void setPI(float PI) {
        this.PI = PI;
    }

    public int getHistogram() {
        return histogram;
    }

    public void setHistogram(int histogram) {
        this.histogram = histogram;
    }

    public int getWaveform() {
        return waveform;
    }

    public void setWaveform(int waveform) {
        this.waveform = waveform;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

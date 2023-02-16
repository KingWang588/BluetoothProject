package com.hzkj.bw.bloodglucoselibrary;

import java.io.Serializable;

public class BloodGlucoseBean implements Serializable {

    private String concentration;//浓度
    private String timestamp;//时间戳

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

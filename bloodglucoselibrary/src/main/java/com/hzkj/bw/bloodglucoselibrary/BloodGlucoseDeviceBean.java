package com.hzkj.bw.bloodglucoselibrary;

import java.io.Serializable;

public class BloodGlucoseDeviceBean implements Serializable {
    private String device_model;//设备型号
    private String device_procedure;//程序编码
    private String device_versions;//版本

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getDevice_procedure() {
        return device_procedure;
    }

    public void setDevice_procedure(String device_procedure) {
        this.device_procedure = device_procedure;
    }

    public String getDevice_versions() {
        return device_versions;
    }

    public void setDevice_versions(String device_versions) {
        this.device_versions = device_versions;
    }
}

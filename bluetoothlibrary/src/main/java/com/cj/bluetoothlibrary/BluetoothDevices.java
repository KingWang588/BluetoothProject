package com.cj.bluetoothlibrary;

public class BluetoothDevices {
    private String name;//蓝牙名称
    private String address;//蓝牙地址
    private int type;//蓝牙类型：1、温度计；2、排卵助手；3、额温枪；4、耳温枪；

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

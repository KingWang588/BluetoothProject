package com.cj.ecglibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * 实时数据
 */
public class ECGRealTimeBean {
    private List<Integer> dataList = new ArrayList<>();//实时数据
    private boolean heartbeat;//心跳声 true响  false不响
    private String heartrate;//心率

    public List<Integer> getDataList() {
        return dataList;
    }

    public void setDataList(List<Integer> dataList) {
        this.dataList = dataList;
    }

    public boolean getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(String heartrate) {
        this.heartrate = heartrate;
    }
}

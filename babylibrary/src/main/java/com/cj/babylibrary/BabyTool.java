package com.cj.babylibrary;

public class BabyTool {
    /**
     * 婴儿贴相关操作
     */
    public static final String BABY_REAL_TIME = "01";//实时监控模式（APP->婴儿贴）
    public static final String BABY_REAL_TIME_NOT = "02";//非实时监控模式（APP->婴儿贴）
    public static final String BABY_QUILT_KICK = "03";//防踢被提醒（APP->婴儿贴）
    public static final String BABY_THERMOMETER = "04";//温度值数据（婴儿贴->APP）
    public static final String BABY_DEAD_LOCK = "05";//On/Off键锁死（APP->婴儿贴）
    public static final String BABY_UNLOCK = "06";//On/Off键解锁（APP->婴儿贴）
    public static final String BABY_VOLTAGE_ERROR = "07";//低电压报错（婴儿贴->APP）
    public static final String BABY_SHORT_OUT = "08";//Err-传感器短路、断路等（婴儿贴->APP）
    public static final String BABY_UPDATE_PATTERN = "09";//更新測量模式和On / Off 狀態（APP->婴儿贴）
    public static final String BABY_VERSIONS = "10";//程序编码和版本（婴儿贴->APP）

    public static final String BABY_REAL_TIME_PAS = "81";//实时监控模式（婴儿贴->APP）
    public static final String BABY_REAL_TIME_NOT_PAS = "82";//非实时监控模式（婴儿贴->APP）
    public static final String BABY_QUILT_KICK_PAS = "83";//防踢被提醒（婴儿贴->APP）
    public static final String BABY_THERMOMETER_PAS = "84";//温度值数据（APP->婴儿贴）
    public static final String BABY_DEAD_LOCK_PAS = "85";//On/Off键锁死（婴儿贴->APP）
    public static final String BABY_UNLOCK_PAS = "86";//On/Off键解锁（婴儿贴->APP）
    public static final String BABY_VOLTAGE_ERROR_PAS = "87";//低电压报错（APP->婴儿贴）
    public static final String BABY_SHORT_OUT_PAS = "88";//Err-传感器短路、断路等（APP->婴儿贴）
    public static final String BABY_UPDATE_PATTERN_PAS = "89";//更新測量模式和On / Off 狀態（婴儿贴->APP）
    public static final String BABY_VERSIONS_PAS = "90";//程序编码和版本（APP->婴儿贴）
}

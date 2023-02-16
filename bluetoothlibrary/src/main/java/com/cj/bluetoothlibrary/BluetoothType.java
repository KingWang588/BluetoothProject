package com.cj.bluetoothlibrary;

public class BluetoothType {
    /*蓝牙规则*/
    public static final String DATA_HEAD = "FAAAAAAF";//起始符(4Byte)
    public static final String DATA_FOOT = "F55F";//结束符(2Byte)
    /*设备类型*/
    public static final int THERMOMETER = 1; // 温度计
    public static final int OVULATION = 2; // 排卵助手
    public static final int HEAD_THERMOMETER = 3; // 额温枪
    public static final int EAR_THERMOMETER = 4; // 耳温枪
    public static final int BLOOD_OXYGEN = 5; // 血氧
    public static final int BABY_CODE = 6; // 婴儿贴
    public static final int BLOOD_GLUCOSE_NAME_CODE = 7; // 血糖
    public static final int BLOOD_PRESSURE_NAME_CODE = 8; // 血压
    public static final int ECG_CODE = 9; // 心电
    public static final int HEMOGLOBIN_NAME_CODE = 10; // 血红蛋白
    public static final int BLOOD_FAT_NAME_CODE = 11; // 血脂

    /**
     * 温度计相关操作（排卵助手）
     */
    public static final String THERMOMETER_RECEIVE = "04";//温度计接收功能字（排卵助手）
    public static final String THERMOMETER_SEND = "84";//温度计发送功能字（排卵助手）
    public static final String EAR_THERMOMETER_RECEIVE = "03";//耳温枪接收功能字(额温枪)
    public static final String EAR_THERMOMETER_RECEIVE_TIME = "06";//耳温枪接收时间同步(额温枪)
    public static final String EAR_THERMOMETER_SEND = "83";//耳温枪发送功能字(额温枪)
    public static final String EAR_THERMOMETER_SEND_TIME = "86";//耳温枪发送时间同步(额温枪)
    /**
     * 血氧相关操作
     */
    public static final String BLOOD_OXYGEN_DATA = "88";//血氧实时数据
    public static final String BLOOD_OXYGEN_REAL_TIME = "84";//同步时间
    public static final String BLOOD_OXYGEN_REAL_TIME_UP = "04";//同步时间(app-设备)
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
    /**
     * 血糖相关操作（血红蛋白）
     */
    public static final String BLOOD_GLUCOSE_CONCENTRATION = "04";//浓度
    public static final String BLOOD_GLUCOSE_CONCENTRATION_RES = "84";//浓度
    public static final String BLOOD_GLUCOSE_RECEIVE_TIME = "06";//同步时间
    public static final String BLOOD_GLUCOSE_RECEIVE_TIME_RES = "86";//同步时间
    public static final String BLOOD_GLUCOSE_TEST_PAPER = "09";//插入试纸
    public static final String BLOOD_GLUCOSE_TEST_PAPER_RES = "89";//插入试纸
    public static final String BLOOD_GLUCOSE_BLEED = "0A";//等待滴血
    public static final String BLOOD_GLUCOSE_BLEED_RES = "8A";//等待滴血
    public static final String BLOOD_GLUCOSE_COUNT_DOWN = "0B";//倒计时
    public static final String BLOOD_GLUCOSE_COUNT_DOWN_RES = "8B";//倒计时
    public static final String BLOOD_GLUCOSE_ER1 = "0C";//Er1
    public static final String BLOOD_GLUCOSE_ER1_RES = "8C";//Er1
    public static final String BLOOD_GLUCOSE_ER2 = "0D";//Er2
    public static final String BLOOD_GLUCOSE_ER2_RES = "8D";//Er2
    public static final String BLOOD_GLUCOSE_ER3 = "0E";//Er3
    public static final String BLOOD_GLUCOSE_ER3_RES = "8E";//Er3
    public static final String BLOOD_GLUCOSE_ER4 = "0F";//Er4
    public static final String BLOOD_GLUCOSE_ER4_RES = "8F";//Er4
    public static final String BLOOD_GLUCOSE_ER5 = "10";//Er5
    public static final String BLOOD_GLUCOSE_ER5_RES = "90";//Er5
    public static final String BLOOD_GLUCOSE_ER6 = "11";//Er6
    public static final String BLOOD_GLUCOSE_ER6_RES = "91";//Er6
    public static final String BLOOD_GLUCOSE_MEMORY = "2F";//记忆同步
    public static final String BLOOD_GLUCOSE_MEMORY_RES = "AF";//记忆同步
    public static final String BLOOD_GLUCOSE_APPARATUS = "19";//仪器主要信息
    public static final String BLOOD_GLUCOSE_APPARATUS_RES = "99";//仪器主要信息

    /**
     * 血压
     */
    public static final String BLOOD_PRESSURE_REAL_TIME_UP = "04";//同步时间(app-设备)
    public static final String BLOOD_PRESSURE_REAL_TIME = "84";//同步时间
    public static final String BLOOD_PRESSURE_DOWNLOAD_MEMORY = "0A";//下载记忆(app-设备)
    public static final String BLOOD_PRESSURE_DOWNLOAD_MEMORY_END = "8B";//记忆结束
    public static final String BLOOD_PRESSURE_RESULT = "03";//测量结果(app-设备)
    public static final String BLOOD_PRESSURE_RESULT_UP = "83";//测量结果
    /**
     * 心电
     */
    public static final String ECG_REAL_TIME_UP = "04";//同步时间(app-设备)
    public static final String ECG_REAL_TIME = "84";//同步时间
    public static final String ECG_DOWNLOAD_MEMORY = "0E";//下载记忆(app-设备)
    public static final String ECG_DOWNLOAD_MEMORY_END = "8E";//记忆上传
    public static final String ECG_RESULT = "86";//测量结果
    public static final String ECG_RESULT_UP = "06";//测量结果(app-设备)
    public static final String ECG_START_RESULT = "85";//开始测量
    public static final String ECG_START_RESULT_UP = "05";//开始测量(app-设备)
    public static final String ECG_REAL_TIME_GET = "87";//实时数据
    public static final String ECG_MODEL = "8C";//发送型号
    public static final String ECG_SHUTDOWN = "8F";//发送关机动作
    /**
     * 血红蛋白
     */
    public static final String HEMOGLOBIN_CONCENTRATION = "04";//浓度
    public static final String HEMOGLOBIN_CONCENTRATION_RES = "84";//浓度(app-设备)
    public static final String HEMOGLOBIN_RECEIVE_TIME = "06";//同步时间
    public static final String HEMOGLOBIN_RECEIVE_TIME_RES = "86";//同步时间
    public static final String HEMOGLOBIN_TEST_PAPER = "09";//插入试纸
    public static final String HEMOGLOBIN_TEST_PAPER_RES = "89";//插入试纸
    public static final String HEMOGLOBIN_BLEED = "0A";//等待滴血
    public static final String HEMOGLOBIN_BLEED_RES = "8A";//等待滴血
    public static final String HEMOGLOBIN_COUNT_DOWN = "0B";//倒计时
    public static final String HEMOGLOBIN_COUNT_DOWN_RES = "8B";//倒计时
    public static final String HEMOGLOBIN_ER1 = "0C";//Er1
    public static final String HEMOGLOBIN_ER1_RES = "8C";//Er1
    public static final String HEMOGLOBIN_ER2 = "0D";//Er2
    public static final String HEMOGLOBIN_ER2_RES = "8D";//Er2
    public static final String HEMOGLOBIN_ER3 = "0E";//Er3
    public static final String HEMOGLOBIN_ER3_RES = "8E";//Er3
    public static final String HEMOGLOBIN_ER4 = "0F";//Er4
    public static final String HEMOGLOBIN_ER4_RES = "8F";//Er4
    public static final String HEMOGLOBIN_ER5 = "10";//Er5
    public static final String HEMOGLOBIN_ER5_RES = "90";//Er5
    public static final String HEMOGLOBIN_ER6 = "11";//Er6
    public static final String HEMOGLOBIN_ER6_RES = "91";//Er6
    public static final String HEMOGLOBIN_MEMORY = "2F";//记忆同步
    public static final String HEMOGLOBIN_MEMORY_RES = "AF";//记忆同步
    public static final String HEMOGLOBIN_APPARATUS = "19";//仪器主要信息
    public static final String HEMOGLOBIN_APPARATUS_RES = "99";//仪器主要信息

    /**
     * 血脂
     */
    public static final String BLOOD_FAT_CONCENTRATION = "04";//浓度
    public static final String BLOOD_FAT_CONCENTRATION_RES = "84";//浓度(app-设备)
    public static final String BLOOD_FAT_RECEIVE_TIME = "06";//同步时间
    public static final String BLOOD_FAT_RECEIVE_TIME_RES = "86";//同步时间
    public static final String BLOOD_FAT_TEST_PAPER = "09";//插入试纸
    public static final String BLOOD_FAT_TEST_PAPER_RES = "89";//插入试纸
    public static final String BLOOD_FAT_BLEED = "0A";//等待滴血
    public static final String BLOOD_FAT_BLEED_RES = "8A";//等待滴血
    public static final String BLOOD_FAT_COUNT_DOWN = "0B";//倒计时
    public static final String BLOOD_FAT_COUNT_DOWN_RES = "8B";//倒计时
    public static final String BLOOD_FAT_ER1 = "0C";//Er1
    public static final String BLOOD_FAT_ER1_RES = "8C";//Er1
    public static final String BLOOD_FAT_ER2 = "0D";//Er2
    public static final String BLOOD_FAT_ER2_RES = "8D";//Er2
    public static final String BLOOD_FAT_ER3 = "0E";//Er3
    public static final String BLOOD_FAT_ER3_RES = "8E";//Er3
    public static final String BLOOD_FAT_ER4 = "0F";//Er4
    public static final String BLOOD_FAT_ER4_RES = "8F";//Er4
    public static final String BLOOD_FAT_ER5 = "10";//Er5
    public static final String BLOOD_FAT_ER5_RES = "90";//Er5
    public static final String BLOOD_FAT_ER6 = "11";//Er6
    public static final String BLOOD_FAT_ER6_RES = "91";//Er6
    public static final String BLOOD_FAT_MEMORY = "2F";//记忆同步
    public static final String BLOOD_FAT_MEMORY_RES = "AF";//记忆同步
    public static final String BLOOD_FAT_APPARATUS = "19";//仪器主要信息
    public static final String BLOOD_FAT_APPARATUS_RES = "99";//仪器主要信息
}

package com.cj.bluetoothlibrary;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存读取SharedPreferences的内容
 */
public class SpHelper {

    // 加密数据的key
    private static final String KEY = "BEN";
    // 默认xml文件名
    private static final String PREFS_NAME = "BluetoothUtil";
    //设备名称
    private static final String BLUETOOTH_NAME = "bluetooth_name";
    //设备地址
    private static final String BLUETOOTH_ADDRESS = "bluetooth_address";
    //设备地址
    private static final String BLUETOOTH_TYPE = "bluetooth_type";

    /**
     * 加密
     *
     * @param value
     * @return
     */
    private static String encoderStr(String value) {
        String md5Key = EncryptUtil.md5Digest(KEY);
        return EncryptUtil.desEncoder(value, md5Key);
    }

    /**
     * 解密
     *
     * @param value
     * @return
     */
    private static String decoderStr(String value) {
        String md5Key = EncryptUtil.md5Digest(KEY);
        return EncryptUtil.desDecoder(value, md5Key);
    }

    /**
     * 保存数据
     *
     * @param prefsName
     * @param key
     * @param value
     */
    public static void setString(Context context, String prefsName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, encoderStr(value));
        editor.apply();
    }

    /**
     * 保存数据
     *
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) {
        setString(context, PREFS_NAME, key, value);
    }

    /**
     * 保存数据
     *
     * @param bluetoothDevice
     */
    public static void setBluetooth(Context context, BluetoothDevice bluetoothDevice, int type) {
        setString(context, BLUETOOTH_NAME, bluetoothDevice.getName());
        setString(context, BLUETOOTH_ADDRESS, bluetoothDevice.getAddress());
        setInt(context, BLUETOOTH_TYPE, type);
    }


    /**
     * 保存数据
     *
     * @param deviceName
     * @param deviceAddress
     */
    public static void setBluetooth(Context context, String deviceName, String deviceAddress,int type) {
        setString(context, BLUETOOTH_NAME, deviceName);
        setString(context, BLUETOOTH_ADDRESS, deviceAddress);
        setInt(context, BLUETOOTH_TYPE, type);
    }

    /**
     * 读取数据
     *
     * @param prefsName
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String prefsName, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        String value = sp.getString(key, defValue);
        return defValue.equals(value) ? value : decoderStr(value);
    }

    /**
     * 读取数据
     *
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        return getString(context, PREFS_NAME, key, defValue);
    }

    /**
     * 保存int类型数据
     *
     * @param key
     * @param value
     */
    public static void setInt(Context context, String key, int value) {
        setString(context, PREFS_NAME, key, String.valueOf(value));
    }

    /**
     * 读取int类型数据
     *
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        return Integer.parseInt(getString(context, PREFS_NAME, key, String.valueOf(0)));
    }

    /**
     * 读取数据(默认值是"")
     *
     * @param key 读取的名称
     * @return 读取的值
     */
    public static String getString(Context context, String key) {
        return getString(context, PREFS_NAME, key, "");
    }

    public static BluetoothDevices getBluetooth(Context context) {
        if (!getString(context, BLUETOOTH_NAME).isEmpty()) {
            if (!getString(context, BLUETOOTH_ADDRESS).isEmpty()) {
                BluetoothDevices bluetoothDevices = new BluetoothDevices();
                bluetoothDevices.setName(getString(context, BLUETOOTH_NAME));
                bluetoothDevices.setAddress(getString(context, BLUETOOTH_ADDRESS));
                bluetoothDevices.setType(getInt(context, BLUETOOTH_TYPE));
                return bluetoothDevices;
            }
        }
        return null;
    }
}

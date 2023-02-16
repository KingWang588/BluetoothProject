package com.cj.thermometerlibrary;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.cj.bluetoothlibrary.BluetoothDevices;
import com.cj.bluetoothlibrary.BluetoothType;
import com.cj.bluetoothlibrary.BluetoothUtil;
import com.cj.bluetoothlibrary.ByteUtils;
import com.cj.bluetoothlibrary.CalculateUtil;
import com.cj.bluetoothlibrary.SpHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 体温计
 */
public class ThermometerBluetoothUtil {
    private BluetoothUtil bluetoothUtil;
    private Context context;
    private OnThermometerBluetoothListener onThermometerBluetoothListener;
    private String datas = "";
    private List<String> myDevice = new ArrayList<>();

    public void setThermometerBluetoothListener(OnThermometerBluetoothListener onThermometerBluetoothListener) {
        this.onThermometerBluetoothListener = onThermometerBluetoothListener;
    }

    public interface OnThermometerBluetoothListener {
        /**
         * 扫描设备开始回调
         */
        void onSearchStarted();

        /**
         * 扫描设备停止回调
         */
        void onSearchStopped();

        /**
         * 扫描到设备回调
         */
        void onDeviceSpyListener(BluetoothDevice device, Integer rssi);

        /**
         * 设备异常断开回调
         */
        void onDeviceBreakListener();

        /**
         * 设备连接成功回调
         */
        void onDeviceConnectSucceed();

        /**
         * 温度值返回
         */
        void onDeviceValueReturnListener(int type, List<ThermometerBean> thermometerBeans);

        /**
         * 收到时间同步命令
         */
        void onGetTimeSynchronization();

        /**
         * 蓝牙信号强度
         */
        void onReadBluetoothRssi(Integer rssi);
        /**
         * 蓝牙连接失败
         * @param code
         */
        void onDeviceConnectFailing(int code);
    }

    public ThermometerBluetoothUtil(Context context, String... names) {
        if (bluetoothUtil == null) {
            this.context = context;
            if (names != null) {
                myDevice.addAll(Arrays.asList(names));
            }
            bluetoothUtil = new BluetoothUtil(context);
        }
        bluetoothUtil.setScanBluetoothListener(new BluetoothUtil.OnScanBluetoothListener() {
            @Override
            public void onSearchStarted() {
                if (onThermometerBluetoothListener != null) {
                    onThermometerBluetoothListener.onSearchStarted();
                }
            }

            @Override
            public void onSearchStopped() {
                if (onThermometerBluetoothListener != null) {
                    onThermometerBluetoothListener.onSearchStopped();
                }
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                //根据传入的名称，分辨设备
                if (device.getName() == null) {
                    return;
                }
                if (device.getName().isEmpty()) {
                    return;
                }
                if (myDevice.size() != 0) {
                    for (String name : myDevice) {
                        if (device.getName().equals(name)) {
                            if (onThermometerBluetoothListener != null) {
                                onThermometerBluetoothListener.onDeviceSpyListener(device, rssi);
                                break;
                            }
                        }
                    }
                } else {
                    if (onThermometerBluetoothListener != null) {
                        onThermometerBluetoothListener.onDeviceSpyListener(device, rssi);
                    }
                }
            }

            @Override
            public void onDeviceConnectSucceed() {
                if (onThermometerBluetoothListener != null) {
                    onThermometerBluetoothListener.onDeviceConnectSucceed();
                }
            }

            @Override
            public void onDeviceBreakListener() {
                if (onThermometerBluetoothListener != null) {
                    onThermometerBluetoothListener.onDeviceBreakListener();
                }
            }

            @Override
            public void onReadBluetoothDataListener(int code, byte[] data) {

            }

            @Override
            public void onWriteBluetoothDataListener() {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onNotifyBluetoothDataListener(UUID service, UUID character, byte[] value) {
                //不是体温计数据不解析
                if (connectBluetoothDeviceType() != BluetoothType.THERMOMETER) {
                    return;
                }

                String result = ByteUtils.byteToString(value);
//                    Log.e("onNotify: ", "数据长度：" + value.length);
//                    Log.e("onNotify: ", "数据：" + result);
                //是温度计或者排卵助手解析数据
                if (result.length() == 24 && result.substring(0, 8).equals(BluetoothType.DATA_HEAD) &&
                        result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                    String type = result.substring(12, 14);
                    //普通温度计
                    if (type.equals(BluetoothType.THERMOMETER_RECEIVE)) {
                        String dataLength = result.substring(8, 12);
                        String thermometerData = result.substring(14, 18);
                        if (BluetoothUtil.makeChecksum(thermometerData, dataLength, BluetoothType.THERMOMETER_RECEIVE).equals(result.substring(18, 20))) {
                            String temperatureValue = CalculateUtil.transform16_2(thermometerData);
                            //是否高精度:0表示为高精度,1表示为低精度
                            String precision = temperatureValue.substring(0, 1);
                            //温度范围:0表示温度范围为32.0℃-42.9℃（低精度）/32.00℃-42.99℃（高精度）
                            //        1表示温度范围为32.0℃-43.9℃（低精度）/32.00℃-43.99℃（高精度）
                            //F=C×1.8+32,  C=(F-32)÷1.8   F－华氏温度，C－摄氏温度
                            String scope = temperatureValue.substring(1, 2);
                            //温度值（2进制）
                            String value2 = temperatureValue.substring(2);
                            //温度值2转10进制
                            int value10 = CalculateUtil.transform2_10(value2);
                            //温度值
                            float values = (float) value10 / 100;
                            String valuesString = null;
                            ThermometerBean thermometerBean = new ThermometerBean();
                            if (precision.equals("0")) {
                                if (scope.equals("1")) {
                                    //华氏度
                                    valuesString = String.format("%.2f", values * 1.8 + 32);
                                    thermometerBean.setTemperature_unit(1);
                                } else {
                                    valuesString = String.format("%.2f", values);
                                    thermometerBean.setTemperature_unit(0);
                                }
                                thermometerBean.setPrecision(0);
                            } else {
                                if (scope.equals("1")) {
                                    //华氏度
                                    valuesString = String.format("%.1f", values * 1.8 + 32);
                                    thermometerBean.setTemperature_unit(1);
                                } else {
                                    valuesString = String.format("%.1f", values);
                                    thermometerBean.setTemperature_unit(0);
                                }
                                thermometerBean.setPrecision(1);
                            }
                            if (onThermometerBluetoothListener != null) {
                                thermometerBean.setType(3);
                                thermometerBean.setTime(CalculateUtil.getNewData());
                                thermometerBean.setTimestamp(String.valueOf(System.currentTimeMillis()));
                                thermometerBean.setTemperature(valuesString);
                                List<ThermometerBean> list = new ArrayList<>();
                                list.add(thermometerBean);
                                onThermometerBluetoothListener.onDeviceValueReturnListener(1, list);
                                writeBluetoothData();
                            }
                        }
                    }
                    return;
                }
                if (result.length() == 20) {
                    if (result.substring(0, 8).equals(BluetoothType.DATA_HEAD) &&
                            result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                        if (result.substring(12, 14).equals(BluetoothType.EAR_THERMOMETER_RECEIVE_TIME)) {
                            if (BluetoothUtil.makeChecksum(result.substring(8, 12), BluetoothType.EAR_THERMOMETER_RECEIVE_TIME).equals(result.substring(14, 16))) {
                                //校验和成功
                                if (onThermometerBluetoothListener != null) {
                                    onThermometerBluetoothListener.onGetTimeSynchronization();
                                }
                            }
                            return;
                        }
                    }
                }
                if (result.length() > 8) {
                    if (result.substring(0, 8).equals(BluetoothType.DATA_HEAD)) {
                        datas = result;
                        return;
                    }
                    if (result.length() == 40 && !result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                        datas += result;
                    }
                }
                if (result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                    datas += result;
                    if (datas.length() < 36) {
                        return;
                    }
                    String type = datas.substring(12, 14);
                    String dataLength = datas.substring(8, 12);
                    if (type.equals(BluetoothType.EAR_THERMOMETER_RECEIVE)) {
                        if (BluetoothUtil.makeChecksum(datas.substring(14, datas.length() - 6), dataLength,
                                BluetoothType.EAR_THERMOMETER_RECEIVE).equals(
                                datas.substring(datas.length() - 6, datas.length() - 4))) {
                            String[] strings = BluetoothUtil.stringSpilt(datas.substring(14, datas.length() - 6), 16);
                            List<ThermometerBean> list = new ArrayList<>();
                            for (String str : strings) {
                                //数据转换处理
                                if (dataTransition(str) != null) {
                                    list.add(dataTransition(str));
                                }
                            }

                            if (onThermometerBluetoothListener != null) {
                                onThermometerBluetoothListener.onDeviceValueReturnListener(2, list);
                                writeBluetoothDataEar();
                            }
                        }
                    }
                }
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {
                if (onThermometerBluetoothListener != null) {
                    onThermometerBluetoothListener.onReadBluetoothRssi(rssi);
                }
            }

            @Override
            public void onDeviceConnectFailing(int code) {
                if (onThermometerBluetoothListener != null) {
                    onThermometerBluetoothListener.onDeviceConnectFailing(code);
                }
            }

        });
    }

    /**
     * 蓝牙状态
     */
    public boolean stateBluetooth() {
        return bluetoothUtil.stateBluetooth();
    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth() {
        bluetoothUtil.openBluetooth();
    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        bluetoothUtil.closeBluetooth();
    }

    /**
     * 设备扫描
     */
    public void scanBluetooth() {
        bluetoothUtil.scanBluetooth();
    }

    /**
     * 设备停止扫描
     */
    public void stopBluetooth() {
        bluetoothUtil.stopBluetooth();
    }

    /**
     * 设备连接蓝牙
     */
    public void connectBluetooth(BluetoothDevice device) {
        bluetoothUtil.connectBluetooth(device, BluetoothType.THERMOMETER);
    }

//    /**
//     * 设备连接蓝牙
//     */
//    public void connectBluetooth(String macAddress) {
//        bluetoothUtil.connectBluetooth(macAddress, BluetoothType.THERMOMETER);
//    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.THERMOMETER);
    }

    /**
     * 蓝牙已连接的设备名称
     */
    public String connectBluetoothDeviceName() {
        if (bluetoothUtil.connectBluetoothDevice() == null) {
            return null;
        }
        return bluetoothUtil.connectBluetoothDevice().getName();
    }

    /**
     * 蓝牙已连接的设备信号强度
     */
    public void connectBluetoothRssi() {
        if (bluetoothUtil.connectBluetoothDevice() == null) {
            return;
        }
        bluetoothUtil.onReadRssi(connectBluetoothDeviceAddress());
    }

    /**
     * 蓝牙已连接的设备地址
     */
    public String connectBluetoothDeviceAddress() {
        if (bluetoothUtil.connectBluetoothDevice() == null) {
            return null;
        }
        return bluetoothUtil.connectBluetoothDevice().getAddress();
    }

    /**
     * 蓝牙已连接的设备类型
     */
    public int connectBluetoothDeviceType() {
        if (bluetoothUtil.connectBluetoothDevice() == null) {
            return 0;
        }
        return bluetoothUtil.connectBluetoothDevice().getType();
    }

    /**
     * 设备断开蓝牙
     */
    public void breakBluetooth() {
        bluetoothUtil.breakBluetooth();
    }

    /**
     * 界面退出
     */
    public void onDestroy() {
        //注销蓝牙连接状态监听回调
        bluetoothUtil.unregisterBluetoothStateListener();
    }

    /**
     * 发送温度
     */
    public void writeBluetoothData() {
        String data = BluetoothType.DATA_HEAD + "000A" + BluetoothType.THERMOMETER_SEND
                + BluetoothUtil.makeChecksum("000A", BluetoothType.THERMOMETER_SEND) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 发送温度(耳温、额温)
     */
    public void writeBluetoothDataEar() {
        String data = BluetoothType.DATA_HEAD + "000A" + BluetoothType.EAR_THERMOMETER_SEND
                + BluetoothUtil.makeChecksum("000A", BluetoothType.EAR_THERMOMETER_SEND) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 发送时间同步
     */
    public void writeBluetoothTime() {
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength(BluetoothUtil.getNowTimeString()) +
                BluetoothType.EAR_THERMOMETER_SEND_TIME + BluetoothUtil.getNowTimeString() +
                BluetoothUtil.makeChecksum(BluetoothUtil.getNowTimeString(),
                        BluetoothUtil.getCmdLength(BluetoothUtil.getNowTimeString()),
                        BluetoothType.EAR_THERMOMETER_SEND_TIME) +
                BluetoothType.DATA_FOOT;

        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 数据转换
     */
    private ThermometerBean dataTransition(String str) {
        if (str.length() != 16) {
            return null;
        }
        ThermometerBean earBean = new ThermometerBean();
        earBean.setType(CalculateUtil.transform16_10(str.substring(0, 2)));
        String temperature = CalculateUtil.transform16_2(str.substring(2, 6));
        earBean.setTemperature_unit(Integer.parseInt(temperature.substring(0, 1)));
        if (earBean.getTemperature_unit() == 1) {
            //转换华氏度
            earBean.setTemperature(String.format("%.2f", (float) CalculateUtil.transform2_10(temperature.substring(1)) / 100 * 1.8 + 32));
        } else {
            earBean.setTemperature(String.format("%.2f", (float) CalculateUtil.transform2_10(temperature.substring(1)) / 100));
        }
        String year = String.valueOf(CalculateUtil.transform16_10(str.substring(6, 8)));
        String month = String.valueOf(CalculateUtil.transform16_10(str.substring(8, 10)));
        String day = String.valueOf(CalculateUtil.transform16_10(str.substring(10, 12)));
        String hour = String.valueOf(CalculateUtil.transform16_10(str.substring(12, 14)));
        String minute = String.valueOf(CalculateUtil.transform16_10(str.substring(14, 16)));
        if (year.length() != 2) {
            year = "0" + year;
        }
        if (month.length() != 2) {
            month = "0" + month;
        }
        if (day.length() != 2) {
            day = "0" + day;
        }
        if (hour.length() != 2) {
            hour = "0" + hour;
        }
        if (minute.length() != 2) {
            minute = "0" + minute;
        }
        earBean.setTime(year + "/" + month + "/" + day + "/" + hour + "/" + minute);
        try {
            String timetamp = dateToStamp(earBean.getTime());
            earBean.setTimestamp(timetamp.substring(0, timetamp.length() - 3));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return earBean;
    }

    /*
     * 将时间转换为时间戳
     */
    private static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd/HH/mm");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 打印默认连接的设备信息
     */
    public void defaultBluetooth() {
        BluetoothDevices bluetoothDevices = SpHelper.getBluetooth(context);
        if (bluetoothDevices != null) {
            Log.e("defaultBluetooth: ", bluetoothDevices.getName());
            Log.e("defaultBluetooth: ", bluetoothDevices.getAddress());
            Log.e("defaultBluetooth: ", bluetoothDevices.getType() + "");
        }
    }

}

package com.cj.ovulationlibrary;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 排卵助手
 */
public class OvulationBluetoothUtil {
    private BluetoothUtil bluetoothUtil;
    private Context context;
    private OnThermometerBluetoothListener onThermometerBluetoothListener;
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
        void onDeviceValueReturnListener(OvulationBean ovulationBean);

        /**
         * 蓝牙信号强度
         */
        void onReadBluetoothRssi(Integer rssi);

        /**
         * 蓝牙连接失败
         *
         * @param code
         */
        void onDeviceConnectFailing(int code);
    }

    public OvulationBluetoothUtil(Context context, String... names) {
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

                if (connectBluetoothDeviceType() != BluetoothType.OVULATION) {
                    return;
                }
//                else {
//                    if (!connectBluetoothDeviceName().endsWith(BluetoothType.OVULATION_NAME)) {
//                        return;
//                    }
//                }
                String result = ByteUtils.byteToString(value);
//                Log.e("onNotify: ", "数据长度：" + value.length);
//                Log.e("onNotify: ", "数据：" + result);
                //不是排卵助手数据不解析
                if (result.length() == 24) {
                    String type = result.substring(12, 14);
                    if (type.equals(BluetoothType.THERMOMETER_RECEIVE)) {
                        String dataLength = result.substring(8, 12);
                        String thermometerData = result.substring(14, 18);
                        if (BluetoothUtil.makeChecksum(thermometerData, dataLength, BluetoothType.THERMOMETER_RECEIVE).equals(result.substring(18, 20))) {
                            String temperatureValue = CalculateUtil.transform16_2(thermometerData);
                            //是否高精度:0表示为高精度,1表示为低精度
                            String precision = temperatureValue.substring(0, 1);
                            //温度范围:0表示温度范围为32.0℃-42.9℃（低精度）/32.00℃-42.99℃（高精度）
                            //        1表示温度范围为32.0℃-43.9℃（低精度）/32.00℃-43.99℃（高精度）
                            //C＝5×（F－32）／9，F＝9×C／5＋32   F－华氏温度，C－摄氏温度
                            String scope = temperatureValue.substring(1, 2);
                            //温度值（2进制）
                            String value2 = temperatureValue.substring(2);
                            //温度值2转10进制
                            int value10 = CalculateUtil.transform2_10(value2);
                            //温度值
                            float values = (float) value10 / 100;
                            String valuesString = null;
                            OvulationBean ovulationBean = new OvulationBean();
                            if (precision.equals("0")) {
                                if (scope.equals("1")) {
                                    //华氏度
                                    valuesString = String.format("%.2f", values * 1.8 + 32);
                                    ovulationBean.setTemperature_unit(1);
                                } else {
                                    valuesString = String.format("%.2f", values);
                                    ovulationBean.setTemperature_unit(0);
                                }
                                ovulationBean.setPrecision(0);
                            } else {
                                if (scope.equals("1")) {
                                    //华氏度
                                    valuesString = String.format("%.1f", values * 1.8 + 32);
                                    ovulationBean.setTemperature_unit(1);
                                } else {
                                    valuesString = String.format("%.1f", values);
                                    ovulationBean.setTemperature_unit(0);
                                }
                                ovulationBean.setPrecision(1);
                            }
                            ovulationBean.setTime(CalculateUtil.getNewData());
                            ovulationBean.setTimestamp(String.valueOf(System.currentTimeMillis()));
                            ovulationBean.setTemperature(valuesString);

                            if (onThermometerBluetoothListener != null) {

                                onThermometerBluetoothListener.onDeviceValueReturnListener(ovulationBean);
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
        bluetoothUtil.connectBluetooth(device, BluetoothType.OVULATION);
    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.OVULATION);
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
     * 写数据（发送）
     */
    public void writeBluetoothData(String database, String function) {
        if (database.length() % 2 != 0) {
            return;
        }
        String data = BluetoothType.DATA_HEAD + function +
                BluetoothUtil.makeChecksum(database, CalculateUtil.transform10_16(database.length() / 2 + 10),
                        function) +
                BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
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

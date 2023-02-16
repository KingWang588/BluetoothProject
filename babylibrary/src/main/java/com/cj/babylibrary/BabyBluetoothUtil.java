package com.cj.babylibrary;

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
 * 婴儿贴
 */
public class BabyBluetoothUtil {
    private BluetoothUtil bluetoothUtil;
    private Context context;
    private OnBabyBluetoothListener onBabyBluetoothListener;
    private List<String> myDevice = new ArrayList<>();


    public void setBabyBluetoothListener(OnBabyBluetoothListener onBabyBluetoothListener) {
        this.onBabyBluetoothListener = onBabyBluetoothListener;
    }

    public interface OnBabyBluetoothListener {
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
         * 温度值返回回调
         *
         * @param values
         */
        void onDeviceValueReturnListener(float values);

        /**
         * 更新測量模式和On / Off 狀態
         *
         * @param pattern
         */
        void onDeviceStateReturnListener(int pattern, int state);

        /**
         * 操作返回
         *
         * @param values
         */
        void onDeviceOperationReturnListener(String values);

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

    public BabyBluetoothUtil(Context context, String... names) {
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
                if (onBabyBluetoothListener != null) {
                    onBabyBluetoothListener.onSearchStarted();
                }
            }

            @Override
            public void onSearchStopped() {
                if (onBabyBluetoothListener != null) {
                    onBabyBluetoothListener.onSearchStopped();
                }
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                //根据传入的名称，分辨设备
                if (myDevice.size() != 0) {
                    for (String name : myDevice) {
                        if (device.getName().equals(name)) {
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceSpyListener(device, rssi);
                                break;
                            }
                        }
                    }
                } else {
                    if (onBabyBluetoothListener != null) {
                        onBabyBluetoothListener.onDeviceSpyListener(device, rssi);
                    }
                }
            }

            @Override
            public void onDeviceConnectSucceed() {
                if (onBabyBluetoothListener != null) {
                    onBabyBluetoothListener.onDeviceConnectSucceed();
                }
            }

            @Override
            public void onDeviceBreakListener() {
                if (onBabyBluetoothListener != null) {
                    onBabyBluetoothListener.onDeviceBreakListener();
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

                //不是婴儿贴数据不解析
                if (connectBluetoothDeviceType() != BluetoothType.BABY_CODE) {
                    return;
                }
                String result = ByteUtils.byteToString(value);
//                Log.e("onNotify: ", "收到数据长度：" + value.length);
//                Log.e("onNotify: ", "收到数据：" + result);
                if (result.length() >= 20) {
                    String type = result.substring(12, 14);
                    String dataLength;
                    String thermometerData;
                    switch (type) {
                        case BluetoothType.BABY_THERMOMETER:
                            //收到温度
                            if (result.length() != 24) {
                                return;
                            }
                            dataLength = result.substring(8, 12);
                            thermometerData = result.substring(14, 18);
                            if (BluetoothUtil.makeChecksum(thermometerData, dataLength, BluetoothType.BABY_THERMOMETER).equals(result.substring(18, 20))) {
                                String temperatureValue = CalculateUtil.transform16_2(thermometerData);
                                //温度值（2进制）
                                String value2 = temperatureValue.substring(2);
                                //温度值2转10进制
                                int value10 = CalculateUtil.transform2_10(value2);
                                //温度值
                                float values = (float) value10 / 10;
                                if (onBabyBluetoothListener != null) {
                                    onBabyBluetoothListener.onDeviceValueReturnListener(values);
                                }
                                writeBluetoothTemperature(thermometerData);
                            }
                            break;
                        case BluetoothType.BABY_VOLTAGE_ERROR:
                            //收到低电压报错
                            dataLength = result.substring(8, 12);
                            if (BluetoothUtil.makeChecksum("", dataLength, BluetoothType.BABY_VOLTAGE_ERROR).equals(result.substring(14, 16))) {
                                writeBluetoothData(BluetoothType.BABY_VOLTAGE_ERROR_PAS);
                            }
                            break;
                        case BluetoothType.BABY_SHORT_OUT:
                            //收到Err-传感器短路、断路等
                            dataLength = result.substring(8, 12);
                            if (BluetoothUtil.makeChecksum("", dataLength, BluetoothType.BABY_SHORT_OUT).equals(result.substring(14, 16))) {
                                writeBluetoothData(BluetoothType.BABY_SHORT_OUT);
                            }
                            break;
                        case BluetoothType.BABY_VERSIONS:
                            //收到程序编码和版本
                            break;
                        case BluetoothType.BABY_UPDATE_PATTERN_PAS:
                            //更新測量模式和On / Off 狀態
                            String state = result.substring(14, 18);
                            writeBluetoothData(BluetoothType.BABY_UPDATE_PATTERN_PAS);
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceStateReturnListener(CalculateUtil.transform16_10(state.substring(0, 2)), CalculateUtil.transform16_10(state.substring(2)));
                            }
                            break;
                        case BluetoothType.BABY_REAL_TIME_PAS:
                            //实时监控模式
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceOperationReturnListener(BluetoothType.BABY_REAL_TIME_PAS);
                            }
                            break;
                        case BluetoothType.BABY_REAL_TIME_NOT_PAS:
                            //非实时监控模式
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceOperationReturnListener(BluetoothType.BABY_REAL_TIME_NOT_PAS);
                            }
                            break;
                        case BluetoothType.BABY_QUILT_KICK_PAS:
                            //防踢被提醒
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceOperationReturnListener(BluetoothType.BABY_QUILT_KICK_PAS);
                            }
                            break;
                        case BluetoothType.BABY_DEAD_LOCK_PAS:
                            //On/Off键锁死
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceOperationReturnListener(BluetoothType.BABY_DEAD_LOCK_PAS);
                            }
                            break;
                        case BluetoothType.BABY_UNLOCK_PAS:
                            //On/Off键解锁
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceOperationReturnListener(BluetoothType.BABY_UNLOCK_PAS);
                            }
                            break;
                        case BluetoothType.BABY_SHORT_OUT_PAS:
                            //Err-传感器短路、断路等
                            if (onBabyBluetoothListener != null) {
                                onBabyBluetoothListener.onDeviceOperationReturnListener(BluetoothType.BABY_SHORT_OUT_PAS);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {
                if (onBabyBluetoothListener != null) {
                    onBabyBluetoothListener.onReadBluetoothRssi(rssi);
                }
            }

            @Override
            public void onDeviceConnectFailing(int code) {
                if (onBabyBluetoothListener != null) {
                    onBabyBluetoothListener.onDeviceConnectFailing(code);
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
        bluetoothUtil.connectBluetooth(device, BluetoothType.BABY_CODE);
    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.BABY_CODE);
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
    private int connectBluetoothDeviceType() {
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
     * 发送温度
     */
    public void writeBluetoothTemperature(String thermometerData) {
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength(thermometerData) +
                BluetoothType.BABY_THERMOMETER_PAS + thermometerData +
                BluetoothUtil.makeChecksum(thermometerData,
                        BluetoothUtil.getCmdLength(thermometerData),
                        BluetoothType.BABY_THERMOMETER_PAS) +
                BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothBabyData(data);
    }

    /**
     * 婴儿贴相关指令
     */
    public void writeBluetoothData(String type) {
        String data = null;
        switch (type) {
            case BluetoothType.BABY_REAL_TIME:
                //实时监控模式
                data = BluetoothType.DATA_HEAD + "000A010B" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_REAL_TIME_NOT:
                //非实时监控模式
                data = BluetoothType.DATA_HEAD + "000A020C" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_QUILT_KICK:
                //防踢被提醒
                data = BluetoothType.DATA_HEAD + "000A030D" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_DEAD_LOCK:
                //On/Off键锁死
                data = BluetoothType.DATA_HEAD + "000A050F" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_UNLOCK:
                //On/Off键解锁
                data = BluetoothType.DATA_HEAD + "000A0610" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_VOLTAGE_ERROR_PAS:
                //低电压报错
                data = BluetoothType.DATA_HEAD + "000A8791" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_SHORT_OUT:
                //Err-传感器短路、断路等
                data = BluetoothType.DATA_HEAD + "000A8892" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_UPDATE_PATTERN:
                //更新測量模式和On / Off 狀態
                data = BluetoothType.DATA_HEAD + "000A0913" + BluetoothType.DATA_FOOT;
                break;
            case BluetoothType.BABY_VERSIONS_PAS:
                //程序编码和版本
                data = BluetoothType.DATA_HEAD + "000A9094" + BluetoothType.DATA_FOOT;
                break;
        }
        if (data != null) {
            bluetoothUtil.writeBluetoothBabyData(data);
        }
    }


    /**
     * 界面退出
     */
    public void onDestroy() {
        //注销蓝牙连接状态监听回调
        bluetoothUtil.unregisterBluetoothStateListener();
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

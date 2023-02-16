package com.hzkj.bw.bloodoxygenslibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
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

public class BloodOxygenBluetoothUtil {

    private Activity context;
    private BluetoothUtil bluetoothUtil;
    private OnBloodBluetoothListener mBloodBluetoothListener;
    private BORealTimeBean boRealTimeBean;
    private List<String> myDevice = new ArrayList<>();

    public void setBloodBluetoothListener(OnBloodBluetoothListener mOnBloodBluetoothListener) {
        this.mBloodBluetoothListener = mOnBloodBluetoothListener;
    }

    public interface OnBloodBluetoothListener {
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
         * 实时监测
         */
        void onDeviceRealTimeReturnListener(BORealTimeBean boRealTimeBean);

//        /**
//         * 测量结果
//         */
//        void onMeasuringResultListener(BORealTimeBean boRealTimeBean);

//        /**
//         * 收到时间同步命令
//         */
//        void onGetTimeSynchronization();

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

    /**
     * 初始化蓝牙
     */
    public BloodOxygenBluetoothUtil(final Activity context, String... names) {
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
                if (mBloodBluetoothListener != null) {
                    mBloodBluetoothListener.onSearchStarted();
                }
            }

            @Override
            public void onSearchStopped() {
                if (mBloodBluetoothListener != null) {
                    mBloodBluetoothListener.onSearchStopped();
                }
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                //根据传入的名称，分辨设备
                if (myDevice.size() != 0) {
                    for (String name : myDevice) {
                        if (device.getName().equals(name)) {
                            if (mBloodBluetoothListener != null) {
                                mBloodBluetoothListener.onDeviceSpyListener(device, rssi);
                                break;
                            }
                        }
                    }
                } else {
                    if (mBloodBluetoothListener != null) {
                        mBloodBluetoothListener.onDeviceSpyListener(device, rssi);
                    }
                }
            }

            @Override
            public void onDeviceConnectSucceed() {
                if (mBloodBluetoothListener != null) {
                    mBloodBluetoothListener.onDeviceConnectSucceed();
                }
            }

            @Override
            public void onDeviceBreakListener() {
                if (mBloodBluetoothListener != null) {
                    mBloodBluetoothListener.onDeviceBreakListener();
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
                //不是血氧数据不解析
                if (connectBluetoothDeviceType() != BluetoothType.BLOOD_OXYGEN) {
                    return;
                }

                String result = ByteUtils.byteToString(value);
//                Log.e("bloodOxygen: ", "数据长度：" + value.length);
//                Log.e("bloodOxygen: ", "数据：" + result);
                String type = result.substring(12, 14);
                if (type.equals(BluetoothType.BLOOD_OXYGEN_DATA)) {
                    //数据块
                    String bloodData = result.substring(14, 24);
                    //血氧
                    int blood_oxygen = CalculateUtil.transform16_10(bloodData.substring(0, 2));
                    //脉率
                    int pulse_rate = CalculateUtil.transform16_10(bloodData.substring(2, 4));
                    //PI
                    float PI = (float) CalculateUtil.transform16_10(bloodData.substring(4, 6)) / 10;
                    //柱状图
                    int histogram = CalculateUtil.transform16_10(bloodData.substring(6, 8));
                    //波形
                    int waveform = CalculateUtil.transform16_10(bloodData.substring(8));

                    boRealTimeBean = new BORealTimeBean();
                    boRealTimeBean.setBlood_oxygen(blood_oxygen);
                    boRealTimeBean.setPulse_rate(pulse_rate);
                    boRealTimeBean.setPI(PI);
                    boRealTimeBean.setHistogram(histogram);
                    boRealTimeBean.setWaveform(waveform);
                    boRealTimeBean.setTime(String.valueOf(System.currentTimeMillis()));
                    if (mBloodBluetoothListener != null) {
                        mBloodBluetoothListener.onDeviceRealTimeReturnListener(boRealTimeBean);
                    }
//                    if (timer == null) {
//                        timer = new Timer();
//                        timer.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                //1s执行一次
//                                timer_time++;
//                                if (timer_time == 20) {
//                                    //发送结果(收到数据20发送一次)
//                                    if (boRealTimeBean.getBlood_oxygen() == 0 && boRealTimeBean.getPulse_rate() == 0) {
//                                        timer_time = timer_time - 1;
//                                        return;
//                                    }
//                                    timer_time = 0;
//                                    context.runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            if (mBloodBluetoothListener != null) {
//                                                mBloodBluetoothListener.onMeasuringResultListener(boRealTimeBean);
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        }, 0, 1000);
//                    }
                }
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {
                if (mBloodBluetoothListener != null) {
                    mBloodBluetoothListener.onReadBluetoothRssi(rssi);
                }
            }

            @Override
            public void onDeviceConnectFailing(int code) {
                if (mBloodBluetoothListener != null) {
                    mBloodBluetoothListener.onDeviceConnectFailing(code);
                }
            }
        });

    }
//
//    private Timer timer;
//    private int timer_time;

//    /**
//     * 关闭计时器
//     */
//    public void closeTimer() {
//        if (timer != null) {
//            timer.cancel();
//        }
//    }

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
        bluetoothUtil.connectBluetooth(device, BluetoothType.BLOOD_OXYGEN);
    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.BLOOD_OXYGEN);
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
        //关闭倒计时
//        closeTimer();
    }

    /**
     * 发送时间同步
     */
    public void writeBluetoothTime() {
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength(BluetoothUtil.getNowTimeString()) +
                BluetoothType.BLOOD_OXYGEN_REAL_TIME + BluetoothUtil.getNowTimeString() +
                BluetoothUtil.makeChecksum(BluetoothUtil.getNowTimeString(),
                        BluetoothUtil.getCmdLength(BluetoothUtil.getNowTimeString()),
                        BluetoothType.BLOOD_OXYGEN_REAL_TIME) +
                BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 发送数据
     */
    public void writeBluetoothData() {
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength("")
                + BluetoothType.EAR_THERMOMETER_SEND
                + BluetoothUtil.makeChecksum(BluetoothUtil.getCmdLength(""),
                BluetoothType.EAR_THERMOMETER_SEND) + BluetoothType.DATA_FOOT;
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

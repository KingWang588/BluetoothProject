package com.hzkj.bw.bloodglucoselibrary;

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

public class BloodGlucoseBluetoothUtil {

    private Context context;
    private BluetoothUtil bluetoothUtil;
    private OnBloodBluetoothListener mBloodBluetoothListener;
    private String datas = "";
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
         * 浓度
         */
        void onConcentrationResultListener(BloodGlucoseBean bloodGlucoseBean);

        /**
         * 插入试纸
         */
        void onTestPaperResultListener();

        /**
         * 等待滴血
         */
        void onBleedResultListener();

        /**
         * 倒计时
         */
        void onDownTimeResultListener(int time);

        /**
         * Er
         */
        void onErTypeResultListener(String er);

        /**
         * 记忆同步
         */
        void onMemorySynListener(List<BloodGlucoseBean> beans);

        /**
         * 设备主要信息
         */
        void onDeviceResultListener(BloodGlucoseDeviceBean bloodGlucoseDeviceBean);

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
    public BloodGlucoseBluetoothUtil(final Context context, String... names) {
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
                //设备连接成功
                //同步时间
                writeBluetoothTime();
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
                //不是血糖数据不解析
                if (connectBluetoothDeviceType() != BluetoothType.BLOOD_GLUCOSE_NAME_CODE) {
                    return;
                }
                String result = ByteUtils.byteToString(value);
//                Log.e("onNotify: ", "数据长度：" + value.length);
//                Log.e("onNotify: ", "数据：" + result);
                if (result.startsWith(BluetoothType.DATA_HEAD) && result.endsWith(BluetoothType.DATA_FOOT)) {
                    datas = result;
                } else {
                    if (result.length() >= 4) {
                        if (result.startsWith(BluetoothType.DATA_HEAD)) {
                            datas = "";
                            datas = result;
                            return;
                        } else if (result.endsWith(BluetoothType.DATA_FOOT)) {
                            datas = datas + result;
                        } else {
                            datas = datas + result;
                            return;
                        }
                    } else {
                        datas = datas + result;
                        if (!datas.endsWith(BluetoothType.DATA_FOOT)) {
                            return;
                        }
                    }
                }

//                Log.e("onNotify: ", "完整数据：" + datas);
                if (datas.length() < 20) {
                    return;
                }
                //功能字
                String type = datas.substring(12, 14);
                //数据块
                String bloodData = "";
                if (datas.length() > 20) {
                    bloodData = datas.substring(14, datas.length() - 6);
                }
                //数据长度
                String dataLength = datas.substring(8, 12);
                //校验和
                String verifySun = datas.substring(datas.length() - 6, datas.length() - 4);
                //进行校验
                if (!BluetoothUtil.makeChecksum(bloodData, dataLength, type)
                        .equals(verifySun)) {
                    //未成功返回
                    return;
                }
                //校验成功开始下一步
                switch (type) {
                    case BluetoothType.BLOOD_GLUCOSE_CONCENTRATION:
                        //收到浓度
                        //发送浓度消息
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_CONCENTRATION_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onConcentrationResultListener(dataTransition(bloodData));
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_RECEIVE_TIME:
                        //时间同步
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_TEST_PAPER:
                        //插入试纸
                        //发送插入试纸
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_TEST_PAPER_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onTestPaperResultListener();
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_BLEED:
                        //等待滴血
                        //发送等待滴血
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_BLEED_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onBleedResultListener();
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_COUNT_DOWN:
                        //倒计时
                        //发送倒计时
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_COUNT_DOWN_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onDownTimeResultListener(CalculateUtil.transform16_10(bloodData));
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_ER1:
                        //Er1
                        //发送Er1
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_ER1_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onErTypeResultListener(BluetoothType.BLOOD_GLUCOSE_ER1_RES);
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_ER2:
                        //Er2
                        //发送Er2
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_ER2_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onErTypeResultListener(BluetoothType.BLOOD_GLUCOSE_ER2_RES);
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_ER3:
                        //Er3
                        //发送Er3
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_ER3_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onErTypeResultListener(BluetoothType.BLOOD_GLUCOSE_ER3_RES);
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_ER4:
                        //Er4
                        //发送Er4
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_ER4_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onErTypeResultListener(BluetoothType.BLOOD_GLUCOSE_ER4_RES);
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_ER5:
                        //Er5
                        //发送Er5
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_ER5_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onErTypeResultListener(BluetoothType.BLOOD_GLUCOSE_ER5_RES);
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_ER6:
                        //Er6
                        //发送Er6
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_ER6_RES);
                        if (mBloodBluetoothListener != null) {
                            mBloodBluetoothListener.onErTypeResultListener(BluetoothType.BLOOD_GLUCOSE_ER6_RES);
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_MEMORY:
                        //记忆同步
                        //a、记忆同步最大30组
                        //b、第一组记忆为本次测量显示
                        //发送记忆同步
                        if (mBloodBluetoothListener != null) {
                            List<BloodGlucoseBean> beans = new ArrayList<>();
                            String[] strings = BluetoothUtil.stringSpilt(bloodData, 12);
                            for (String str : strings) {
                                //数据转换处理
                                beans.add(dataTransition(str));
                            }
                            mBloodBluetoothListener.onMemorySynListener(beans);
                            writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_MEMORY_RES);
                        }
                        break;
                    case BluetoothType.BLOOD_GLUCOSE_APPARATUS:
                        //仪器主要信息
                        //发送仪器主要信息
                        writeBluetoothData(BluetoothType.BLOOD_GLUCOSE_APPARATUS_RES);
                        if (mBloodBluetoothListener != null) {
                            BloodGlucoseDeviceBean deviceBean = new BloodGlucoseDeviceBean();
                            deviceBean.setDevice_model(String.valueOf(CalculateUtil.transform16_10(bloodData.substring(0, bloodData.length() - 6))));
                            deviceBean.setDevice_procedure(String.valueOf(CalculateUtil.transform16_10(
                                    bloodData.substring(bloodData.length() - 6, bloodData.length() - 2))));
                            deviceBean.setDevice_versions(String.valueOf(CalculateUtil.transform16_10(bloodData.substring(bloodData.length() - 2))));
                            mBloodBluetoothListener.onDeviceResultListener(deviceBean);
                        }
                        break;
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
        bluetoothUtil.connectBluetooth(device, BluetoothType.BLOOD_GLUCOSE_NAME_CODE);
    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.BLOOD_GLUCOSE_NAME_CODE);
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
     * 发送时间同步
     */
    public void writeBluetoothTime() {
        String tranTime = BluetoothUtil.getNowTimestamp();
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength(tranTime) +
                BluetoothType.BLOOD_GLUCOSE_RECEIVE_TIME_RES + tranTime +
                BluetoothUtil.makeChecksum(tranTime,
                        BluetoothUtil.getCmdLength(tranTime),
                        BluetoothType.BLOOD_GLUCOSE_RECEIVE_TIME_RES) +
                BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 血糖相关指令
     */
    public void writeBluetoothData(String type) {
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength("")
                + type + BluetoothUtil.makeChecksum(BluetoothUtil.getCmdLength("")
                , type) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 界面退出
     */
    public void onDestroy() {
        //注销蓝牙连接状态监听回调
        bluetoothUtil.unregisterBluetoothStateListener();
    }

    /**
     * 数据转换
     */
    public static BloodGlucoseBean dataTransition(String str) {
        if (str.length() != 12) {
            return null;
        }
        BloodGlucoseBean bloodGlucoseBean = new BloodGlucoseBean();
        String time = String.valueOf(CalculateUtil.transform16_10(str.substring(6, 8) +
                str.substring(4, 6) + str.substring(2, 4) + str.substring(0, 2))).trim();
        @SuppressLint("DefaultLocale")
        String concentration = String.format("%.2f", ((float) CalculateUtil.transform16_10(str.substring(10, 12)
                + str.substring(8, 10)) / 18));
        bloodGlucoseBean.setTimestamp(time);
        bloodGlucoseBean.setConcentration(concentration);
        return bloodGlucoseBean;
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

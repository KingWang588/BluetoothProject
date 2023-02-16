package com.cj.boodpressurelibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.CountDownTimer;
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
 * 血压
 */
public class BloodPressureBluetoothUtil {
    private BluetoothUtil bluetoothUtil;
    private Activity context;
    private OnBloodPressureBluetoothListener onBloodPressureBluetoothListener;
    private String datas = "";
    private boolean isMemory;
    private List<String> myDevice = new ArrayList<>();

    public void setBloodPressureBluetoothListener(OnBloodPressureBluetoothListener onBloodPressureBluetoothListener) {
        this.onBloodPressureBluetoothListener = onBloodPressureBluetoothListener;
    }

    public interface OnBloodPressureBluetoothListener {
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
         * 测量结果
         */
        void onDeviceValueReturnListener(BloodPressureBean bloodPressureBean);

        /**
         * 测量结果
         */
        void onDeviceMemoryValueReturnListener(BloodPressureBean bloodPressureBean);

        /**
         * 错误类型
         */
        void onErrorReturnListener(int type);

        /**
         * 92方法回传
         */
        void onReturnDataListener(String type, String string);

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

    public BloodPressureBluetoothUtil(final Activity context, String... names) {
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
                if (onBloodPressureBluetoothListener != null) {
                    onBloodPressureBluetoothListener.onSearchStarted();
                }
            }

            @Override
            public void onSearchStopped() {
                if (onBloodPressureBluetoothListener != null) {
                    onBloodPressureBluetoothListener.onSearchStopped();
                }
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                //根据传入的名称，分辨设备
                if (myDevice.size() != 0) {
                    for (String name : myDevice) {
                        if (device.getName().equals(name)) {
                            if (onBloodPressureBluetoothListener != null) {
                                onBloodPressureBluetoothListener.onDeviceSpyListener(device,rssi);
                                break;
                            }
                        }
                    }
                } else {
                    if (onBloodPressureBluetoothListener != null) {
                        onBloodPressureBluetoothListener.onDeviceSpyListener(device,rssi);
                    }
                }
            }

            @Override
            public void onDeviceConnectSucceed() {
                //设备连接成功
                //同步时间
                writeBluetoothTime();
                if (onBloodPressureBluetoothListener != null) {
                    onBloodPressureBluetoothListener.onDeviceConnectSucceed();
                }
            }

            @Override
            public void onDeviceBreakListener() {
                if (onBloodPressureBluetoothListener != null) {
                    onBloodPressureBluetoothListener.onDeviceBreakListener();
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
                //不是血压数据不解析
                if (connectBluetoothDeviceType() != BluetoothType.BLOOD_PRESSURE_NAME_CODE) {
                    return;
                }
                String result = ByteUtils.byteToString(value);
//                Log.e("onNotify: ", "数据长度：" + value.length);
//                Log.e("onNotify: ", "数据：" + result);

                if (result.length() == 40) {
                    if (result.substring(0, 8).equals(BluetoothType.DATA_HEAD) &&
                            result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                        //其他命令数据
                        String type = result.substring(12, 14);
                        if (type.equals("93") || type.equals("94") || type.equals("95") || type.equals("96")) {
                            //传出数据
                            if (onBloodPressureBluetoothListener != null) {
                                onBloodPressureBluetoothListener.onReturnDataListener(type, result);
                            }
                        }
                        return;
                    }
                    if (result.substring(0, 8).equals(BluetoothType.DATA_HEAD)) {
                        datas = result;
                        return;
                    }
                    if (result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                        datas = datas + result;
                    } else {
                        datas = datas + result;
                        return;
                    }
                } else {
                    if (result.length() > 8) {
                        if (result.substring(0, 8).equals(BluetoothType.DATA_HEAD) &&
                                result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                            //其他命令数据
                            String type = result.substring(12, 14);
                            if (type.equals(BluetoothType.BLOOD_PRESSURE_REAL_TIME)) {
                                //收到时间同步
                                synchrotimer = true;
                            }
                            if (type.equals(BluetoothType.BLOOD_PRESSURE_DOWNLOAD_MEMORY_END)) {
                                //收到记忆结束
                                isMemory = false;
                                download_memory = true;
                            }
                            if (type.equals("92")) {
                                //传出数据
                                if (onBloodPressureBluetoothListener != null) {
                                    onBloodPressureBluetoothListener.onReturnDataListener(type, result);
                                }
                            }
                            if (type.equals(BluetoothType.BLOOD_PRESSURE_RESULT_UP)) {
                                if (isMemory) {
                                    //下载记忆数据
                                    if (onBloodPressureBluetoothListener != null) {
                                        onBloodPressureBluetoothListener.onDeviceMemoryValueReturnListener(dataTransition(result.substring(14, result.length() - 6)));
                                    }
                                } else {
                                    //测量结果
                                    if (onBloodPressureBluetoothListener != null) {
                                        onBloodPressureBluetoothListener.onDeviceValueReturnListener(dataTransition(result.substring(14, result.length() - 6)));
                                    }
                                    //发送测量结果应答
                                    writeBluetoothData();
                                }

                            }
                            return;
                        }
                        if (result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                            datas = datas + result;
                        }
                    } else {
                        String meData = datas + result;
                        if (!meData.substring(meData.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                            return;
                        } else {
                            datas = meData;
                        }
                    }
                }
                //其他命令数据
                String type = datas.substring(12, 14);
                String dataLength = datas.substring(8, 12);
                if (type.equals(BluetoothType.BLOOD_PRESSURE_RESULT_UP)) {
                    if (BluetoothUtil.makeChecksum(datas.substring(14, datas.length() - 6), dataLength,
                            BluetoothType.BLOOD_PRESSURE_RESULT_UP).equals(
                            datas.substring(datas.length() - 6, datas.length() - 4))) {
                        if (isMemory) {
                            //下载记忆数据
                            if (onBloodPressureBluetoothListener != null) {
                                onBloodPressureBluetoothListener.onDeviceMemoryValueReturnListener(dataTransition(datas.substring(14, datas.length() - 6)));
                            }
                        } else {
                            //测量结果
                            if (onBloodPressureBluetoothListener != null) {
                                onBloodPressureBluetoothListener.onDeviceValueReturnListener(dataTransition(datas.substring(14, datas.length() - 6)));
                            }
                            //发送测量结果应答
                            writeBluetoothData();
                        }
                        //发送测量结果应答
                        writeBluetoothData();
                    }
                }

            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {
                if (onBloodPressureBluetoothListener!=null){
                    onBloodPressureBluetoothListener.onReadBluetoothRssi(rssi);
                }
            }

            @Override
            public void onDeviceConnectFailing(int code) {
                if (onBloodPressureBluetoothListener!=null){
                    onBloodPressureBluetoothListener.onDeviceConnectFailing(code);
                }
            }
        });
    }

    private CountDownTimer countDownTimer;
    private int total_time = 6000;
    private int down_time = 2000;
    private boolean synchrotimer;//时间同步
    private boolean download_memory;//下载记忆

    private void openCountDownTimer(final int type) {
        countDownTimer = new CountDownTimer(total_time, down_time) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (context.isDestroyed()) {
                    countDownTimer.cancel();
                }
                switch (type) {
                    case BloodPressureTool.ERROR_REAL_TIME:
                        //时间同步
                        if (synchrotimer) {
                            countDownTimer.cancel();
                        } else {
                            //发送时间同步
                            writeBluetoothTime();
                        }
                        break;
                    case BloodPressureTool.ERROR_DOWNLOAD_MEMORY:
                        //下载记忆
                        if (download_memory) {
                            countDownTimer.cancel();
                        } else {
                            //发送下载记忆
                            writeBluetoothResult();
                        }
                        break;
                }
            }

            @Override
            public void onFinish() {
                switch (type) {
                    case BloodPressureTool.ERROR_REAL_TIME:
                        //时间同步
                        if (synchrotimer) {
                            countDownTimer.cancel();
                        } else {
                            if (onBloodPressureBluetoothListener != null) {
                                onBloodPressureBluetoothListener.onErrorReturnListener(BloodPressureTool.ERROR_REAL_TIME);
                            }
                        }
                        break;
                    case BloodPressureTool.ERROR_DOWNLOAD_MEMORY:
                        //下载记忆
                        if (download_memory) {
                            countDownTimer.cancel();
                        } else {
                            if (onBloodPressureBluetoothListener != null) {
                                onBloodPressureBluetoothListener.onErrorReturnListener(BloodPressureTool.ERROR_DOWNLOAD_MEMORY);
                            }
                        }
                        break;
                }
            }
        };
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
        bluetoothUtil.connectBluetooth(device, BluetoothType.BLOOD_PRESSURE_NAME_CODE);
    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.BLOOD_PRESSURE_NAME_CODE);
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
    public void connectBluetoothRssi(){
        if (bluetoothUtil.connectBluetoothDevice() == null) {
            return ;
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * 发送测量结果
     */
    public void writeBluetoothData() {
        String data = BluetoothType.DATA_HEAD + "000A" + BluetoothType.BLOOD_PRESSURE_RESULT
                + BluetoothUtil.makeChecksum("000A", BluetoothType.BLOOD_PRESSURE_RESULT) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 发送下载记忆
     */
    public void writeBluetoothResult() {
        isMemory = true;
        String data = BluetoothType.DATA_HEAD + "000A" + BluetoothType.BLOOD_PRESSURE_DOWNLOAD_MEMORY
                + BluetoothUtil.makeChecksum("000A", BluetoothType.BLOOD_PRESSURE_DOWNLOAD_MEMORY) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
        download_memory = false;
        openCountDownTimer(BloodPressureTool.ERROR_DOWNLOAD_MEMORY);
    }

    /**
     * 发送时间同步
     */
    public void writeBluetoothTime() {
        String time = BluetoothUtil.getNowTimeString();
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength(time) +
                BluetoothType.BLOOD_PRESSURE_REAL_TIME_UP + time +
                BluetoothUtil.makeChecksum(time, BluetoothUtil.getCmdLength(time),
                        BluetoothType.BLOOD_PRESSURE_REAL_TIME_UP) +
                BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
        synchrotimer = false;
        openCountDownTimer(BloodPressureTool.ERROR_REAL_TIME);
    }

    /**
     * 数据转换
     */
    private BloodPressureBean dataTransition(String str) {
        if (str.length() != 24) {
            return null;
        }
        BloodPressureBean bloodPressureBean = new BloodPressureBean();
        //收缩压
        bloodPressureBean.setSystolicPressure(String.valueOf(CalculateUtil.transform16_10(str.substring(0, 4))));
        //舒张压
        bloodPressureBean.setDiastolicPressure(String.valueOf(CalculateUtil.transform16_10(str.substring(4, 8))));
        //心率
        bloodPressureBean.setHeartRate(String.valueOf(CalculateUtil.transform16_10(str.substring(8, 10))));
        //心颤
        bloodPressureBean.setHeartFibrillation(String.valueOf(CalculateUtil.transform16_10(str.substring(10, 12))));
        //年
        bloodPressureBean.setYear(String.valueOf(CalculateUtil.transform16_10(str.substring(12, 14))));
        //月
        bloodPressureBean.setMonth(String.valueOf(CalculateUtil.transform16_10(str.substring(14, 16))));
        //日
        bloodPressureBean.setDay(String.valueOf(CalculateUtil.transform16_10(str.substring(16, 18))));
        //时
        bloodPressureBean.setHour(String.valueOf(CalculateUtil.transform16_10(str.substring(18, 20))));
        //分
        bloodPressureBean.setMinute(String.valueOf(CalculateUtil.transform16_10(str.substring(20, 22))));
        //组边
        bloodPressureBean.setGroup(String.valueOf(CalculateUtil.transform16_10(str.substring(22))));

        return bloodPressureBean;
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

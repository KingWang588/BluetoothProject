package com.cj.ecglibrary;

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
public class ECGBluetoothUtil {
    private BluetoothUtil bluetoothUtil;
    private Activity context;
    private OnECGBluetoothListener onECGBluetoothListener;
    private String datas = "";
    private List<String> myDevice = new ArrayList<>();

    public void setECGBluetoothListener(OnECGBluetoothListener onECGBluetoothListener) {
        this.onECGBluetoothListener = onECGBluetoothListener;
    }

    public interface OnECGBluetoothListener {
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
        void onDeviceValueReturnListener(ECGReturnBean ecgReturnBean);

        /**
         * 实时数据
         */
        void onDeviceRealTimeListener(ECGRealTimeBean ecgRealTimeBean);

        /**
         * 记忆上传
         */
        void onMemoryUpListener(List<ECGReturnBean> data);

        /**
         * 错误（开始测量未收到数据）
         */
        void onErrorReturnListener();

        /**
         * 错误（时间同步未收到数据）
         */
        void onErrorRealTimeListener();

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

    public ECGBluetoothUtil(final Activity context, String... names) {
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
                if (onECGBluetoothListener != null) {
                    onECGBluetoothListener.onSearchStarted();
                }
            }

            @Override
            public void onSearchStopped() {
                if (onECGBluetoothListener != null) {
                    onECGBluetoothListener.onSearchStopped();
                }
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                //根据传入的名称，分辨设备
                if (myDevice.size() != 0) {
                    for (String name : myDevice) {
                        if (device.getName().equals(name)) {
                            if (onECGBluetoothListener != null) {
                                onECGBluetoothListener.onDeviceSpyListener(device, rssi);
                                break;
                            }
                        }
                    }
                } else {
                    if (onECGBluetoothListener != null) {
                        onECGBluetoothListener.onDeviceSpyListener(device, rssi);
                    }
                }
            }

            @Override
            public void onDeviceConnectSucceed() {
                writeBluetoothTime();
                openCountDownTimerRealTime();
                if (onECGBluetoothListener != null) {
                    onECGBluetoothListener.onDeviceConnectSucceed();
                }
            }

            @Override
            public void onDeviceBreakListener() {
                if (onECGBluetoothListener != null) {
                    onECGBluetoothListener.onDeviceBreakListener();
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
                if (connectBluetoothDeviceType() != BluetoothType.ECG_CODE) {
                    return;
                }
                String result = ByteUtils.byteToString(value);
//                Log.e("onNotify: ", "数据长度：" + value.length);
//                Log.e("onNotify: ", "数据：" + result);

                if (result.length() == 40) {
                    if (result.substring(0, 8).equals(BluetoothType.DATA_HEAD) &&
                            result.substring(result.length() - 4).equals(BluetoothType.DATA_FOOT)) {
                        //其他命令数据
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
                            String getData = result.substring(14, result.length() - 6);
                            switch (type) {
                                case BluetoothType.ECG_REAL_TIME:
                                    //收到时间同步
                                    real_time = true;
                                    break;
                                case BluetoothType.ECG_START_RESULT:
                                    //收到开始测量
                                    writeBluetoothMeasurement();
                                    break;
                                case BluetoothType.ECG_SHUTDOWN:
                                    //收到关机动作
                                    break;
                                case BluetoothType.ECG_MODEL:
                                    //收到型号
                                    break;
                                case BluetoothType.ECG_RESULT:
                                    //测量结果
                                    if (onECGBluetoothListener != null) {
                                        onECGBluetoothListener.onDeviceValueReturnListener(dataTransitionReturn(getData));
                                    }
                                    //测量结果回复
                                    writeBluetoothData();
                                    break;
                                case BluetoothType.ECG_REAL_TIME_GET:
                                    //实时数据
                                    real_data = true;
                                    if (onECGBluetoothListener != null) {
                                        onECGBluetoothListener.onDeviceRealTimeListener(dataTransitionRealTime(getData));
                                    }
                                    break;
                                case BluetoothType.ECG_DOWNLOAD_MEMORY_END:
                                    //记忆上传
                                    if (onECGBluetoothListener != null) {
                                        List<ECGReturnBean> list = new ArrayList<>();
                                        int length = 0;
                                        while (length < getData.length()) {
                                            try {
                                                String myData = getData.substring(length, length + 4);
                                                ECGReturnBean ecgReturnBean = new ECGReturnBean();
                                                ecgReturnBean.setHeartbeat(String.valueOf(CalculateUtil.transform16_10(myData.substring(0, 2))));
                                                ecgReturnBean.setResult(String.valueOf(CalculateUtil.transform16_10(myData.substring(2))));
                                                list.add(ecgReturnBean);
//                                                list.add(CalculateUtil.transform16_10(getData.substring(length, length + 4)));
                                                length = length + 4;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        onECGBluetoothListener.onMemoryUpListener(list);
                                    }
                                    break;
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
//                Log.e("onNotify: ","收到的数据："+datas);
                //其他命令数据
                String type = datas.substring(12, 14);
                String getData = datas.substring(14, datas.length() - 6);
                switch (type) {
                    case BluetoothType.ECG_REAL_TIME_GET:
                        //实时数据
                        real_data = true;
                        if (onECGBluetoothListener != null) {
                            onECGBluetoothListener.onDeviceRealTimeListener(dataTransitionRealTime(getData));
                        }
                        break;
                    case BluetoothType.ECG_DOWNLOAD_MEMORY_END:
                        //记忆上传
                        if (onECGBluetoothListener != null) {
                            List<ECGReturnBean> list = new ArrayList<>();
                            int length = 0;
                            while (length < getData.length()) {
                                try {
                                    String myData = getData.substring(length, length + 4);
                                    ECGReturnBean ecgReturnBean = new ECGReturnBean();
                                    ecgReturnBean.setHeartbeat(String.valueOf(CalculateUtil.transform16_10(myData.substring(0, 2))));
                                    ecgReturnBean.setResult(String.valueOf(CalculateUtil.transform16_10(myData.substring(2))));
                                    list.add(ecgReturnBean);
//                                    list.add(CalculateUtil.transform16_10(getData.substring(length, length + 4)));
                                    length = length + 4;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            onECGBluetoothListener.onMemoryUpListener(list);
                        }
                        break;
                }

            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {
                if (onECGBluetoothListener != null) {
                    onECGBluetoothListener.onReadBluetoothRssi(rssi);
                }
            }

            @Override
            public void onDeviceConnectFailing(int code) {
                if (onECGBluetoothListener != null) {
                    onECGBluetoothListener.onDeviceConnectFailing(code);
                }
            }
        });
    }

    private CountDownTimer countDownTimer, count;
    private int total_time = 3000;
    private int down_time = 1000;
    private boolean real_data;//实时数据
    private boolean real_time;//时间同步

    //时间同步计时器
    private void openCountDownTimerRealTime() {
        count = new CountDownTimer(total_time, down_time) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (real_time) {
                    count.cancel();
                } else {
                    //发送时间同步
                    writeBluetoothTime();
                }
            }

            @Override
            public void onFinish() {
                if (real_time) {
                    count.cancel();
                } else {
                    if (onECGBluetoothListener != null) {
                        onECGBluetoothListener.onErrorRealTimeListener();
                    }
                }
            }
        };
    }

    //开始测量计时器
    private void openCountDownTimer() {
        countDownTimer = new CountDownTimer(total_time, down_time) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (real_data) {
                    countDownTimer.cancel();
                } else {
                    //发送开始测量
                    writeBluetoothMeasurement();
                }
            }

            @Override
            public void onFinish() {
                if (real_data) {
                    countDownTimer.cancel();
                } else {
                    if (onECGBluetoothListener != null) {
                        onECGBluetoothListener.onErrorReturnListener();
                    }
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
        bluetoothUtil.connectBluetooth(device, BluetoothType.ECG_CODE);
    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.ECG_CODE);
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
     * 发送开始测量回复
     */
    private void writeBluetoothMeasurement() {
        String data = BluetoothType.DATA_HEAD + "000A" + BluetoothType.ECG_START_RESULT_UP
                + BluetoothUtil.makeChecksum("000A", BluetoothType.ECG_START_RESULT_UP) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
        real_data = false;
        openCountDownTimer();
    }

    /**
     * 发送测量结果
     */
    public void writeBluetoothData() {
        String data = BluetoothType.DATA_HEAD + "000A" + BluetoothType.ECG_RESULT_UP
                + BluetoothUtil.makeChecksum("000A", BluetoothType.ECG_RESULT_UP) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 发送下载记忆
     */
    public void writeBluetoothResult() {
        String data = BluetoothType.DATA_HEAD + "000A" + BluetoothType.ECG_DOWNLOAD_MEMORY
                + BluetoothUtil.makeChecksum("000A", BluetoothType.ECG_DOWNLOAD_MEMORY) + BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothBabyData(data);
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 发送时间同步
     */
    public void writeBluetoothTime() {
        String time = BluetoothUtil.getNowTimeString();
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength(time) +
                BluetoothType.ECG_REAL_TIME_UP + time +
                BluetoothUtil.makeChecksum(time, BluetoothUtil.getCmdLength(time),
                        BluetoothType.ECG_REAL_TIME_UP) +
                BluetoothType.DATA_FOOT;
        real_time = false;
        bluetoothUtil.writeBluetoothData(data);
    }

    /**
     * 实时数据转换
     */
    private ECGRealTimeBean dataTransitionRealTime(String str) {
        if (str.length() < 4) {
            return null;
        }
        ECGRealTimeBean ecgRealTimeBean = new ECGRealTimeBean();
        int length = 0;
        List<Integer> dataList = new ArrayList<>();
        while (length < str.length() - 4) {
            dataList.add(CalculateUtil.transform16_10(str.substring(length, length + 4)));
            length = length + 4;
        }
        ecgRealTimeBean.setDataList(dataList);
        //心跳声 true响  false不响
        if (str.substring(str.length() - 4, str.length() - 2).equals("FF")) {
            ecgRealTimeBean.setHeartbeat(true);
        } else {
            ecgRealTimeBean.setHeartbeat(false);
        }
        ecgRealTimeBean.setHeartrate(String.valueOf(CalculateUtil.transform16_10(str.substring(str.length() - 2))));

        return ecgRealTimeBean;
    }

    /**
     * 测量结果转换
     */
    private ECGReturnBean dataTransitionReturn(String str) {
        if (str.length() != 18) {
            return null;
        }
        ECGReturnBean ecgReturnBean = new ECGReturnBean();
        //心跳
        ecgReturnBean.setHeartbeat(String.valueOf(CalculateUtil.transform16_10(str.substring(0, 4))));
        //测量结果
        ecgReturnBean.setResult(String.valueOf(CalculateUtil.transform16_10(str.substring(4, 6))));
        //年
        ecgReturnBean.setYear(String.valueOf(CalculateUtil.transform16_10(str.substring(6, 8))));
        //月
        ecgReturnBean.setMonth(String.valueOf(CalculateUtil.transform16_10(str.substring(8, 10))));
        //日
        ecgReturnBean.setDay(String.valueOf(CalculateUtil.transform16_10(str.substring(10, 12))));
        //时
        ecgReturnBean.setHour(String.valueOf(CalculateUtil.transform16_10(str.substring(12, 14))));
        //分
        ecgReturnBean.setMinute(String.valueOf(CalculateUtil.transform16_10(str.substring(14, 16))));
        //组边
        ecgReturnBean.setGroup(String.valueOf(CalculateUtil.transform16_10(str.substring(16))));

        return ecgReturnBean;
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

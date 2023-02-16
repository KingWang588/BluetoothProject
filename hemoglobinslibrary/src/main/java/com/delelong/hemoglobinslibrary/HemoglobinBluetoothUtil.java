package com.delelong.hemoglobinslibrary;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HemoglobinBluetoothUtil {

    private Context context;
    private BluetoothUtil bluetoothUtil;
    private OnHemoglobinBluetoothListener mHemoglobinBluetoothListener;
    private String datas = "";
    private List<String> myDevice = new ArrayList<>();

    public void setHemoglobinBluetoothListener(OnHemoglobinBluetoothListener mOnHemoglobinBluetoothListener) {
        this.mHemoglobinBluetoothListener = mOnHemoglobinBluetoothListener;
    }

    public interface OnHemoglobinBluetoothListener {
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
        void onConcentrationResultListener(HemoglobinBean hemoglobinBean);

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
        void onMemorySynListener(List<HemoglobinBean> beans);

        /**
         * 设备主要信息
         */
        void onDeviceResultListener(HemoglobinDeviceBean hemoglobinDeviceBean);

        /**
         * 蓝牙信号强度
         */
        void onReadBluetoothRssi( Integer rssi);
        /**
         * 蓝牙连接失败
         * @param code
         */
        void onDeviceConnectFailing(int code);
    }

    /**
     * 初始化蓝牙
     */
    public HemoglobinBluetoothUtil(final Context context, String... names) {
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
                if (mHemoglobinBluetoothListener != null) {
                    mHemoglobinBluetoothListener.onSearchStarted();
                }
            }

            @Override
            public void onSearchStopped() {
                if (mHemoglobinBluetoothListener != null) {
                    mHemoglobinBluetoothListener.onSearchStopped();
                }
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                //根据传入的名称，分辨设备
                if (myDevice.size() != 0) {
                    for (String name : myDevice) {
                        if (device.getName().equals(name)) {
                            if (mHemoglobinBluetoothListener != null) {
                                mHemoglobinBluetoothListener.onDeviceSpyListener(device,rssi);
                                break;
                            }
                        }
                    }
                } else {
                    if (mHemoglobinBluetoothListener != null) {
                        mHemoglobinBluetoothListener.onDeviceSpyListener(device,rssi);
                    }
                }
            }

            @Override
            public void onDeviceConnectSucceed() {
                //设备连接成功
                //同步时间
                writeBluetoothTime();
                if (mHemoglobinBluetoothListener != null) {
                    mHemoglobinBluetoothListener.onDeviceConnectSucceed();
                }
            }

            @Override
            public void onDeviceBreakListener() {
                if (mHemoglobinBluetoothListener != null) {
                    mHemoglobinBluetoothListener.onDeviceBreakListener();
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
                //不是血红蛋白数据不解析
                if (connectBluetoothDeviceType() != BluetoothType.HEMOGLOBIN_NAME_CODE) {
                    return;
                }
                String result = ByteUtils.byteToString(value);
                if (result.startsWith(BluetoothType.DATA_HEAD) && result.endsWith(BluetoothType.DATA_FOOT)) {
                    datas = result;
                } else if (result.startsWith("55AAAA55") && result.length() == 40) {
                    //仪器主要信息数据
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
                        if (datas.startsWith("55AAAA55") && datas.endsWith("5AA5") && datas.length() == 42) {
                            //仪器主要信息
                            //发送仪器主要信息
                            writeBluetoothData(BluetoothType.HEMOGLOBIN_APPARATUS_RES);
                            if (mHemoglobinBluetoothListener != null) {
                                String bloodData = datas.substring(14, datas.length() - 6);
                                HemoglobinDeviceBean deviceBean = new HemoglobinDeviceBean();
                                deviceBean.setDevice_model(String.valueOf(CalculateUtil.transform16_10(bloodData.substring(0, bloodData.length() - 6))));
                                deviceBean.setDevice_procedure(String.valueOf(CalculateUtil.transform16_10(
                                        bloodData.substring(bloodData.length() - 6, bloodData.length() - 2))));
                                deviceBean.setDevice_versions(String.valueOf(CalculateUtil.transform16_10(bloodData.substring(bloodData.length() - 2))));
                                mHemoglobinBluetoothListener.onDeviceResultListener(deviceBean);
                            }
                            return;
                        }
                        if (!datas.endsWith(BluetoothType.DATA_FOOT)) {
                            return;
                        }
                    }
                }
//                Log.e("onNotify: ", "数据长度：" + value.length);
//                Log.e("onNotify: ", "数据：" + result);
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
                    case BluetoothType.HEMOGLOBIN_CONCENTRATION:
                        //收到浓度
                        //发送浓度消息
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_CONCENTRATION_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onConcentrationResultListener(dataTransition(bloodData));
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_RECEIVE_TIME:
                        //时间同步
                        writeBluetoothTime();
                        break;
                    case BluetoothType.HEMOGLOBIN_TEST_PAPER:
                        //插入试纸
                        //发送插入试纸
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_TEST_PAPER_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onTestPaperResultListener();
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_BLEED:
                        //等待滴血
                        //发送等待滴血
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_BLEED_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onBleedResultListener();
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_COUNT_DOWN:
                        //倒计时
                        //发送倒计时
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_COUNT_DOWN_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onDownTimeResultListener(CalculateUtil.transform16_10(bloodData));
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_ER1:
                        //Er1
                        //发送Er1
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_ER1_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onErTypeResultListener(BluetoothType.HEMOGLOBIN_ER1_RES);
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_ER2:
                        //Er2
                        //发送Er2
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_ER2_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onErTypeResultListener(BluetoothType.HEMOGLOBIN_ER2_RES);
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_ER3:
                        //Er3
                        //发送Er3
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_ER3_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onErTypeResultListener(BluetoothType.HEMOGLOBIN_ER3_RES);
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_ER4:
                        //Er4
                        //发送Er4
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_ER4_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onErTypeResultListener(BluetoothType.HEMOGLOBIN_ER4_RES);
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_ER5:
                        //Er5
                        //发送Er5
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_ER5_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onErTypeResultListener(BluetoothType.HEMOGLOBIN_ER5_RES);
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_ER6:
                        //Er6
                        //发送Er6
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_ER6_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            mHemoglobinBluetoothListener.onErTypeResultListener(BluetoothType.HEMOGLOBIN_ER6_RES);
                        }
                        break;
                    case BluetoothType.HEMOGLOBIN_MEMORY:
                        //记忆同步
                        //a、记忆同步最大30组
                        //b、第一组记忆为本次测量显示
                        //发送记忆同步
                        writeBluetoothData(BluetoothType.HEMOGLOBIN_MEMORY_RES);
                        if (mHemoglobinBluetoothListener != null) {
                            List<HemoglobinBean> beans = new ArrayList<>();
                            String[] strings = BluetoothUtil.stringSpilt(bloodData, 14);
                            for (String str : strings) {
                                //数据转换处理
                                if (dataTransition(str) != null) {
                                    beans.add(dataTransition(str));
                                }
                            }
                            mHemoglobinBluetoothListener.onMemorySynListener(beans);
                        }
                        break;
                }
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {
                if (mHemoglobinBluetoothListener!=null){
                    mHemoglobinBluetoothListener.onReadBluetoothRssi(rssi);
                }
            }

            @Override
            public void onDeviceConnectFailing(int code) {
                if (mHemoglobinBluetoothListener!=null){
                    mHemoglobinBluetoothListener.onDeviceConnectFailing(code);
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
        bluetoothUtil.connectBluetooth(device, BluetoothType.HEMOGLOBIN_NAME_CODE);
    }

    /**
     * 设备自动连接蓝牙
     */
    public void connectAutomaticBluetooth() {
        bluetoothUtil.connectAutomaticBluetooth(BluetoothType.HEMOGLOBIN_NAME_CODE);
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
     * 发送时间同步
     */
    public void writeBluetoothTime() {
        String tranTime = BluetoothUtil.getNowTimeString();
        String data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength(tranTime) +
                BluetoothType.HEMOGLOBIN_RECEIVE_TIME_RES + tranTime +
                BluetoothUtil.makeChecksum(tranTime,
                        BluetoothUtil.getCmdLength(tranTime),
                        BluetoothType.HEMOGLOBIN_RECEIVE_TIME_RES) +
                BluetoothType.DATA_FOOT;
        bluetoothUtil.writeBluetoothData(data);
        bluetoothUtil.writeBluetoothBabyData(data);
    }

    /**
     * 血糖相关指令
     */
    public void writeBluetoothData(String type) {
        String data;
        if (type.equals(BluetoothType.HEMOGLOBIN_APPARATUS_RES)) {
            data = "55AAAA55" + BluetoothUtil.getCmdLength("")
                    + type + BluetoothUtil.makeChecksum(BluetoothUtil.getCmdLength("")
                    , type) + "5AA5";
        } else {
            data = BluetoothType.DATA_HEAD + BluetoothUtil.getCmdLength("")
                    + type + BluetoothUtil.makeChecksum(BluetoothUtil.getCmdLength("")
                    , type) + BluetoothType.DATA_FOOT;
        }

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
    public static HemoglobinBean dataTransition(String str) {
        if (str.length() != 14) {
            return null;
        }
        HemoglobinBean hemoglobinBean = new HemoglobinBean();
//        float concentration = (float) (((CalculateUtil.transform16_10(str.substring(8, 10)) & 0x0f) << 8) + (CalculateUtil.transform16_10(str.substring(str.length() - 2)))) / 10;//浓度
        int concentration  = (CalculateUtil.transform16_10(str.substring(10, 12)) & 0x0f) * 0x100 + (CalculateUtil.transform16_10(str.substring(str.length() - 2)));
        String idNumber = String.valueOf(((CalculateUtil.transform16_10(str.substring(0, 2)) & 0x0f) * 256 + (CalculateUtil.transform16_10(str.substring(2, 4)))));
        String year = String.valueOf(CalculateUtil.transform16_10(str.substring(4, 6)));//年
        String month = String.valueOf((CalculateUtil.transform16_10(str.substring(10, 12)) >> 4));//月
        String day = String.valueOf((CalculateUtil.transform16_10(str.substring(6, 8)) >> 3));//日
        String hour = String.valueOf((CalculateUtil.transform16_10(str.substring(8, 10)) >> 3));//时
        int minute = ((CalculateUtil.transform16_10(str.substring(6, 8)) & 0x07) << 3) + (int) (CalculateUtil.transform16_10(str.substring(8, 10)) & 0x07);//分
        String minutes = String.valueOf(minute).length() == 1 ? "0" + minute : String.valueOf(minute);
        String unit = null;//单位
        String concentrations = null;
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        switch ((CalculateUtil.transform16_10(str.substring(0, 2)) >> 4 & 0x0f)) {
            case 1:
                unit = "g/dL";
                concentrations = decimalFormat.format(concentration/10f);
                break;
            case 2:
                unit = "mmol/L";
                concentrations = decimalFormat.format(concentration/16.1);
                break;
            case 3:
                unit = "g/L";
                concentrations = String.valueOf(concentration);
                break;
        }
        hemoglobinBean.setYear(year);
        hemoglobinBean.setMonth(month);
        hemoglobinBean.setDay(day);
        hemoglobinBean.setHour(hour);
        hemoglobinBean.setMinute(minutes);
        hemoglobinBean.setUnit(unit);
        hemoglobinBean.setConcentration(concentrations);
        hemoglobinBean.setIdNumber(idNumber);
//        Log.e("hemoglobinBean: ", "===年===  " + year);
//        Log.e("hemoglobinBean: ", "===月===  " + month);
//        Log.e("hemoglobinBean: ", "===日===  " + day);
//        Log.e("hemoglobinBean: ", "===时===  " + hour);
//        Log.e("hemoglobinBean: ", "===分===  " + minutes);
//        Log.e("hemoglobinBean: ", "===单位===  " + unit);
//        Log.e("hemoglobinBean: ", "===浓度===  " + concentration);
//        Log.e("hemoglobinBean: ", "===ID号===  " + idNumber);
//        Log.e("hemoglobinBean: ", "===浓度===  " + concentrations);
        return hemoglobinBean;
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

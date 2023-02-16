//package com.cj.bluetoothlibrary;
//
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.inuker.bluetooth.library.BluetoothClient;
//import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
//import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
//import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
//import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
//import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
//import com.inuker.bluetooth.library.connect.response.BleReadResponse;
//import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
//import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
//import com.inuker.bluetooth.library.model.BleGattCharacter;
//import com.inuker.bluetooth.library.model.BleGattProfile;
//import com.inuker.bluetooth.library.model.BleGattService;
//import com.inuker.bluetooth.library.search.SearchRequest;
//import com.inuker.bluetooth.library.search.SearchResult;
//import com.inuker.bluetooth.library.search.response.SearchResponse;
//
//import java.util.Calendar;
//import java.util.UUID;
//
//import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;
//import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
//import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTED;
//import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_DISCONNECTED;
//import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;
//
///**
// * 蓝牙操作工具类
// */
//public class BluetoothUtil {
//    private static BluetoothClient mBluetoothClient;
//    private static BluetoothDevices CONNECT_MAC_DATA;
//    private Context context;
//    private OnScanBluetoothListener scanBluetoothListener;
//    private static UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
//    private static UUID UUID_NOTIFY = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
//    private static UUID UUID_WRITE = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
//
//    public void setScanBluetoothListener(OnScanBluetoothListener mOnScanBluetoothListener) {
//        this.scanBluetoothListener = mOnScanBluetoothListener;
//    }
//
//    public interface OnScanBluetoothListener {
//        /**
//         * 扫描设备开始回调
//         */
//        void onSearchStarted();
//
//        /**
//         * 扫描设备停止回调
//         */
//        void onSearchStopped();
//
//        /**
//         * 扫描到设备回调
//         */
//        void onDeviceSpyListener(BluetoothDevice device);
//
//        /**
//         * 设备连接成功
//         */
//        void onDeviceConnectSucceed();
//
//        /**
//         * 设备异常断开回调
//         */
//        void onDeviceBreakListener();
//
//        /**
//         * 读取数据（接收）
//         *
//         * @param code
//         * @param data
//         */
//        void onReadBluetoothDataListener(int code, byte[] data);
//
//        /**
//         * 读取数据（接收）
//         */
//        void onWriteBluetoothDataListener();
//
//        /**
//         * Notify接收通知
//         *
//         * @param service
//         * @param character
//         * @param value
//         */
//        void onNotifyBluetoothDataListener(UUID service, UUID character, byte[] value);
//    }
//
//    /**
//     * 初始化蓝牙
//     */
//    public BluetoothUtil(Context context) {
//        this.context = context;
//        if (mBluetoothClient == null) {
//            CONNECT_MAC_DATA = SpHelper.getBluetooth(context);
//            mBluetoothClient = new BluetoothClient(context);
//        }
//    }
//
//    /**
//     * 蓝牙非空判断
//     */
//    private boolean nullBluetooth() {
//        return mBluetoothClient != null;
//    }
//
//    /**
//     * 蓝牙状态
//     */
//    public boolean stateBluetooth() {
//        if (nullBluetooth()) {
//            return mBluetoothClient.isBluetoothOpened();
//        }
//        return false;
//    }
//
//    /**
//     * 打开蓝牙
//     */
//    public void openBluetooth() {
//        if (nullBluetooth()) {
//            if (!stateBluetooth()) {
//                mBluetoothClient.openBluetooth();
//                mBluetoothClient.registerBluetoothStateListener(mBluetoothStateListener);
//            }
//        }
//    }
//
//    /**
//     * 关闭蓝牙
//     */
//    public void closeBluetooth() {
//        if (nullBluetooth()) {
//            if (stateBluetooth()) {
//                mBluetoothClient.closeBluetooth();
//                mBluetoothClient.registerBluetoothStateListener(mBluetoothStateListener);
//            }
//
//        }
//    }
//
//    /**
//     * 扫描蓝牙，如果有连接，进行断开
//     */
//    public void isConnectDetection(BluetoothDevice device) {
//        BluetoothDevices devices = SpHelper.getBluetooth(context);
//        if (devices != null) {
//            //判断是否是同一蓝牙
//            if (!device.getAddress().equals(devices.getAddress())) {
//                //不是的话判断蓝牙连接状态
//                int status = mBluetoothClient.getConnectStatus(devices.getAddress());
//                if (status == STATUS_DEVICE_CONNECTED) {
//                    Log.e("isConnectDetection: ", "不同蓝牙，已连接，进行断开操作");
//                    //处于连接状态先断开连接
//                    mBluetoothClient.disconnect(devices.getAddress());
//                } else if (status == STATUS_DEVICE_DISCONNECTED) {
//                    Log.e("isConnectDetection: ", "不同蓝牙，未连接，不做处理");
//                }
//            } else {
//                Log.e("isConnectDetection: ", "相同蓝牙，不做处理");
//            }
//        }
//    }
//
//    /**
//     * 蓝牙开关监听回调
//     */
//    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
//        @Override
//        public void onBluetoothStateChanged(boolean openOrClosed) {
//            if (openOrClosed) {
//                Toast.makeText(context, "蓝牙已经打开了", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "蓝牙已经关闭了", Toast.LENGTH_SHORT).show();
//            }
//            //收到监听后解除回调
//            mBluetoothClient.unregisterBluetoothStateListener(mBluetoothStateListener);
//        }
//    };
//
//    /**
//     * 注销开关监听回调
//     */
//    public void unregisterBluetoothStateListener() {
//        //注销开关监听回调
//        mBluetoothClient.unregisterBluetoothStateListener(mBluetoothStateListener);
//        BluetoothDevices bluetoothDevices = SpHelper.getBluetooth(context);
//        if (bluetoothDevices != null) {
//            //蓝牙设备断开监听取消
//            mBluetoothClient.unregisterConnectStatusListener(bluetoothDevices.getAddress(), mBleConnectStatusListener);
//            //断开蓝牙
//            //是否有连接的蓝牙，有的话先断开
//            if (CONNECT_MAC_DATA != null) {
//                mBluetoothClient.disconnect(CONNECT_MAC_DATA.getAddress());
//            }
//        }
//        if (CONNECT_MAC_DATA != null) {
//            //关闭Notify
//            unNotifyBluetoothData();
//        }
//    }
//
//    /**
//     * 设备扫描
//     */
//    public void scanBluetooth() {
//        if (nullBluetooth()) {
//            //如果正在扫描，先停止
//            stopBluetooth();
//            SearchRequest request = new SearchRequest.Builder()
//                    .searchBluetoothLeDevice(3000, 1)   // 先扫BLE设备1次，每次1s
//                    .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
//                    .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
//                    .build();
//
//            mBluetoothClient.search(request, new SearchResponse() {
//                @Override
//                public void onSearchStarted() {
//                    //开始搜素
//                    if (scanBluetoothListener != null) {
//                        scanBluetoothListener.onSearchStarted();
//                    }
//                }
//
//                @Override
//                public void onDeviceFounded(SearchResult device) {
//                    //找到设备 可通过manufacture过滤
//                    if (scanBluetoothListener != null) {
//                        //找到设备
//                        //名称为空跳过
//                        if (device.device.getName() == null) {
//                            return;
//                        }
//                        scanBluetoothListener.onDeviceSpyListener(device.device);
//                    }
//                }
//
//                @Override
//                public void onSearchStopped() {
//                    //搜索停止
//                    if (scanBluetoothListener != null) {
//                        scanBluetoothListener.onSearchStopped();
//                    }
//                }
//
//                @Override
//                public void onSearchCanceled() {
//                    //搜索取消
//                }
//            });
//        }
//    }
//
//    /**
//     * 设备停止扫描
//     */
//    public void stopBluetooth() {
//        if (mBluetoothClient != null) {
//            mBluetoothClient.stopSearch();
//            if (scanBluetoothListener != null) {
//                scanBluetoothListener.onSearchStopped();
//            }
//        }
//    }
//
//    /**
//     * 蓝牙已连接的设备
//     */
//    public BluetoothDevices connectBluetoothDevice() {
//        BluetoothDevices CONNECT_MAC = SpHelper.getBluetooth(context);
//        if (CONNECT_MAC == null) {
//            return null;
//        }
//        return CONNECT_MAC;
//    }
//
//    /**
//     * 设备连接蓝牙
//     */
//    public void connectBluetooth(final BluetoothDevice device, final int type) {
//        //停止扫描蓝牙
//        stopBluetooth();
//        //是否有连接的蓝牙，有的话先断开
//        isConnectDetection(device);
//        //再去连接蓝牙
//        BleConnectOptions options = new BleConnectOptions.Builder()
//                .setConnectRetry(3)   // 连接如果失败重试3次
//                .setConnectTimeout(30000)   // 连接超时30s
//                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
//                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
//                .build();
//
//        mBluetoothClient.connect(device.getAddress(), options, new BleConnectResponse() {
//            @Override
//            public void onResponse(int code, BleGattProfile data) {
//                //连接成功
//                if (code == REQUEST_SUCCESS) {
//                    //保存最后一次连接
//                    SpHelper.setBluetooth(context, device, type);
//                    //连接设备赋值
//                    CONNECT_MAC_DATA = SpHelper.getBluetooth(context);
//                    //连接成功监听蓝牙连接状态，防止中途意外断开
//                    mBluetoothClient.registerConnectStatusListener(CONNECT_MAC_DATA.getAddress(), mBleConnectStatusListener);
//                    notifyBluetoothData();
//                    if (scanBluetoothListener != null) {
//                        scanBluetoothListener.onDeviceConnectSucceed();
//                    }
//                }
//            }
//        });
//
//    }
//
//    /**
//     * 设备自动连接蓝牙
//     */
//    public void connectAutomaticBluetooth(int type) {
//        if (nullBluetooth()) {
//            if (!stateBluetooth()) {
//                return;
//            }
//            if (CONNECT_MAC_DATA != null) {
//                if (CONNECT_MAC_DATA.getType() != type) {
//                    return;
//                }
//                //如果正在扫描，先停止
//                stopBluetooth();
//                //再去连接蓝牙
//                BleConnectOptions options = new BleConnectOptions.Builder()
////                        .setConnectRetry(3)   // 连接如果失败重试3次
//                        .setConnectTimeout(4000)   // 连接超时4s
////                        .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
////                        .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
//                        .build();
//
//                mBluetoothClient.connect(CONNECT_MAC_DATA.getAddress(), options, new BleConnectResponse() {
//                    @Override
//                    public void onResponse(int code, BleGattProfile data) {
//                        //连接成功
//                        if (code == REQUEST_SUCCESS) {
//                            //连接成功监听蓝牙连接状态，防止中途意外断开
//                            mBluetoothClient.registerConnectStatusListener(CONNECT_MAC_DATA.getAddress(), mBleConnectStatusListener);
//                            notifyBluetoothData();
//                            if (scanBluetoothListener != null) {
//                                scanBluetoothListener.onDeviceConnectSucceed();
//                            }
//                        }
//                    }
//                });
//            }
//        }
//    }
//
//    /**
//     * 设备断开蓝牙
//     */
//    public void breakBluetooth() {
//        if (CONNECT_MAC_DATA != null) {
//            mBluetoothClient.disconnect(CONNECT_MAC_DATA.getAddress());
//        }
//    }
//
//    /**
//     * 连接状态监听
//     */
//    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {
//
//        @Override
//        public void onConnectStatusChanged(String mac, int status) {
//            if (status == STATUS_CONNECTED) {
//                //蓝牙设备处于连接状态
//            } else if (status == STATUS_DISCONNECTED) {
//                //异常断开监听
//                if (scanBluetoothListener != null) {
//                    scanBluetoothListener.onDeviceBreakListener();
//                }
//                //蓝牙设备断开(取消连接状态监听)
//                mBluetoothClient.unregisterConnectStatusListener(mac, mBleConnectStatusListener);
//                //关闭Notify
//                unNotifyBluetoothData();
//            }
//        }
//    };
//
//    /**
//     * 读取数据（主动去接收）
//     */
//    public void readBluetoothData() {
//        if (CONNECT_MAC_DATA==null){
//            return;
//        }
//        mBluetoothClient.read(CONNECT_MAC_DATA.getAddress(), UUID_SERVICE, UUID_NOTIFY, new BleReadResponse() {
//            @Override
//            public void onResponse(int code, byte[] data) {
//                if (code == REQUEST_SUCCESS) {
//                    if (scanBluetoothListener != null) {
//                        scanBluetoothListener.onReadBluetoothDataListener(code, data);
//                    }
//                }
//            }
//        });
//    }
//
//    /**
//     * 写数据（发送）
//     *
//     * @param data
//     */
//    public void writeBluetoothData(String data) {
//        if (CONNECT_MAC_DATA != null) {
//            Log.e("onNotify: ", "发送数据" + data);
//            Log.e("onNotify: ", "发送蓝牙地址" + CONNECT_MAC_DATA.getAddress());
//            Log.e("onNotify: ", "发送蓝牙地址" + UUID_SERVICE);
//            Log.e("onNotify: ", "发送蓝牙地址" + UUID_WRITE);
//            mBluetoothClient.write(CONNECT_MAC_DATA.getAddress(), UUID_SERVICE, UUID_WRITE, ByteUtils.stringToBytes(data), new BleWriteResponse() {
//                @Override
//                public void onResponse(int code) {
//                    if (code == REQUEST_SUCCESS) {
//                        Log.e("onNotify: ", "发送成功了~");
//                    } else {
//                        Log.e("onNotify: ", "发送失败");
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * 写数据（婴儿贴发送）
//     *
//     * @param data
//     */
//    public void writeBluetoothBabyData(String data) {
//        if (CONNECT_MAC_DATA != null) {
//            Log.e("onNotify: ", "发送数据" + data);
//            mBluetoothClient.writeNoRsp(CONNECT_MAC_DATA.getAddress(), UUID_SERVICE, UUID_WRITE, ByteUtils.stringToBytes(data), new BleWriteResponse() {
//                @Override
//                public void onResponse(int code) {
//                    if (code == REQUEST_SUCCESS) {
//                        Log.e("onNotify: ", "发送成功了~");
//                    } else {
//                        Log.e("onNotify: ", "发送失败");
//                    }
//                }
//            });
//        }
//    }
//
//
//    /**
//     * 打开Notify接收通知
//     */
//    public void notifyBluetoothData() {
//        mBluetoothClient.notify(CONNECT_MAC_DATA.getAddress(), UUID_SERVICE, UUID_NOTIFY, new BleNotifyResponse() {
//            @SuppressLint("DefaultLocale")
//            @Override
//            public void onNotify(UUID service, UUID character, byte[] value) {
//                if (scanBluetoothListener != null) {
//                    scanBluetoothListener.onNotifyBluetoothDataListener(service, character, value);
//                }
//            }
//
//            @Override
//            public void onResponse(int code) {
//            }
//        });
//    }
//
//    /**
//     * 关闭Notify
//     */
//    public void unNotifyBluetoothData() {
//        mBluetoothClient.unnotify(CONNECT_MAC_DATA.getAddress(), UUID_SERVICE, UUID_NOTIFY, new BleUnnotifyResponse() {
//            @Override
//            public void onResponse(int code) {
//                if (code == REQUEST_SUCCESS) {
//
//                }
//            }
//        });
//    }
//
//
//    /**
//     * @return 获取数据域长度
//     */
//    public static String getCmdLength(String data) {
//        //从“起始符”到“结束符”全部内容长度之和(2字节)
//        int length = 0;
//        if (!data.isEmpty()) {
//            length = data.length() / 2;
//        }
//        length = length + 10;
//        //转16进制
//        String dataLength = CalculateUtil.transform10_16(length).toUpperCase();
//        if (dataLength.length() == 1) {
//            dataLength = "0" + dataLength;
//        }
//        return "00" + dataLength;
//    }
//
//    /**
//     * 计算校验和（数据长度、功能字、数据块所有字节的十六进制累加和的低字节）
//     *
//     * @param data       数据
//     * @param dataLength 数据长度
//     * @param function   功能字
//     */
//    public static String makeChecksum(String data, String dataLength, String function) {
//        if (data == null || data.equals("")) {
//            data = "";
//        }
//        int total = 0;
//        int len = data.length();
//        int num = 0;
//        while (num < len) {
//            String s = data.substring(num, num + 2);
//            total += Integer.parseInt(s, 16);
//            num = num + 2;
//        }
//        //数据长度、功能字、数据块
//        total = total + Integer.parseInt(dataLength, 16);
//        total = total + Integer.parseInt(function, 16);
//        /**
//         * 用256求余最大是255，即16进制的FF
//         */
//        int mod = total % 256;
//        String hex = Integer.toHexString(mod);
//        len = hex.length();
//// 如果不够校验位的长度，补0,这里用的是两位校验
//        if (len < 2) {
//            hex = "0" + hex;
//        }
//
//        return hex.toUpperCase();
//    }
//
//
//    /**
//     * 计算校验和（数据长度、功能字、数据块所有字节的十六进制累加和的低字节）
//     */
//    public static String makeChecksum(String dataLength, String function) {
//
//        int total = 0;
//        //数据长度、功能字、数据块
//        total += +Integer.parseInt(dataLength, 16) + Integer.parseInt(function, 16);
//        /**
//         * 用256求余最大是255，即16进制的FF
//         */
//        int mod = total % 256;
//        String hex = Integer.toHexString(mod);
//        int len = hex.length();
//// 如果不够校验位的长度，补0,这里用的是两位校验
//        if (len < 2) {
//            hex = "0" + hex;
//        }
//
//        return hex.toUpperCase();
//    }
//
//    /**
//     * 年、月、日、时、分
//     * 00 00 00 00 00 00
//     *
//     * @return 获取当前时间并转换成命令
//     */
//    public static String getNowTimeString() {
//        Calendar cd = Calendar.getInstance();
//        int year = cd.get(Calendar.YEAR) - 2000;
//        int month = cd.get(Calendar.MONTH) + 1;
//        int day = cd.get(Calendar.DATE);
//        int hour = cd.get(Calendar.HOUR_OF_DAY);
//        int min = cd.get(Calendar.MINUTE);
//        String time = CalculateUtil.transform10_16(year) + CalculateUtil.transform10_16(month) +
//                CalculateUtil.transform10_16(day) + CalculateUtil.transform10_16(hour)
//                + CalculateUtil.transform10_16(min);
//        return time.toUpperCase();
//    }
//
//    /**
//     * 年、月、日、时、分
//     * 00 00 00 00
//     *
//     * @return 获取当前时间戳并倒序转换成命令
//     */
//    public static String getNowTimestamp() {
//        String time = CalculateUtil.transform10_16((int) (System.currentTimeMillis() / 1000));
//        String tranTime = time.substring(6, 8) + time.substring(4, 6) + time.substring(2, 4) + time.substring(0, 2);
//        return tranTime.toUpperCase();
//    }
//
//    /**
//     * 年、月、日、时、分
//     * 00 00 00 00
//     *
//     * @return 获取倒序命令并转换成时间戳
//     */
//    public static String getTimestamp(String time) {
//        String timeData = time.substring(6, 8) + time.substring(4, 6) + time.substring(2, 4) + time.substring(0, 2);
//        return String.valueOf(CalculateUtil.transform16_10(timeData));
//    }
//
//    public static String[] stringSpilt(String s, int len) {
//        int spiltNum;//len->想要分割获得的子字符串的长度
//        int i;
//        int cache = len;//存放需要切割的数组长度
//        spiltNum = (s.length()) / len;
//        String[] subs;//创建可分割数量的数组
//        if ((s.length() % len) > 0) {
//            subs = new String[spiltNum + 1];
//        } else {
//            subs = new String[spiltNum];
//        }
////可用一个全局变量保存分割的数组的长度
////System.out.println(subs.length);
////        leng = subs.length;
//        int start = 0;
//        if (spiltNum > 0) {
//            for (i = 0; i < subs.length; i++) {
//                if (i == 0) {
//                    subs[0] = s.substring(start, len);
//                    start = len;
//                } else if (i > 0 && i < subs.length - 1) {
//                    len = len + cache;
//                    subs[i] = s.substring(start, len);
//                    start = len;
//                } else {
//                    subs[i] = s.substring(len, s.length());
//                }
//            }
//        }
//        return subs;
//    }
//
//}

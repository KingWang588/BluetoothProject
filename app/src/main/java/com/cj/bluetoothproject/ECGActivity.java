package com.cj.bluetoothproject;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cj.ecglibrary.ECGBluetoothUtil;
import com.cj.ecglibrary.ECGRealTimeBean;
import com.cj.ecglibrary.ECGReturnBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ECGActivity extends AppCompatActivity {
    @BindView(R.id.is_connect)
    TextView isConnect;
    @BindView(R.id.ecg_text)
    TextView ecgText;
    @BindView(R.id.ecg_text1)
    TextView ecgText1;
    @BindView(R.id.ecg_text2)
    TextView ecgText2;
    @BindView(R.id.ecg_listView_1)
    ListView ecgListView1;
    @BindView(R.id.ecg_progress_bar)
    ProgressBar ecgProgressBar;
    private ECGBluetoothUtil bluetoothUtil;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        ButterKnife.bind(this);
        bluetoothUtil = new ECGBluetoothUtil(this);
        bluetoothUtil.connectAutomaticBluetooth();
        mainAdapter = new MainAdapter(this);
        ecgListView1.setAdapter(mainAdapter);
        bluetoothUtil.setECGBluetoothListener(new ECGBluetoothUtil.OnECGBluetoothListener() {
            @Override
            public void onSearchStarted() {
                mainAdapter.setClearData();
                ecgProgressBar.setVisibility(View.VISIBLE);
                ecgListView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchStopped() {
                ecgProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                mainAdapter.setData(device);
            }

            @Override
            public void onDeviceBreakListener() {
                isConnect.setText("设备已断开");
                Toast.makeText(ECGActivity.this, "设备已断开", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("设备已连接");
                Toast.makeText(ECGActivity.this, "设备已连接", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceValueReturnListener(ECGReturnBean ecgReturnBean) {
                //测量结果
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("测量结果：\n")
                        .append("心跳：")
                        .append(ecgReturnBean.getHeartbeat())
                        .append("\n")
                        .append("测量结果：")
                        .append(ecgReturnBean.getResult())
                        .append("\n")
                        .append("年：")
                        .append(ecgReturnBean.getYear())
                        .append("\n")
                        .append("月：")
                        .append(ecgReturnBean.getMonth())
                        .append("\n")
                        .append("日：")
                        .append(ecgReturnBean.getDay())
                        .append("\n")
                        .append("时：")
                        .append(ecgReturnBean.getHour())
                        .append("\n")
                        .append("分：")
                        .append(ecgReturnBean.getMinute())
                        .append("\n")
                        .append("组别：")
                        .append(ecgReturnBean.getGroup())
                        .append("\n");
                ecgText1.setText(stringBuilder);
            }

            @Override
            public void onDeviceRealTimeListener(ECGRealTimeBean ecgRealTimeBean) {
                //实时数据
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("实时数据：\n");
                for (int i = 0; i < ecgRealTimeBean.getDataList().size(); i++) {
                    stringBuilder.append(i + 1)
                            .append("心跳：")
                            .append(ecgRealTimeBean.getDataList().get(i))
                            .append("\n");
                }
                stringBuilder.append("心跳声：")
                        .append(ecgRealTimeBean.getHeartbeat())
                        .append("\n")
                        .append("心率：")
                        .append(ecgRealTimeBean.getHeartrate())
                        .append("\n");
                ecgText1.setText(stringBuilder);
            }

            @Override
            public void onMemoryUpListener(List<ECGReturnBean> data) {
                //记忆上传
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("记忆上传获取数据：\n");
                for (int i = 0; i < data.size(); i++) {
                    stringBuilder.append(i + 1)
                            .append("心跳声：")
                            .append(data.get(i).getHeartbeat())
                            .append("心跳：")
                            .append(data.get(i).getResult())
                            .append("\n");
                }
                ecgText2.setText(stringBuilder);
            }

            @Override
            public void onErrorReturnListener() {
                //上传三次开始测量，无反应，报异常
            }

            @Override
            public void onErrorRealTimeListener() {
                //三次时间同步无反应，报错误，断开设备连接
                bluetoothUtil.breakBluetooth();
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {

            }

            @Override
            public void onDeviceConnectFailing(int code) {

            }

        });
        ecgListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothUtil.connectBluetooth(mainAdapter.deviceList.get(position));
            }
        });
    }

    @OnClick({R.id.ecg_button_1, R.id.ecg_button_2, R.id.ecg_button_3, R.id.ecg_button_4, R.id.ecg_button_5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ecg_button_1:
                Toast.makeText(this, "蓝牙状态：" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.ecg_button_2:
                bluetoothUtil.openBluetooth();
                break;
            case R.id.ecg_button_3:
                bluetoothUtil.closeBluetooth();
                break;
            case R.id.ecg_button_4:
                bluetoothUtil.scanBluetooth();
                break;
            case R.id.ecg_button_5:
                //下载记忆
                bluetoothUtil.writeBluetoothResult();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothUtil.onDestroy();
    }
}

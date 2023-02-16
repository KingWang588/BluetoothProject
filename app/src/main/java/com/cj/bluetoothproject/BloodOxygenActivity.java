package com.cj.bluetoothproject;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cj.ovulationlibrary.OvulationBluetoothUtil;
import com.hzkj.bw.bloodoxygenslibrary.BORealTimeBean;
import com.hzkj.bw.bloodoxygenslibrary.BloodOxygenBluetoothUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BloodOxygenActivity extends AppCompatActivity {
    @BindView(R.id.is_connect)
    TextView isConnect;
    @BindView(R.id.blood_oxygen_text_1)
    TextView blood_oxygenText1;
    @BindView(R.id.blood_oxygen_text_2)
    TextView blood_oxygenText2;
    @BindView(R.id.blood_oxygen_listView_1)
    ListView blood_oxygenListView1;
    @BindView(R.id.blood_oxygen_progress_bar)
    ProgressBar blood_oxygenProgressBar;
    private BloodOxygenBluetoothUtil bluetoothUtil;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_oxygen);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("name").isEmpty()) {
            bluetoothUtil = new BloodOxygenBluetoothUtil(this);
        } else {
            bluetoothUtil = new BloodOxygenBluetoothUtil(this, getIntent().getStringExtra("name"));
        }
        bluetoothUtil.connectAutomaticBluetooth();
        mainAdapter = new MainAdapter(this);
        blood_oxygenListView1.setAdapter(mainAdapter);
        bluetoothUtil.setBloodBluetoothListener(new BloodOxygenBluetoothUtil.OnBloodBluetoothListener() {
            @Override
            public void onSearchStarted() {
                mainAdapter.setClearData();
                blood_oxygenProgressBar.setVisibility(View.VISIBLE);
                blood_oxygenListView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchStopped() {
                blood_oxygenProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                mainAdapter.setData(device);
            }

            @Override
            public void onDeviceBreakListener() {
                isConnect.setText("设备已断开");
                Toast.makeText(BloodOxygenActivity.this,"设备已断开",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("设备已连接");
                Toast.makeText(BloodOxygenActivity.this,"设备已连接",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceRealTimeReturnListener(BORealTimeBean boRealTimeBean) {
                StringBuilder data = new StringBuilder();
                data.append("时间：")
                        .append(boRealTimeBean.getTime())
                        .append("\n血氧：")
                        .append(boRealTimeBean.getBlood_oxygen())
                        .append("\n脉率：")
                        .append(boRealTimeBean.getPulse_rate())
                        .append("\nPI%：")
                        .append(boRealTimeBean.getPI())
                        .append("\n柱状图：")
                        .append(boRealTimeBean.getHistogram())
                        .append("\n波形：")
                        .append(boRealTimeBean.getWaveform());
                blood_oxygenText1.setText(data);
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {

            }

            @Override
            public void onDeviceConnectFailing(int code) {

            }

//            @Override
//            public void onMeasuringResultListener(BORealTimeBean boRealTimeBean) {
//                StringBuilder data = new StringBuilder();
//                data.append("测量结果：\n时间：")
//                        .append(boRealTimeBean.getTime())
//                        .append("\n血氧：")
//                        .append(boRealTimeBean.getBlood_oxygen())
//                        .append("\n脉率：")
//                        .append(boRealTimeBean.getPulse_rate())
//                        .append("\nPI%：")
//                        .append(boRealTimeBean.getPI())
//                        .append("\n柱状图：")
//                        .append(boRealTimeBean.getHistogram())
//                        .append("\n波形：")
//                        .append(boRealTimeBean.getWaveform());
//                blood_oxygenText2.setText(data);
//            }
        });
        blood_oxygenListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothUtil.connectBluetooth(mainAdapter.deviceList.get(position));
            }
        });
    }

    @OnClick({R.id.blood_oxygen_button_1, R.id.blood_oxygen_button_2, R.id.blood_oxygen_button_3, R.id.blood_oxygen_button_4, R.id.blood_oxygen_button_5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.blood_oxygen_button_1:
                Toast.makeText(this, "蓝牙状态：" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.blood_oxygen_button_2:
                bluetoothUtil.openBluetooth();
                break;
            case R.id.blood_oxygen_button_3:
                bluetoothUtil.closeBluetooth();
                break;
            case R.id.blood_oxygen_button_4:
                bluetoothUtil.scanBluetooth();
                break;
            case R.id.blood_oxygen_button_5:
                bluetoothUtil.writeBluetoothTime();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothUtil.onDestroy();
    }
}

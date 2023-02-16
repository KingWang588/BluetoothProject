package com.cj.bluetoothproject;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.delelong.hemoglobinslibrary.HemoglobinBean;
import com.delelong.hemoglobinslibrary.HemoglobinBluetoothUtil;
import com.delelong.hemoglobinslibrary.HemoglobinDeviceBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HemoglobinActivity extends AppCompatActivity {
    @BindView(R.id.is_connect)
    TextView isConnect;
    @BindView(R.id.blood_glucose_text)
    TextView bloodGlucoseText;
    @BindView(R.id.blood_glucose_text1)
    TextView bloodGlucoseText1;
    @BindView(R.id.blood_glucose_text2)
    TextView bloodGlucoseText2;
    @BindView(R.id.blood_glucose_text3)
    TextView bloodGlucoseText3;
    @BindView(R.id.blood_glucose_text4)
    TextView bloodGlucoseText4;
    @BindView(R.id.blood_glucose_listView_1)
    MyListView bloodGlucoseListView1;
    @BindView(R.id.blood_glucose_progress_bar)
    ProgressBar bloodGlucoseProgressBar;
    private HemoglobinBluetoothUtil bluetoothUtil;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_glucose);
        ButterKnife.bind(this);
        bluetoothUtil = new HemoglobinBluetoothUtil(this);
        bluetoothUtil.connectAutomaticBluetooth();
        mainAdapter = new MainAdapter(this);
        bloodGlucoseListView1.setAdapter(mainAdapter);

        bluetoothUtil.setHemoglobinBluetoothListener(new HemoglobinBluetoothUtil.OnHemoglobinBluetoothListener() {
            @Override
            public void onSearchStarted() {
                mainAdapter.setClearData();
                bloodGlucoseProgressBar.setVisibility(View.VISIBLE);
                bloodGlucoseListView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchStopped() {
                bloodGlucoseProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                mainAdapter.setData(device);
            }

            @Override
            public void onDeviceBreakListener() {
                isConnect.setText("设备已断开");
                Toast.makeText(HemoglobinActivity.this, "设备已断开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("设备已连接");
                Toast.makeText(HemoglobinActivity.this, "设备已连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConcentrationResultListener(HemoglobinBean hemoglobinBean) {
                //收到浓度
                StringBuilder data = new StringBuilder();
                data.append("收到浓度：")
                        .append("\n时间：")
                        .append(hemoglobinBean.getYear())
                        .append("年")
                        .append(hemoglobinBean.getMonth())
                        .append("月")
                        .append(hemoglobinBean.getDay())
                        .append("日\u3000")
                        .append(hemoglobinBean.getHour())
                        .append("时")
                        .append(hemoglobinBean.getMinute())
                        .append("分")
                        .append("\n浓度：")
                        .append(hemoglobinBean.getConcentration())
                        .append(hemoglobinBean.getUnit());
                bloodGlucoseText.setText(data);
            }

            @Override
            public void onTestPaperResultListener() {
                bloodGlucoseText1.setText("插入试纸");

            }

            @Override
            public void onBleedResultListener() {
                bloodGlucoseText1.setText("等待滴血");
            }

            @Override
            public void onDownTimeResultListener(int time) {
                //倒计时
                bloodGlucoseText1.setText("倒计时：" + time);
            }

            @Override
            public void onErTypeResultListener(String er) {
                bloodGlucoseText2.setText("收到的Er" + er);
            }

            @Override
            public void onMemorySynListener(List<HemoglobinBean> beans) {
                //记忆同步
                StringBuilder data = new StringBuilder();
                for (int i = 0; i < beans.size(); i++) {
                    data.append(i + 1)
                            .append("\n时间：")
                            .append(beans.get(i).getYear())
                            .append("年")
                            .append(beans.get(i).getMonth())
                            .append("月")
                            .append(beans.get(i).getDay())
                            .append("日\u3000")
                            .append(beans.get(i).getHour())
                            .append("时")
                            .append(beans.get(i).getMinute())
                            .append("分")
                            .append("\n浓度：")
                            .append(beans.get(i).getConcentration())
                            .append(beans.get(i).getUnit());
                }
                bloodGlucoseText3.setText("收到记忆同步:\n" + data);
            }

            @Override
            public void onDeviceResultListener(HemoglobinDeviceBean hemoglobinDeviceBean) {
                StringBuilder data = new StringBuilder();
                data.append("仪器主要信息：")
                        .append("\n型号：")
                        .append(hemoglobinDeviceBean.getDevice_model())
                        .append("\n程序编码：")
                        .append(hemoglobinDeviceBean.getDevice_procedure())
                        .append("\n版本：")
                        .append(hemoglobinDeviceBean.getDevice_versions());
                bloodGlucoseText4.setText(data);
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {

            }

            @Override
            public void onDeviceConnectFailing(int code) {

            }

        });
        bloodGlucoseListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothUtil.connectBluetooth(mainAdapter.deviceList.get(position));
            }
        });
    }

    @OnClick({R.id.blood_glucose_button_1, R.id.blood_glucose_button_2, R.id.blood_glucose_button_3, R.id.blood_glucose_button_4,
            R.id.blood_glucose_button_5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.blood_glucose_button_1:
                Toast.makeText(this, "蓝牙状态：" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.blood_glucose_button_2:
                bluetoothUtil.openBluetooth();
                break;
            case R.id.blood_glucose_button_3:
                bluetoothUtil.closeBluetooth();
                break;
            case R.id.blood_glucose_button_4:
                bluetoothUtil.scanBluetooth();
                break;
            case R.id.blood_glucose_button_5:
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


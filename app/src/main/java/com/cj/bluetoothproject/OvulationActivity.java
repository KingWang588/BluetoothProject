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

import com.cj.ovulationlibrary.OvulationBean;
import com.cj.ovulationlibrary.OvulationBluetoothUtil;
import com.cj.thermometerlibrary.ThermometerBluetoothUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OvulationActivity extends AppCompatActivity {
    @BindView(R.id.is_connect)
    TextView isConnect;
    @BindView(R.id.ovulation_text)
    TextView ovulationText;
    @BindView(R.id.ovulation_listView_1)
    ListView ovulationListView1;
    @BindView(R.id.ovulation_progress_bar)
    ProgressBar ovulationProgressBar;
    private OvulationBluetoothUtil bluetoothUtil;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ovulation);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("name").isEmpty()) {
            bluetoothUtil = new OvulationBluetoothUtil(this);
        } else {
            bluetoothUtil = new OvulationBluetoothUtil(this, getIntent().getStringExtra("name"));
        }
        bluetoothUtil.connectAutomaticBluetooth();
        mainAdapter = new MainAdapter(this);
        ovulationListView1.setAdapter(mainAdapter);
        bluetoothUtil.setThermometerBluetoothListener(new OvulationBluetoothUtil.OnThermometerBluetoothListener() {
            @Override
            public void onSearchStarted() {
                mainAdapter.setClearData();
                ovulationProgressBar.setVisibility(View.VISIBLE);
                ovulationListView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchStopped() {
                ovulationProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                mainAdapter.setData(device);
            }

            @Override
            public void onDeviceBreakListener() {
                isConnect.setText("设备已断开");
                Toast.makeText(OvulationActivity.this,"设备已断开",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("设备已连接");
                Toast.makeText(OvulationActivity.this,"设备已连接",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceValueReturnListener(OvulationBean ovulationBean) {
                StringBuilder data = new StringBuilder();
                data.append("测量温度：")
                        .append(ovulationBean.getTemperature())
                        .append("\n温度单位：")
                        .append(ovulationBean.getTemperature_unit())
                        .append("\n测量时间：")
                        .append(ovulationBean.getTime())
                        .append("\n测量时间戳：")
                        .append(ovulationBean.getTimestamp())
                        .append("\n测量精度：")
                        .append(ovulationBean.getPrecision())
                        .append("\n");
                ovulationText.setText(data);
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {

            }

            @Override
            public void onDeviceConnectFailing(int code) {

            }
        });
        ovulationListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothUtil.connectBluetooth(mainAdapter.deviceList.get(position));
            }
        });
    }

    @OnClick({R.id.ovulation_button_1, R.id.ovulation_button_2, R.id.ovulation_button_3, R.id.ovulation_button_4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ovulation_button_1:
                Toast.makeText(this, "蓝牙状态：" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.ovulation_button_2:
                bluetoothUtil.openBluetooth();
                break;
            case R.id.ovulation_button_3:
                bluetoothUtil.closeBluetooth();
                break;
            case R.id.ovulation_button_4:
                bluetoothUtil.scanBluetooth();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothUtil.onDestroy();
    }
}

package com.cj.bluetoothproject;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cj.thermometerlibrary.ThermometerBean;
import com.cj.thermometerlibrary.ThermometerBluetoothUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ThermometerActivity extends AppCompatActivity {
    @BindView(R.id.is_connect)
    TextView isConnect;
    @BindView(R.id.thermometer_text)
    TextView thermometerText;
    @BindView(R.id.thermometer_listView_1)
    MyListView thermometerListView1;
    @BindView(R.id.thermometer_progress_bar)
    ProgressBar thermometerProgressBar;
    private ThermometerBluetoothUtil bluetoothUtil;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("name").isEmpty()) {
            bluetoothUtil = new ThermometerBluetoothUtil(this);
        } else {
            bluetoothUtil = new ThermometerBluetoothUtil(this, getIntent().getStringExtra("name"));
        }
        bluetoothUtil.connectAutomaticBluetooth();
        mainAdapter = new MainAdapter(this);
        thermometerListView1.setAdapter(mainAdapter);

        bluetoothUtil.setThermometerBluetoothListener(new ThermometerBluetoothUtil.OnThermometerBluetoothListener() {
            @Override
            public void onSearchStarted() {
                mainAdapter.setClearData();
                thermometerProgressBar.setVisibility(View.VISIBLE);
                thermometerListView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchStopped() {
                thermometerProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                mainAdapter.setData(device);
            }

            @Override
            public void onDeviceBreakListener() {
                isConnect.setText("设备已断开");
                Toast.makeText(ThermometerActivity.this, "设备已断开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("设备已连接");
                Toast.makeText(ThermometerActivity.this, "设备已连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceValueReturnListener(int type, List<ThermometerBean> thermometerBeans) {
                //返回数据
                //1代表温度计(普通温度计只有一组数据)
                //2代表耳温、额温
                StringBuilder data = new StringBuilder();
                for (int i = 0; i < thermometerBeans.size(); i++) {
                    data.append("测量部位：")
                            .append(thermometerBeans.get(i).getType())
                            .append("\n测量温度：")
                            .append(thermometerBeans.get(i).getTemperature())
                            .append("\n温度单位：")
                            .append(thermometerBeans.get(i).getTemperature_unit())
                            .append("\n测量时间：")
                            .append(thermometerBeans.get(i).getTime())
                            .append("\n测量时间戳：")
                            .append(thermometerBeans.get(i).getTimestamp())
                            .append("\n测量精度：")
                            .append(thermometerBeans.get(i).getPrecision())
                            .append("\n");
                }
                thermometerText.setText(data);
            }

            @Override
            public void onGetTimeSynchronization() {
                //收到时间同步命令
                bluetoothUtil.writeBluetoothTime();
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {

            }

            @Override
            public void onDeviceConnectFailing(int code) {

            }
        });
        thermometerListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothUtil.connectBluetooth(mainAdapter.deviceList.get(position));
            }
        });
    }

    @OnClick({R.id.thermometer_button_1, R.id.thermometer_button_2, R.id.thermometer_button_3, R.id.thermometer_button_4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.thermometer_button_1:
                Toast.makeText(this, "蓝牙状态：" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.thermometer_button_2:
                bluetoothUtil.openBluetooth();
                break;
            case R.id.thermometer_button_3:
                bluetoothUtil.closeBluetooth();
                break;
            case R.id.thermometer_button_4:
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

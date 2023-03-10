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

import com.cj.boodpressurelibrary.BloodPressureBean;
import com.cj.boodpressurelibrary.BloodPressureBluetoothUtil;
import com.cj.boodpressurelibrary.BloodPressureTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BloodPressureActivity extends AppCompatActivity {
    @BindView(R.id.is_connect)
    TextView isConnect;
    @BindView(R.id.blood_pressure_text_1)
    TextView blood_pressureText1;
    @BindView(R.id.blood_pressure_text_2)
    TextView blood_pressureText2;
    @BindView(R.id.blood_pressure_listView_1)
    MyListView blood_pressureListView1;
    @BindView(R.id.blood_pressure_progress_bar)
    ProgressBar blood_pressureProgressBar;
    private BloodPressureBluetoothUtil bluetoothUtil;
    private MainAdapter mainAdapter;
    private StringBuilder data = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        ButterKnife.bind(this);
        bluetoothUtil = new BloodPressureBluetoothUtil(this);
        bluetoothUtil.connectAutomaticBluetooth();
        mainAdapter = new MainAdapter(this);
        blood_pressureListView1.setAdapter(mainAdapter);
        bluetoothUtil.setBloodPressureBluetoothListener(new BloodPressureBluetoothUtil.OnBloodPressureBluetoothListener() {
            @Override
            public void onSearchStarted() {
                mainAdapter.setClearData();
                blood_pressureProgressBar.setVisibility(View.VISIBLE);
                blood_pressureListView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchStopped() {
                blood_pressureProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                mainAdapter.setData(device);
            }

            @Override
            public void onDeviceBreakListener() {
                isConnect.setText("???????????????");
                Toast.makeText(BloodPressureActivity.this, "???????????????", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("???????????????");
                Toast.makeText(BloodPressureActivity.this, "???????????????", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceValueReturnListener(BloodPressureBean bloodPressureBean) {
                StringBuilder data = new StringBuilder();
                data.append("???????????????\n")
                        .append("????????????")
                        .append(bloodPressureBean.getSystolicPressure())
                        .append("\n????????????")
                        .append(bloodPressureBean.getDiastolicPressure())
                        .append("\n?????????")
                        .append(bloodPressureBean.getHeartRate())
                        .append("\n?????????")
                        .append(bloodPressureBean.getHeartFibrillation())
                        .append("\n??????")
                        .append(bloodPressureBean.getYear())
                        .append("\n??????")
                        .append(bloodPressureBean.getMonth())
                        .append("\n??????")
                        .append(bloodPressureBean.getDay())
                        .append("\n??????")
                        .append(bloodPressureBean.getHour())
                        .append("\n??????")
                        .append(bloodPressureBean.getMinute())
                        .append("\n?????????")
                        .append(bloodPressureBean.getGroup());
                blood_pressureText1.setText(data);
            }

            @Override
            public void onDeviceMemoryValueReturnListener(BloodPressureBean bloodPressureBean) {
                if (data.length() != 0) {
                    data.append("\n????????????")
                            .append(bloodPressureBean.getSystolicPressure())
                            .append("\n????????????")
                            .append(bloodPressureBean.getDiastolicPressure())
                            .append("\n?????????")
                            .append(bloodPressureBean.getHeartRate())
                            .append("\n?????????")
                            .append(bloodPressureBean.getHeartFibrillation())
                            .append("\n??????")
                            .append(bloodPressureBean.getYear())
                            .append("\n??????")
                            .append(bloodPressureBean.getMonth())
                            .append("\n??????")
                            .append(bloodPressureBean.getDay())
                            .append("\n??????")
                            .append(bloodPressureBean.getHour())
                            .append("\n??????")
                            .append(bloodPressureBean.getMinute())
                            .append("\n?????????")
                            .append(bloodPressureBean.getGroup());
                } else {
                    data = new StringBuilder();
                    data.append("???????????????")
                            .append("\n????????????")
                            .append(bloodPressureBean.getSystolicPressure())
                            .append("\n????????????")
                            .append(bloodPressureBean.getDiastolicPressure())
                            .append("\n?????????")
                            .append(bloodPressureBean.getHeartRate())
                            .append("\n?????????")
                            .append(bloodPressureBean.getHeartFibrillation())
                            .append("\n??????")
                            .append(bloodPressureBean.getYear())
                            .append("\n??????")
                            .append(bloodPressureBean.getMonth())
                            .append("\n??????")
                            .append(bloodPressureBean.getDay())
                            .append("\n??????")
                            .append(bloodPressureBean.getHour())
                            .append("\n??????")
                            .append(bloodPressureBean.getMinute())
                            .append("\n?????????")
                            .append(bloodPressureBean.getGroup());
                }
                blood_pressureText2.setText(data);
            }

            @Override
            public void onErrorReturnListener(int type) {
                switch (type) {
                    case BloodPressureTool.ERROR_REAL_TIME:
                        Toast.makeText(BloodPressureActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                        break;
                    case BloodPressureTool.ERROR_DOWNLOAD_MEMORY:
                        Toast.makeText(BloodPressureActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onReturnDataListener(String type, String string) {
                //type???
                Toast.makeText(BloodPressureActivity.this, "?????????"
                        + type + "\n??????" + string, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {

            }

            @Override
            public void onDeviceConnectFailing(int code) {

            }
        });
        blood_pressureListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothUtil.connectBluetooth(mainAdapter.deviceList.get(position));
            }
        });
    }

    @OnClick({R.id.blood_pressure_button_1, R.id.blood_pressure_button_2, R.id.blood_pressure_button_3, R.id.blood_pressure_button_4, R.id.blood_pressure_button_5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.blood_pressure_button_1:
                Toast.makeText(this, "???????????????" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.blood_pressure_button_2:
                bluetoothUtil.openBluetooth();
                break;
            case R.id.blood_pressure_button_3:
                bluetoothUtil.closeBluetooth();
                break;
            case R.id.blood_pressure_button_4:
                bluetoothUtil.scanBluetooth();
                break;
            case R.id.blood_pressure_button_5:
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

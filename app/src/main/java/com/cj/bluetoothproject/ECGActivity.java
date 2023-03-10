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
                isConnect.setText("???????????????");
                Toast.makeText(ECGActivity.this, "???????????????", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("???????????????");
                Toast.makeText(ECGActivity.this, "???????????????", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceValueReturnListener(ECGReturnBean ecgReturnBean) {
                //????????????
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("???????????????\n")
                        .append("?????????")
                        .append(ecgReturnBean.getHeartbeat())
                        .append("\n")
                        .append("???????????????")
                        .append(ecgReturnBean.getResult())
                        .append("\n")
                        .append("??????")
                        .append(ecgReturnBean.getYear())
                        .append("\n")
                        .append("??????")
                        .append(ecgReturnBean.getMonth())
                        .append("\n")
                        .append("??????")
                        .append(ecgReturnBean.getDay())
                        .append("\n")
                        .append("??????")
                        .append(ecgReturnBean.getHour())
                        .append("\n")
                        .append("??????")
                        .append(ecgReturnBean.getMinute())
                        .append("\n")
                        .append("?????????")
                        .append(ecgReturnBean.getGroup())
                        .append("\n");
                ecgText1.setText(stringBuilder);
            }

            @Override
            public void onDeviceRealTimeListener(ECGRealTimeBean ecgRealTimeBean) {
                //????????????
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("???????????????\n");
                for (int i = 0; i < ecgRealTimeBean.getDataList().size(); i++) {
                    stringBuilder.append(i + 1)
                            .append("?????????")
                            .append(ecgRealTimeBean.getDataList().get(i))
                            .append("\n");
                }
                stringBuilder.append("????????????")
                        .append(ecgRealTimeBean.getHeartbeat())
                        .append("\n")
                        .append("?????????")
                        .append(ecgRealTimeBean.getHeartrate())
                        .append("\n");
                ecgText1.setText(stringBuilder);
            }

            @Override
            public void onMemoryUpListener(List<ECGReturnBean> data) {
                //????????????
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("???????????????????????????\n");
                for (int i = 0; i < data.size(); i++) {
                    stringBuilder.append(i + 1)
                            .append("????????????")
                            .append(data.get(i).getHeartbeat())
                            .append("?????????")
                            .append(data.get(i).getResult())
                            .append("\n");
                }
                ecgText2.setText(stringBuilder);
            }

            @Override
            public void onErrorReturnListener() {
                //????????????????????????????????????????????????
            }

            @Override
            public void onErrorRealTimeListener() {
                //????????????????????????????????????????????????????????????
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
                Toast.makeText(this, "???????????????" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
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
                //????????????
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

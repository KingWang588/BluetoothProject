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

import com.cj.babylibrary.BabyBluetoothUtil;
import com.cj.babylibrary.BabyTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BabyActivity extends AppCompatActivity {
    @BindView(R.id.is_connect)
    TextView isConnect;
    @BindView(R.id.baby_text)
    TextView babyText;
    @BindView(R.id.baby_listView_1)
    ListView babyListView1;
    @BindView(R.id.baby_progress_bar)
    ProgressBar babyProgressBar;
    private BabyBluetoothUtil bluetoothUtil;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby);
        ButterKnife.bind(this);
        bluetoothUtil = new BabyBluetoothUtil(this, "DMT-4750", "DMT-4578");
        bluetoothUtil.connectAutomaticBluetooth();
        mainAdapter = new MainAdapter(this);
        babyListView1.setAdapter(mainAdapter);
        bluetoothUtil.setBabyBluetoothListener(new BabyBluetoothUtil.OnBabyBluetoothListener() {
            @Override
            public void onSearchStarted() {
                mainAdapter.setClearData();
                babyProgressBar.setVisibility(View.VISIBLE);
                babyListView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchStopped() {
                babyProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceSpyListener(BluetoothDevice device, Integer rssi) {
                Log.e("onDeviceSpyListener: ", device.getName() + "=====" + rssi);
                mainAdapter.setData(device);
            }

            @Override
            public void onDeviceBreakListener() {
                isConnect.setText("???????????????");
                Toast.makeText(BabyActivity.this, "???????????????", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceConnectSucceed() {
                isConnect.setText("???????????????");
                Toast.makeText(BabyActivity.this, "???????????????", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceValueReturnListener(float values) {
                babyText.setText("?????????" + values);
            }

            @Override
            public void onDeviceStateReturnListener(int pattern, int state) {
                /**
                 * pattern 1 ??????????????????   2 ?????????????????????   3 ???????????????
                 * state ????????????  1 ??????  2 ??????
                 */
                Toast.makeText(BabyActivity.this, "??????????????????=" + pattern + "=On / Off ??????:" + state, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceOperationReturnListener(String values) {
                switch (values) {
                    case BabyTool.BABY_REAL_TIME_PAS:
                        //??????????????????
                        Toast.makeText(BabyActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                        break;
                    case BabyTool.BABY_REAL_TIME_NOT_PAS:
                        //?????????????????????
                        Toast.makeText(BabyActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                        break;
                    case BabyTool.BABY_QUILT_KICK_PAS:
                        //???????????????
                        Toast.makeText(BabyActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                        break;
                    case BabyTool.BABY_DEAD_LOCK_PAS:
                        //On/Off?????????
                        Toast.makeText(BabyActivity.this, "On/Off?????????", Toast.LENGTH_SHORT).show();
                        break;
                    case BabyTool.BABY_UNLOCK_PAS:
                        //On/Off?????????
                        Toast.makeText(BabyActivity.this, "On/Off?????????", Toast.LENGTH_SHORT).show();
                        break;
                    case BabyTool.BABY_SHORT_OUT_PAS:
                        //Err-???????????????????????????
                        Toast.makeText(BabyActivity.this, "Err-???????????????????????????", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onReadBluetoothRssi(Integer rssi) {
                Toast.makeText(BabyActivity.this, rssi + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectFailing(int code) {

            }

        });
        babyListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothUtil.connectBluetooth(mainAdapter.deviceList.get(position));
            }
        });
//        bluetoothUtil.connectBluetoothRssi(BluetoothDevice device);
    }

    @OnClick({R.id.baby_button_1, R.id.baby_button_2, R.id.baby_button_3, R.id.baby_button_4, R.id.baby_button_5,
            R.id.baby_button_6, R.id.baby_button_7, R.id.baby_button_8, R.id.baby_button_9, R.id.baby_button_10})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.baby_button_1:
                Toast.makeText(this, "???????????????" + bluetoothUtil.stateBluetooth(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.baby_button_2:
                bluetoothUtil.openBluetooth();
                break;
            case R.id.baby_button_3:
                bluetoothUtil.closeBluetooth();
                break;
            case R.id.baby_button_4:
                bluetoothUtil.scanBluetooth();
                break;
            case R.id.baby_button_5:
                bluetoothUtil.writeBluetoothData(BabyTool.BABY_REAL_TIME);
                break;
            case R.id.baby_button_6:
                bluetoothUtil.writeBluetoothData(BabyTool.BABY_REAL_TIME_NOT);
                break;
            case R.id.baby_button_7:
                bluetoothUtil.writeBluetoothData(BabyTool.BABY_QUILT_KICK);
                break;
            case R.id.baby_button_8:
                bluetoothUtil.writeBluetoothData(BabyTool.BABY_DEAD_LOCK);
                break;
            case R.id.baby_button_9:
                bluetoothUtil.writeBluetoothData(BabyTool.BABY_UNLOCK);
                break;
            case R.id.baby_button_10:
                bluetoothUtil.writeBluetoothData(BabyTool.BABY_UPDATE_PATTERN);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothUtil.onDestroy();
    }
}


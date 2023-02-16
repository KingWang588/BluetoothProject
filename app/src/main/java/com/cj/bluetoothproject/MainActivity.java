package com.cj.bluetoothproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.delelong.bloodfatlibrary.BloodFatBluetoothUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.start_edit)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPermission();
        BloodFatBluetoothUtil.dataTransition("270915A4630F6C0E631172");
        BloodFatBluetoothUtil.dataTransition("2c04157fa1016a01970003");
        BloodFatBluetoothUtil.dataTransition("2c03157ea50183019d0004");
        BloodFatBluetoothUtil.dataTransition("2c02157ea1018e019f0004");
    }

    /**
     * 检查权限
     */
    private void initPermission() {
        PermissionManager.getInstance().get(this)
                .requestCodes(1000)
                .request(new PermissionManager.RequestPermissionCallBack() {
                    @Override
                    public void noM() {
                        //检测GPS
                        initGPSLocation();
                    }

                    @Override
                    public void granted() {
                        //检测GPS
                        initGPSLocation();
                    }

                    @Override
                    public void denied() {
                        Toast.makeText(MainActivity.this, "请手动开启相关权限", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 是否开启GPS
     */
    private void initGPSLocation() {
        if (GPSSettingsUtil.checkGPSIsOpen(this)) {
            //开启了进行下一步
        } else {
            GPSSettingsUtil.ShowAlertDialog(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPSSettingsUtil.GPS_REQUEST_CODE) {
            initGPSLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnClick({R.id.start_button_1, R.id.start_button_2, R.id.start_button_3, R.id.start_button_4,
            R.id.start_button_5, R.id.start_button_6, R.id.start_button_7, R.id.start_button_8,
            R.id.start_button_9})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_button_1:
                //温度计(耳温枪/额温枪)
                startActivity(new Intent(this, ThermometerActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_2:
                //排卵助手
                startActivity(new Intent(this, OvulationActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_3:
                //血氧
                startActivity(new Intent(this, BloodOxygenActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_4:
                //婴儿贴
                startActivity(new Intent(this, BabyActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_5:
                //血糖
                startActivity(new Intent(this, BloodGlucoseActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_6:
                //血压
                startActivity(new Intent(this, BloodPressureActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_7:
                //心电
                startActivity(new Intent(this, ECGActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_8:
                //血红蛋白
                startActivity(new Intent(this, HemoglobinActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
            case R.id.start_button_9:
                //血脂
                startActivity(new Intent(this, BloodFatActivity.class)
                        .putExtra("name", editText.getText().toString()));
                break;
        }
    }
}

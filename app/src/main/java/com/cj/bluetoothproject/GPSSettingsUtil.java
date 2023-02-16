package com.cj.bluetoothproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class GPSSettingsUtil {

    public static int GPS_REQUEST_CODE = 10;
    private static AlertDialog alertDialog;

    /**
     * 检测GPS是否打开
     *
     * @return
     */
    public static boolean checkGPSIsOpen(Activity activity) {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    @SuppressLint("ResourceType")
    public static void ShowAlertDialog(Activity context) {
        //没有打开则弹出对话框
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("温馨提示")
                    .setMessage("开启位置信息后方能搜索蓝牙，是否打开权限？")
                    // 拒绝, 退出应用
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    context.startActivityForResult(intent, GPS_REQUEST_CODE);
                                }
                            })

                    .setCancelable(false)
                    .show();
        } else {
            if (!alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(context)
                        .setTitle("温馨提示")
                        .setMessage("开启位置信息后方能搜索蓝牙，是否打开权限？")
                        // 拒绝, 退出应用
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //跳转GPS设置界面
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        context.startActivityForResult(intent, GPS_REQUEST_CODE);
                                    }
                                })

                        .setCancelable(false)
                        .show();
            }
        }
    }

}

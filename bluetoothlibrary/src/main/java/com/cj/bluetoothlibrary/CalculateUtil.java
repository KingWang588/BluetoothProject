package com.cj.bluetoothlibrary;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculateUtil {

    /**
     * 10进制转16进制
     */
    public static String transform10_16(int number) {
        String data = Integer.toHexString(number);
        if (data.length() == 1) {
            data = "0" + data;
        }
        return data;
    }

    /**
     * 16进制转10进制
     */
    public static int transform16_10(String data) {
        return Integer.parseInt(data, 16);
    }

    /**
     * 10进制转2进制
     */
    public static String transform10_2(int number) {
        return Integer.toBinaryString(number);
    }

    /**
     * 2进制转10进制
     */
    public static int transform2_10(String data) {
        return Integer.parseInt(data, 2);
    }

    /**
     * 16进制转2进制
     */
    public static String transform16_2(String data) {
        StringBuilder newdata = new StringBuilder(Integer.toBinaryString(Integer.parseInt(data, 16)));
        while (newdata.length() != 16) {
            newdata.insert(0, "0");
        }
        return String.valueOf(newdata);
    }

    /**
     * 2进制转16进制
     */
    public static String transform2_16(String data) {
        return Integer.toHexString(Integer.parseInt(data, 2));
    }


    /*获取当前时间*/
    public static String getNewData() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd/HH/mm");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}

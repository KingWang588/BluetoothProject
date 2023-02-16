package com.cj.bluetoothlibrary;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java 加解密工具类
 */
public class EncryptUtil {
    private final static String TAG = EncryptUtil.class.getSimpleName();

    private static final String UTF8 = "utf-8";
    // 定义 DES加密算法,可用 DES,DESede,Blowfish
    private static final String DES_ALGORITHM = "DESede";
    // DES加密所用的key，可自定义
    private static final String DES_KEY = "chen_key";

    private static final int BUFFER_SIZE = 1024;

    /**
     * MD5数字签名
     *
     * @param src
     * @return
     */
    public static String md5Digest(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        // 定义数字签名方法, 可用：MD5, SHA-1
        byte[] b = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(src.getBytes(UTF8));
            b = digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byte2HexStr(b).toLowerCase();
    }

    /**
     * SHA-1数字签名
     *
     * @param src
     * @return
     */
    public static String shA1Digest(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        // 定义数字签名方法, 可用：MD5, SHA-1
        byte[] b = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(src.getBytes(UTF8));
            b = digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byte2HexStr(b).toLowerCase();
    }

    /**
     * BASE64 加密
     *
     * @param src
     * @return
     */
    public static String base64Encoder(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        byte[] b = null;
        try {
            byte[] encode = src.getBytes(UTF8);
            // base64 加密
            b = Base64.encode(encode, 0, encode.length, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byte2HexStr(b);
    }

    /**
     * BASE64解密
     *
     * @param dest
     * @return
     * @throws Exception
     */

    public static String base64Decoder(String dest) {
        if (TextUtils.isEmpty(dest)) {
            return "";
        }
        String result = "";
        try {
            byte[] encode = str2ByteArray(dest);
            // base64 解密
            result = new String(Base64.decode(encode, 0, encode.length, Base64.DEFAULT), UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 3DES加密
     *
     * @param src
     * @return
     */
    public static String desEncoder(String src) {
        return desEncoder(src, DES_KEY);
    }

    /**
     * 3DES加密
     *
     * @param src
     * @param key
     * @return
     */
    public static String desEncoder(String src, String key) {
        byte[] b = null;
        try {
            SecretKey secretKey = new SecretKeySpec(build3DesKey(key), DES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            b = cipher.doFinal(src.getBytes(UTF8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byte2HexStr(b);
    }

    /**
     * 3DES解密
     *
     * @param dest
     * @return
     */
    public static String desDecoder(String dest) {
        return desDecoder(dest, DES_KEY);
    }

    /**
     * 3DES解密
     *
     * @param dest
     * @param key
     * @return
     */
    public static String desDecoder(String dest, String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(build3DesKey(key), DES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] b = cipher.doFinal(str2ByteArray(dest));
            return new String(b, UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字节数组转化为大写16进制字符串
     *
     * @param b
     * @return
     */
    private static String byte2HexStr(byte[] b) {
        if (null == b) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 字符串转字节数组
     *
     * @param s
     * @return
     */
    private static byte[] str2ByteArray(String s) {
        int byteArrayLength = s.length() / 2;
        byte[] b = new byte[byteArrayLength];
        for (int i = 0; i < byteArrayLength; i++) {
            byte b0 = (byte) Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16).intValue();
            b[i] = b0;
        }
        return b;
    }

    /**
     * 构造3DES加解密方法key
     *
     * @param keyStr
     * @return
     */
    private static byte[] build3DesKey(String keyStr) {
        byte[] key = new byte[24];
        try {
            byte[] temp = keyStr.getBytes(UTF8);
            if (key.length > temp.length) {
                System.arraycopy(temp, 0, key, 0, temp.length);
            } else {
                System.arraycopy(temp, 0, key, 0, key.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * GZIP 加密
     *
     * @param str
     * @return
     */
    public static String gZipEncoder(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            // gzip压缩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(str.getBytes(UTF8));
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return byte2HexStr(encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * GZIP 解密
     *
     * @param str
     * @return
     */
    public static String gZipDecoder(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            byte[] decode = str2ByteArray(str);
            // gzip 解压缩
            ByteArrayInputStream bais = new ByteArrayInputStream(decode);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = gzip.read(buf, 0, BUFFER_SIZE)) != -1) {
                baos.write(buf, 0, len);
            }
            gzip.close();
            baos.flush();
            decode = baos.toByteArray();
            baos.close();
            return new String(decode, UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

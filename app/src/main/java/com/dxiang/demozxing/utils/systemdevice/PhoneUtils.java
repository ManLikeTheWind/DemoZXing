package com.dxiang.demozxing.utils.systemdevice;

import android.os.Build;

import java.lang.reflect.Field;

/**
 * 作者：dongixang
 * 时间：2018/1/6 12:48
 * 功能：
 * 使用：
 */

public class PhoneUtils {

    public static String getPhoneDetails(){
        return "Product Model: "
                + android.os.Build.MODEL + ","//获取手机型号:HM NOTE 1S,
                + android.os.Build.VERSION.SDK + ","//  SDK 版本号 : 19
                + android.os.Build.VERSION.RELEASE;//获取版本号:4.4.4
    }
    /**
     * 获取手机的硬件信 */
    public static String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

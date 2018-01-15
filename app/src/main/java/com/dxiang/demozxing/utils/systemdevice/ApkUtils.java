package com.dxiang.demozxing.utils.systemdevice;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.dxiang.demozxing.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 作者：dongixang
 * 时间：2018/1/6 12:47
 * 功能：
 * 使用：
 */

public class ApkUtils {
    /**
     * 获取进程号对应的进程名
     * @param pid 进程号 android.os.Process.myPid()
     * @return 进程名
     */
    public  static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 取得版本name，
     *
     * @return
     */
    public static String getVersionName(Context mContext) {
        try {
            PackageManager pm = mContext.getApplicationContext().getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getApplicationContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return mContext.getApplicationContext().getString(R.string.crash_unknow_version);
        }
    }

    /** 取得版本code*/
    public static int getVersionCode(Context mContext) {
        try {
            PackageManager pm = mContext.getApplicationContext().getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getApplicationContext().getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }
}

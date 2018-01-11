package com.dxiang.demozxing.utils.systemdevice;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;

import java.util.List;

/**
 * 作者：dongixang
 * 时间：2018/1/11 11:41
 * 功能：
 * 使用：
 */

public class ActivityManagerUtils {
    /**判断应用是否在后台*/
    public static boolean isBackgroundRunning(Context context) {
        if(context == null) return false; // 防止应用打开时没来得及初始化AppContext
        String processName = context.getPackageName();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        if (activityManager == null) return false;
        // get running application processes
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processList) {
            if (process.processName.startsWith(processName)) {
                boolean isBackground = process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }
}

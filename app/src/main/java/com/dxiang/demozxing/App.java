package com.dxiang.demozxing;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.dxiang.demozxing.utils.systemdevice.ApkUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.Locale;

/**
 * 作者：dongixang
 * 时间：2017/12/21 17:22
 * 功能：
 * 使用：
 */

public class App extends Application {
    public static final String TAG=App.class.getSimpleName();
    public static App app;
    public static String M_CACHE_CODE_RESULT_BITMAP_FILE_PATH=null;
    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
        initData();
        initBugly();
        registerActivityLifeCallbasks();

    }

    /**
     * <br>1.添加两个依赖库，compile 'com.tencent.bugly:crashreport:latest.release'
     * <br>                  compile 'com.tencent.bugly:nativecrashreport:latest.release'
     * <br>     注：    其中latest.release指代最新Bugly SDK版本号，也可以指定明确的版本号，例如2.1.9
     * <br>2.配置清单文件权限：WRITE_EXTERNAL_STORAGE、READ_EXTERNAL_STORAGE、INTERNET、ACCESS_NETWORK_STATE、ACCESS_WIFI_STATE、READ_LOGS；
     * <br>2.在moudle-gradle：android{defaultConfig{ndk{abiFilters 'armeabi'}}}
     * <br>3.在application中初始化App.initBugly（）
     *
     * <br>4.关于bugly的使用：
     *          4.1崩溃的时候，就立即上传到bugly管理里面了；
     *          4.2关于混淆代码的日志查看：需要在项目-->异常上报-->异常A--->符号表里面进行上传你的Mapping文件（app/outputs/mapping/flavors/release/mapping.txt）
     */
    private void initBugly() {
        boolean isStrategy=false;
        Context context = getApplicationContext();
        if (isStrategy){
            // 获取当前包名
            String packageName = context.getPackageName();
            // 获取当前进程名
            String processName = ApkUtils.getProcessName(android.os.Process.myPid());
            // 设置是否为上报进程
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
            strategy.setUploadProcess(processName == null || processName.equals(packageName));
            // 初始化Bugly
            CrashReport.initCrashReport(context, getString(R.string.bugly_appid),BuildConfig.DEBUG, strategy);
            // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下---由于最终以代码为准，所以还是写代码吧；
            // CrashReport.initCrashReport(context, strategy);
            Log.e(TAG, String.format(Locale.getDefault(),
                    "initBugly packageName= %s;processName = %s;BuildConfig.LOG_DEBUG = %s",
                    packageName,processName,BuildConfig.LOG_DEBUG+"") );
        }else {
            CrashReport.initCrashReport(context, getString(R.string.bugly_appid),BuildConfig.DEBUG);
        }
    }

    private void initData() {
        if (getExternalCacheDir().exists()){
            M_CACHE_CODE_RESULT_BITMAP_FILE_PATH =getExternalCacheDir().getAbsolutePath();
        }else {
            M_CACHE_CODE_RESULT_BITMAP_FILE_PATH =getCacheDir().getAbsolutePath();
        }
        M_CACHE_CODE_RESULT_BITMAP_FILE_PATH += File.separator+"scan_code_bitmap_cache.png";
    }

    public static App get(){
        return app;
    }
    /**
     * 返回配置的版本名称:方法一通过BuildConfig 常量进行判断；
     * @return
     */
    public String getVersionNameByConfig() {
        return BuildConfig.FLAVOR;
//     return getResources().getString(R.string.product_flavors);
    }
    private int mFinalCount;
    private void registerActivityLifeCallbasks(){//前后台切换
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityCreated mFinalCount = "+mFinalCount+";activity = "+activity.getClass().getName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityStarted mFinalCount = "+mFinalCount+";activity = "+activity.getClass().getName());
                if (mFinalCount == 1){
                    Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityStarted mFinalCount ==1 说明是从后台到前台");
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityResumed mFinalCount = "+mFinalCount+";activity = "+activity.getClass().getName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityPaused mFinalCount = "+mFinalCount+";activity = "+activity.getClass().getName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台
                Log.i("onActivityStopped", mFinalCount +"");
                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityStopped mFinalCount = "+mFinalCount+";activity = "+activity.getClass().getName());
                if (mFinalCount == 0){
                    Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityStopped mFinalCount ==0，说明是前台到后台");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivitySaveInstanceState mFinalCount = "+mFinalCount+";activity = "+activity.getClass().getName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityDestroyed mFinalCount = "+mFinalCount+";activity = "+activity.getClass().getName());
            }
        });
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.e(TAG,"killApp RCSApplication onTerminate");
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.e(TAG,"killApp RCSApplication onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.e(TAG,"killApp RCSApplication onTrimMemory = "+level);
        super.onTrimMemory(level);
    }
}

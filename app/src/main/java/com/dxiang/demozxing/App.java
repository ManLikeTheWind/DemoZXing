package com.dxiang.demozxing;

import android.app.Application;

import java.io.File;

/**
 * 作者：dongixang
 * 时间：2017/12/21 17:22
 * 功能：
 * 使用：
 */

public class App extends Application {
    public static App app;
    public static String M_CACHE_CODE_RESULT_BITMAP_FILE_PATH=null;
    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
        initData();

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
}

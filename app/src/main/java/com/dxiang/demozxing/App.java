package com.dxiang.demozxing;

import android.app.Application;

/**
 * 作者：dongixang
 * 时间：2017/12/21 17:22
 * 功能：
 * 使用：
 */

public class App extends Application {
    public static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }
    public static App get(){
        return app;
    }
}

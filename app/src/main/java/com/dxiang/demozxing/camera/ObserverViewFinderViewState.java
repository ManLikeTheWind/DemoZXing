package com.dxiang.demozxing.camera;

/**
 * 作者：dongixang
 * 时间：2018/1/5 17:52
 * 功能：
 * 使用：
 */

public enum ObserverViewFinderViewState {
    STATE_PRE(-1,"STATE_PRE"),STATE_RUNNING(0,"STATE_RUNNING"),STATE_FINISH(1,"STATE_FINISH");

   private  int code;
   private  String value;
   ObserverViewFinderViewState(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", value='" + value + '\'' +
                '}';
    }
}

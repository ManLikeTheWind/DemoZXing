package com.dxiang.demozxing.info;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：dongixang
 * 时间：2018/1/11 10:10
 * 功能：
 * 使用：
 */

public class ConfigInfo implements Parcelable,Cloneable{

    /**
     * flavor : base01
     * crashfile : true
     * reloadapp : true
     * logpath : /zxing/logcash/
     */

    private String flavor;
    private boolean crashfile;
    private boolean reloadapp;
    private String logpath;


//================== clone
    @Override
    public  ConfigInfo clone() {
        ConfigInfo info=null;
        try {
            info= (ConfigInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return info;
    }
//=================== parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(flavor);
        dest.writeInt(crashfile?1:-1);
        dest.writeInt(reloadapp?1:-1);
        dest.writeString(logpath);
    }

    public ConfigInfo(Parcel parcel){
        flavor = parcel.readString();
        crashfile = parcel.readInt()==1?true:false;
        reloadapp = parcel.readInt()==1?true:false;
        logpath = parcel.readString();
    }

    public static final Parcelable.Creator<ConfigInfo> CREATOR = new Parcelable.Creator<ConfigInfo>() {
        /**
         * Rebuilds a ConfigInfo previously stored with writeToParcel().
         **/
        public ConfigInfo createFromParcel(Parcel p) {
            ConfigInfo fi = new ConfigInfo(p);
            if (fi == null) {
                throw new RuntimeException("Failed to unparcel ConfigInfo");
            }
            return fi;
        }
        public ConfigInfo[] newArray(int size) {
            return new ConfigInfo[size];
        }
    };
//======================== get-set start
    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public boolean isCrashfile() {
        return crashfile;
    }

    public void setCrashfile(boolean crashfile) {
        this.crashfile = crashfile;
    }

    public boolean isReloadapp() {
        return reloadapp;
    }

    public void setReloadapp(boolean reloadapp) {
        this.reloadapp = reloadapp;
    }

    public String getLogpath() {
        return logpath;
    }

    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }
}

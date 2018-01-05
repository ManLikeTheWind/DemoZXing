package com.dxiang.demozxing.runnable;

import android.graphics.Bitmap;
import android.os.Handler;

import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.utils.CreateCodeBitmapUtils;
import com.dxiang.demozxing.utils.StringUtils;

/**
 * 作者：dongixang
 * 时间：2017/12/21 15:54
 * 功能：
 * 使用：
 */

public class RunnableCreateBarCode implements Runnable {
    private Handler mHandler;
    private String mData;
    private int mWidthPix;
    private int mHeightPix;
    private boolean mIsShowData;
    public <T extends CharSequence>RunnableCreateBarCode(Handler handler, T data,int widthPix,int heightPix,boolean isShowData) {
        this.mHandler = handler;
        this.mData=(data+"").trim();
        this.mWidthPix =widthPix;
        this.mHeightPix =heightPix;
        this.mIsShowData =isShowData;
        if (handler==null){
            throw new IllegalArgumentException("RunnableCreateBarCode.handler 参数不能为空");
        }
    }
    @Override
    public void run() {
        if (StringUtils.isNullorEmpty(mData)){
            mHandler.sendMessage(mHandler.obtainMessage(Constants.ERROR_CODE_GENERATE_DATA_NULL));
            return;
        }
        if (StringUtils.chineseSum(mData)!=0) {
            mHandler.sendMessage(mHandler.obtainMessage(Constants.ERROR_CODE_GENERATE_DATA));
            return;
        }
        if (mData.length()>80){
            mHandler.sendMessage(mHandler.obtainMessage(Constants.ERROR_CODE_GENERATE_DATA_DATA_LENGTH_TOO_LONG));
        }
        Bitmap bitmap = CreateCodeBitmapUtils.creatBarcode(mData, mWidthPix, mHeightPix, mIsShowData);
        int messageWhat = bitmap==null?Constants.GENERATE_CODE_FAILURE:Constants.GENERATE_CODE_SUCCESS;
        mHandler.sendMessage(mHandler.obtainMessage(messageWhat,bitmap));
    }
}

package com.dxiang.demozxing.runnable;

import android.graphics.Bitmap;
import android.os.Handler;

import com.dxiang.demozxing.R;
import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.utils.BitmapUtils;
import com.dxiang.demozxing.utils.ParseCodeBitmapUtils;
import com.dxiang.demozxing.utils.StringUtils;
import com.google.zxing.Result;

/**
 * 作者：dongixang
 * 时间：2017/12/21 15:54
 * 功能：
 * 使用：
 */

public class RunnableSaveImg implements Runnable {
    private Handler mHandler;
    private Bitmap mBitmap;
    private String mFilePath;
    public <T extends CharSequence> RunnableSaveImg(Handler handler, Bitmap bitmapCode,String filePath) {
        this.mHandler = handler;
        this.mBitmap=bitmapCode;
        this.mFilePath=filePath;
        if (handler==null){
            throw new IllegalArgumentException("RunnableCreateBarCode.handler 参数不能为空");
        }
    }
    @Override
    public void run() {
        if (mBitmap==null){
            mHandler.sendMessage(mHandler.obtainMessage(Constants.ERROR_CODE_PARSEGENERATE_IMG_NULL));
            return;
        }
        boolean resultB = BitmapUtils.saveBitmap2FilePath(mFilePath, mBitmap);
        if (!resultB){
            mHandler.sendMessage(mHandler.obtainMessage(Constants.SAVE_BITMAP_FAILE));
        }else {
            mHandler.sendMessage(mHandler.obtainMessage(Constants.SAVE_BITMAP_SUCCESS));
        }
    }
}

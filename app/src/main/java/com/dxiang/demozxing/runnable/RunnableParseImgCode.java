package com.dxiang.demozxing.runnable;

import android.graphics.Bitmap;
import android.os.Handler;

import com.dxiang.demozxing.R;
import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.utils.CreateCodeBitmapUtils;
import com.dxiang.demozxing.utils.ParseCodeBitmapUtils;
import com.dxiang.demozxing.utils.StringUtils;
import com.google.zxing.Result;

/**
 * 作者：dongixang
 * 时间：2017/12/21 15:54
 * 功能：
 * 使用：
 */

public class RunnableParseImgCode implements Runnable {
    private Handler mHandler;
    private Bitmap mBitmap;
    public <T extends CharSequence> RunnableParseImgCode(Handler handler, Bitmap bitmapCode) {
        this.mHandler = handler;
        this.mBitmap=bitmapCode;
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
        Result result = ParseCodeBitmapUtils.parseBitmapImg(mBitmap);
        String resultStr=result.getText();
        if (StringUtils.isNullorEmpty(resultStr)){
            mHandler.sendMessage(mHandler.obtainMessage(Constants.FAILE_CODE_PARSE_IMG));
        }else {
            mHandler.sendMessage(mHandler.obtainMessage(R.id.decode_succeeded,result));
        }
    }
}

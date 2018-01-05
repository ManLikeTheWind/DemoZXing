package com.dxiang.demozxing.runnable;

import android.graphics.Bitmap;
import android.os.Handler;

import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.utils.BitmapUtils;
import com.dxiang.demozxing.utils.CreateCodeBitmapUtils;
import com.dxiang.demozxing.utils.StringUtils;

/**
 * 作者：dongixang
 * 时间：2017/12/21 17:21
 * 功能：生成二维码，有图和没图
 * 使用：
 */

public class RunnableCreateQRCode implements Runnable {
    private Handler mHandler;
    private String mData;
    private int mWidthPix;
    private int mHeightPix;

    private Bitmap mLogoBitmap;
    private boolean mIsRoundLogo;

    /** 生成二维码没有图的*/
    public <T extends CharSequence> RunnableCreateQRCode(Handler handler, T data, int widthPix, int heightPix) {
      this( handler,  data,widthPix, heightPix,null,false);
    }
    public <T extends CharSequence> RunnableCreateQRCode(Handler handler, T data, int widthPix, int heightPix,Bitmap logoBitmap,boolean isRoundLogo) {
        this.mHandler = handler;
        this.mData=data+"";
        this.mWidthPix =widthPix;
        this.mHeightPix =heightPix;
        this.mLogoBitmap =logoBitmap;
        this.mIsRoundLogo =isRoundLogo;
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
//        if (StringUtils.chineseSum(mData)!=0) {
//            mSetResultHandler.sendMessage(mSetResultHandler.obtainMessage(Constants.ERROR_CODE_GENERATE_DATA));
//            return;
//        }
        if (mLogoBitmap !=null&&mIsRoundLogo){
            mLogoBitmap = BitmapUtils.getRoundCornerBitmap(mLogoBitmap, Constants.QRCODE_ROUND_RATE_360);
        }
        Bitmap bitmap=null;
        if (mLogoBitmap==null){
            bitmap = CreateCodeBitmapUtils.createQRImage(mData,mWidthPix,mHeightPix);
        }else {
            bitmap = CreateCodeBitmapUtils.createQRImageLogo(mData,mWidthPix,mHeightPix, mLogoBitmap);
        }
        int messageWhat = bitmap==null?Constants.GENERATE_CODE_FAILURE:Constants.GENERATE_CODE_SUCCESS;
        mHandler.sendMessage(mHandler.obtainMessage(messageWhat,bitmap));
    }
}

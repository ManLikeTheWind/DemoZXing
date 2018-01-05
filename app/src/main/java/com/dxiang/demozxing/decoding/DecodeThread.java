/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dxiang.demozxing.decoding;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.dxiang.demozxing.activity.CaptureActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 */
public final class DecodeThread extends Thread {
  public static  final  String SCAN_CODE_BITMAP ="SCAN_CODE_BITMAP";
  private final CaptureActivity mCaptureActivity;
  /** 设置解码的参数：边距，编码格式，最大宽高等*/
  private final Hashtable<DecodeHintType,Object> mHints;
  private Handler mHandler;
  private final CountDownLatch mHandlerInitLatch;

  public DecodeThread(CaptureActivity mCaptureActivity,
                      Vector<BarcodeFormat> mDecodeFormats,
                      String mCharacterSet,
                      ResultPointCallback resultPointCallback) {
    this.mCaptureActivity = mCaptureActivity;
    this.mHandlerInitLatch=new CountDownLatch(1);
    mHints=new Hashtable<DecodeHintType,Object>(3);
    if (mDecodeFormats==null||mDecodeFormats.isEmpty()){
      mDecodeFormats=new Vector<BarcodeFormat>();
      mDecodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
      mDecodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
      mDecodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
    }
    mHints.put(DecodeHintType.POSSIBLE_FORMATS,mDecodeFormats);
    if (TextUtils.isEmpty(mCharacterSet)){
      mCharacterSet="UTF-8";
    }
    mHints.put(DecodeHintType.CHARACTER_SET,mCharacterSet);
    mHints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK,resultPointCallback);
  }
  public  Handler getHandler(){
    Log.e("Decodethre", "getHandler: ");
    try {
      mHandlerInitLatch.await();
    } catch (InterruptedException e) {
      //continue
    }
    return mHandler;
  }

  @Override
  public void run() {
    Looper.prepare();
    mHandler=new DecodeHandler(mCaptureActivity,mHints);
    mHandlerInitLatch.countDown();
    Looper.loop();
  }
}

/*
 * Copyright (C) 2010 ZXing authors
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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

//import com.example.zxing_update.zxing.camera.PlanarYUVLuminanceSource;
import com.dxiang.demozxing.R;
import com.dxiang.demozxing.activity.CaptureActivity;
import com.dxiang.demozxing.camera.CameraManager;
import com.dxiang.demozxing.camera.PlanarYUVLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;

final class DecodeHandler extends Handler {

  public  static final String TAG = DecodeHandler.class.getSimpleName();
  private final CaptureActivity mCaptureActivity;
  private MultiFormatReader mMultiFormatReader;


  DecodeHandler(CaptureActivity mCaptureActivity, Hashtable<DecodeHintType,Object> mHints) {
    this.mCaptureActivity = mCaptureActivity;
    this.mMultiFormatReader = new MultiFormatReader();
    mMultiFormatReader.setHints(mHints);
  }

  @Override
  public void handleMessage(Message msg) {
    switch (msg.what) {
      case R.id.decode:
        decode((byte[]) msg.obj, msg.arg1, msg.arg2);
        break;
      case R.id.quit:
        Looper.myLooper().quit();
        break;
    }
  }

  private void decode(byte[]data,int width,int height){
    long start= System.currentTimeMillis();
    Result rawResult=null;
    byte[] rotatedData=new byte[data.length];
    for (int y=0;y<height;y++){
      for (int x=0;x<width;x++){
        rotatedData[x*height+height-y-1]=data[x+y*width];
      }
    }
    width=width+height; // Here we are swapping, that's the difference to #11
    height=width-height;
    width=width-height;

    PlanarYUVLuminanceSource source= CameraManager.get().buildLuminanceSource(rotatedData,width,height);
    BinaryBitmap binaryBitmap=new BinaryBitmap(new HybridBinarizer(source));
    try {
      rawResult=mMultiFormatReader.decodeWithState(binaryBitmap);

    } catch (ReaderException e) {
      //continue
    }finally {
      mMultiFormatReader.reset();
    }
    if (rawResult!=null){
      long end=System.currentTimeMillis();
      Log.e(TAG, "decode: time = "+(end-start)+"ms;\n"+rawResult.toString() );
      Message message=Message.obtain(mCaptureActivity.getHandler(),R.id.decode_succeeded,rawResult);
      Bundle bundle=new Bundle();
      bundle.putParcelable(DecodeThread.SCAN_CODE_BITMAP,source.renderCroppedGreyscaleBitmap());
      message.setData(bundle);
      message.sendToTarget();
    }else {
      Message message=Message.obtain(mCaptureActivity.getHandler(),R.id.decode_failed);
      message.sendToTarget();
    }


  }


}

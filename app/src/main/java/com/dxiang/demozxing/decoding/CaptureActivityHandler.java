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


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dxiang.demozxing.R;
import com.dxiang.demozxing.activity.CaptureActivity;
import com.dxiang.demozxing.camera.CameraManager;
import com.dxiang.demozxing.view.ViewfinderResultPointCallback;
import com.google.zxing.BarcodeFormat;

import java.util.Vector;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureActivityHandler extends Handler {
  public  static final  String TAG=CaptureActivityHandler.class.getSimpleName();
  private final CaptureActivity mCaptureActivity;
  private final DecodeThread mDecodeThread;
  private State state;
  private enum State{
    PREVIEW,
    SUCCESS,
    DONE
  }
  
  public CaptureActivityHandler(CaptureActivity mCaptureActivity, Vector<BarcodeFormat>mDecodeFormats,
                                String mCharacterSet){
    this.mCaptureActivity=mCaptureActivity;
    mDecodeThread=new DecodeThread(mCaptureActivity,mDecodeFormats,mCharacterSet,
            new ViewfinderResultPointCallback(mCaptureActivity.getViewfinderView()));
    mDecodeThread.start();state = State.SUCCESS;
    CameraManager.get().startPreview();
    restartPreviewAndDecode();
  }

  private void restartPreviewAndDecode() {
    if (state==State.SUCCESS){
      state=State.PREVIEW;
      CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
      CameraManager.get().requestAutoFocus(this,R.id.auto_focus);
      mCaptureActivity.drawViewfinder();
    }
  }

  public void quitSynchronously(){
    state=State.DONE;
    CameraManager.get().stopPreview();
    Message quit=Message.obtain(mDecodeThread.getHandler(),R.id.quit);
    quit.sendToTarget();
    try {
      mDecodeThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    removeMessages(R.id.decode_succeeded);
    removeMessages(R.id.decode_failed);
  }

  @Override
  public void handleMessage(Message msg) {
    switch (msg.what){
      case R.id.auto_focus:
        Log.e(TAG, "handleMessage: auto_focus");
        // When one auto focus pass finishes, start another. This is the closest thing to
        // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
        if (state==State.PREVIEW){
          CameraManager.get().requestAutoFocus(this,R.id.auto_focus);
        }
        break;
      case R.id.restart_preview:
        Log.e(TAG, "handleMessage: restart_preview");
        restartPreviewAndDecode();
        break;
      case R.id.decode_succeeded:
        Log.e(TAG, "handleMessage: decode_successed ");
//        Bundle bundle=msg.getData();
//        Bitmap barcode= bundle==null?
//                null:
//                (Bitmap) bundle.getParcelable(DecodeThread.SCAN_CODE_BITMAP);//二维码扫描图片；
//        mCaptureActivity.handleMsg((Result)msg.obj,barcode);
        Message messageT=Message.obtain(mCaptureActivity.mSetResultHandler,R.id.decode_succeeded,msg.obj);
        messageT.setData(msg.getData());
        mCaptureActivity.mSetResultHandler.sendMessage(messageT);
        break;
      case R.id.decode_failed:
        Log.e(TAG, "handleMessage: decode_failed ");
        state=State.PREVIEW;
        CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(),R.id.decode);
        break;
      case R.id.return_scan_result://没用到
        Log.e(TAG, "handleMessage: return_scan_result" );
        mCaptureActivity.setResult(Activity.RESULT_OK, (Intent) msg.obj);
        mCaptureActivity.finish();
        break;
      case R.id.launch_product_query://没用到
        Log.e(TAG, "handleMessage: launch_product_query" );
        String url= (String) msg.obj;
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mCaptureActivity.startActivity(intent);
        break;
    }
  }
}

package com.dxiang.demozxing.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.dxiang.demozxing.App;
import com.dxiang.demozxing.R;
import com.dxiang.demozxing.camera.CameraManager;
import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.decoding.CaptureActivityHandler;
import com.dxiang.demozxing.decoding.DecodeThread;
import com.dxiang.demozxing.decoding.InactivityTimer;
import com.dxiang.demozxing.runnable.RunnableParseImgCode;
import com.dxiang.demozxing.runnable.RunnableSaveImg;
import com.dxiang.demozxing.runnable.ThreadPool;
import com.dxiang.demozxing.utils.SystemViewUtils;
import com.dxiang.demozxing.utils.ToastUtils;
import com.dxiang.demozxing.utils.ViewUtilsM;
import com.dxiang.demozxing.utils.systemdevice.AudioUtils;
import com.dxiang.demozxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Vector;

public class CaptureActivity extends AppCompatActivity implements
        View.OnClickListener,
        SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener{
    public static  final  String TAG=CaptureActivity.class.getSimpleName();
    private CaptureActivityHandler mCaptureActivityHandler;
    private boolean mHasSurface;
    private Vector<BarcodeFormat> mDecodeFormats;
    private String mCharacterSet;
    private InactivityTimer mInactivityTimer;
    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private boolean mVibrate;

    private ViewfinderView mViewFinderView;

    private String mResultDataStr=null;
    /** 是否返回扫描出来的Bitmap*/
    private boolean mIsResultBitmap=true;

    private View view_load;

    private int ifOpenLight = 0; // 判断是否开启闪光灯

    public Handler mSetResultHandler =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case Constants.SUCCESS_CODE_PARSE_IMG://扫描成功
                    ViewUtilsM.setViewVisible(view_load,View.GONE);
                    break;
                case Constants.FAILE_CODE_PARSE_IMG://扫描失败
                    ViewUtilsM.setViewVisible(view_load,View.GONE);
                    ToastUtils.showToastCenterShort(R.string.parse_code_error, CaptureActivity.this);
                    break;
                case R.id.decode_succeeded:
                    mInactivityTimer.onActivity();
                    playBeepSoundAndVibrate();
                    Result result= (Result) msg.obj;
                    mResultDataStr = result.getText();
                    if (mResultDataStr.equals("")) {
                       ToastUtils.showToastCenterShort(R.string.parse_code_error,CaptureActivity.this);
                        SystemViewUtils.setResultBackCaptureActivity(CaptureActivity.this,RESULT_CANCELED,null,null);
                    } else {
                        Bundle bundle=msg.getData();
                        Bitmap bitmap=bundle.getParcelable(DecodeThread.SCAN_CODE_BITMAP);
                        if (mIsResultBitmap&&bitmap!=null){
                            ToastUtils.showToastCenterShort(R.string.current_img_saving,CaptureActivity.this);
                            ThreadPool.get().execute(new RunnableSaveImg(mSetResultHandler,bitmap,App.M_CACHE_CODE_RESULT_BITMAP_FILE_PATH));
                        }else {
                            SystemViewUtils.setResultBackCaptureActivity(CaptureActivity.this,RESULT_OK,null,mResultDataStr);
                        }
                    }
                    break;
                case Constants.SAVE_BITMAP_FAILE:
                    ToastUtils.showToastCenterShort(R.string.current_img_saveing_faile,CaptureActivity.this);
                    CaptureActivity.this.finish();
                    break;
                case Constants.SAVE_BITMAP_SUCCESS:
                    ToastUtils.showToastCenterShort(R.string.current_img_saveing_success,CaptureActivity.this);
                    SystemViewUtils.setResultBackCaptureActivity(CaptureActivity.this,RESULT_OK,App.M_CACHE_CODE_RESULT_BITMAP_FILE_PATH,mResultDataStr);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        CameraManager.init(getApplication());
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mViewFinderView= (ViewfinderView) findViewById(R.id.viewfinder_view);
        view_load=findViewById(R.id.view_load);
        ViewUtilsM.setViewVisible(view_load,View.GONE);
        Log.e(TAG, "initView: view_load include == "+view_load );
    }
    private void initData() {
        mHasSurface=false;
        mInactivityTimer=new InactivityTimer(this);
    }

    private void initListener() {
        findViewById(R.id.btn_cancel_scan).setOnClickListener(this);
        findViewById(R.id.btn_openLight).setOnClickListener(this);
        findViewById(R.id.btn_pick_img_ablum).setOnClickListener(this);
    }

    private void initCamera(SurfaceHolder surfaceHolder){
        try{
         CameraManager.get().openDriver(surfaceHolder);
        }catch (Exception e){
            Log.e(TAG, "initCamera: ",e );
        }
        if (mCaptureActivityHandler==null){
            mCaptureActivityHandler=new CaptureActivityHandler(
                    this,mDecodeFormats,mCharacterSet);
        }
    }

    private void initBeepSound(){
        if (mPlayBeep&&mMediaPlayer==null){
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer=new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(this);

            AssetFileDescriptor fileDescriptor=getResources().openRawResourceFd(R.raw.beep);
            try{
                mMediaPlayer.setDataSource(
                        fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                fileDescriptor.close();
                mMediaPlayer.setVolume(Constants.BEEP_VOLUME,Constants.BEEP_VOLUME);
                mMediaPlayer.prepare();
            }catch (Exception e){
                Log.e(TAG, "initBeepSound: ",e );
            }
        }
    }

    private void playBeepSoundAndVibrate(){
        if (mPlayBeep&&mMediaPlayer!=null){
            mMediaPlayer.start();
        }
        if (mVibrate){
            Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(Constants.VIBRATE_DURATION);
        }
    }

    public void drawViewfinder() {
        mViewFinderView.drawViewfinder();
    }
    public ViewfinderView getViewfinderView() {
        return mViewFinderView;
    }
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView= (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder=surfaceView.getHolder();
        if (mHasSurface){
            initCamera(surfaceHolder);
        }else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats=null;
        mCharacterSet=null;
        if (AudioUtils.getAudioRingMode(this)!=AudioManager.RINGER_MODE_NORMAL){
            mPlayBeep=false;
        }
        initBeepSound();
        mVibrate=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCaptureActivityHandler!=null){
            mCaptureActivityHandler.quitSynchronously();
            mCaptureActivityHandler=null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constants.ACTIVITY_REQUEST_CODE_IMG:
                if (resultCode==RESULT_OK){
                    Uri uri=data.getData();
                    SystemViewUtils.gotoCropSystemView(uri,this,Constants.ACTIVITY_REQUEST_CODE_GOTO_CROP_SCANNING);
                }
                break;
            case Constants.ACTIVITY_REQUEST_CODE_GOTO_CROP_SCANNING:
                if (resultCode==RESULT_OK){
                    Bitmap bitmap=data.getParcelableExtra("data");
                    Log.e(TAG, "图片大小：" + bitmap.getByteCount() + "；图片宽:"
                            + bitmap.getWidth() + "；图片高：" + bitmap.getHeight());
                    ThreadPool.get().execute(new RunnableParseImgCode(mSetResultHandler,bitmap));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel_scan:
                CaptureActivity.this.finish();
                break;
            case R.id.btn_openLight:
                ifOpenLight++;
                if (ifOpenLight % 2==0){// 关闭
                    CameraManager.get().closeLight();
                } else{// 打开
                    CameraManager.get().openLight(); // 开闪光灯
                 }
                break;
            case R.id.btn_pick_img_ablum://获取带有二维码的相片进行扫描；
                SystemViewUtils.gotoPickImgFromAblum(CaptureActivity.this,Constants.ACTIVITY_REQUEST_CODE_IMG);
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        if (!mHasSurface){
            mHasSurface=true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: " );

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: " );
        mHasSurface=false;
    }
    public ViewfinderView getmViewFinderView(){
        return mViewFinderView;
    }
    public Handler getHandler(){
        return mCaptureActivityHandler;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.seekTo(0);
    }

}





















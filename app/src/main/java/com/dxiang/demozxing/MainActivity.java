package com.dxiang.demozxing;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.runnable.RunnableCreateBarCode;
import com.dxiang.demozxing.runnable.RunnableCreateQRCode;
import com.dxiang.demozxing.runnable.RunnableSaveImg;
import com.dxiang.demozxing.runnable.ThreadPool;
import com.dxiang.demozxing.utils.DisplayUtils;
import com.dxiang.demozxing.utils.PermissionCheckUtils;
import com.dxiang.demozxing.utils.StringUtils;
import com.dxiang.demozxing.utils.SystemViewUtils;
import com.dxiang.demozxing.utils.ToastUtils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    public static String TAG=MainActivity.class.getSimpleName();
    private EditText et_data;
    private ImageView iv_qr_image;
    private Bitmap  mCodeBitmap = null;
    private String mCodeResultStr = null;
    private SaveBitmapState mSaveBitmapState=SaveBitmapState.NOT_SAVE;
    private enum SaveBitmapState{
        NOT_SAVE(0,"NOT_SAVE"),SAVEING(1,"NOT_SAVE"),SAVE_FAILE(1,"NOT_SAVE"),SAVE_OK(2,"NOT_SAVE");
        private int code;
        private String value;
        SaveBitmapState(int code, String value) {
            this.code = code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
    }

    private SoftReference<Activity> mContext=null;

    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ERROR_CODE_GENERATE_DATA:
                    ToastUtils.showToastCenterShort(R.string.generate_error_code_data,mContext.get());
                    break;
                case Constants.ERROR_CODE_GENERATE_DATA_NULL:
                    ToastUtils.showToastCenterShort(R.string.generate_error_code_data_null,mContext.get());
                    break;
                case Constants.ERROR_CODE_GENERATE_DATA_DATA_LENGTH_TOO_LONG:
                    ToastUtils.showToastCenterShort(R.string.generate_error_code_data_too_long,mContext.get());
                    break;
                case Constants.GENERATE_CODE_SUCCESS:
                     mCodeBitmap=(Bitmap) msg.obj;
                     mSaveBitmapState=SaveBitmapState.NOT_SAVE;
                     iv_qr_image.setImageBitmap(mCodeBitmap);
                    break;
                case Constants.GENERATE_CODE_FAILURE:
                      mSaveBitmapState=SaveBitmapState.NOT_SAVE;
                      iv_qr_image.setImageResource(R.mipmap.ic_launcher_round);
                    ToastUtils.showToastCenterShort(R.string.current_img_saveing_faile,MainActivity.this);
                    break;
                case Constants.SAVE_BITMAP_FAILE:
                    mSaveBitmapState=SaveBitmapState.SAVE_FAILE;
                    ToastUtils.showToastCenterShort(R.string.current_img_saveing_faile,MainActivity.this);
                    break;
                case Constants.SAVE_BITMAP_SUCCESS:
                    mSaveBitmapState=SaveBitmapState.SAVE_OK;
                    ToastUtils.showToastCenterShort(R.string.current_img_saveing_success,MainActivity.this);
                    break;

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=new SoftReference<Activity>(this);
        initView();
        initListener();
        initDate();
    }

    private void initView() {
        et_data = (EditText) findViewById(R.id.et_data);
        iv_qr_image= (ImageView) findViewById(R.id.iv_qr_image);
    }
    private void initListener() {
        findViewById(R.id.bt_bigin_scan).setOnClickListener(this);
        findViewById(R.id.bt_create_nologo_code).setOnClickListener(this);
        findViewById(R.id.bt_create_logo_code).setOnClickListener(this);
        findViewById(R.id.bt_create_bar_code).setOnClickListener(this);
        findViewById(R.id.bt_goto_browser).setOnClickListener(this);
        findViewById(R.id.bt_save_img).setOnClickListener(this);
        findViewById(R.id.bt_share_img).setOnClickListener(this);

    }
    private void initDate() {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_bigin_scan:
                String[]permissions=new String[]{
                        Manifest.permission.CAMERA,
//                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                String[] needRequestPermissonArr = PermissionCheckUtils.checkPermissonSingles(mContext.get(),permissions);
//                        Manifest.permission_group.CAMERA,
//                        Manifest.permission.SYSTEM_ALERT_WINDOW,
//                        Manifest.permission_group.STORAGE);
                Log.e(TAG, "onRequestPermissionsResult:permissions = "+ Arrays.toString(needRequestPermissonArr) );
//                needRequestPermissonArr=permission;
                if (needRequestPermissonArr.length<1){
                    SystemViewUtils.gotoScanCodeForRessult(mContext.get(),Constants.ACTIVITY_REQUEST_CODE_SCANNING_CODE);
                }else {
                    ActivityCompat.requestPermissions(mContext.get(),needRequestPermissonArr,Constants.ACTIVITY_PERMISSION_REQUEST_COMMON);
                }
                break;
            case R.id.bt_create_nologo_code:
                ThreadPool.get().execute(new RunnableCreateQRCode(
                        mHandler,
                        et_data.getText(),
                        DisplayUtils.getDimenPix(mContext.get(),R.dimen.qr_code_img_width),
                        DisplayUtils.getDimenPix(mContext.get(),R.dimen.qr_code_img_height)
                        ));
                break;
            case R.id.bt_create_logo_code:
                if (StringUtils.isNullorEmpty(et_data.getText())){
                    ToastUtils.showToastCenterShort((R.string.data_not_null),mContext.get());
                }
                SystemViewUtils.gotoPickImgFromAblum(this,Constants.ACTIVITY_REQUEST_CODE_IMG);
                break;
            case R.id.bt_create_bar_code:
                ThreadPool.get().execute(
                        new RunnableCreateBarCode(
                                mHandler,
                                et_data.getText(),
                                DisplayUtils.getDimenPix(mContext.get(),R.dimen.bar_code_img_width),
                                DisplayUtils.getDimenPix(mContext.get(),R.dimen.bar_code_img_height),
                                true));
                break;
            case R.id.bt_save_img:
                if (mCodeBitmap!=null
                        &&mSaveBitmapState.getCode()!=SaveBitmapState.SAVEING.getCode()){
                    mSaveBitmapState=SaveBitmapState.SAVEING;
                    ThreadPool.get().execute(
                            new RunnableSaveImg(
                                 mHandler,
                                    mCodeBitmap,
                                 App.M_CACHE_CODE_RESULT_BITMAP_FILE_PATH));
                }else {
                    ToastUtils.showToastCenterShort(R.string.current_img_saving,MainActivity.this);
                }
                break;
            case R.id.bt_goto_browser:
                if (et_data.getText() == null) {
                    return;
                }
                String result = et_data.getText().toString();
                Intent i = new Intent();
                i.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(result));
                startActivity(i);
                break;
            case R.id.bt_share_img:
                if (mSaveBitmapState.getCode()==SaveBitmapState.SAVE_OK.getCode()){
                    File file=new File(App.M_CACHE_CODE_RESULT_BITMAP_FILE_PATH);
                    if (file.exists()){
                        SystemViewUtils.gotoSystemShare(Uri.parse(App.M_CACHE_CODE_RESULT_BITMAP_FILE_PATH),MainActivity.this);
                        return;
                    }
                }else if (mSaveBitmapState.getCode()==SaveBitmapState.NOT_SAVE.getCode()){
                    ToastUtils.showToastCenterShort(R.string.current_img_not_save,MainActivity.this);
                }else if (mSaveBitmapState.getCode()==SaveBitmapState.SAVEING.getCode()){
                    ToastUtils.showToastCenterShort(R.string.current_img_saving,MainActivity.this);
                }else if (mSaveBitmapState.getCode()==SaveBitmapState.SAVE_FAILE.getCode()){
                    ToastUtils.showToastCenterShort(R.string.current_img_saveing_faile,MainActivity.this);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.ACTIVITY_REQUEST_CODE_IMG:
                if (resultCode==RESULT_OK){
                    Uri uri=data.getData();
                    SystemViewUtils.gotoCropSystemView(uri,this,Constants.ACTIVITY_REQUEST_CODE_IMG_CROP_GENERATE_QR);
                }
                break;
            case Constants.ACTIVITY_REQUEST_CODE_IMG_CROP_GENERATE_QR:
                if (resultCode!=RESULT_OK&&data==null){
                    return;
                }
                Bitmap logoBitmap = (Bitmap) data.getParcelableExtra("data");
                ThreadPool.get().execute(new RunnableCreateQRCode(
                        mHandler,
                        et_data.getText(),
                        DisplayUtils.getDimenPix(mContext.get(),R.dimen.qr_code_img_width),
                        DisplayUtils.getDimenPix(mContext.get(),R.dimen.qr_code_img_height),
                        logoBitmap,
                        true
                        ));
                break;
            case Constants.ACTIVITY_REQUEST_CODE_SCANNING_CODE:
                if (resultCode!=RESULT_OK&&data==null){
                    return;
                }
                 String  mCodeResultBitmapPath = data.getExtras().getString(Constants.ACTIVITY_RESULT_DATA_SCAN_CODE_BITMAP_PATH);
                 mCodeBitmap = StringUtils.isNullorEmpty(mCodeResultBitmapPath)?null:BitmapFactory.decodeFile(mCodeResultBitmapPath);
                 mCodeResultStr = data.getExtras().getString(Constants.ACTIVITY_RESULT_DATA_SCAN_CODE_STRING);
                et_data.setText(mCodeResultStr);
                if (mCodeBitmap!=null){
                    iv_qr_image.setImageBitmap(mCodeBitmap);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){//0-g;-1=no
            case Constants.ACTIVITY_PERMISSION_REQUEST_COMMON:
                Log.e(TAG, "onRequestPermissionsResult:requestCode = "+requestCode+"; permissions = "+ Arrays.toString(permissions) +";grantResults = "+Arrays.toString(grantResults) );
                String[] resultFalse = PermissionCheckUtils.checkPermissionResultFalse(permissions, grantResults);
                if (resultFalse.length<1){
                    SystemViewUtils.gotoScanCodeForRessult(mContext.get(),Constants.ACTIVITY_REQUEST_CODE_SCANNING_CODE);
                }else {
                    StringBuffer sb=new StringBuffer();
                    sb.append(" ");
                    for (int i=0;i<resultFalse.length;i++){
                        sb.append(getString(PermissionCheckUtils.permissionManifest2IntRes(resultFalse[i]))+" ");
                    }
                    ToastUtils.showToastCenterShort(getString(R.string.permission_unobtain_x,resultFalse.length,sb.toString()),mContext.get());
                }
                break;
        }





    }
}

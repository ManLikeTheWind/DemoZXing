package com.dxiang.demozxing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.dxiang.demozxing.activity.ScanQRCodeActivity;
import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.runnable.RunnableCreateBarCode;
import com.dxiang.demozxing.runnable.RunnableCreateQRCode;
import com.dxiang.demozxing.runnable.ThreadPool;
import com.dxiang.demozxing.utils.CreateCodeBitmapUtils;
import com.dxiang.demozxing.utils.DisplayUtils;
import com.dxiang.demozxing.utils.StringUtils;
import com.dxiang.demozxing.utils.SystemViewUtils;
import com.dxiang.demozxing.utils.ToastUtils;

import java.lang.ref.SoftReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText et_data;
    private ImageView iv_qr_image;
    private SoftReference<Context> mContext=null;

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
                case Constants.SUCCESS_CODE_GENERATE:
                    if (msg.obj!=null){
                        iv_qr_image.setImageBitmap((Bitmap) msg.obj);
                    }else {
                        iv_qr_image.setImageResource(R.mipmap.ic_launcher_round);
                    }
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
        mContext=new SoftReference<Context>(this);
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
        findViewById(R.id.bt_goto_browser).setOnClickListener(this);
        findViewById(R.id.bt_create_nologo_code).setOnClickListener(this);
        findViewById(R.id.bt_create_logo_code).setOnClickListener(this);
        findViewById(R.id.bt_create_bar_code).setOnClickListener(this);
        findViewById(R.id.bt_save_img).setOnClickListener(this);
    }
    private void initDate() {

    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        Intent intentWrapper=null;
        switch (v.getId()){
            case R.id.bt_bigin_scan:
                intent=new Intent();
                intent.setClass(mContext.get(), ScanQRCodeActivity.class);
                startActivityForResult(intent,Constants.ACTIVITY_REQUEST_CODE_SCANNING_CODE);
//                overridePendingTransition();
                break;
            case R.id.bt_goto_browser:
                if (et_data.getText() == null) {
                    return;
                }
                String result = et_data.getText().toString();
                // Intent intent = new Intent(ZxingFrame.this,CheckResult.class);
                // intent.putExtra("result", result);
                // startActivity(intent);
                Intent i = new Intent();
                i.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(result));
                startActivity(i);
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
                Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.test_addlogo);
                ThreadPool.get().execute(new RunnableCreateQRCode(
                        mHandler,
                        et_data.getText(),
                        DisplayUtils.getDimenPix(mContext.get(),R.dimen.qr_code_img_width),
                        DisplayUtils.getDimenPix(mContext.get(),R.dimen.qr_code_img_height),
                        bitmap,
                        true
                ));
//                intent=new Intent();
//                intent.setAction(Intent.ACTION_PICK);
////                intent.setAction(Intent.ACTION_PICK_ACTIVITY);
//                intent.setType("image/*");
//                intentWrapper=Intent.createChooser(intent,DisplayUtils.getString(mContext.get(),(R.string.chose_logo)));
//                startActivityForResult(intentWrapper,Constants.ACTIVITY_REQUEST_CODE_IMG);

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
                new Thread(){
                    @Override
                    public void run() {
                        Bitmap bitmap= BitmapFactory.decodeResource(mContext.get().getResources(),R.mipmap.test_addlogo);
                        bitmap= CreateCodeBitmapUtils.addLogo(bitmap,bitmap);
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.SUCCESS_CODE_GENERATE,bitmap));
                    }
                }.start();
                break;
            case R.id.bt_share_img:

//                SystemViewUtils.systemShare();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=RESULT_OK){
            return;
        }
        Uri uri=null;
        switch (requestCode){
            case Constants.ACTIVITY_REQUEST_CODE_IMG:
                if (data==null){
                    return;
                }
                uri=data.getData();
                SystemViewUtils.gotoCropSystemView(uri,MainActivity.this, Constants.ACTIVITY_REQUEST_CODE_IMG_CROPE);
                break;
            case Constants.ACTIVITY_REQUEST_CODE_IMG_CROPE:
                if (data==null){
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



                break;
        }
    }

}

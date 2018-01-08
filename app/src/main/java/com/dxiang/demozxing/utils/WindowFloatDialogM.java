package com.dxiang.demozxing.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dxiang.demozxing.R;

/**
 * 作者：dongixang
 * 时间：2018/1/6 17:31
 * 功能：
 * 使用：
 */

public class WindowFloatDialogM {
    private Context mContext;
    private WindowFloatDialogM mWindowFloatDialogM;
    private IOnClickListener mIOnClickListener;
    private MIOnClickListener mViewIOnClickListenerT;
    public WindowFloatDialogM(Context mContext) {
        this.mContext = mContext;
        mWindowFloatDialogM=this;
        mViewIOnClickListenerT = new MIOnClickListener();
    }

    public  void showDialogFloat( IOnClickListener onClickListener) {

//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        WindowManager.LayoutParams params=new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
        //初始化后不首先获得窗口焦点。不妨碍设备上其他部件的点击、触摸事件。
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_float, null);
        TextView dialog_float_tv_content = view.findViewById(R.id.dialog_float_tv_content);

        TextView dialog_float_bt_negitive = view.findViewById(R.id.dialog_float_bt_negitive);
        TextView dialog_float_bt_neutrality = view.findViewById(R.id.dialog_float_bt_neutrality);
        TextView dialog_float_bt_positive = view.findViewById(R.id.dialog_float_bt_positive);
        dialog_float_bt_negitive.setOnClickListener(mViewIOnClickListenerT);
        dialog_float_bt_neutrality.setOnClickListener(mViewIOnClickListenerT);
        dialog_float_bt_positive.setOnClickListener(mViewIOnClickListenerT);

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(view, params);
    }

    public void setmWindowFloatDialogM(IOnClickListener mIOnClickListener) {
        this.mIOnClickListener = mIOnClickListener;
    }

    public interface  IOnClickListener{
        void onClick(WindowFloatDialogM dialog, ClickWhichButton which);
    }

    private class MIOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ClickWhichButton whichButton=ClickWhichButton.BUTTON_NEGATIVE;
            switch (v.getId()){
                case R.id.dialog_float_bt_negitive:
                    break;
                case R.id.dialog_float_bt_neutrality:
                    whichButton=ClickWhichButton.BUTTON_NEUTRALITY;
                    break;
                case R.id.dialog_float_bt_positive:
                    whichButton=ClickWhichButton.BUTTON_POSITIVE;
                    break;
            }
            if (mIOnClickListener !=null){
                mIOnClickListener.onClick(mWindowFloatDialogM,whichButton);
            }
        }
    }

    public enum ClickWhichButton{
        BUTTON_NEGATIVE(-1,"BUTTON_NEGATIVE"),BUTTON_NEUTRALITY(1,"BUTTON_NEUTRALITY"),BUTTON_POSITIVE(1,"BUTTON_POSITIVE");
        private int code;
        private String value;
        ClickWhichButton(int code, String value) {
            this.code = code;
            this.value = value;
        }
    }

}






















package com.dxiang.demozxing.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 作者：dongixang
 * 时间：2017/12/21 17:03
 * 功能：
 * 使用：
 */

public class ToastUtils {

    public static  void showToastCenterShort(int res, Context context){
        Toast toast=Toast.makeText(context,res,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

}

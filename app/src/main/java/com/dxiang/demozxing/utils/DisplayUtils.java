package com.dxiang.demozxing.utils;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.dxiang.demozxing.R;

/**
 * 作者：dongixang
 * 时间：2017/12/21 16:36
 * 功能：
 * 使用：
 */

public class DisplayUtils {

    public static int getDimenPix(Context context,int dimenRes){
       return context.getResources().getDimensionPixelOffset(dimenRes);
    }
    public static String getString(Context context,int strRes){
       return context.getResources().getString(strRes);
    }

    public static int[] getWindowXY(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new int[]{displayMetrics.widthPixels,displayMetrics.heightPixels};
    }

    /**
     * 将 dp 转成px
     *  从dimens.xml 获取的 尺寸都是  px，即使你写的是dp 和sp
     * @param dp
     * @param context
     * @return
     */
    public static int  dp2Pix(int dp,Context context) {

        float scale = context.getResources().getDisplayMetrics().density;

//			 	int widthPx=200*2;
        int pix=(int) (dp*scale + 0.5f);

        return pix;
    }
    /**
     *
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *  从dimens.xml 获取的 尺寸都是  px，即使你写的是dp 和sp
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.e("scale==", scale+"*******");
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *  从dimens.xml 获取的 尺寸都是  px，即使你写的是dp 和sp
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.e("scale==", scale+"*******");
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *  从dimens.xml 获取的 尺寸都是  px，即使你写的是dp 和sp
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        Log.e("scale==", fontScale+"*******");
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *  从dimens.xml 获取的 尺寸都是  px，即使你写的是dp 和sp
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        Log.e("scale==", fontScale+"*******");
        return (int) (spValue * fontScale + 0.5f);
    }
}

package com.dxiang.demozxing.utils;

import android.view.View;

/**
 * 作者：dongixang
 * 时间：2018/1/4 11:00
 * 功能：
 * 使用：
 */

public class ViewUtilsM {
    /**
     * Set the visibility state of this view.
     *
     * @param visibility One of {@link View#VISIBLE}, {@link View#INVISIBLE}, or {@link View#GONE}.
     * @attr ref android.R.styleable#View_visibility
     */
    public static void setViewVisible(View view,int visibility){
        if (view.getVisibility()!=visibility){
            view.setVisibility(visibility);
        }
    }
}

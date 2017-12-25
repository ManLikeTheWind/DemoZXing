package com.dxiang.demozxing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * 作者：dongixang
 * 时间：2017/12/22 9:02
 * 功能：
 * 使用：
 */

public class SystemViewUtils {

    public static void gotoCropSystemView(Uri inputRri,Activity avtivity,int requestCodp) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputRri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 设置剪切框的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150); // 图片输出大小
        intent.putExtra("outputY", 150); // 图片输出大小
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        avtivity.startActivityForResult(intent, requestCodp);
    }

    public static void systemShare(Uri imageFileUri,Activity activity){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageFileUri);
        shareIntent.setType("image/*");
//自定义提示语
        activity.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /** 取得版本号*/
    public static String getVersionName(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

    /** 取得版本号*/
    public static int getVersionCode(Context context) {
        try {
            PackageInfo manager = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return manager.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static String getPhoneDetails(){
        return "Product Model: "
                + android.os.Build.MODEL + ","//获取手机型号:HM NOTE 1S,
                + android.os.Build.VERSION.SDK + ","//  SDK 版本号 : 19
                + android.os.Build.VERSION.RELEASE;//获取版本号:4.4.4
    }

}

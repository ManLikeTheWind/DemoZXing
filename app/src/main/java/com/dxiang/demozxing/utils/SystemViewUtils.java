package com.dxiang.demozxing.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.dxiang.demozxing.App;
import com.dxiang.demozxing.R;
import com.dxiang.demozxing.activity.CaptureActivity;
import com.dxiang.demozxing.constants.Constants;

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

    public static void gotoSystemShare(Uri imageFileUri, Activity activity){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageFileUri);
        shareIntent.setType("image/*");
//自定义提示语
        activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.system_share_to)));
    }

    public static  void  gotoPickImgFromAblum(Activity activity,int requestCode){
        Intent innerIntent=new Intent();
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.KITKAT){
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        }else {
            innerIntent.setAction(Intent.ACTION_PICK);
        }
         innerIntent.setType("image/*");
        Intent wrapperIntent=createChooser(innerIntent, activity.getString(R.string.chose_scan_img),null);
        activity.startActivityForResult(wrapperIntent, requestCode);
    }

    /**
     * 参照{@link Intent#createChooser(Intent, CharSequence,IntentSender)}
     * {@link Context#startActivity(Intent) Context.startActivity()} and
     * related methods.
     */
    public static Intent createChooser(Intent target, CharSequence title, IntentSender sender) {
        Intent intent = new Intent(Intent.ACTION_CHOOSER);
        intent.putExtra(Intent.EXTRA_INTENT, target);
        if (title != null) {
            intent.putExtra(Intent.EXTRA_TITLE, title);
        }
        if (sender != null) {
            intent.putExtra(Intent.EXTRA_CHOSEN_COMPONENT_INTENT_SENDER, sender);
        }
        // Migrate any clip data and flags from target.
        int permFlags = target.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (permFlags != 0) {
            ClipData targetClipData = target.getClipData();
            if (targetClipData == null && target.getData() != null) {
                ClipData.Item item = new ClipData.Item(target.getData());
                String[] mimeTypes;
                if (target.getType() != null) {
                    mimeTypes = new String[] { target.getType() };
                } else {
                    mimeTypes = new String[] { };
                }
                targetClipData = new ClipData(null, mimeTypes, item);
            }
            if (targetClipData != null) {
                intent.setClipData(targetClipData);
                intent.addFlags(permFlags);
            }
        }
        return intent;
    }


    public static  void setResultBackCaptureActivity(Activity activity, int resultCode, String codePath,String codeStr){
        Intent resultIntent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putString(Constants.ACTIVITY_RESULT_DATA_SCAN_CODE_BITMAP_PATH, codePath);////二维码扫描图片；-- bitmap在SetResult的finish 返回不过去；需要临时保存
        bundle.putString(Constants.ACTIVITY_RESULT_DATA_SCAN_CODE_STRING, codeStr);
        resultIntent.putExtras(bundle);
        activity.setResult(resultCode, resultIntent);
        activity.finish();
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

package com.dxiang.demozxing.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.dxiang.demozxing.R;
import com.dxiang.demozxing.activity.CaptureActivity;
import com.dxiang.demozxing.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：dongixang
 * 时间：2017/12/22 9:02
 * 功能：
 * 使用：
 */

public class SystemViewUtils {
    public static final String TAG=SystemViewUtils.class.getSimpleName();

    public static final int GOTO_SHARE_TYPE_DEFAULT=-1;
    public static final int GOTO_SHARE_TYPE_TEXT=0;
    public static final int GOTO_SHARE_TYPE_IMG_SINGLE=1;
    public static final int GOTO_SHARE_TYPE_IMG_MULTIS=2;
    public static final int GOTO_SHARE_TYPE_FILE_SINGLE=3;
    public static final int GOTO_SHARE_TYPE_FILE_MULTIS=4;

    /**
     * @param inputRri
     * @param activity
     * @param requestCodp
     * @param bitmapLocalCachePath 添加此处原由，如果剪裁的时候返回的图片过大，则会返回intent.data为空
     */
    public static Uri gotoCropSystemView(Uri inputRri, Activity activity, int requestCodp,String bitmapLocalCachePath) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputRri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 设置剪切框的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150); // 图片输出大小
        intent.putExtra("outputY", 150); // 图片输出大小
        intent.putExtra("noFaceDetection", true);//脸部识别-不包含
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        Uri imageUri=null;
        File outputImage=new File(bitmapLocalCachePath);
        if (outputImage.exists()) {
           outputImage.delete();
        }
        try {
            outputImage.createNewFile();////创建file文件，用于存储缓存的照片；若不创建则保存失败；
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//将File对象转换为Uri对象，先进行系统版本的判定，Android7.0以后的版本和之前的版本不太一样
           //authorities  必须和清单文件写的一致:不然会报错：
            // java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.res.XmlResourceParser android.content.pm.PackageItemInfo.loadXmlMetaData(android.content.pm.PackageManager, java.lang.Stri-->
            imageUri = FileProvider.getUriForFile(activity, "com.dxiang.demozxing.fileprovider", outputImage);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            //将存储图片的uri读写权限授权给剪裁工具应用
            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        } else {
            imageUri =Uri.fromFile(outputImage);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        intent.putExtra("outputFormat",Bitmap.CompressFormat.PNG.toString());

        activity.startActivityForResult(intent, requestCodp);
        return imageUri;
    }

    /**
     * @param shareType {@link #GOTO_SHARE_TYPE_DEFAULT}、{@link #GOTO_SHARE_TYPE_TEXT}、
     *      {@link #GOTO_SHARE_TYPE_IMG_SINGLE}、{@link #GOTO_SHARE_TYPE_IMG_MULTIS}、
     *      {@link #GOTO_SHARE_TYPE_FILE_SINGLE}、{@link #GOTO_SHARE_TYPE_FILE_MULTIS}、
     * @param imageFileUri 不定长数组
     */
    public static void gotoSystemShare(Activity activity,int shareType,String...imageFileUri){
        Intent shareIntent = new Intent();
        File shareFile ;
        StringBuffer sbNoExist=null;
        ArrayList<Uri> uriList;
        switch (shareType){
            case GOTO_SHARE_TYPE_IMG_SINGLE://单张图片:由文件得到uri
                shareFile = new File(imageFileUri[0]);
                sbNoExist=new StringBuffer();
                if (shareFile!=null&&shareFile.exists()){
                    Uri imageUri = Uri.fromFile(shareFile);
                    Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                }else {
                    sbNoExist.append(imageFileUri[0]+";\n");
                }
                break;
            case GOTO_SHARE_TYPE_IMG_MULTIS: //分享多张图片
                uriList = new ArrayList<Uri>();
                sbNoExist=new StringBuffer();
                for (String abPath:imageFileUri) {
                    shareFile = new File(abPath);
                    if (shareFile!=null&&shareFile.exists()){
                        uriList.add(Uri.fromFile(shareFile));
                    }else {
                        sbNoExist.append(abPath+";\n");
                    }
                }
                if (uriList.size()!=0){
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
                    shareIntent.setType("image/*");
                }
            break;
            case GOTO_SHARE_TYPE_FILE_SINGLE:
                shareFile = new File(imageFileUri[0]);
                sbNoExist=new StringBuffer();
                if (shareFile!=null&&shareFile.exists()){
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
                    shareIntent.setType(FileUtils.getMimeType(shareFile.getAbsolutePath()));//此处可发送多种文件
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }else {
                    sbNoExist.append(imageFileUri[0]+";\n");
                }
                break;
            case GOTO_SHARE_TYPE_FILE_MULTIS:
                uriList = new ArrayList<Uri>();
                sbNoExist=new StringBuffer();
                for (String abPath:imageFileUri) {
                    shareFile = new File(abPath);
                    if (shareFile!=null&&shareFile.exists()){
                        uriList.add(Uri.fromFile(shareFile));
                    }else {
                        sbNoExist.append(abPath+";\n");
                    }
                }
                if (uriList.size()!=0){
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
                    shareIntent.setType("image/*");
//                  shareIntent.setType(getMimeType(shareFile.getAbsolutePath()));//此处可发送多种文件
//                  shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                  shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                break;
            case GOTO_SHARE_TYPE_TEXT://文字
            default:
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, imageFileUri[0]);
                shareIntent.setType("text/plain");
                break;
        }
        if (sbNoExist!=null&&sbNoExist.length()>0){
            ToastUtils.showToastCenterShort(activity.getString(R.string.share_file_no_exist,sbNoExist.toString()),activity);
            if (shareType==GOTO_SHARE_TYPE_IMG_SINGLE
                    ||shareType==GOTO_SHARE_TYPE_FILE_SINGLE
                    ||imageFileUri.length<=1){
                return;
            }
        }
        //设置分享列表的标题，并且每次都显示分享列表--6.x后，需要申请权限
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Intent wrapperIntent=createChooser(shareIntent, activity.getString(R.string.system_share_to),null);
        activity.startActivity(wrapperIntent);

    }

    public static void gotoSystemBrowser(Uri content_url,Activity activity,int browserType){
        try {
            Intent intent;
            switch (browserType){
                case 1://其他浏览器，不建议使用这种，如果app没有安装会报android.content.ActivityNotFoundException指针:也可以通过packagemanager 检查系统有没有这个窗体；
                    intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(content_url);
                    intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                    // uc浏览器"："com.uc.browser", "com.uc.browser.ActivityUpdate“
                    //opera："com.opera.mini.android", "com.opera.mini.android.Browser"
                    //qq浏览器："com.tencent.mtt", "com.tencent.mtt.MainActivity"
                    activity.startActivity(intent);
                    break;
                default://
                    intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(content_url);
                    activity.startActivity(intent);
                    break;
            }
        }catch (Exception e){
            Log.e(TAG, "gotoSystemBrowser: "+e.getMessage() );
            ToastUtils.showToastCenterShort("打不开这个地址-地址没有格式化",activity);
            gotoSystemShare(activity,GOTO_SHARE_TYPE_TEXT,content_url.getPath());
        }

    }

    public static void gotoScanCodeForRessult(@RequiresPermission Activity activity, int requestCode){
        boolean mIsReturnScanSuccessBitmap=true;
        Intent intent=new Intent();
        intent.setClass(activity, CaptureActivity.class);
        intent.putExtra(Constants.ACTIVITY_REQUEST_DATA_SCAN_IS_RETURN_IMG,mIsReturnScanSuccessBitmap);
        activity.startActivityForResult(intent,requestCode);
        //overridePendingTransition();
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


    /**设置好参数后，将当前的Activity进行关掉*/
    public static  void setResultBackCaptureActivityAndFinishThisA(Activity activity, int resultCode, String codePath, String codeStr){
        Intent resultIntent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putString(Constants.ACTIVITY_RESULT_DATA_SCAN_CODE_BITMAP_PATH, codePath);////二维码扫描图片；-- bitmap在SetResult的finish 返回不过去；需要临时保存
        bundle.putString(Constants.ACTIVITY_RESULT_DATA_SCAN_CODE_STRING, codeStr);
        resultIntent.putExtras(bundle);
        activity.setResult(resultCode, resultIntent);
        activity.finish();
    }

    public static <T extends Activity> void  gotoAppMainActivity(Context mContext,Class<T> activityClass){
        Intent intent = new Intent(mContext.getApplicationContext(),activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,restartIntent);
    }

}

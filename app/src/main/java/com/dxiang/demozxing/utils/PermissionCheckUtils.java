package com.dxiang.demozxing.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.dxiang.demozxing.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：dongixang
 * 时间：2018/1/6 15:25
 * 功能：
 * 使用：
 */

public class PermissionCheckUtils {
    /**权限检查
     *  @return 需要检查权限的String【】数组；不能是权限组，
     */
    public static String[] checkPermissonSingles(Context context, String...permissionArr){
        if (permissionArr==null){
            return new String[0];
        }
        List<String> needRequestList=new ArrayList<String>();
        for (int i=0;i<permissionArr.length;i++){
            if (ContextCompat.checkSelfPermission(context,permissionArr[i])!= PackageManager.PERMISSION_GRANTED){
                needRequestList.add(permissionArr[i]);
            }
        }
        String[] needRequestArr = needRequestList.toArray(new String[needRequestList.size()]);
        return needRequestArr;
    }

    public static String[] checkPermissionResultFalse(String[]permissionArr,int[]permissionResult){
        List<String>arrList=new ArrayList<>(permissionArr.length);
        for (int i=0;i<permissionArr.length;i++){
            if (permissionResult[i]!=PackageManager.PERMISSION_GRANTED){
                arrList.add(permissionArr[i]);
            }
        }
        return arrList.toArray(new String[arrList.size()]);
    }
    
    public static int permissionManifest2IntRes(String permissionManifest){
        int resResult=(R.string.permission_int_res_unknow);
        switch (permissionManifest){
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                resResult=(R.string.permission_int_res_storage_write);
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                resResult=(R.string.permission_int_res_storage_read);
                break;
            case Manifest.permission.CAMERA:
                resResult=(R.string.permission_int_res_camera);
                break;
        }
        return resResult;
    }
    
}

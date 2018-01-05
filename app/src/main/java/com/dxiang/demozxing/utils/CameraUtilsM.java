package com.dxiang.demozxing.utils;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import java.util.regex.Pattern;

/**
 * 作者：dongixang
 * 时间：2018/1/5 15:14
 * 功能：
 * 使用：
 */

public class CameraUtilsM {
    public static  final String TAG=CameraUtilsM.class.getSimpleName();
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }


    /** 修改 图像的拉伸和 扭曲的 修复；  而且扫描的效率也提高了；
     *  <br>{@link #findBestPreviewSizeValue(CharSequence, Point)}
     *  和 {@link #getCameraResolution(Camera.Parameters, Point)}*/
    public static Point getTempScreenViewResolutionForCamera(Point screenViewResolution){
//    修改 图像的拉伸和 扭曲的 修复；  而且扫描的效率也提高了；
        Point screenViewResolutionForCameraT = new Point(screenViewResolution.x,screenViewResolution.y);
        // 相机的分辨率总是：大*小，所以要预先处理下；
        // 对应的使用在相机的最佳分辨率获取：本类的方法：getCameraResolution(...).findBestPreviewSizeValue(...)看注释连接
        // preview size is always something like 480*320, other 320*480
        if (screenViewResolution.x < screenViewResolution.y) {//疑问：是否还存在着压缩：如有下浮 菜单栏的：比如魅族手机；
                                                                //分析：肯定不存在压缩，此处是若:x>y，则就不交换了，所以不存在（首先确定xy互换的原因：　相机的分辨率总是大*小）；
            screenViewResolutionForCameraT.x = screenViewResolution.y;
            screenViewResolutionForCameraT.y = screenViewResolution.x;
        }
        return screenViewResolutionForCameraT;
    }

    public static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {
        String previewSizeValueString = parameters.get("preview-size-values");
        // saw this on Xperia
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }
        Point cameraResolution = null;
        if (previewSizeValueString != null) {
            Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }
        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
            cameraResolution = new Point(
                    (screenResolution.x >> 3) << 3,
                    (screenResolution.y >> 3) << 3);
        }
        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }
            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }
            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }
        }
        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    public static Point findBestPointViewValue(Point pointView,Point pointCamera){
        if (pointView.x>pointView.y&&pointCamera.x<pointCamera.y){
            pointCamera.set(pointCamera.y,pointCamera.x);
        }
        if (pointView.x<pointView.y&&pointCamera.x>pointCamera.y){
            pointCamera.set(pointCamera.y,pointCamera.x);
        }
        int solidWidth=pointView.x;//一般View的x大小是固定的；只需要求y的大小即可
        pointView.y= (int) (1.0f*pointCamera.y/pointCamera.x*pointView.x);

        return pointView;
    }


}

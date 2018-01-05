package com.dxiang.demozxing.constants;

/**
 * 作者：dongixang
 * 时间：2017/12/21 16:12
 * 功能：
 * 使用：
 */

public class Constants {
    /** 请求码-扫描本地图片*/
    public static final int ACTIVITY_REQUEST_CODE_IMG=1;
    /** 请求码-剪裁图片--为了生成二维码*/
    public static final int ACTIVITY_REQUEST_CODE_IMG_CROP_GENERATE_QR =ACTIVITY_REQUEST_CODE_IMG+1;
    /** 请求码-扫描二维码请求*/
    public static final int ACTIVITY_REQUEST_CODE_SCANNING_CODE= ACTIVITY_REQUEST_CODE_IMG_CROP_GENERATE_QR +1;
    /** 请求码-剪裁图片--为了扫描二维码*/
    public static final int ACTIVITY_REQUEST_CODE_GOTO_CROP_SCANNING =ACTIVITY_REQUEST_CODE_SCANNING_CODE+1;

    public static  final int ERROR_CODE_GENERATE_DATA =100;
    public static  final int ERROR_CODE_GENERATE_DATA_NULL =ERROR_CODE_GENERATE_DATA+1;
    public static  final int ERROR_CODE_GENERATE_DATA_DATA_LENGTH_TOO_LONG =ERROR_CODE_GENERATE_DATA_NULL+1;
    public static  final int ERROR_CODE_PARSEGENERATE_IMG_NULL= ERROR_CODE_GENERATE_DATA_DATA_LENGTH_TOO_LONG +1;

    public static  final int GENERATE_CODE_SUCCESS =200;
    public static  final int GENERATE_CODE_FAILURE =GENERATE_CODE_SUCCESS +1;
    public static  final int PARSE_IMG_CODE_SUCCESS = GENERATE_CODE_FAILURE +1;
    public static  final int PARSE_IMG_CODE_FAILE = PARSE_IMG_CODE_SUCCESS +1;

    public static final int  SAVE_BITMAP_SUCCESS= PARSE_IMG_CODE_FAILE +1;
    public static final int  SAVE_BITMAP_FAILE=SAVE_BITMAP_SUCCESS+1 ;


    public static final int  QRCODE_ROUND_RATE_360=360;

    public static  final float  QRCODE_LOG_MAX_SCALE=0.2F;
    public static  final float  QRCODE_LOG_MIN_SCALE=0.1F;



    public static final float BEEP_VOLUME=0.1f;

    public static final long VIBRATE_DURATION = 200L;

    public static final String ACTIVITY_RESULT_DATA_SCAN_CODE_BITMAP_PATH= "ACTIVITY_RESULT_DATA_SCAN_CODE_BITMAP_PATH";
    public static final String ACTIVITY_RESULT_DATA_SCAN_CODE_STRING= "SCAN_CODE_STRING";
    public static final String ACTIVITY_REQUEST_DATA_SCAN_IS_RETURN_IMG="ACTIVITY_REQUEST_DATA_SCAN_IS_RETURN_IMG";


}

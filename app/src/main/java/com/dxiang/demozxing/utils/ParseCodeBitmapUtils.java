package com.dxiang.demozxing.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.dxiang.demozxing.decoding.RGBLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;

/**
 * 作者：dongixang
 * 时间：2018/1/4 11:31
 * 功能：
 * 使用：
 */

public class ParseCodeBitmapUtils {

    public static Result parseBitmapImg(Bitmap bitmap){
        if (bitmap ==null){
            throw new NullPointerException("parseBitmapImg bitmap参数不能为空");
        }
        Hashtable<DecodeHintType ,String> hints=new Hashtable<DecodeHintType ,String>();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");//设置二维码内容编码
        RGBLuminanceSource source=new RGBLuminanceSource(bitmap);

        BinaryBitmap binaryBitmap=new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        try {
            //QRCodeReader reader = new QRCodeReader();
            // Result result = reader.decode(bitmap1, hints);// 这句是真理 进行解析二维码图片
            Result decode = multiFormatReader.decode(binaryBitmap, hints);
            return decode;
        }catch (Exception e){
            Log.e("ParseCodeBitmapUtils", "parseBitmapImg: "+e.getMessage());
        }finally {
            if (bitmap!=null){
                bitmap.recycle();
                bitmap=null;
            }
        }
        return null;
    }

}

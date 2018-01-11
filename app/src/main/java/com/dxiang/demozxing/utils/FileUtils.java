package com.dxiang.demozxing.utils;

import android.content.res.AssetManager;
import android.util.Log;

import com.dxiang.demozxing.constants.Constants;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * 作者：dongixang
 * 时间：2018/1/11 10:37
 * 功能：
 * 使用：
 */

public class FileUtils {
    public static final String TAG= FileUtils.class.getSimpleName();
    /**读文件.-assets 里面的文件*/
    public static StringBuffer readFileAsset(AssetManager assertmgr, String fileName) throws IOException {
        if (fileName == null) {
            Log.e(TAG, "Illegal  Argument!");
            throw new IllegalArgumentException("fileName is null.");
        }
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferReader = null;
        try {
            InputStream in = assertmgr.open(fileName);
            InputStreamReader isr = new InputStreamReader(in, Constants.CHARACTER);
            bufferReader = new BufferedReader(isr);
            // 缓冲数组
            char[] b = new char[1024 * 5];
            int len;
            while ((len = bufferReader.read(b)) != -1) {
                stringBuffer.append(new String(b,0,len));
            }
        } finally {
            closeStreamReader(bufferReader);
        }
        return stringBuffer;
    }

    public static<T extends Closeable,Reader> void closeStreamReader(T is){
        try {
            if (is!=null){
                is.close();
                is=null;
            }
        }catch (Exception e){
            Log.e("SystemPropertyUtils", "closeStream: "+e.getMessage());
        }
    }
}

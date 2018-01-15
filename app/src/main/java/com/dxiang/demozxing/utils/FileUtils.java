package com.dxiang.demozxing.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.dxiang.demozxing.R;
import com.dxiang.demozxing.constants.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    /**  由指定的路径和文件名创建文件 */
    public static File createFile(String path, String name) throws IOException {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + "/" + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /** 判断文件是否存在 */
    public static boolean fileExist(String path, String name) {
        File file = new File(path + name);
        if (file.exists() && !file.isDirectory()) {
            return true;
        }
        return false;
    }

    /**
     *
     * @Title: copyDrawableFiles
     * @Description: 拷贝drawable下的图片到指定位置
     * @param name
     * @param pathDir 需要有后缀File.separator
     * @return boolean
     */
    public static boolean copyDrawableFiles(Context c, String name, String pathDir) {
        try {
            int id = R.drawable.class.getField(name).getInt(R.drawable.class);
            Bitmap bitmap = BitmapFactory.decodeResource(c.getResources(), id);
            BitmapUtils.saveBitmap2FilePath(pathDir+name+".png",bitmap);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @Title: getTestData
     * @Description: 该方法用于读取测试数据，例如一串sql,json,xml等
     * @param @param rawid raw目录下对应的文件名称
     * @return String
     */
    public static String getTestDrawData(Context c,int rawid) {
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = c.getResources().openRawResource(rawid);
            reader = new InputStreamReader(is);
            br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            closeStreamReader(br);
           closeStreamReader(reader);
           closeStreamReader(is);
        }
        return sb.toString();
    }

    /**
     * @Title: deleteFile
     * @Description: 删除文件
     * @param path
     * @param name
     * @return boolean
     */
    public static boolean deleteFile(String path, String name) {
        if (fileExist(path, name)) {
            return false;
        }
        File file = new File(path, name);
        file.delete();
        return true;
    }

    /**
     * @Title: copyFile
     * @Description: 拷贝文件
     * @param @param srcPath
     * @param @param srcName
     * @param @param desPath
     * @param @param desName
     * @return boolean
     */
    public static boolean copyFile(String srcPath, String srcName, String desPath, String desName) {
        if (!fileExist(srcPath, srcName)) {
            return false;
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File inFile = new File(srcPath, srcName);
            File outFile = new File(desPath, desName);

            if(!fileExist(desPath, desName)){
                createFile(desPath, desName);
            }

            fis = new FileInputStream(inFile);
            bis = new BufferedInputStream(fis);

            fos = new FileOutputStream(outFile);
            bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[1024 * 8];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           closeStreamReader(bis);
           closeStreamReader(fis);
           closeStreamReader(bos);
           closeStreamReader(fos);
        }
        return false;
    }

    // 根据文件后缀名获得对应的MIME类型。
    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                Log.e(TAG, "getMimeType: mime = "+mime );
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }
}

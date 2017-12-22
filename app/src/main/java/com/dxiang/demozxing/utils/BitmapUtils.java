package com.dxiang.demozxing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.dxiang.demozxing.App;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 作者：dongixang
 * 时间：2017/12/22 10:22
 * 功能：
 * 使用：
 */

public class BitmapUtils {
    public static  void compressBitmap(Bitmap bitmap) throws Exception {
        String cachePath= App.get().getCacheDir().getAbsolutePath()+ File.separator+System.currentTimeMillis()+".jpeg";
        Log.e("CreateCodeBitmapUtils", "此处生成二维码 完毕 *********************cachePath = "+cachePath);
        //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
        boolean b = bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(cachePath));
    }
    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 数据源
     * @param roundRate  圆角角度，360是圆形
     * @return
     */
    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, int roundRate) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_4444);// 获取高像素的图片
        Canvas canvas = new Canvas(output);
         int color = 0xff424242;
         Paint paint = new Paint();
         Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
         RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundRate, roundRate, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 保存生成的二维码图片
     */
    private void saveBitmap(Bitmap bitmap, String bitName, Context context){
        //获取与应用相关联的路径
        String imageFilePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File imageFile = new File(imageFilePath,"/" + bitName);// 通过路径创建保存文件
        Uri imageFileUri = Uri.fromFile(imageFile);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
        }
    }

    public static  Bitmap scaleMatrixBitmap(Bitmap bitmap,float scalex,float scaley){
        // 将logo图片按martix设置的信息缩放
        Matrix m = new Matrix();
        m.setScale(scalex,scaley);
        bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,false);
        return bitmap;
    }
}

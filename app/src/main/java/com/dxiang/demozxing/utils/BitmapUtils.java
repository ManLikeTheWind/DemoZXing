package com.dxiang.demozxing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    /**
     * bitmap 存到指定的 绝对路径中
     *
     * 生成图片的格式，生成图片的质量，都可以在这里修改，包括压缩图片
     * @param strFilePath 绝对路径  : 图片可以不存在
     * 					外部存贮路径 String filePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";
     * 					内部缓存 String dir = FileUtils.getCacheDir(context) + "Image" + File.separator+"test.jpg";
     * @param bitmap
     * @return
     */
    public static boolean saveBitmap2FilePath(String strFilePath, Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("图片地址不能为空", "图片地址不能为空");
            return false;
        }
        File bitmapFile = new File(strFilePath);
        if (bitmapFile.exists()) {
            bitmapFile.delete();
        }
        File dir=bitmapFile.getParentFile();
        System.out.println("dirdir = "+dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(bitmapFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos); // 生成图片的格式，图片质量，要要保存到哪个流当中
            bos.flush();
            bos.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 压缩  制定路径的图片
     * @param srcPath
     * @return
     */
    public static Bitmap compress(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f           这里我写死了尺寸
        float ww = 480f;//这里设置宽度为480f           这里我写死了尺寸
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return bitmap;//压缩好比例大小后再进行质量压缩
        //return bitmap;
    }
}

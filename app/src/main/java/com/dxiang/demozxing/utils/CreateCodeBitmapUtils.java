package com.dxiang.demozxing.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.dxiang.demozxing.constants.Constants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * 作者：dongixang
 * 时间：2017/12/21 16:24
 * 功能：
 * 使用：
 */

public class CreateCodeBitmapUtils {
//===================生成BarCode    start============
    /**
     * 生成条形码
     * @param dataStr 需要生成的内容
     * @param barCodedWidth 生成条形码的宽带
     * @param barCodeHeight 生成条形码的高度
     * @param isShowData  是否在条形码下方显示内容
     * @return
     */
    public static Bitmap creatBarcode( String dataStr,int barCodedWidth, int barCodeHeight, boolean isShowData) {
        Bitmap ruseltBitmap = null;
        try{
            //配置参数
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //容错级别
            hints.put(EncodeHintType.MARGIN, 2); // //设置空白边距的宽度default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new MultiFormatWriter().encode(dataStr,BarcodeFormat.CODE_128, barCodedWidth, barCodeHeight, hints);
            int[] pixels = new int[barCodedWidth * barCodeHeight];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            for (int y = 0; y < barCodeHeight; y++) {//两个for循环是图片横列扫描的结果,遍历每个点的颜色值
                for (int x = 0; x < barCodedWidth; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * barCodedWidth + x] = 0xFF000000;//黑色
                    } else {
                        pixels[y * barCodedWidth + x] =  0xFFFFFFFF;//白色
                    }
//                    pixels[y * barCodedWidth + x]=bitMatrix.get(x, y)?0xFF000000:0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(barCodedWidth, barCodeHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, barCodedWidth, 0, 0, barCodedWidth, barCodeHeight);
            if (isShowData){
                ruseltBitmap = mixtureBitmap(bitmap,dataStr);
            }
        }catch (Exception e){
            Log.e("CreateCodeBitmapUtils","creatBarcode ",e);
        }
        return ruseltBitmap;
    }

//    /**
//     * 将文字 生成 文字图片 生成显示编码的Bitmap
//     *
//     * @param contents
//     * @param width
//     * @param height
//     * @param context
//     * @return
//     */
//    public static Bitmap creatCodeBitmap(String contents, int width,
//                                         int height, Context context) {
//        TextView tv = new TextView(context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        tv.setLayoutParams(layoutParams);
//        tv.setText(contents);
//        // tv.setTextSize(Utils.px2sp(context, height/3));
//        // tv.setText(Html.fromHtml(contents));
//        // tv.setHeight(height);上面布局已经写好了 参数布局
//        tv.setWidth(width);// 但是宽度 还是应该保持和上面图片的宽度一样的
//        tv.setGravity(Gravity.CENTER_HORIZONTAL);
//        tv.setDrawingCacheEnabled(true);
//        tv.setTextColor(Color.BLACK);
//        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
//
//        tv.buildDrawingCache();
//        Bitmap bitmapCode = tv.getDrawingCache();
//        return bitmapCode;
//    }

    /**
     * 将两个Bitmap合并成一个
     *
     * @param bitmap
     * @param data
     * @return
     */
    private static Bitmap mixtureBitmap(Bitmap bitmap, String data) {
        if (bitmap == null || data == null ) {
            return null;
        }
        int margin = (int) (bitmap.getHeight()*1.0f/10+0.5);
        int textHeight= (int) (bitmap.getHeight()*1.0f/4+0.5);
        int textMarginTop=margin;
        Paint paintText=new Paint();
        paintText.setARGB(0xff,0x00,0x00,0x00);
        paintText.setAntiAlias(true);
        paintText.setTextSize(textHeight);

        Paint paintBitmap=new Paint();
        paintBitmap.setAntiAlias(true);

        int canvasWidth=bitmap.getWidth()+2*margin;
        int canvasHeith=bitmap.getHeight()+2*margin+textMarginTop+textHeight;
        float widthText = paintText.measureText(data);
        float startXPaint=0;
        float startYPaint=margin+bitmap.getHeight()+textMarginTop+10;
        if (bitmap.getWidth()<=widthText){
            startXPaint=margin;
        }else {
            startXPaint=margin+(bitmap.getWidth()-widthText)/2;
        }
        /** 图片两端所保留的空白的宽度*/
        Bitmap emptyBitmap=Bitmap.createBitmap(canvasWidth,canvasHeith,Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(emptyBitmap);// 创建 一个画布
        cv.drawBitmap(bitmap, margin, margin, paintBitmap);// 先画第一张图片，起始点：marginW 是空白间隔；画笔
        cv.drawText(data,startXPaint ,startYPaint, paintText);// 画笔的
        cv.save();//Canvas.ALL_SAVE_FLAG
        cv.restore();
        return emptyBitmap;
    }


//===================生成BarCode    end============
//=========================生成QRCode  start============
    /**
     * @param content
     * @param widthPix
     * @param heightPix
     * @return 生成没有log的二维码
     */
public static Bitmap createQRImage(String content, int widthPix, int heightPix) {
    return createQRImageLogo(content,  widthPix,  heightPix,null);
//    Bitmap bitmap=null;
//    try{
//        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
//        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//        BitMatrix matrix = new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE, widthPix, heightPix);
//        int width = matrix.getWidth();
//        int height = matrix.getHeight();
//        int[] pixels = new int[width * height];
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (matrix.get(x, y)) {
//                    pixels[y * width + x] = 0xff000000;
//                } else {
//                    pixels[y * widthPix + x] = 0xffffffff;//白色
//                }
//            }
//        }
//        bitmap = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//    }catch (Exception e){
//        e.printStackTrace();
//    }
//    return bitmap;
}

    /**
     * 生成二维码Bitmap ,保存到指定路径
     * 带有logo的 二维码
     * @param content   内容
     * @param widthPix  二维码 图片宽度
     * @param heightPix 二维码 图片高度
     * @param logoBm    二维码中心的Logo图标（可以为null）
     * @return 生成二维码及保存文件是否成功
     */
    public static Bitmap createQRImageLogo(String content, int widthPix, int heightPix, Bitmap logoBm) {
        try {
            if (StringUtils.isNullorEmpty(content)) {
                Log.e("Illegale Exception",  " 内容 不能为 null 或者 为空格" );
                return null;
            }
            //配置参数
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //容错级别

            hints.put(EncodeHintType.MARGIN, 2); //设置空白边距的宽度default is 4
//          hints.put(EncodeHintType.MAX_SIZE, widthPix);// 设置图片的最大值
//          hints.put(EncodeHintType.MIN_SIZE, heightPix);// 设置图片的最小值

            // 图像数据转换，使用了矩阵转换
    		BitMatrix bitMatrix = new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE, widthPix, widthPix, hints);
//          BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            for (int y = 0; y < height; y++) {//两个for循环是图片横列扫描的结果,遍历每个点的颜色值
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;//黑色
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;//白色
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);  //此处生成二维码 完毕
            Log.e("CreateCodeBitmapUtils", "此处生成二维码 完毕 *********************cachePath = ");
            //下面就是讲Logo 添加到 二维码中
            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }
            return bitmap;
        } catch (Exception e) {
            Log.e("CreateCodeBitmapUtils", " Exception create_Logo_QRImage",e);
        }
        return null;
    }
    /** 在二维码中间添加Logo图案*/
    public  static Bitmap addLogo(Bitmap qrcodeBitmap, Bitmap logoBitmap) {
        if (logoBitmap == null||qrcodeBitmap == null) {
            return qrcodeBitmap;
        }
        //获取图片的宽高
        int srcWidth = qrcodeBitmap.getWidth();
        int srcHeight = qrcodeBitmap.getHeight();
        int logoWidth = logoBitmap.getWidth();
        int logoHeight = logoBitmap.getHeight();
        if (srcWidth == 0 || srcHeight == 0
            ||logoWidth == 0 || logoHeight == 0) {
            return null;
        }
        //logo大小为二维码整体大小的1/5 防止容错
        float scaleFactorWidth = srcWidth * Constants.QRCODE_LOG_MAX_SCALE / logoWidth ;
        float scaleFactorHeight = srcHeight * Constants.QRCODE_LOG_MAX_SCALE / logoHeight;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(qrcodeBitmap, 0, 0, null);

            canvas.scale(scaleFactorWidth, scaleFactorHeight, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logoBitmap, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
          canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    /**
     * 从指定路径获取  bitmap
     * @param strPath  全称路径=str+文件名.jpg；若不知道，可以参照getFileAllPath() 写法
     * @param inSampleSize 压缩比例    intSample<=1  则图片将不做任何处理
     * @return  不做处理的 bitmap
     */
    public static Bitmap getBitmapFromPath(String strPath,int inSampleSize) {
        if ( inSampleSize<=1) {
            inSampleSize=1;
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=inSampleSize;
        return BitmapFactory.decodeFile(strPath,options);
    }


//    方式二：通过像素点进行融合:http://blog.csdn.net/dgs960825/article/details/51200863
    /**黑点颜色*/
    private static final int BLACK = 0xFF000000;
    /**白色*/
    private static final int WHITE = 0xFFFFFFFF;
    /**正方形二维码宽度 */
    private static final int CODE_WIDTH = 440;
    /**LOGO宽度值,最大不能大于二维码20%宽度值,大于可能会导致二维码信息失效 */
    private static final int LOGO_WIDTH_MAX = CODE_WIDTH / 5;
    /**LOGO宽度值,最小不能小于二维码10%宽度值,小于影响Logo与二维码的整体搭配*/
    private static final int LOGO_WIDTH_MIN = CODE_WIDTH / 10;
    /**生成带LOGO的二维码*/
    public Bitmap createCode(String content, Bitmap logoBitmap)
            throws WriterException {
        int logoWidth = logoBitmap.getWidth();
        int logoHeight = logoBitmap.getHeight();
        int logoHaleWidth = logoWidth >= CODE_WIDTH ? LOGO_WIDTH_MIN
                : LOGO_WIDTH_MAX;
        int logoHaleHeight = logoHeight >= CODE_WIDTH ? LOGO_WIDTH_MIN
                : LOGO_WIDTH_MAX;
        // 将logo图片按martix设置的信息缩放
        Matrix m = new Matrix();
        /*
         * 给的源码是,由于CSDN上传的资源不能改动，这里注意改一下
         * float sx = (float) 2*logoHaleWidth / logoWidth;
         * float sy = (float) 2*logoHaleHeight / logoHeight;
         */
        float sx = (float) logoHaleWidth / logoWidth;
        float sy = (float) logoHaleHeight / logoHeight;
        m.setScale(sx, sy);// 设置缩放信息
        Bitmap newLogoBitmap = Bitmap.createBitmap(logoBitmap, 0, 0, logoWidth,
                logoHeight, m, false);
        int newLogoWidth = newLogoBitmap.getWidth();
        int newLogoHeight = newLogoBitmap.getHeight();
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//设置容错级别,H为最高
        hints.put(EncodeHintType.MAX_SIZE, LOGO_WIDTH_MAX);// 设置图片的最大值
        hints.put(EncodeHintType.MIN_SIZE, LOGO_WIDTH_MIN);// 设置图片的最小值
        hints.put(EncodeHintType.MARGIN, 2);//设置白色边距值
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_WIDTH, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int halfW = width / 2;
        int halfH = height / 2;
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            /*
                 * 取值范围,可以画图理解下
                 * halfW + newLogoWidth / 2 - (halfW - newLogoWidth / 2) = newLogoWidth
                 * halfH + newLogoHeight / 2 - (halfH - newLogoHeight) = newLogoHeight
                 */
                if (x > halfW - newLogoWidth / 2&& x < halfW + newLogoWidth / 2
                        && y > halfH - newLogoHeight / 2 && y < halfH + newLogoHeight / 2) {// 该位置用于存放图片信息
                    /*
                     *  记录图片每个像素信息
                     *  halfW - newLogoWidth / 2 < x < halfW + newLogoWidth / 2
                     *  --> 0 < x - halfW + newLogoWidth / 2 < newLogoWidth
                     *   halfH - newLogoHeight / 2  < y < halfH + newLogoHeight / 2
                     *   -->0 < y - halfH + newLogoHeight / 2 < newLogoHeight
                     *   刚好取值newLogoBitmap。getPixel(0-newLogoWidth,0-newLogoHeight);
                     */
                    pixels[y * width + x] = newLogoBitmap.getPixel(
                            x - halfW + newLogoWidth / 2, y - halfH + newLogoHeight / 2);
                } else {
                    pixels[y * width + x] = matrix.get(x, y) ? BLACK: WHITE;// 设置信息
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
//=========================生成QRCode  end============
}

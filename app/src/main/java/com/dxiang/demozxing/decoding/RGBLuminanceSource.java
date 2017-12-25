package com.dxiang.demozxing.decoding;

/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.zxing_update.utils.CreateQRCode_Utils;
import com.google.zxing.LuminanceSource;

/**
 * This class is used to help decode images from files which arrive as RGB data
 * from Android bitmaps. It does not support cropping or rotation.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class RGBLuminanceSource extends LuminanceSource {
	private final byte[] luminances;

	public RGBLuminanceSource(String path) throws FileNotFoundException {
		this(loadBitmap(path));
	}

	public RGBLuminanceSource(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight());
		//得到图片的宽高  
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		//得到图片的像素
		int[] pixels = new int[width * height];
		//
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		// In order to measure pure decoding speed, we convert the entire image
		//为了测量纯解码速度，我们将整个图像灰度阵列前面，这是一样的通道
		// to a greyscale array
		// up front, which is the same as the Y channel of the
		// YUVLuminanceSource in the real app.
		
		//得到像素大小的字节数
		luminances = new byte[width * height];
		//得到图片每点像素颜色
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				int pixel = pixels[offset + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				//当某一点三种颜色值相同时，相应字节对应空间赋值 为其赋值
				if (r == g && g == b) {
					// Image is already greyscale, so pick any channel.
					luminances[offset + x] = (byte) r;
				} else {
					 //其它情况字节空间对应赋值为：
					// Calculate luminance cheaply, favoring green.
					luminances[offset + x] = (byte) ((r + g + g + b) >> 2);
//					luminances[offset + x] = (byte) ((0.299*r + 0.578*g + 0.114*b));
				}
			}
		}
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException(
					"Requested row is outside the image: " + y);
		}
		int width = getWidth();
		if (row == null || row.length < width) {
			row = new byte[width];
		}
		System.arraycopy(luminances, y * width, row, 0, width);
		return row;
	}

	// Since this class does not support cropping, the underlying byte array
	// already contains
	// exactly what the caller is asking for, so give it to them without a copy.
	@Override
	public byte[] getMatrix() {
		return luminances;
	}

	private static Bitmap loadBitmap(String path) throws FileNotFoundException {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if (bitmap == null) {
			throw new FileNotFoundException("Couldn't open " + path);
		}
		return bitmap;
	}
	
	 /**扫描选择相册图片,判断是否压缩的临界值*/
    public static final Long SCAN_DEFAULT_SIZE=(long)4000000; 
    /**
     * 4000000,我发现的问题手机3000000就挂了,
     * 所以给了这个值具体的临界值我也不是很清楚,
     * 你可能会问为什么要给个临界值盘判断是否压缩,那是因为有些小图压缩了不能解析识别,
     * 所以我进行了判断,大图压缩解析,小图直接解析
     * */
	private static Bitmap loadBitmap(String path, boolean isOkSize) throws FileNotFoundException {
        Bitmap bitmap = null;
        if (isOkSize) {  //小图,直接调用功能系统的解析返回
            bitmap = BitmapFactory.decodeFile(path);
        } else { //大图 先压缩在返回
            bitmap = CreateQRCode_Utils.compress(path);
        }
        //ZXing源码
        if (bitmap == null) {
            throw new FileNotFoundException("Couldn't open " + path);
        }
        return bitmap;
    }
	
	
}


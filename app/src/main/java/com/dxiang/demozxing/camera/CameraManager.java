/*
 * Copyright (C) 2008 ZXing authors
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

package com.dxiang.demozxing.camera;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.dxiang.demozxing.utils.DisplayUtils;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

//	2016.08.18  将下面的值 修改成 dp
	private static  int MIN_FRAME_WIDTH = 160;//此处是每个边的长度
	private static  int MIN_FRAME_HEIGHT = 160;//此处是每个边的长度
	private static  int MAX_FRAME_WIDTH = 260;//此处是每个边的长度
	private static  int MAX_FRAME_HEIGHT = 260;//此处是每个边的长度

	private static CameraManager cameraManager;

	static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
	static {
		int sdkInt;
		try {
			sdkInt = Integer.parseInt(Build.VERSION.SDK);
		} catch (NumberFormatException nfe) {
			// Just to be safe
			sdkInt = 10000;
		}
		SDK_INT = sdkInt;
	}

	private final Context context;
	private final CameraConfigurationManager configManager;
	private Camera camera;
	private Rect framingRect;
	private Rect framingRectInPreview;
	private boolean initialized;
	private boolean previewing;
	private final boolean useOneShotPreviewCallback;
	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final PreviewCallback previewCallback;
	/**
	 * Autofocus callbacks arrive here, and are dispatched to the Handler which
	 * requested them.
	 */
	private final AutoFocusCallback autoFocusCallback;

	/**
	 * Initializes this static object with the Context of the calling Activity.
	 * 
	 * @param context
	 *            The Activity which wants to use the camera.
	 */
	public static void init(Context context) {
		if (cameraManager == null) {
			cameraManager = new CameraManager(context);
		}
	}

	/**
	 * Gets the CameraManager singleton instance.
	 * 
	 * @return A reference to the CameraManager singleton.
	 */
	public static CameraManager get() {
		return cameraManager;
	}

	private CameraManager(Context context) {

		this.context = context;
		this.configManager = new CameraConfigurationManager(context);

		// Camera.setOneShotPreviewCallback() has a race condition in Cupcake,
		// so we use the older
		// Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later,
		// we need to use
		// the more efficient one shot callback, as the older one can swamp the
		// system and cause it
		// to run out of memory. We can't use SDK_INT because it was introduced
		// in the Donut SDK.
		// useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) >
		// Build.VERSION_CODES.CUPCAKE;
		useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3; // 3
																				// =
																				// Cupcake

		previewCallback = new PreviewCallback(configManager,
				useOneShotPreviewCallback);
		autoFocusCallback = new AutoFocusCallback();
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public void openDriver(SurfaceHolder holder) throws IOException {
		if (camera == null) {
			camera = Camera.open();
			if (camera == null) {
				throw new IOException();
			}
			camera.setPreviewDisplay(holder);

			if (!initialized) {
				initialized = true;
				configManager.initFromCameraParameters(camera);
			}
			configManager.setDesiredCameraParameters(camera);

			// FIXME
			// SharedPreferences prefs =
			// PreferenceManager.getDefaultSharedPreferences(context);
			// �Ƿ�ʹ��ǰ��
			// if (prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false))
			// {
			// FlashlightManager.enableFlashlight();
			// }
			FlashlightManager.enableFlashlight();
		}
	}

	/**Closes the camera driver if still in use.*/
	public void closeDriver() {
		if (camera != null) {
			FlashlightManager.disableFlashlight();
			camera.release();
			camera = null;
		}
	}

	/**Asks the camera hardware to begin drawing preview frames to the screen.*/
	public void startPreview() {
		if (camera != null && !previewing) {
			camera.startPreview();
			previewing = true;
		}
	}

	/**Tells the camera to stop drawing preview frames.*/
	public void stopPreview() {
		if (camera != null && previewing) {
			if (!useOneShotPreviewCallback) {
				camera.setPreviewCallback(null);
			}
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			autoFocusCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively.
	 * 
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public void requestPreviewFrame(Handler handler, int message) {
		if (camera != null && previewing) {
			previewCallback.setHandler(handler, message);
			if (useOneShotPreviewCallback) {
				camera.setOneShotPreviewCallback(previewCallback);
			} else {
				camera.setPreviewCallback(previewCallback);
			}
		}
	}

	/**
	 * Asks the camera hardware to perform an autofocus.
	 * 
	 * @param handler
	 *            The Handler to notify when the autofocus completes.
	 * @param message
	 *            The message to deliver.
	 */
	public void requestAutoFocus(Handler handler, int message) {
		if (camera != null && previewing) {
			autoFocusCallback.setHandler(handler, message);
			// Log.d(TAG, "Requesting auto-focus callback");
			camera.autoFocus(autoFocusCallback);
		}
	}

	
	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the barcode. This target helps with alignment as well as
	 * forces the user to hold the device far enough away to ensure the image
	 * will be in focus.
	 * 
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public Rect getFramingRect() {
		if (framingRect == null) {
			if (camera == null) {
				return null;
			}
			Point screenResolution = configManager.getScreenResolution();
			 if (screenResolution == null) {
		            // Called early, before init even finished
		            return null;
		        }
//========下面是我添加上去的：  将dp 转成 px;  将px 转成dp 没意思
			MIN_FRAME_WIDTH= DisplayUtils.dip2px(context, MIN_FRAME_WIDTH);
			MIN_FRAME_HEIGHT=DisplayUtils.dip2px(context, MIN_FRAME_HEIGHT);
			
			MAX_FRAME_WIDTH=DisplayUtils.dip2px(context, MAX_FRAME_WIDTH);
			MAX_FRAME_HEIGHT=DisplayUtils.dip2px(context, MAX_FRAME_HEIGHT);
//========上面是我添加上去的 		 
			
			int width = screenResolution.x * 1;  // 这两处的比例，会影响相机  扫描距离，越小，距离越远。 为1 虽然占内存，但是不受距离影响
//			int width = screenResolution.x * 7/10;
//			int width = screenResolution.x * 3 / 4;
			if (width < MIN_FRAME_WIDTH) {
				width = MIN_FRAME_WIDTH;
			} else if (width > MAX_FRAME_WIDTH) {
				width = MAX_FRAME_WIDTH;
			}
			int height = screenResolution.y * 1;
//			int height = screenResolution.y * 7/10;
//			int height = screenResolution.y * 3 / 4;
			if (height < MIN_FRAME_HEIGHT) {
				height = MIN_FRAME_HEIGHT;
			} else if (height > MAX_FRAME_HEIGHT) {
				height = MAX_FRAME_HEIGHT;
			}
//========下面是我添加上去的：  如果比较获得两者，取最小值；以确保 是正方行
			if (width<height) {
				height=width;
			}
//========上面是我添加上去的 		
			
			Log.e(TAG, "width="+width+";height="+height+";\n MIN_FRAME_WIDTH="+MIN_FRAME_WIDTH+";MIN_FRAME_HEIGHT="+MIN_FRAME_HEIGHT);
			Log.e(TAG, "MAX_FRAME_WIDTH="+MAX_FRAME_WIDTH+";MAX_FRAME_HEIGHT="+MAX_FRAME_HEIGHT);
			
			int leftOffset = (screenResolution.x - width) / 2;//获得 两边的距离
			int topOffset = (screenResolution.y - height) / 2;//获得上下距离
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.d(TAG, "Calculated framing rect: " + framingRect);
		}
		return framingRect;
	}
	private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
	    int dim = 5 * resolution / 8; // Target 5/8 of each dimension
	    if (dim < hardMin) {
	        return hardMin;
	    }
	    if (dim > hardMax) {
	        return hardMax;
	    }
	    return dim;
	}
	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview
	 * frame, not UI / screen.
	 */
	public Rect getFramingRectInPreview() {
	if (framingRectInPreview == null) {
			 Rect framingRect = getFramingRect();
		        if (framingRect == null) {
		            return null;
		        }
			// 获取相机分辨率和屏幕分辨率
			Rect rect = new Rect(framingRect);//150 516 930 1296
			Point cameraResolution = configManager.getCameraResolution();//1280*720
			Point screenResolution = configManager.getScreenResolution();//1080*1800
			// 根据相机分辨率和屏幕分辨率的比例对屏幕中央聚焦框进行调整
			// modify here
//			 rect.left = rect.left * cameraResolution.x / screenResolution.x;//182*720/1080
//			 rect.right = rect.right * cameraResolution.x /screenResolution.x;//
//			 rect.top = rect.top * cameraResolution.y / screenResolution.y;//
//			 rect.bottom = rect.bottom * cameraResolution.y /screenResolution.y;//
			String log="getFramingRectInPreview cameraResolution = "+cameraResolution+";screenResolution = "+screenResolution+";rectRes = "+rect;
			Log.e(TAG, log);//									1920*1080;1080,1812
//			Toast.makeText(context, log, Toast.LENGTH_LONG).show();
			float scalX=cameraResolution.y*1.0f / screenResolution.x;//
			float scalY=cameraResolution.x*1.0f / screenResolution.y;//2017-08-31-屏幕与相片的大小不成比例的时候，进行左变小点截的图更小 ，右变大，上变小，低变大
			rect.left = (int) (rect.left * scalX+0.5);    //182*720/1080  --》变小点截的图更小 //150*1080/1080,
			rect.right = (int) (rect.right * scalX+0.5);  //897*720/1080  --》变小点截的图更大// 930*1080/1080
			rect.top = (int) (rect.top * scalY+0.5);	    //542*1280/1800 --》变小点截的图更小// 516*1920/1812
			rect.bottom = (int) (rect.bottom * scalY+0.5);//1257*1280/1800--》变大点截的图更大//1296*1920/1812
			
//			String log2= "getFramingRectInPreview  0 framing rect: " + rect;
//			Toast.makeText(context, log2, Toast.LENGTH_LONG).show();
//			if (scalX>1) {
//				rect.left = (int) (rect.left/scalX);//变小
//				rect.right = (int) (rect.right *scalX);//正常
//			}else {
//				rect.left = (int) (rect.left*scalX);//正常
//				rect.right = (int) (rect.right/scalX);//变大
//				if (rect.right>screenResolution.x) {
//					rect.right=screenResolution.x;
//				}
//			}
//			if (scalY>1) {
//				rect.top = (int) (rect.top/scalY);//变小
//				rect.bottom = (int) (rect.bottom *scalY);//正常
//			}else {
//				rect.top = (int) (rect.top*scalY);//正常
//				rect.bottom = (int) (rect.bottom/scalY);//变大
//				if (rect.bottom>screenResolution.y) {
//					rect.bottom=screenResolution.y;
//				}
//			}
			String log2= "getFramingRectInPreview cameraResolution = "+cameraResolution+";screenResolution = "+screenResolution+";rectCropImg = "+rect;
			Log.e(TAG, log2);//
//			Toast.makeText(context, log2, Toast.LENGTH_LONG).show();
			framingRectInPreview = rect;
		}
		return framingRectInPreview;
	}

	/**
	 * Converts the result points from still resolution coordinates to screen
	 * coordinates.
	 * 
	 * @param points
	 *            The points returned by the Reader subclass through
	 *            Result.getResultPoints().
	 * @return An array of Points scaled to the size of the framing rect and
	 *         offset appropriately so they can be drawn in screen coordinates.
	 */
	/*
	 * public Point[] convertResultPoints(ResultPoint[] points) { Rect frame =
	 * getFramingRectInPreview(); int count = points.length; Point[] output =
	 * new Point[count]; for (int x = 0; x < count; x++) { output[x] = new
	 * Point(); output[x].x = frame.left + (int) (points[x].getX() + 0.5f);
	 * output[x].y = frame.top + (int) (points[x].getY() + 0.5f); } return
	 * output; }
	 */

	/**
	 * A factory method to build the appropriate LuminanceSource object based on
	 * the format of the preview buffers, as described by Camera.Parameters.
	 * 
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,int width, int height) {
		  // 取得预览框内的矩形
	    Rect rect = getFramingRectInPreview();
	    if (rect == null) {
	        return null;
	    }
//	    // Go ahead and assume it's YUV rather than die.
//	    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
	  
	    //不仅仅使用聚焦框里的图像数据，而是采用全幅图像的数据，对上面的return做一个进化，要求cpu高，但提高了识别的精度
//	    return new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height);
	    
	    //			
//		// 取得预览框内的矩形
//		
//		Rect rect = getFramingRectInPreview();
		int previewFormat = configManager.getPreviewFormat();
		String previewFormatString = configManager.getPreviewFormatString();
		switch (previewFormat) {
		// This is the standard Android format which all devices are REQUIRED to
		// support.
		// In theory, it's the only one we should ever care about.
		case PixelFormat.YCbCr_420_SP:
			// This format has never been seen in the wild, but is compatible as
			// we only care
			// about the Y channel, so allow it.
		case PixelFormat.YCbCr_422_SP:
			return new PlanarYUVLuminanceSource(data, width, height, rect.left,
					rect.top, rect.width(), rect.height());
		default:
			// The Samsung Moment incorrectly uses this variant instead of the
			// 'sp' version.
			// Fortunately, it too has all the Y data up front, so we can read
			// it.
			if ("yuv420p".equals(previewFormatString)) {
				return new PlanarYUVLuminanceSource(data, width, height,
						rect.left, rect.top, rect.width(), rect.height());
			}
		}
		throw new IllegalArgumentException("Unsupported picture format: "
				+ previewFormat + '/' + previewFormatString);
	}

	public Context getContext() {
		return context;
	}
	
	public void openLight()   //打开闪光灯
    {
        if(camera!=null)
        {
             Parameters parameter=camera.getParameters();  
             parameter.setFlashMode(Parameters.FLASH_MODE_TORCH); 
             camera.setParameters(parameter);
        }
    }
    
    public void closeLight()  //关闭闪光灯
    {
        if(camera!=null)
        {
             Parameters parameter=camera.getParameters();  
             parameter.setFlashMode(Parameters.FLASH_MODE_OFF); 
             camera.setParameters(parameter);
        }
    }

}

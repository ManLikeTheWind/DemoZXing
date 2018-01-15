package com.dxiang.demozxing.appconfig;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.dxiang.demozxing.MainActivity;
import com.dxiang.demozxing.R;
import com.dxiang.demozxing.constants.ConfigConstants;
import com.dxiang.demozxing.utils.FileUtils;
import com.dxiang.demozxing.utils.SystemPropertyUtil;
import com.dxiang.demozxing.utils.SystemViewUtils;
import com.dxiang.demozxing.utils.ToastUtils;
import com.dxiang.demozxing.utils.systemdevice.ApkUtils;
import com.dxiang.demozxing.utils.systemdevice.PhoneUtils;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UnCatchException implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	public static final String TAG = "CatchExcep";
//	private BaseApp application;
	private Context mContext;
//	ArrayList<Activity> list;

	public UnCatchException(Application application) {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		mContext = application.getApplicationContext();
//		this.application = application;
//		list = new ArrayList<Activity>();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String versioninfo = ApkUtils.getVersionName(mContext);
		String mobileInfo = PhoneUtils.getMobileInfo();
		String errorinfo = getErrorInfo(ex);
		Log.e("CrashHandler", "errorinfo-->" + errorinfo);
		// 是发布版，并且还得运行输入错误日志到文件
		if (ConfigConstants.CRASH_FILE) {
			StringBuilder sb = new StringBuilder();
			sb.append(mContext.getString(R.string.crash_version_info, versioninfo))
					.append("\n")
					.append(mContext
							.getString(R.string.crash_mobile_info, mobileInfo))
					.append("\n")
					.append(mContext.getString(R.string.crash_error_info, errorinfo));
			saveCrashInfo2File(sb.toString());
		}
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			if (ConfigConstants.IS_RELOAD_APP) {
				SystemViewUtils.gotoAppMainActivity(mContext, MainActivity.class);
			}
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				if (ConfigConstants.IS_RELOAD_APP){
					ToastUtils.showToastCenterShort(R.string.crash_app_exception_notification,mContext);
				}else {
					ToastUtils.showToastCenterShort(R.string.crash_app_exception_notification,mContext);
				}
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**获取错误的信*/
	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}


	/**
	 * 保存错误信息到文件中
	 * @return 返回文件名称,便于将文件传送到服务
	 */
	private String saveCrashInfo2File(String logMsg) {
		DateFormat formatter = new SimpleDateFormat("MM-dd-HH-mm-ss");
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "log-" + time + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				if (!FileUtils.fileExist(ConfigConstants.LOG_PATH, fileName)) {
					FileUtils.createFile(ConfigConstants.LOG_PATH, fileName);
				}
				FileOutputStream fos = new FileOutputStream(ConfigConstants.LOG_PATH+ fileName);
				fos.write(logMsg.getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
}

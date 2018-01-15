package com.dxiang.demozxing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.dxiang.demozxing.BuildConfig;
import com.dxiang.demozxing.R;
import com.dxiang.demozxing.constants.ConfigConstants;
import com.dxiang.demozxing.constants.Constants;
import com.dxiang.demozxing.info.ConfigInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author DC
 */
public class SystemPropertyUtil {
    public static final String TAG = SystemPropertyUtil.class.getSimpleName();

    /**初始化系统的配置文件*/
    public static void initSystemAssetProperties(Context c) {
        InputStream is = null;
        Properties properties = new Properties();
        try {
            is = c.getAssets().open("config.properties");
            if (is == null) {
                return;
            }
            properties.load(is);
//			int size = properties.size();
            ConfigConstants.IS_DEBUG = BuildConfig.DEBUG;
            ConfigConstants.CRASH_FILE = Boolean.parseBoolean(properties.getProperty("crashfile", "false"));
            ConfigConstants.IS_RELOAD_APP = Boolean.parseBoolean(properties.getProperty("reloadapp", "false"));
            try {
                ConfigConstants.LOG_PATH = c.getExternalCacheDir().getAbsolutePath() + properties.getProperty("logpath") + "/";
            } catch (Exception e) {
                ConfigConstants.LOG_PATH = c.getCacheDir() + "/";
                Log.e(TAG, "init Variable.FILE_PAH,LOG_PAHT error ： " + e.getMessage());
            }
        } catch (IOException e) {
            Log.e("", e.getMessage());
        } finally {
            FileUtils.closeStreamReader(is);
        }
    }

    /**初始化系统的配置文件*/
    public static void initSystemAssetGson(Context applicationContext) {//2017-07-20根据登录的账号进行区分是哪个配置文件
        List<ConfigInfo> listConfigs = new ArrayList<ConfigInfo>();
        AssetManager assertmgr = applicationContext.getAssets();
        String fileName = "config3.json";
        try {
            String json = FileUtils.readFileAsset(assertmgr, fileName).toString();
            String resLockAreaConfig = json;
//					LockConfigsUtils.unlockString(ComConstants.KEY_QRCODE_DES_ENCODE,json).trim();
            Gson gson = new Gson();
            listConfigs = gson.fromJson(resLockAreaConfig, new TypeToken<List<ConfigInfo>>() {//这是一个配置集合
            }.getType());
        } catch (Exception e) {
            Log.e(TAG, "initSystemAssetGson" + e.getMessage());
            throw new IllegalStateException(e.getCause());
        }
        boolean isequals = false;
        for (ConfigInfo temp : listConfigs) {
            if (ConfigConstants.FLAVOR.equals(temp.getFlavor())) {
                ConfigConstants.IS_DEBUG = BuildConfig.DEBUG;
                ConfigConstants.CRASH_FILE = temp.isCrashfile();
                ConfigConstants.IS_RELOAD_APP = temp.isReloadapp();
                try {
                    ConfigConstants.LOG_PATH = applicationContext.getExternalCacheDir().getAbsolutePath() + temp.getLogpath() + "/";
                } catch (Exception e) {
                    ConfigConstants.LOG_PATH = applicationContext.getCacheDir() + "/"+ temp.getLogpath() + "/";
                    Log.e(TAG, "init Variable.FILE_PAH,LOG_PAHT error ： " + e.getMessage());
                }
                isequals = true;
                break;
            }
        }
        if (!isequals) {
            String msg = "ConfigConstants.FLAVOR error:" + ConfigConstants.FLAVOR;
            throw new IllegalStateException(msg);
        }
    }


    public static void playSystemRawFile(Activity activity , boolean mPlayBeep, MediaPlayer mMediaPlayer,boolean mPlayVibrate,Vibrator mVibrator) {
        if (mPlayBeep && mMediaPlayer == null) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.seekTo(0);
                }
            });
            AssetFileDescriptor fileDescriptor =activity.getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(
                        fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                fileDescriptor.close();
                mMediaPlayer.setVolume(Constants.BEEP_VOLUME, Constants.BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (Exception e) {
                Log.e(TAG, "initBeepSound: ", e);
                mMediaPlayer=null;
            }
        }
        if (mPlayBeep&&mMediaPlayer!=null){
            mMediaPlayer.start();
        }
        if (mPlayVibrate){
            mVibrator= (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
        }
        if (mVibrator!=null){
            mVibrator.vibrate(Constants.VIBRATE_DURATION);
        }
    }

    /**播放来新消息提示音*/
    public void playAssetRingFile(Context context) {
        MediaPlayer mp = new MediaPlayer();
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd("msg_ring.mp3");
            mp.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getStartOffset());
            mp.prepare();
            mp.start();
//            Uri deRingNotify =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Uri deRingCall =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            Uri deRingAlarm=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//
//            Uri ring=deRingNotify==null?(deRingCall==null?deRingAlarm:deRingCall):deRingNotify;
//
//            mp.setDataSource(context, ring);
//            mp.prepare();
//            mp.start();
        } catch (Exception e) {
            Log.e(TAG,TAG+"_"+e.getMessage());
            e.printStackTrace();
            Uri deRingNotify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone mRingtone_notify = RingtoneManager.getRingtone(
                    context, deRingNotify);
            if (mRingtone_notify != null) {
                if (mRingtone_notify.isPlaying()) {
                    mRingtone_notify.stop();
                } else {
                    mRingtone_notify
                            .setStreamType(AudioManager.STREAM_NOTIFICATION);
                    mRingtone_notify.play();
                }
            }
        }
    }



}

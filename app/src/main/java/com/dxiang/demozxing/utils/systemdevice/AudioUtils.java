package com.dxiang.demozxing.utils.systemdevice;

import android.content.Context;
import android.media.AudioManager;

public class AudioUtils {
    public static int getAudioRingMode(Context context) {
        AudioManager audioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return  audioManager.getRingerMode();
    }
}
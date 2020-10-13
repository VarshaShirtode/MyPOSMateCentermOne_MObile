package com.quagnitia.myposmate.utils;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import java.util.HashMap;

/**
 * @author wangwenxun
 * @date 2019/9/22
 */

public class MySoundPlayer {
    private Context mContext;
    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;
    private AudioManager mAudioManager;
    private int currentSoundID = 0;

    public MySoundPlayer() {
    }

    public void initSounds(Context theContext) {
        this.mContext = theContext;
        this.mSoundPool = new SoundPool(4, 2, 5);
        this.mSoundPoolMap = new HashMap();
        this.mAudioManager = (AudioManager)this.mContext.getSystemService("audio");
    }

    public void addSound(int index, int SoundID) {
        this.mSoundPoolMap.put(index, this.mSoundPool.load(this.mContext, SoundID, 1));
    }

    public int playSound(int index) {
        float streamVolume = (float)this.mAudioManager.getStreamVolume(2);
        streamVolume /= (float)this.mAudioManager.getStreamMaxVolume(2);
        this.currentSoundID = this.mSoundPool.play((Integer)this.mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1.0F);
        return this.currentSoundID;
    }

    public int playLoopedSound(int index) {
        float streamVolume = (float)this.mAudioManager.getStreamVolume(2);
        streamVolume /= (float)this.mAudioManager.getStreamMaxVolume(2);
        this.currentSoundID = this.mSoundPool.play((Integer)this.mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1.0F);
        return this.currentSoundID;
    }

    public void stopCurrentSound(int streamID) {
        this.mSoundPool.stop(streamID);
    }
}



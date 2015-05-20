package com.ckt.francis.musicplayer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.ckt.francis.musicplayer.controller.MusicController;
import com.ckt.francis.musicplayer.controller.OnTimeChangeListener;
import com.ckt.francis.musicplayer.utils.MusicState;

public class PlayMusicService extends Service implements OnTimeChangeListener {
    private MusicController mMusicController;
    private OnStateListener mOnStateListener;


    public interface OnStateListener{
        void onStateChange(MusicState mMusicState);
        void onTimeChanges(int time);
    }

    public class MusicBinder extends Binder{
        public PlayMusicService getService(){
            return PlayMusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicController = MusicController.getInstance();
        mMusicController.setOnTimeChangeListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public void seekMusic(int time) {
        mMusicController.seekMusic(time,true);
    }

    public void seekToMusic(int rate){
        mMusicController.seekMusic(rate,false);
    }
    public void playOrPauseMusic(String path){
        if(mMusicController.getMusicState() == MusicState.PLAYING) {
            mMusicController.pauseMusic();
        }else{
            mMusicController.playMusic(this, Uri.parse(path));
        }
        if (mOnStateListener != null) {
            mOnStateListener.onStateChange(mMusicController.getMusicState());
        }

    }

    public void setOnStateListener(OnStateListener mOnStateListener) {
        this.mOnStateListener = mOnStateListener;
    }

    @Override
    public void onTimeChange(int rate) {
        if(mOnStateListener != null){
            mOnStateListener.onTimeChanges(rate);
        }
        if(rate >=100 && mOnStateListener != null){
            mOnStateListener.onStateChange(mMusicController.getMusicState());
        }
    }
}

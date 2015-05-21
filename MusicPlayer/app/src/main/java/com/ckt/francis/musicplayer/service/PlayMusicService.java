package com.ckt.francis.musicplayer.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.ckt.francis.musicplayer.controller.MusicController;
import com.ckt.francis.musicplayer.controller.OnStateListener;
import com.ckt.francis.musicplayer.utils.Constant;
import com.ckt.francis.musicplayer.utils.MusicState;

public class PlayMusicService extends Service implements OnStateListener {
    private MusicController mMusicController;
    private Notification mNotification;
    @Override
    public void onStateChange() {
        refreshView();
    }

    @Override
    public void onTimeChange(int current, int total) {
        refreshView(current, total);
    }

    @Override
    public void playComplete() {
        Intent _intent = new Intent();
        _intent.setAction(Constant.ACTION_CHANGE);
        _intent.putExtra(Constant.PLAYNEXT,true);
        sendBroadcast(_intent);
    }

    public class MusicBinder extends Binder {
        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicController = MusicController.getInstance();
        mMusicController.setOnStateListener(this);
//
//        mNotification = new Notification();
//        mNotification.icon =  ;
//        mNotification.when = System.currentTimeMillis();
//        mNotification.tickerText = "";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public void seekMusic(int time) {
        mMusicController.seekMusic(time, true);
    }

    public void seekToMusic(int rate) {
        mMusicController.seekMusic(rate, false);
    }

    public void playOrPauseMusic(String path) {
        if (mMusicController.getMusicState() == MusicState.PLAYING) {
            mMusicController.pauseMusic();

        } else {
            mMusicController.playAndPauseMusic(this, Uri.parse(path));
        }
    }

    public void play(String path) {
        mMusicController.playMusic(this, Uri.parse(path));
    }

    public void refreshView() {
        Intent _intent = new Intent();
        _intent.setAction(Constant.ACTION_CHANGE);
        _intent.putExtra(Constant.STATUS, mMusicController.getMusicState());
        sendBroadcast(_intent);
    }

    public void refreshView(int time, int total) {
        Intent _intent = new Intent();
        _intent.setAction(Constant.ACTION_CHANGE);
        _intent.putExtra(Constant.CURRENT, time);
        _intent.putExtra(Constant.TOTAL, total);
        sendBroadcast(_intent);
    }

}

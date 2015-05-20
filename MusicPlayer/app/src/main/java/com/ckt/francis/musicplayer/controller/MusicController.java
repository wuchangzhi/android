package com.ckt.francis.musicplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.ckt.francis.musicplayer.service.PlayMusicService;
import com.ckt.francis.musicplayer.utils.MusicState;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicController {
    private static MusicController mMusicController;
    private MediaPlayer mMediaPlayer;
    private MusicState mMusicState = MusicState.STOP;
    private Timer mTimer;
    private OnStateListener mOnStateListener;

    private MusicController() {
    }

    public void playMusic(Context context, Uri uri) {
        if (mMediaPlayer != null) {
            stopMusic();
        }
        playAndPauseMusic(context, uri);
    }

    public void playAndPauseMusic(Context context, Uri uri) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mTimer = new Timer("");
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMusic();
                }
            });
            try {
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {
                        mp.start();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mOnStateListener.onTimeChange(Math.round(mp.getCurrentPosition() / 1000f), Math.round(mp.getDuration() / 1000f));
                            }
                        }, 0, 1000);
                        Log.d("test", Math.round(mp.getDuration() / 1000.0) + "");
                    }
                });
                mMediaPlayer.setDataSource(context, uri);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mMediaPlayer.start();
        }
        mMusicState = MusicState.PLAYING;
        if (mOnStateListener != null) {
            mOnStateListener.onStateChange();
        }
    }

    public void pauseMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mMusicState = MusicState.PAUSE;
        if (mOnStateListener != null) {
            mOnStateListener.onStateChange();
        }
    }

    public void seekMusic(int time, boolean isTo) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            if (isTo) {
                time = mMediaPlayer.getCurrentPosition() + time;
                time = mMediaPlayer.getDuration() > time ? time : mMediaPlayer.getDuration();
                mMediaPlayer.seekTo(time);
            } else {
                Log.d("test", (time * 1000) + "");
                mMediaPlayer.seekTo(time * 1000);
            }
        }
    }

    public void stopMusic() {
        mTimer.cancel();
        int duration = Math.round(mMediaPlayer.getDuration() / 1000f);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMusicState = MusicState.STOP;
            if (mOnStateListener != null) {
                mOnStateListener.onStateChange();
                mOnStateListener.onTimeChange(0, duration);
            }
            release();
        }
    }

    public static synchronized MusicController getInstance() {
        if (mMusicController == null) {
            mMusicController = new MusicController();
        }
        return mMusicController;
    }

    private void release() {
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public MusicState getMusicState() {
        return mMusicState;
    }

    public void setOnStateListener(OnStateListener mOnStateListener) {
        this.mOnStateListener = mOnStateListener;
    }
}

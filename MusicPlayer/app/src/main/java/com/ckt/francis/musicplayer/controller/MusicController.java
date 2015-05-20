package com.ckt.francis.musicplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.ckt.francis.musicplayer.utils.MusicState;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicController {
    private static MusicController mMusicController;
    private MediaPlayer mMediaPlayer;
    private MusicState mMusicState = MusicState.STOP;
    private OnTimeChangeListener mListener;
    private Timer mTimer;

    private MusicController() {
    }

    public void playMusic(Context context, Uri uri) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mTimer = new Timer("");
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mTimer.cancel();
                    stopMusic();
                    mListener.onTimeChange(100);

                }
            });
            try {
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {
                        mp.start();
                        final int duration = mMediaPlayer.getDuration();
                        if(mListener != null) {
                            mTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    int now = mp.getCurrentPosition();
                                    mListener.onTimeChange(now * 100 / duration);
                                }
                            }, 0, 1000);
                        }
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
    }

    public void pauseMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mMusicState = MusicState.PAUSE;
    }

    public void seekMusic(int time,boolean isTo) {
        if (mMediaPlayer != null) {
            if(isTo) {
                time = mMediaPlayer.getCurrentPosition() + time;
                time = mMediaPlayer.getDuration() > time ? time : mMediaPlayer.getDuration();
                mMediaPlayer.seekTo(time);
            }else{
                mMediaPlayer.seekTo(mMediaPlayer.getDuration() * time /100);
            }
        }
    }

    public void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMusicState = MusicState.STOP;
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

    public void setOnTimeChangeListener(OnTimeChangeListener mListener) {
        this.mListener = mListener;
    }
}

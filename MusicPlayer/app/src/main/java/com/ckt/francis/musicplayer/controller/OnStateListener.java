package com.ckt.francis.musicplayer.controller;

/**
 * Created by wuchangzhi on 15年5月20日.
 */
public interface OnStateListener {
    void onStateChange();
    void onTimeChange(int current, int total);
    void playComplete();
}

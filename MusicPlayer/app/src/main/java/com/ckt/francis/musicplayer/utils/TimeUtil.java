package com.ckt.francis.musicplayer.utils;

/**
 * Created by wuchangzhi on 15年5月20日.
 */
public class TimeUtil {
    public static String convertTime(int time){
        return String.format("%02d:%02d",time/60,time%60);
    }
}

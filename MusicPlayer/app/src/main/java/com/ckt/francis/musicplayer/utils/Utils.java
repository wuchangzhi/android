package com.ckt.francis.musicplayer.utils;

/**
 * Created by wuchangzhi on 15年5月20日.
 */
public class Utils {
    public static String convertTime(long millis) {
        int totalSeconds = (int) millis / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return String.format("%1$d:%2$02d:%3$02d", hours, minutes, seconds);
        } else {
            return String.format("%1$02d:%2$02d", minutes, seconds);
        }
    }


    public static String convertSize(long size) {
        if (size * 1.0f / 1024 / 1024 / 1024 > 1.0f) {
            return String.format("%1$.2f", size * 1.0f / 1024 / 1024 / 1024) + "GB";
        } else {
            return String.format("%1$.2f", size * 1.0f / 1024 / 1024) + "MB";
        }


    }
}

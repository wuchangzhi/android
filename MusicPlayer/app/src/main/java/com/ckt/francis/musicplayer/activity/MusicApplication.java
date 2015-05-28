package com.ckt.francis.musicplayer.activity;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by wuchangzhi on 15年5月28日.
 */
public class MusicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        scanFiles();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void scanFiles(){

    }
}

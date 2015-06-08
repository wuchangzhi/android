package com.ckt.francis.musicplayer.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.ckt.francis.musicplayer.R;

/**
 * Created by wuchangzhi on 15年6月2日.
 */
public class MusicSettings extends PreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.music_settings);
        Preference store_diraction = findPreference("store_diraction");
        
    }
}

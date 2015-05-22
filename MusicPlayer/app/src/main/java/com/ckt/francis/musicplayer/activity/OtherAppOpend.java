package com.ckt.francis.musicplayer.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.controller.MusicController;
import com.ckt.francis.musicplayer.controller.OnStateListener;
import com.ckt.francis.musicplayer.utils.MediaUtil;


public class OtherAppOpend extends Activity implements OnStateListener {
    private MusicController mMusicController;
    private TextView mTextView;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);

        mMusicController = MusicController.getInstance();
        mMusicController.setOnStateListener(this);

        initViews();

        Cursor cursor = MediaUtil.getResult(this, MediaStore.Audio.Media.DATA + " = '" + getIntent().getData().getPath() + "'", null, null);
        if (cursor.moveToFirst()) {
            mTextView.setText(cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));
            mSeekBar.setMax((int) cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) / 1000);
        }
        mMusicController.playMusic(this, getIntent().getData());

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMusicController.seekMusic(seekBar.getProgress(),false);
            }
        });
    }

    private void initViews() {
        mTextView = (TextView) findViewById(R.id.music_title_dialog);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar_dialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMusicController.stopMusic();
        mMusicController.removeOnStateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStateChange() {

    }

    @Override
    public void onTimeChange(int current) {
        mSeekBar.setProgress(current);
    }

    @Override
    public void playComplete() {

    }
}

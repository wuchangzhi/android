package com.ckt.francis.musicplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.activity.base.BaseActivity;
import com.ckt.francis.musicplayer.service.PlayMusicService;
import com.ckt.francis.musicplayer.utils.MusicState;


public class MainActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, PlayMusicService.OnStateListener {
    private SeekBar mSeekBar;
    private Button mPlay;
    private Button mForward;
    private Button mRewind;
    private Intent mIntent;
    private String musicPath;
    private PlayMusicService mService;
    private boolean isBind = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();
        initData();

        startService(mIntent);
        isBind = bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mPlay = (Button) findViewById(R.id.b_play);
        mForward = (Button) findViewById(R.id.b_forward);
        mRewind = (Button) findViewById(R.id.b_rewind);
    }

    private void initEvents() {
        mSeekBar.setOnSeekBarChangeListener(this);
        mPlay.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mRewind.setOnClickListener(this);
    }

    private void initData() {
        mIntent = new Intent(this, PlayMusicService.class);
        musicPath = Environment.getExternalStorageDirectory() + "/zou.mp3";
    }

    @Override
    public void onClick(View v) {
        Log.d("test", mService + "");
        switch (v.getId()) {
            case R.id.b_forward:
                mService.seekMusic(3000);
                break;
            case R.id.b_rewind:
                mService.seekMusic(-3000);
                break;
            case R.id.b_play:
                mService.playOrPauseMusic(musicPath);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mService.seekToMusic(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("test", "onServiceConnected");
            mService = ((PlayMusicService.MusicBinder) service).getService();
            mService.setOnStateListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("test","onStop");
        if (isBind) {
            unbindService(mConnection);
            isBind = false;
        }
    }


    @Override
    public void onStateChange(MusicState mMusicState) {
        Log.d("test","mMusicState:" +mMusicState);
        switch (mMusicState) {
            case PLAYING:
                mPlay.setText(getString(R.string.pause));
                break;
           default:
                mPlay.setText(getString(R.string.play));
                break;
        }
    }

    @Override
    public void onTimeChanges(int rates) {
        Log.d("test", rates + "");
        mSeekBar.setProgress(rates);
    }
}
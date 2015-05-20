package com.ckt.francis.musicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.activity.base.BaseActivity;
import com.ckt.francis.musicplayer.adapter.MusicsAdapter;
import com.ckt.francis.musicplayer.service.PlayMusicService;
import com.ckt.francis.musicplayer.utils.Constant;
import com.ckt.francis.musicplayer.utils.MusicState;
import com.ckt.francis.musicplayer.utils.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private SeekBar mSeekBar;
    private TextView mCurrent;
    private TextView mTotal;
    private Button mPlay;
    private Button mForward;
    private Button mRewind;
    private Intent mIntent;
    private String musicPath;
    private PlayMusicService mService;
    private ListView mMusicList;
    private List<Map<String,String>> mAllMusics = new ArrayList<Map<String, String>>();
    private MusicsAdapter mAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MusicState status = (MusicState) intent.getSerializableExtra(Constant.STATUS);
            int current = intent.getIntExtra(Constant.CURRENT,0);
            int total = intent.getIntExtra(Constant.TOTAL,-1);
            if(status !=null) {
                switch (status) {
                    case PLAYING:
                        mPlay.setText(getString(R.string.pause));
                        break;
                    default:
                        mPlay.setText(getString(R.string.play));
                        break;
                }
            }
            if(total != -1) {
                mSeekBar.setMax(total);
                mSeekBar.setProgress(current);
                mCurrent.setText(TimeUtil.convertTime(current));
                mTotal.setText(TimeUtil.convertTime(total));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();
        initData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String columns[] = { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION};

                Cursor cursor = MainActivity.this.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Audio.Media.DURATION +" > 3000", null, null);

                while (cursor.moveToNext()) {
                    Map<String,String> items = new HashMap<String,String>();
                    items.put(Constant.NAME, cursor.getString(1));
                    items.put(Constant.PATH, cursor.getString(2));
                    items.put(Constant.ALBUM, cursor.getString(3));
                    items.put(Constant.ARTIST, cursor.getString(4));
                    items.put(Constant.DURATION,TimeUtil.convertTime(cursor.getInt(5)/1000) + "");
                    mAllMusics.add(items);
                }
            }
        }).start();

        startService(mIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        mMusicList = (ListView) findViewById(R.id.listView);
        mCurrent = (TextView) findViewById(R.id.current);
        mTotal = (TextView) findViewById(R.id.total);
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
        mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = mAllMusics.get(position).get(Constant.PATH);
                mService.play(path);
            }
        });
    }

    private void initData() {
        mIntent = new Intent(this, PlayMusicService.class);
        musicPath = Environment.getExternalStorageDirectory() + "/zou.mp3";
        mAdapter = new MusicsAdapter(this,mAllMusics);
        mMusicList.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_forward:
                mService.seekMusic(3000);
                break;
            case R.id.b_rewind:
                mService.seekMusic(-3000);
                break;
            case R.id.b_play:
                if(mAllMusics.size() != 0) {
                    mService.playOrPauseMusic(mAllMusics.get(0).get(Constant.PATH));
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            mService.seekToMusic(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mService.seekToMusic(seekBar.getProgress());
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((PlayMusicService.MusicBinder) service).getService();
            mService.refreshView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        unregisterReceiver(mReceiver);
    }
}
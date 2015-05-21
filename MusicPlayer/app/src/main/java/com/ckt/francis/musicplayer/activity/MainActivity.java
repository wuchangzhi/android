package com.ckt.francis.musicplayer.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.activity.base.BaseActivity;
import com.ckt.francis.musicplayer.adapter.MusicsAdapter;
import com.ckt.francis.musicplayer.service.PlayMusicService;
import com.ckt.francis.musicplayer.utils.Constant;
import com.ckt.francis.musicplayer.utils.MusicState;
import com.ckt.francis.musicplayer.utils.Utils;

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
    private List<Map<String, String>> mAllMusics = new ArrayList<Map<String, String>>();
    private MusicsAdapter mAdapter;
    private int currentPosition = 0;
    private int totalNums;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MusicState status = (MusicState) intent.getSerializableExtra(Constant.STATUS);
            int current = intent.getIntExtra(Constant.CURRENT, 0);
            int total = intent.getIntExtra(Constant.TOTAL, -1);
            if (status != null) {
                switch (status) {
                    case PLAYING:
                        mPlay.setText(getString(R.string.pause));
                        break;
                    default:
                        mPlay.setText(getString(R.string.play));
                        break;
                }
            }
            if (total != -1) {
                mSeekBar.setMax(total);
                mSeekBar.setProgress(current);
                mCurrent.setText(Utils.convertTime(current));
                mTotal.setText(Utils.convertTime(total));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanFiles();
        initViews();
        initEvents();
        initData();

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
                currentPosition = position;
                String path = mAllMusics.get(position).get(Constant.PATH);
                mService.play(path);
            }
        });
        registerForContextMenu(mMusicList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo menuInfos = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(mAllMusics.get(menuInfos.position).get(Constant.NAME));
        menu.add(0, Constant.PLAY, 0, getString(R.string.play));
        menu.add(0, Constant.DELETE, 0, getString(R.string.delete));
        menu.add(0, Constant.RENAME, 0, getString(R.string.rename));
        menu.add(0, Constant.SHARE, 0, getString(R.string.share));
        menu.add(0, Constant.DETAIL, 0, getString(R.string.details));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case  Constant.PLAY:
                mService.play(mAllMusics.get(menuInfo.position).get(Constant.PATH));
                break;
            case Constant.DELETE:
                deleteFile(menuInfo.position);
                break;
            case Constant.RENAME:
                renameFile(menuInfo.position);
                break;
            case Constant.SHARE:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("audio/*");
                Uri u = Uri.parse(mAllMusics.get(menuInfo.position).get(Constant.PATH));
                intent.putExtra(Intent.EXTRA_STREAM, u);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
                break;
            case Constant.DETAIL:
                showDetails(menuInfo.position);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFile(final int position) {
        new AlertDialog.Builder(this)
                .setTitle(mAllMusics.get(position).get(Constant.NAME))
                .setMessage(R.string.delete_item)

                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                MediaStore.Audio.Media._ID + " = ? ",
                                new String[]{mAllMusics.get(position).get("_ID")});
                        scanFiles();
                    }
                })
                .setNegativeButton(android.R.string.cancel,null).show();
        return;
    }

    private void renameFile(final int position) {
        View view = getLayoutInflater().inflate(R.layout.rename_item,null);
        final EditText renameItem = (EditText)view.findViewById(R.id.item_name);
        renameItem.setText(mAllMusics.get(position).get(Constant.NAME));

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rename))
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, renameItem.getText().toString());
                        getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues,
                                MediaStore.Audio.Media._ID + " = ? ",
                                new String[]{mAllMusics.get(position).get("_ID")});
                        scanFiles();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    private void showDetails(int position){
        StringBuffer sb = new StringBuffer();
        sb.append("名称:" + mAllMusics.get(position).get(Constant.NAME) + "\n");
        sb.append("时间:" + mAllMusics.get(position).get(Constant.DURATION) + "\n");
        sb.append("大小:" + mAllMusics.get(position).get(Constant.SIZE) + "\n");
        sb.append("路径:" + mAllMusics.get(position).get(Constant.PATH) + "\n");


        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.details))
                .setMessage(sb.toString())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void scanFiles(){
        Intent _intent = new Intent();
        _intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/");
        _intent.setData(uri);
        sendBroadcast(_intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAllMusics.clear();
                String columns[] = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE};

                Cursor cursor = MainActivity.this.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Audio.Media.DURATION + " > 3000", null, null);
                totalNums = cursor.getCount();
                while (cursor.moveToNext()) {
                    Map<String, String> items = new HashMap<String, String>();
                    items.put("_ID", cursor.getString(0));
                    items.put(Constant.NAME, cursor.getString(1));
                    items.put(Constant.PATH, cursor.getString(2));
                    items.put(Constant.ALBUM, cursor.getString(3));
                    items.put(Constant.ARTIST, cursor.getString(4));
                    items.put(Constant.DURATION, Utils.convertTime(cursor.getLong(5)) + "");
                    items.put(Constant.SIZE, Utils.convertSize(cursor.getLong(6)));
                    mAllMusics.add(items);
                }
                if (cursor != null) {
                    cursor.close();
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initData() {
        mIntent = new Intent(this, PlayMusicService.class);
        musicPath = Environment.getExternalStorageDirectory() + "/zou.mp3";
        mAdapter = new MusicsAdapter(this, mAllMusics);
        mMusicList.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_forward:
                //mService.seekMusic(3000);
                if (mAllMusics.size() != 0) {
                    currentPosition = (currentPosition + 1) % totalNums;
                    mService.play(mAllMusics.get(currentPosition).get(Constant.PATH));
                }

                break;
            case R.id.b_rewind:
                //mService.seekMusic(-3000);
                if (mAllMusics.size() != 0) {
                    currentPosition = (currentPosition - 1 + totalNums) % totalNums;
                    mService.play(mAllMusics.get(currentPosition).get(Constant.PATH));
                }
                break;
            case R.id.b_play:
                if (mAllMusics.size() != 0) {
                    mService.playOrPauseMusic(mAllMusics.get(0).get(Constant.PATH));
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
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

            if(getIntent().getAction().equals(Intent.ACTION_VIEW)){
                Uri uri = getIntent().getData();
                mService.play(uri.getPath());
            }
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
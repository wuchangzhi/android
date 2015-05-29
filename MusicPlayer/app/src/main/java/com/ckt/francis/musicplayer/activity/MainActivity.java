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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.activity.base.BaseActivity;
import com.ckt.francis.musicplayer.adapter.MusicsAdapter;
import com.ckt.francis.musicplayer.model.Mp3Info;
import com.ckt.francis.musicplayer.service.PlayMusicService;
import com.ckt.francis.musicplayer.utils.Constant;
import com.ckt.francis.musicplayer.utils.MediaUtil;
import com.ckt.francis.musicplayer.utils.MusicState;
import com.ckt.francis.musicplayer.utils.Utils;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private SeekBar mSeekBar;
    private TextView mCurrent;
    private DrawerLayout mDrawerLayout;
    private TextView mTotal;
    private ImageButton mPlay;
    private ImageButton mForward;
    private ImageButton mRewind;
    private TextView mMusicTitle;
    private Button mDownload;
    private Intent mIntent;
    private PlayMusicService mService;
    private ListView mMusicList;
    private List<Mp3Info> mAllMusics = new ArrayList<>();
    private MusicsAdapter mAdapter;
    private int currentPosition = 0;
    private int totalNums;
    private ImageView mMusicIcon;
    private SharedPreferences mSharedPreferences;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MusicState status = (MusicState) intent.getSerializableExtra(Constant.STATUS);
            int current = intent.getIntExtra(Constant.CURRENT, -1);
            if (status != null) {
                refreshViews(currentPosition);
                switch (status) {
                    case PLAYING:
                        //mPlay.(getString(R.string.pause));
                        mPlay.setImageResource(android.R.drawable.ic_media_pause);
                        break;
                    default:
                        //mPlay.setText(getString(R.string.play));
                        mPlay.setImageResource(android.R.drawable.ic_media_play);
                        break;
                }
            }
            if (current != -1) {
                mSeekBar.setProgress(current);
                mCurrent.setText(Utils.convertTime(current));
            }
            if (intent.getBooleanExtra(Constant.PLAYNEXT, false) && mService != null) {
                playNext();
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
        //mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mMusicList = (ListView) findViewById(R.id.listView);
        mCurrent = (TextView) findViewById(R.id.current);
        mTotal = (TextView) findViewById(R.id.total);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mPlay = (ImageButton) findViewById(R.id.b_play);
        mForward = (ImageButton) findViewById(R.id.b_forward);
        mRewind = (ImageButton) findViewById(R.id.b_rewind);
        mDownload = (Button) findViewById(R.id.download_music);
        mMusicIcon = (ImageView) findViewById(R.id.music_icon);
        mMusicTitle = (TextView) findViewById(R.id.play_music_title);
    }

    private void initEvents() {
        mSeekBar.setOnSeekBarChangeListener(this);
        mPlay.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mRewind.setOnClickListener(this);
        mDownload.setOnClickListener(this);
        mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                String path = mAllMusics.get(position).getPath();
                mService.play(path);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        registerForContextMenu(mMusicList);
    }

    private void initData() {
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        int position = mSharedPreferences.getInt(Constant.CURRENT, -1);
        if (position != -1) {
            currentPosition = position;
        }
        mIntent = new Intent(this, PlayMusicService.class);
        mAdapter = new MusicsAdapter(this, mAllMusics);
        mMusicList.setAdapter(mAdapter);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo menuInfos = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(mAllMusics.get(menuInfos.position).getTitle());
        menu.add(0, Constant.PLAY, 0, getString(R.string.play));
        menu.add(0, Constant.DELETE, 0, getString(R.string.delete));
        menu.add(0, Constant.RENAME, 0, getString(R.string.rename));
        menu.add(0, Constant.SHARE, 0, getString(R.string.share));
        menu.add(0, Constant.DETAIL, 0, getString(R.string.details));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case Constant.PLAY:
//                mTotal.setText(Utils.convertTime(mAllMusics.get(menuInfo.position).getDuration()));
//                mSeekBar.setMax((int) mAllMusics.get(menuInfo.position).getDuration());
                currentPosition = menuInfo.position;
                mService.play(mAllMusics.get(currentPosition).getPath());

                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case Constant.DELETE:
                deleteFile(menuInfo.position);
                break;
            case Constant.RENAME:
                renameFile(menuInfo.position);
                break;
            case Constant.SHARE:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("audio/*");
                Uri u = Uri.parse(mAllMusics.get(menuInfo.position).getPath());
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
                .setTitle(mAllMusics.get(position).getTitle())
                .setMessage(R.string.delete_item)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(mAllMusics.get(position).getPath());
                        file.delete();

                        MediaUtil.delete(getContentResolver(), mAllMusics.get(position).getId());
                        scanFiles();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    private void renameFile(final int position) {
        View view = getLayoutInflater()
                .inflate(R.layout.rename_item, null);
        final EditText renameItem = (EditText) view.findViewById(R.id.item_name);
        renameItem.setText(mAllMusics.get(position).getTitle());

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rename))
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.Audio.Media.TITLE, renameItem.getText().toString());
                        MediaUtil.update(getContentResolver(), contentValues, mAllMusics.get(position).getId());

                        scanFiles();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    private void showDetails(int position) {
        StringBuffer sb = new StringBuffer();
        sb.append("名称 : " + mAllMusics.get(position).getTitle() + "\n");
        sb.append("时间 : " + Utils.convertTime(mAllMusics.get(position).getDuration()) + "\n");
        sb.append("大小 : " + Utils.convertSize(mAllMusics.get(position).getSize()) + "\n");
        sb.append("路径 : " + mAllMusics.get(position).getPath() + "\n");


        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.details))
                .setMessage(sb.toString())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void scanFiles() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mAllMusics != null) {
                    mAllMusics.clear();
                }
                MediaUtil.getMp3Infos(mAllMusics, MainActivity.this);
                totalNums = mAllMusics.size();
            }
        }).start();
        if(mAdapter !=null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_forward :
                //mService.seekMusic(3000);
                playNext();

                break;
            case R.id.b_rewind :
                //mService.seekMusic(-3000);
                playRewind();
                break;
            case R.id.b_play :
                if (mAllMusics.size() != 0) {
                    mService.playOrPauseMusic(mAllMusics.get(0).getPath());
                }
                break;
            case R.id.download_music :
                Intent _intent = new Intent(this,DownLoadActivity.class);
                startActivityForResult(_intent, 0);
        }
    }

    private void refreshViews(int position) {
        if (mAllMusics.size() != 0) {
            Mp3Info mp3Info = mAllMusics.get(position);
            mTotal.setText(Utils.convertTime(mp3Info.getDuration()));
            mSeekBar.setMax((int) mp3Info.getDuration());
            MediaUtil.displayImage(mMusicIcon, mp3Info.getAlbumId());
            StringBuffer sb = new StringBuffer();
            sb.append("歌手:");
            sb.append(mp3Info.getArtist() + "\n");
            sb.append("专辑:");
            sb.append(mp3Info.getAlbum() + "\n");
            sb.append("歌曲:");
            sb.append(mp3Info.getTitle());
            mMusicTitle.setText(sb.toString());
        }
    }

    private void playNext() {
        if (mAllMusics.size() != 0) {
            currentPosition = (currentPosition + 1) % totalNums;
            mService.play(mAllMusics.get(currentPosition).getPath());
        }
    }

    private void playRewind() {
        if (mAllMusics.size() != 0) {
            currentPosition = (currentPosition - 1 + totalNums) % totalNums;
            mService.play(mAllMusics.get(currentPosition).getPath());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (fromUser) {
//            mService.seekToMusic(progress);
//        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.edit().putInt(Constant.CURRENT, currentPosition).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0 && data != null && data.getBooleanExtra(Constant.FLAG,false)){
            scanFiles();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
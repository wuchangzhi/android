package com.ckt.francis.musicplayer.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.model.Mp3Info;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class MediaUtil {

    //获取专辑封面的Uri
    private static final String albumArtUri = "content://media/external/audio/albumart/";

    public static void getMp3Infos(List<Mp3Info> mp3Infos, Context context) {
        Cursor cursor = getResult(context, MediaStore.Audio.Media.DURATION + " > 3000", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor != null) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                Mp3Info mp3Info = new Mp3Info();
                long id = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media._ID));    //音乐id
                String title = cursor.getString((cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
                String artist = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
                String album = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ALBUM));    //专辑
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                long duration = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
                long size = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
                int isMusic = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
                if (isMusic != 0) { // 只把音乐添加到集合当中
                    mp3Info.setId(id);
                    mp3Info.setTitle(title);
                    mp3Info.setArtist(artist);
                    mp3Info.setAlbum(album);
                    mp3Info.setAlbumId(albumId);
                    mp3Info.setDuration(duration / 1000);
                    mp3Info.setSize(size);
                    mp3Info.setPath(path);
                    mp3Infos.add(mp3Info);
                }
            }
            cursor.close();
        }
    }

    public static Cursor getResult(Context context, String selection, String[] selectionArgs, String sortOrder) {
        String columns[] = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.IS_MUSIC
        };
        return context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs,
                sortOrder);
    }

    public static void displayImage(ImageView view, long albumId) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .showImageOnFail(R.mipmap.albumart_mp_unknown)
                .showImageForEmptyUri(R.mipmap.albumart_mp_unknown)
                .build();
        ImageLoader.getInstance().displayImage(albumArtUri + albumId, view, options);
    }

    public static void insert(ContentResolver resolver, ContentValues values) {
        resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static void delete(ContentResolver resolver, long id) {
        resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media._ID + " = ? ",
                new String[]{id + ""});

    }

    public static void update(ContentResolver resolver, ContentValues values, long id) {
        resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values,
                MediaStore.Audio.Media._ID + " = ? ",
                new String[]{id + ""});
    }

    public static void scanFiles(Context context,String path) {
        Intent _intent = new Intent();
        _intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/" + path);
        _intent.setData(uri);
        context.sendBroadcast(_intent);
    }

}
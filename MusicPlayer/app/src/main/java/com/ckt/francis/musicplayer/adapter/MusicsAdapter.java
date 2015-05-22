package com.ckt.francis.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.model.Mp3Info;
import com.ckt.francis.musicplayer.utils.Utils;

import java.util.List;

/**
 * Created by wuchangzhi on 15年5月20日.
 */
public class MusicsAdapter  extends BaseAdapter{
    private Context mContext;
    private List<Mp3Info> mAllMusics;

    public MusicsAdapter(Context mContext,List<Mp3Info> mAllMusics) {
        this.mContext= mContext;
        this.mAllMusics = mAllMusics;
    }

    @Override
    public int getCount() {
        return mAllMusics.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllMusics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.music_items, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.music_title);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.music_duration);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.music_artist);
            viewHolder.album = (TextView) convertView.findViewById(R.id.music_album);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.music_icon);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(mAllMusics.get(position).getTitle());
        viewHolder.duration.setText(Utils.convertTime(mAllMusics.get(position).getDuration()));
        viewHolder.artist.setText(mAllMusics.get(position).getArtist());
        viewHolder.album.setText(mAllMusics.get(position).getAlbum());
        /*viewHolder.icon.setImageBitmap(
                MediaUtil.getArtwork(mContext,
                        mAllMusics.get(position).getId(),
                        mAllMusics.get(position).getAlbumId(),
                        true,true));*/
        return convertView;
    }

    private class ViewHolder{
        public TextView title;
        public TextView duration;
        public TextView artist;
        public TextView album;
        public ImageView icon;
    }
}

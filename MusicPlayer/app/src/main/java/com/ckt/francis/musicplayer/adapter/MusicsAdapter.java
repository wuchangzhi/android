package com.ckt.francis.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuchangzhi on 15年5月20日.
 */
public class MusicsAdapter  extends BaseAdapter{
    private Context mContext;
    private List<Map<String,String>> mAllMusics;

    public MusicsAdapter(Context mContext,List<Map<String, String>> mAllMusics) {
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
            viewHolder.path = (TextView) convertView.findViewById(R.id.music_path);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(mAllMusics.get(position).get(Constant.NAME));
        viewHolder.path.setText(mAllMusics.get(position).get(Constant.PATH));
        return convertView;
    }

    private class ViewHolder{
        public TextView title;
        public TextView path;
    }
}

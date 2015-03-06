package com.ckt.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ckt.myfirstapp.R;

public class LeftFragment extends ListFragment {
	private int[] icons = { R.drawable.a1, R.drawable.a2, R.drawable.a3,
	        R.drawable.a4, R.drawable.a5, R.drawable.a6, R.drawable.a7,
	        R.drawable.a8, R.drawable.a9, R.drawable.a10, R.drawable.a11,
	        R.drawable.a12, R.drawable.a13, R.drawable.a14, R.drawable.a15 };
	private String[] titles = { "注意", "危险", "拨号", "邮件", "感叹", "地图", "时钟",
	        "USB", "电话", "快进", "下一首", "暂停", "播放", "上一首", "快退" };
	private ListAdapter adapter;
	private Callback callback;
	private int selectItem = -1;

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public interface Callback {
		void callback(String title);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = (Callback) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ListAdapter();
		Bundle bundle = getArguments();
		if (bundle != null) {
		}
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_list, container, false);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private class ViewHolder {
		ImageView image;
		TextView title;
	}

	private class ListAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
				        R.layout.left_fragment, parent, false);
				holder.image = (ImageView) convertView
				        .findViewById(R.id.left_icon);
				holder.title = (TextView) convertView
				        .findViewById(R.id.left_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == selectItem) {
				convertView.setBackgroundColor(Color.argb(255, 170, 170, 170));
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			holder.image.setBackgroundResource(icons[position]);
			holder.title.setText(titles[position]);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return icons.length;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		selectItem = position;
		adapter.notifyDataSetChanged();
		callback.callback(titles[position]);
	}
}

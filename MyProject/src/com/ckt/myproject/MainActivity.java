package com.ckt.myproject;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ckt.dao.Mode;
import com.ckt.fragment.MyFragment;

public class MainActivity extends Activity {
	private FrameLayout fragment;
	private ListView mListView;
	private List<Mode> lists;
	private float dxStart;
	private float dxMove;
	private float max;
	private float min;
	private boolean isShow;
	private boolean flag = true;
	private List<Fragment> fragments;
	private LayoutParams mLayoutParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		max = metrics.widthPixels * 7 / 8;
		min = metrics.widthPixels / 8;
		Log.d("test", "min" + min);
		Log.d("test", "max" + max);
		initview();
		initData();

		mListView.setAdapter(new ListAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
				Log.d("test", isShow + "");
				if (!isShow) {
					show();
				}
				Fragment fragment = fragments.get(position);
				getFragmentManager().beginTransaction().replace(R.id.fragment,
				        fragment).commit();
			}

		});
	}

	private void initData() {
		lists = new ArrayList<Mode>();
		fragments = new ArrayList<Fragment>();
		for (int i = 0; i < 15; i++) {
			Fragment fragment = new MyFragment(i);
			fragments.add(fragment);
			Mode mode = new Mode();
			mode.setBitmapID(R.drawable.ic_settings_bluetooth2);
			mode.setTitle("Test" + i);
			lists.add(mode);
		}
		getFragmentManager().beginTransaction().add(R.id.fragment,
		        fragments.get(0)).commit();
	}

	private void initview() {
		fragment = (FrameLayout) findViewById(R.id.fragment);
		mListView = (ListView) findViewById(R.id.list_view);
		isShow = true;
		mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
		        LayoutParams.MATCH_PARENT);
		mLayoutParams.setMarginStart((int) min);
		fragment.setLayoutParams(mLayoutParams);
		fragment.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						dxStart = event.getX();
						Log.d("test", "1 :" + dxStart);
						break;
					case MotionEvent.ACTION_MOVE:
						dxMove = event.getX() - dxStart;
						float x = mLayoutParams.getMarginStart() + dxMove;
						if (Math.abs(dxMove) > 20 && x < max && x > min) {
							mLayoutParams.setMarginStart((int) x);
							fragment.setLayoutParams(mLayoutParams);
						}
						break;
					case MotionEvent.ACTION_UP:
						dxMove = event.getX() - dxStart;
						Log.d("test", "dx : " + mLayoutParams.getMarginStart());
						if (mLayoutParams.getMarginStart() > (max + min) / 2) {
							hide();
						} else {
							show();
						}
						break;
					default:
						break;
				}
				return true;
			}
		});
		show();
	}

	private void show() {
		mLayoutParams.setMarginStart((int) min);
		fragment.setLayoutParams(mLayoutParams);
		isShow = true;
	}

	private void hide() {
		mLayoutParams.setMarginStart((int) max);
		fragment.setLayoutParams(mLayoutParams);
		isShow = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Mode getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
				        .from(MainActivity.this);
				convertView = inflater.inflate(R.layout.items, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
				        .findViewById(R.id.image);
				viewHolder.textView = (TextView) convertView
				        .findViewById(R.id.text);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.imageView.setBackgroundResource(lists.get(position)
			        .getBitmapID());
			viewHolder.textView.setText(lists.get(position).getTitle());
			return convertView;
		}

		private class ViewHolder {
			public ImageView imageView;
			public TextView textView;
		}
	}
}

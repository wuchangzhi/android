package com.ckt.trainticket;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ckt.fragment.HasMoreFragment;
import com.ckt.fragment.My12306Fragment;
import com.ckt.fragment.MyOrderFragment;
import com.ckt.fragment.SearchTrainFragment;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private ViewPager vierPager;
	private TextView serachTrain;
	private TextView myOrder;
	private TextView my12306;
	private TextView hasMore;
	private List<Fragment> fragmentList;
	private List<TextView> buttomViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_train);
		fragmentList = new ArrayList<Fragment>();
		buttomViews = new ArrayList<TextView>();
//		initView();
//		vierPager.setCurrentItem(0);
//		serachTrain.setEnabled(false);
	}

	private void initView() {
		vierPager = (ViewPager) findViewById(R.id.viewpager);
		serachTrain = (TextView) findViewById(R.id.serach_train);
		myOrder = (TextView) findViewById(R.id.my_order);
		my12306 = (TextView) findViewById(R.id.my12306);
		hasMore = (TextView) findViewById(R.id.has_more);

		serachTrain.setOnClickListener(this);
		myOrder.setOnClickListener(this);
		my12306.setOnClickListener(this);
		hasMore.setOnClickListener(this);

		buttomViews.add(serachTrain);
		buttomViews.add(myOrder);
		buttomViews.add(my12306);
		buttomViews.add(hasMore);

		fragmentList.add(new SearchTrainFragment());
		fragmentList.add(new MyOrderFragment());
		fragmentList.add(new My12306Fragment());
		fragmentList.add(new HasMoreFragment());

		vierPager.setAdapter(new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragmentList.size();
			}

			@Override
			public Fragment getItem(int position) {
				return fragmentList.get(position);
			}
		});
		vierPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setButtomBarStatus(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setButtomBarStatus(int position) {
		for (TextView textView : buttomViews) {
			textView.setEnabled(true);
		}
		buttomViews.get(position).setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.serach_train:
			vierPager.setCurrentItem(0);
			break;
		case R.id.my_order:
			vierPager.setCurrentItem(1);
			break;
		case R.id.my12306:
			vierPager.setCurrentItem(2);
			break;
		case R.id.has_more:
			vierPager.setCurrentItem(3);
			break;
		default:
			break;
		}

	}
}

package com.ckt.myfirstapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ckt.fragment.LeftFragment;
import com.ckt.fragment.LeftFragment.Callback;
import com.ckt.fragment.RightFragment;
import com.ckt.util.Utils;
import com.ckt.view.DrawerLayout;

public class MainActivity extends Activity implements Callback {
	private float x_postion;
	private DrawerLayout drawerLayout;
	private FrameLayout left;
	private FrameLayout right;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initFragment();
		initEvents();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		Utils.CENTER = metrics.widthPixels / 2;
	}

	private void initEvents() {
		right.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						x_postion = event.getRawX();
						break;
					case MotionEvent.ACTION_MOVE:
						if (Math.abs(x_postion - event.getRawX()) > 10) {
							drawerLayout.setLeftDistance((int) event.getRawX(),
							        false);
						}
						break;
					case MotionEvent.ACTION_UP:
						if (Math.abs(x_postion - event.getRawX()) < 10) {
							return false;
						}
						drawerLayout.setLeftDistance((int) event.getRawX(),
						        true);
						break;
					default:
						break;
				}
				return true;
			}
		});
	}

	private void initFragment() {
		// TODO Auto-generated method stub
		LeftFragment leftFragment = new LeftFragment();
		RightFragment rightFragment = new RightFragment();

		getFragmentManager().beginTransaction().replace(R.id.left_fragment,
		        leftFragment).commit();
		getFragmentManager().beginTransaction().replace(R.id.right_fragment,
		        rightFragment).commit();
	}

	private void initView() {
		drawerLayout = (DrawerLayout) findViewById(R.id.container);
		left = (FrameLayout) findViewById(R.id.left_fragment);
		right = (FrameLayout) findViewById(R.id.right_fragment);
	}

	@Override
	public void callback(String title) {
		drawerLayout.setLeftDistance(Utils.STARTPOSITON, true);
		Bundle bundle = new Bundle();
		bundle.putString(Utils.TITLE, title);
		RightFragment rightFragment = new RightFragment();
		rightFragment.setArguments(bundle);

		getFragmentManager().beginTransaction().replace(R.id.right_fragment,
		        rightFragment).commit();
	}
}

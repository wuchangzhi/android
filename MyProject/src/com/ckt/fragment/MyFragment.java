package com.ckt.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ckt.myproject.R;

public class MyFragment extends Fragment {
	private int position;

	public MyFragment(int position) {
		this.position = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment, container, false);
		((TextView) view.findViewById(R.id.my_text)).setText("Text" + position);
		return view;
	}
}

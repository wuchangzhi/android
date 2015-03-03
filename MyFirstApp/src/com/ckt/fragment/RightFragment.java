package com.ckt.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.myfirstapp.R;
import com.ckt.util.Utils;

public class RightFragment extends Fragment {
	private String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			title = bundle.getString(Utils.TITLE, "title");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.right_fragment, container, false);
		Button button = (Button) view.findViewById(R.id.button);
		TextView textview = (TextView) view.findViewById(R.id.right_text);
		textview.setText(title);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}
}

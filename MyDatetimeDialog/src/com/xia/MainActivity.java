package com.xia;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xia.ui.component.MyDateTimePickerDialog;
import com.xia.ui.component.MyDateTimePickerDialog.OnDateTimeSetListener;

public class MainActivity extends Activity {
    private TextView text;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        LinearLayout l=new LinearLayout(this);
        Button btn=new Button(this);
        text=new TextView(this);
      
        l.addView(text);
        l.addView(btn);
        btn.setText("时间测试");
        setContentView(l);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new MyDateTimePickerDialog(MainActivity.this, new OnDateTimeSetListener() {
					
					@Override
					public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth,
							int hour, int minute) {
					  text.setText(year+"年"+monthOfYear+"月"+dayOfMonth+"日"+hour+"时"+minute+"分");	
					}
				}).show();
			}
			
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}

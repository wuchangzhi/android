package com.ltc.demo.https;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView msgTextView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        msgTextView = (TextView)findViewById(R.id.result);
        
        findViewById(R.id.button).setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String strUrl = "https://mail.google.com";
//				HttpGet httpRequest = new HttpGet(strUrl);
				HttpPost httpRequest = new HttpPost(strUrl);
				
				HttpClient httpClient = HttpUtils.getNewHttpClient(MainActivity.this);
				
				try {
					HttpResponse httpResponse = httpClient.execute(httpRequest);
					if(httpResponse.getStatusLine().getStatusCode() == 200) {
						String strResult = EntityUtils.toString(httpResponse.getEntity());
						System.out.println(strResult);
						msgTextView.setText(strResult);
					} else {
						String strResult = EntityUtils.toString(httpResponse.getEntity());
						msgTextView.setText(strResult);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
    }
}
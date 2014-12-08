package com.ltc.demo.https;

import java.security.KeyStore;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

public class HttpUtils {
	public static HttpClient getNewHttpClient(Context context) {
//		try {
//			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			trustStore.load(null, null);
//			SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
//			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			HttpParams params = new BasicHttpParams();
//			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//			SchemeRegistry registry = new SchemeRegistry();
//			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//			registry.register(new Scheme("https", sf, 443));
//
//			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
//					params, registry);
//			return new DefaultHttpClient(ccm, params);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new DefaultHttpClient();
//		}
		
		try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

//            HttpConnectionParams.setConnectionTimeout(params, 10000);
//            HttpConnectionParams.setSoTimeout(params, 10000);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            // Set the default socket timeout (SO_TIMEOUT) // in
            // milliseconds which is the timeout for waiting for data.
//            HttpConnectionParams.setConnectionTimeout(params, 500000);
//            HttpConnectionParams.setSoTimeout(params, 200000);
            HttpClient client = new DefaultHttpClient(ccm, params);
            
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                // 获取当前正在使用的APN接入点
                Uri uri = Uri.parse("content://telephony/carriers/preferapn");
                Cursor mCursor = context.getContentResolver().query(uri, null, null, null, null);
                if (mCursor != null && mCursor.moveToFirst()) {
                    // 游标移至第一条记录，当然也只有一条
                    String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
                    if (proxyStr != null && proxyStr.trim().length() > 0) {
                        HttpHost proxy = new HttpHost(proxyStr, 80);
                        client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                    }
                    mCursor.close();
                }
            }
            return client;
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.e("HttpUtils", "exception");
            return new DefaultHttpClient();
        }
	}
}

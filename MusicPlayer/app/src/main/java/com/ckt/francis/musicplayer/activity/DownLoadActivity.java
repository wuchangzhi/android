package com.ckt.francis.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.utils.Constant;
import com.ckt.francis.musicplayer.utils.MediaUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by wuchangzhi on 15年5月28日.
 */
public class DownLoadActivity extends Activity {
    private WebView mWebView;
    private Intent mIntent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.download_music);

        mIntent = new Intent();
        mIntent.putExtra(Constant.FLAG, false);
        mWebView = (WebView) findViewById(R.id.music_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("http://music.baidu.com/data/music/file")) {
                    final String name = url.substring(url.lastIndexOf('=') + 1, url.length());

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(url, new FileAsyncHttpResponseHandler(DownLoadActivity.this) {
                        @Override
                        public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                            Log.d("test", "onFailure");
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, File file) {
                            FileOutputStream fo = null;
                            FileInputStream fi = null;
                            try {
                                File files = new File(Environment.getExternalStorageDirectory() + "/" + URLDecoder.decode(name, "utf-8") + ".mp3");
                                fo = new FileOutputStream(files);
                                fi = new FileInputStream(file);
                                byte[] buffer = new byte[1024];
                                int len = 0;
                                while ((len = fi.read(buffer)) != -1) {
                                    fo.write(buffer, 0, len);
                                }
                                MediaUtil.scanFiles(DownLoadActivity.this, files.getName());

                                mIntent.putExtra(Constant.FLAG, true);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (fi != null) {
                                    try {
                                        fi.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (fo != null) {
                                    try {
                                        fo.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                setProgress(newProgress * 100);
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.loadUrl("http://music.baidu.com");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setResult(0, mIntent);
    }
}

package com.ckt.francis.musicplayer.activity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ckt.francis.musicplayer.R;
import com.ckt.francis.musicplayer.activity.base.BaseActivity;
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
public class DownLoadActivity extends BaseActivity {
    private WebView mWebView;
    private Intent mIntent;
    private Notification mNotification;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.download_music);

        mIntent = new Intent();
        mIntent.putExtra(Constant.FLAG, false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        public void onProgress(int bytesWritten, int totalSize) {
                            super.onProgress(bytesWritten, totalSize);
                            Log.d("test","" + bytesWritten + " " + totalSize);
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, File file) {
                            FileOutputStream fo = null;
                            FileInputStream fi = null;
                            try {
                                String music_name = URLDecoder.decode(name, "utf-8") + ".mp3";
                                File files = new File(Environment.getExternalStorageDirectory() + "/" + music_name);
                                fo = new FileOutputStream(files);
                                fi = new FileInputStream(file);
                                byte[] buffer = new byte[1024];
                                int len = 0;
                                while ((len = fi.read(buffer)) != -1) {
                                    fo.write(buffer, 0, len);
                                }
                                fo.flush();
                                MediaUtil.scanFiles(DownLoadActivity.this,music_name);
                                mIntent.putExtra(Constant.FLAG, true);
                                Toast.makeText(DownLoadActivity.this, music_name + "下载完成",Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            mIntent.putExtra(Constant.FLAG,true);
            setResult(0, mIntent);
            finish();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

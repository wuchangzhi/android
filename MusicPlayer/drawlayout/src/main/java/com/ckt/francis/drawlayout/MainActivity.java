package com.ckt.francis.drawlayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("test",getActionBar() + "");
        getActionBar().setDisplayHomeAsUpEnabled(true);//给home icon的左边加上一个返回的图标
        getActionBar().setHomeButtonEnabled(true); //需要api level 14  使用home-icon 可点击
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setDisplayShowHomeEnabled(true);
        initViews();

        initEvents();
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,new String[]{"zhangsan"});
        mListView.setAdapter(adapter);
        mDrawerLayout.setDrawerListener(mToggle);
    }

    private void initEvents() {
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.action_settings,R.string.action_settings){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(drawerView instanceof TextView){
                    Toast.makeText(MainActivity.this,"TextView",1).show();
                }else{
                    Toast.makeText(MainActivity.this,"ListViewss",1).show();
                }
                Log.d("test",drawerView + "");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mToggle.syncState();
    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mListView = (ListView) findViewById(R.id.list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {//actionbar上的home icon
            //END即gravity.right 从右向左显示   START即left  从左向右弹出显示
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);//关闭抽屉
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);//打开抽屉
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

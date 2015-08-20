package com.ckt.francis.navigationmap.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.ckt.francis.navigationmap.R;
import com.ckt.francis.navigationmap.util.ToolUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends Activity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 定位的核心类:LocationClient
    private LocationClient mLocClient;
    private boolean isFirstLoc = true;
    private MyLocationListenner myListener = null ;

    private static final String APP_FOLDER_NAME = "BNSDKDemo";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private String authinfo = null;
    private String mSDCardPath = null;
    private BNRoutePlanNode sNode = null;
    private BNRoutePlanNode eNode = null;
    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        public void onGetPoiResult(PoiResult result){
            //获取POI检索结果
            if(result != null) {
                List<PoiInfo> poiInfos = result.getAllPoi();
                LatLng eLatLng = ToolUtils.bd09_To_Gcj02(new LatLng(poiInfos.get(0).location.latitude, poiInfos.get(0).location.longitude));
                eNode = new BNRoutePlanNode(eLatLng.longitude,eLatLng.latitude ,
                        poiInfos.get(0).name, null);
            }else {
                Toast.makeText(MainActivity.this,"ERROR",Toast.LENGTH_LONG).show();
            }
        }
        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
            Toast.makeText(MainActivity.this,result+"1",Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));

        mLocClient = new LocationClient(this);
        // 定位的回调接口
        myListener = new MyLocationListenner();
        mLocClient.registerLocationListener(myListener);

        // 设置mLocationClient数据,如是否打开GPS,使用LocationClientOption类.
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        // 设置发起定位请求的间隔. < 1000,则为app主动请求定位,>=1000,则为定时定位模式
        option.setScanSpan(100);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        //option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        //option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        mLocClient.setLocOption(option);
        //初始化poi搜索核心类
        mPoiSearch = PoiSearch.newInstance();
        //注册poi搜索回调接口
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        if (initDirs()) {
            initNavi();
        }
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        mMapView.onResume();

        mLocClient.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
       mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocClient.stop();
        mPoiSearch.destroy();
        mMapView.onDestroy();
    }

    private boolean initDirs() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            mSDCardPath = Environment.getExternalStorageDirectory().toString();
        }
        if ( mSDCardPath == null ) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if ( !f.exists() ) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        BaiduNaviManager.getInstance().setNativeLibraryPath(mSDCardPath + "/BaiduNaviSDK_SO");
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        Toast.makeText(MainActivity.this,authinfo,Toast.LENGTH_LONG).show();
                    }

                    public void initSuccess() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        if(mPoiSearch != null) {
                            mPoiSearch.searchInCity((new PoiCitySearchOption())
                                    .city("深圳市")
                                    .keyword("西乡地铁站"));
                        }
                    }

                    public void initStart() {
                    }

                    public void initFailed() {
                    }
                }, null /*mTTSCallback*/);
    }

    private void initListener() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (BaiduNaviManager.isNaviInited()) {
                    routeplanToNavi();
                }
            }
        });
        findViewById(R.id.buttons).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isFirstLoc = true;
                //mLocClient.start();
                mLocClient.requestLocation();
            }
        });
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.WGS84);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.GCJ02);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09_MC);
    }

    private void routeplanToNavi() {
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener());
        }
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

        return super.onOptionsItemSelected(item);
    }

    /*
         定位成功之后的回调接口
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 获取定位精度
                    .direction(100) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude()) // 获取纬度坐标
                    .longitude(location.getLongitude())// 获取精度坐标
                    .build();
            mBaiduMap.setMyLocationData(locData);
            LatLng sLatLng = ToolUtils.bd09_To_Gcj02(new LatLng(location.getLatitude(),location.getLongitude()));
            sNode = new BNRoutePlanNode(sLatLng.longitude,sLatLng.latitude,"",null);
            //sNode = new BNRoutePlanNode(sLatLng.longitude,sLatLng.longitude,"",null);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u,1000);
            }
        }
    }

    /*
        启动导航的监听器
     */
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(MainActivity.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, sNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {

        }
    }
}
